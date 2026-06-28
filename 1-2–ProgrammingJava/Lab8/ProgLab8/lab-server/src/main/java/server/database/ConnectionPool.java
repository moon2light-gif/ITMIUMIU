package server.database;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Класс {@code ConnectionPool} управляет пулом готовых подключений к БД.
 * Ограничивает максимальное количество активных подключений до 10
 */
public class ConnectionPool {
    private static final Logger LOGGER = Logger.getLogger(ConnectionPool.class.getName());

    private final DatabaseConnector databaseConnector;
    private final BlockingQueue<Connection> pool; //коллекция, хранящая соединения с БД (сам пул)

    private final AtomicInteger activeConnectionsCount = new AtomicInteger(0);
    //счётчик для безопасного отслеживания общего числа созданных физических соединений

    private static final int MAX_POOL_SIZE = 10;

    public ConnectionPool(DatabaseConnector databaseConnector) {
        this.databaseConnector = databaseConnector;
        this.pool = new LinkedBlockingQueue<>(MAX_POOL_SIZE);
        //выставляем лимит числа подключений одновременных (10 штук будет)
        initializePool();
    }

    /**
     * Метод, который теперь сообщает о готовности пула к старту
     * (раньше инициализировал сам пул, я его пересобрал для динамической версии)
     */
    private void initializePool() {
        LOGGER.log(Level.INFO, "Динамический пул соединений готов к работе! (Максимум " + MAX_POOL_SIZE + " подключений).");
    }

    public Connection getConnection() throws SQLException {
        Connection connection = pool.poll(); //пытаемся взять уже существующее и свободное соединение из очереди соединений

        if (connection != null) {
            try {
                if (connection.isClosed()) {
                    LOGGER.log(Level.INFO, "Пул обнаружил закрытое соединение, уменьшаем счётчик");
                    activeConnectionsCount.decrementAndGet();
                } else {
                    LOGGER.log(Level.INFO, "Переиспользуем уже существующее соединение из пула.");
                    return connection;
                }
            } catch (SQLException ex) {
                activeConnectionsCount.decrementAndGet();
            }
        }

        //сюда идём, если свободных подключений нет
        while (true) {
            int currentCount = activeConnectionsCount.get();

            if (currentCount < MAX_POOL_SIZE) {
                if (activeConnectionsCount.compareAndSet(currentCount, currentCount + 1)) {
                    try {
                        LOGGER.log(Level.INFO, "Создаём новое физическое соединение. Всего активных на данный момент: " + (currentCount+1));
                        return databaseConnector.createNewConnection();
                    } catch (SQLException ex) {
                        //если БД сейчас недоступна, сбрасываем счётчик назад
                        activeConnectionsCount.decrementAndGet();
                        throw new SQLException("Ошибка создания подключения (БД недоступна).");
                    }
                }
            } else {
                //сюда попали, если достигнут лимит в 10 соединений, и все они сейчас заняты
                LOGGER.log(Level.INFO, "Все " + MAX_POOL_SIZE + " соединений заняты! Запрос поставлен в очередь ожидания...");
                try {
                    connection = pool.poll(5, TimeUnit.SECONDS);
                    if (connection == null) {
                        throw new SQLException("Превышено время ожидания свободного соединения из пула! Все " + MAX_POOL_SIZE + " подключений заняты!");
                    }
                    else if (connection.isClosed()) {
                        activeConnectionsCount.decrementAndGet();
                        continue; // Пробуем заново в цикле
                    }
                    LOGGER.log(Level.INFO, "Дождались освобождения соединения из пула подключений!");
                    return connection;
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    throw new SQLException("Поток обработки запроса был прерван во время ожидания соединения из пула", ex);
                }
            }
        }
    }

    /**
     * Метод, позволяющий вернуть соединение обратно в пул для повторного использования
     * @param connection соединение, которое мы собираемся вернуть
     */
    public void releaseConnection(Connection connection) {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    //возвращаем соединение в очередь, если очередь заполнена, закрываем тогда само соединение
                    if (!pool.offer(connection)) {
                        connection.close();
                        activeConnectionsCount.decrementAndGet();
                        LOGGER.log(Level.INFO, "Очередь переполнена, соединение закрыто!");
                    } else {
                        LOGGER.log(Level.INFO, "Соединение успешно возвращено в пул соединений для последующего использования!");
                    }
                } else {
                    activeConnectionsCount.decrementAndGet();
                }

            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "Не получилось безопасно вернуть соединение в пул", ex);
                activeConnectionsCount.decrementAndGet();
            }
        }
    }

    /**
     * Метод, предназначенный для закрытия всех физических соединений и очищения пула при остановке сервера
     */
    public void shutdown() {
        System.out.println("Закрытие пула соединений к базе данных...");
        System.out.flush(); //сразу выталкиваю текст в консоль
        Connection connection;
        while ((connection = pool.poll()) != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException ex) {
                System.err.println("Ошибка при закрытии соединения при выключении сервера!");
                System.err.flush();
            }
        }
        pool.clear();
        activeConnectionsCount.set(0);
    }
}
