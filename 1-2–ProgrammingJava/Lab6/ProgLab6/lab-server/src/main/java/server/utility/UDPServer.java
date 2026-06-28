package server.utility;

import common.transport.Request;
import common.transport.Response;
import common.utility.Serializer;
import server.Server;
import server.commands.Command;
import server.service.RequestExecutor;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Класс {@code UDPServer} отвечает за работу с DatagramChannel в неблокирующем режиме
 *
 * @author mikhailkramar
 */
public class UDPServer {
    private static final Logger LOGGER = Logger.getLogger(UDPServer.class.getName());

    private final int PORT;
    private final ServerConsole serverConsole;
    private final RequestExecutor requestExecutor;
    private DatagramChannel datagramChannel;
    private boolean isRunning;
    private final ByteBuffer buffer = ByteBuffer.allocate(65536);

    /**
     * Конструктор UPDServer
     * @param PORT порт, на котором сервер ожидает входящие пакеты
     * @param requestExecutor исполнитель запроса, возвращающий объект Response
     */
    public UDPServer(int PORT, RequestExecutor requestExecutor, ServerConsole serverConsole) {
        this.PORT = PORT;
        this.serverConsole = serverConsole;
        this.requestExecutor = requestExecutor;
    }

    /**
     * Метод, отвечающий за запуск приёма подключений от клиентов и команд на сервере
     */
    public void start() {

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            requestExecutor.execute(new Request("save", null, null));
            System.out.println("Завершение работы программы...");
        }));

        try {

            datagramChannel = DatagramChannel.open();
            datagramChannel.configureBlocking(false); //non-blocking режим, receive() не будет зависать в ожидании ответа, сразу вернет null, если нет пакетов
            datagramChannel.bind(new InetSocketAddress(PORT)); //привязываем сервер к порту

            isRunning = true;
            LOGGER.log(Level.INFO, "Сервер запущен на порту: " + PORT);

            while(isRunning) {
                //работа с сетью
                receive();

                //работа с консолью сервера
                serverConsole.checkCommandLine();

            }

        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Ошибка сервера", ex);
        } finally {
            stop();
        }
    }

    /**
     * Метод, отвечающий за остановку приёма подключений от клиентов - путём закрытия DatagramChannel
     */
    public void stop() {
        isRunning = false;
        try {
            if (datagramChannel != null && datagramChannel.isOpen()) {
                datagramChannel.close();
            }
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Ошибка закрытия канала", ex);
        }

    }

    /**
     * Метод, отвечающий за обработку полученного запроса от клиента
     * @throws IOException
     */
    public void receive() throws IOException {
        buffer.clear();
        SocketAddress clientAddress = datagramChannel.receive(buffer); //записали в буфер и вернули адрес клиента

        if (clientAddress != null) {
            buffer.flip(); //перевод в режим чтения
            byte[] data = new byte[buffer.remaining()];
            buffer.get(data);

            processRequest(data, clientAddress);
        }
    }

    /**
     * Метод, выполняющий сам запрос и отправляющий результат клиенту
     * @param data полученный запрос, записанный массивом байтов
     * @param clientAddress IP-адрес клиента, которому нужно отправить ответ
     */
    public void processRequest(byte[] data, SocketAddress clientAddress) {
        try {
            Request request = (Request) Serializer.deserialize(data);
            LOGGER.log(Level.INFO, "Получена команда: " + request.getCommandName());

            Response response = requestExecutor.execute(request);

            byte[] responseData = Serializer.serialize(response);
            ByteBuffer sendBuffer = ByteBuffer.wrap(responseData);

            datagramChannel.send(sendBuffer, clientAddress);

            LOGGER.log(Level.INFO, "Ответ отправлен клиенту: " + clientAddress);

        } catch (ClassNotFoundException | IOException ex) {
            LOGGER.log(Level.WARNING, "Ошибка обработки запроса: ", ex);
        }
    }
}
