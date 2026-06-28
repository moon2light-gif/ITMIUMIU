package client.connection;

import client.exceptions.ServerUnavailableException;
import common.transport.Request;
import common.transport.Response;
import common.utility.Serializer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;

/**
 * Класс {@code UDPClient} отвечает за отправку сериализованных объектов запроса
 * и приём ответов от сервера с их последующей десериализацией
 *
 * @author mikhailkramar
 */
public class UDPClient implements Client {
    private final String HOST;
    private final int SERVICE_PORT;

    /**
     * Конструктор класса UDPClient
     * @param host имя хоста
     * @param servicePort порт на котором работает сервер
     */
    public UDPClient(String host, int servicePort) {
        this.HOST = host;
        this.SERVICE_PORT = servicePort;
    }

    @Override
    public Response sendAndReceive(Request request) {
        int timeout = 2000;
        int MAX_SIZE = 65536;

        try (DatagramSocket clientSocket = new DatagramSocket()) {
            clientSocket.setSoTimeout(timeout);

                //получаем IP-адрес сервера
                InetAddress IPAddress = InetAddress.getByName(HOST);

                //сериализуем запрос в массив байтов
                byte[] sendingDataBuffer = Serializer.serialize(request);

                //создаём UDP-пакет для передачи его на сервер
                DatagramPacket sendingPacket = new DatagramPacket(sendingDataBuffer, sendingDataBuffer.length, IPAddress, SERVICE_PORT);
                clientSocket.send(sendingPacket); //отправили его серверу

                byte[] receivingDataBuffer = new byte[MAX_SIZE];
                DatagramPacket receivingPacket = new DatagramPacket(receivingDataBuffer, receivingDataBuffer.length);

                clientSocket.receive(receivingPacket);

                //извлекаем полученные байты
                byte[] data = new byte[receivingPacket.getLength()];
                System.arraycopy(
                        receivingPacket.getData(),
                        receivingPacket.getOffset(),
                        data,
                        0,
                        receivingPacket.getLength()
                );

                //десериализуем полученный ответ
                return (Response) Serializer.deserialize(data);

            } catch (SocketTimeoutException ex) {
                throw new ServerUnavailableException("Сервер не ответил в течение таймаута!");
            } catch (ClassNotFoundException | IOException ex) {
                System.err.println("Ошибка сети: " + ex.getMessage());
                return new Response(false, null, "Ошибка отправки: " + ex.getMessage());
            }
    }
}
