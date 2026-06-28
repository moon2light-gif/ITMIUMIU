package server.database;

import server.utility.UDPServer;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Класс {@code DatabaseConnector} отвечает за поиск драйвера и подключение к базе данных
 *
 * @author mikhailkramar
 */
public class DatabaseConnector {
    private static final Logger LOGGER = Logger.getLogger(DatabaseConnector.class.getName());
    private static final Properties properties = new Properties();

    //статический блок инициализации, поэтому выполнится один раз при загрузке этого класса
    static {
        try (FileInputStream fileInputStream = new FileInputStream("db.cfg")) {
            properties.load(fileInputStream);
            LOGGER.log(Level.INFO, "Конфигурационный файл db.cfg успешно загружен!");
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Не удалось найти или прочитать указанный файл db.cfg!", ex);
        }

        try {
            Class.forName("org.postgresql.Driver"); //ищем в библиотеках, которые мы добавили, класс драйвера для PostgreSQL и загружаем его
            //                           ^ полный путь к классу драйвера
            LOGGER.log(Level.INFO, "PostgreSQL JDBC Driver успешно зарегистрирован!");
        } catch (ClassNotFoundException ex) {
            LOGGER.log(Level.SEVERE, "Ошибка: PostgreSQL JDBC Driver не найден!");
        }

    }

    static final String DB_URL = properties.getProperty("db.url");
    static final String USER = properties.getProperty("db.user");
    static final String PASS = properties.getProperty("db.pass");

    /**
     * Метод, отвечающий за выдачу нового физического подключения к базе данных
     * @return объект Connection
     */
    public Connection createNewConnection() throws SQLException {
        LOGGER.log(Level.INFO, "Установка физического соединения с базой данных: " + DB_URL);

        //тут может выскочить исключение SQLException, когда база не подключена, я пробрасываю его выше
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }
}
