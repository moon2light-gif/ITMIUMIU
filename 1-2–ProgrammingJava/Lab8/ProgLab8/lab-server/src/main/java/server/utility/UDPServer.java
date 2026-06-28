package server.utility;

import common.transport.Request;
import common.transport.Response;
import common.utility.Serializer;
import server.database.ConnectionPool;
import server.service.RequestExecutor;

import javax.imageio.IIOException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Класс {@code UDPServer} отвечает за работу с сетью
 *
 * @author mikhailkramar
 */
public class UDPServer {
    private static final Logger LOGGER = Logger.getLogger(UDPServer.class.getName());

    private final int PORT;
    private final ServerConsole serverConsole;
    private final RequestExecutor requestExecutor;

    private final ExecutorService readingPool = new ForkJoinPool(); //пул для многопоточного чтения запросов
    private final ExecutorService processingPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()); //пул для многопоточной обработки запросов
    private final ExecutorService sendingPool = Executors.newCachedThreadPool(); //пул для многопоточной отправки ответа

    private DatagramChannel datagramChannel; //канал для получения и отправки пользовательских и серверных датаграмм соответственно
    private boolean isRunning; //флажок, говорящий о том, идёт ли процесс или нет

    //мапа, которая хранит ID каждого запроса, который сейчас выполняется и адрес клиента, который этот запрос послал
    private final ConcurrentMap<UUID, SocketAddress> activeRequests = new ConcurrentHashMap<>();

    /**
     * Конструктор UPDServer
     * @param PORT порт, на котором сервер ожидает входящие пакеты
     * @param requestExecutor исполнитель запроса, возвращающий объект Response
     * @param serverConsole объект управления консолью сервера
     */
    public UDPServer(int PORT, RequestExecutor requestExecutor, ServerConsole serverConsole) {
        this.PORT = PORT;
        this.serverConsole = serverConsole;
        this.requestExecutor = requestExecutor;
    }

    /**
     * Метод, предназначенный для старта работы с сетью
     * @throws IOException ошибка, связанная с адресом или портом
     */
    public void start() throws IOException {
        datagramChannel = DatagramChannel.open();
        datagramChannel.configureBlocking(true); //теперь тут блокирующий режим
        datagramChannel.bind(new InetSocketAddress(PORT)); //привязываем сервер к порту

        isRunning = true;
        LOGGER.log(Level.INFO, "Сервер запущен на порту: " + PORT + "\n");

        //чтение запросов делаю в отдельном потоке
        Thread receiverThread = new Thread(this::receiveLoop, "ReceiverThread");
        receiverThread.start();

        //обработка консоли самого сервера
        runConsoleLoop();
    }

    /**
     * Метод, вызывающий управление консолью сервера в главном потоке
     */
    public void runConsoleLoop() {
        while (isRunning) {
            try {
                serverConsole.checkCommandLine();
            } catch (IOException ex) {
                LOGGER.log(Level.WARNING, "Ошибка консоли сервера: " + ex.getMessage());
            }
        }
    }

    /**
     * Метод, предназначенный для чтения UDP-пакетов из сети (работает в receiverThread)
     */
    public void receiveLoop() {
        //у потока чтения есть свой собственный буфер
        ByteBuffer byteBuffer = ByteBuffer.allocate(65536);

        while (isRunning) {
            try {
                byteBuffer.clear();
                SocketAddress clientAddress = datagramChannel.receive(byteBuffer);

                if (clientAddress != null && isRunning) {

                    byteBuffer.flip(); //переходим к чтению из буфера
                    byte[] data = new byte[byteBuffer.remaining()]; //выделяем ровно столько байт, сколько в буфере записано
                    byteBuffer.get(data); //копирую байты

                    //передаём задание по обработке в пул потоков
                    readingPool.submit(() -> readRequest(data, clientAddress));
                }
            } catch (IOException ex) {
                if (isRunning) {
                    LOGGER.log(Level.WARNING, "Ошибка при чтении пакета: " + ex.getMessage());
                }
            }
        }
    }

    /**
     * Метод, предназначенный для чтения запроса (работает в readingPool)
     * @param data массив байтов, который мы получили по сети
     * @param clientAddress IP-адрес устройства клиента, который послал запрос
     */
    public void readRequest(byte[] data, SocketAddress clientAddress) {
        try {
            Request request = (Request) Serializer.deserialize(data);
            LOGGER.log(Level.INFO, "[" + Thread.currentThread().getName() + "] Запрос успешно прочитан. Обработка команды: " + request.getCommandName());

            UUID requestId = request.getRequestId(); //тут получаю ID пришедшего запроса

            //тут проверка на то, обрабатывается ли этот запрос прямо сейчас
            if (activeRequests.putIfAbsent(requestId, clientAddress) != null) {
                //тут если не null, то такой ID уже есть в мапе
                activeRequests.put(requestId, clientAddress);
                LOGGER.log(Level.INFO, "Получен дубликатик запроса" + requestId);
                return; //тут не заходим в processingPool
            }

            //передаём готовый объект запроса Request на обработку в отдельный пул потоков
            processingPool.submit(() -> processRequest(request, clientAddress));

        } catch (ClassNotFoundException | IOException ex) {
            LOGGER.log(Level.WARNING, "Ошибка обработки запроса: ", ex);
        }
    }
    /**
     * Метод, предназначенный для обработки запроса (работает в processingPool)
     * @param request десериализованный объект запроса клиента
     * @param clientAddress IP-адрес устройства клиента, который послал запрос
     */
    public void processRequest(Request request, SocketAddress clientAddress) {
        //исполняем запрос клиента
        Response response = requestExecutor.execute(request);
        LOGGER.log(Level.INFO, "[" + Thread.currentThread().getName() + "] Запрос успешно обработан!");

        UUID requestId = request.getRequestId();

        SocketAddress actualAddress = activeRequests.remove(requestId);
        if (actualAddress == null) {
            actualAddress = clientAddress;
        }

        SocketAddress finalAddress = actualAddress;
        //передаём задачу по отправке объекта Response в отдельный пул потоков
        sendingPool.submit(() -> sendResponse(response, finalAddress));
    }

    /**
     * Метод, предназначенный для отправки ответа клиенту (работает в sendingPool)
     * @param response объект ответа от сервера
     * @param clientAddress IP-адрес устройства клиента, которому нужно отправить ответ
     */
    public void sendResponse(Response response, SocketAddress clientAddress) {
        try {
            byte[] responseData = Serializer.serialize(response);
            ByteBuffer sendBuffer = ByteBuffer.wrap(responseData);

            datagramChannel.send(sendBuffer, clientAddress);
            LOGGER.log(Level.INFO, "[" + Thread.currentThread().getName() + "] Ответ отправлен клиенту: " + clientAddress);
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "Ошибка при отправке ответа клиенту: " + ex.getMessage());
        }
    }

    /**
     * Метод, предназначенный для корректной остановки всех пулов
     */
    public void stop() {
        this.isRunning = false;
        readingPool.shutdown();
        processingPool.shutdown();
        sendingPool.shutdown();
        try {
            if (datagramChannel != null && datagramChannel.isOpen()) {
                datagramChannel.close();
            }
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Ошибка закрытия DatagramChannel: " + ex.getMessage());
        }
        LOGGER.log(Level.INFO, "Все пулы сервера успешно остановлены.");
    }
}
