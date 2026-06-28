package client.connection;

import client.exceptions.ServerUnavailableException;
import common.transport.Request;
import common.transport.Response;

public class RetryClientDecorator implements Client {
    private final Client wrappedUDPClient;
    private final int maxAttempts = 5;

    public RetryClientDecorator(Client wrappedUDPClient) {
        this.wrappedUDPClient = wrappedUDPClient;
    }

    @Override
    public Response sendAndReceive(Request request) {
        long timeout = 2000;
        int maxAttempts = 4;

        for (int attempt = 1; attempt <= maxAttempts; ++attempt) {
            try {

                return wrappedUDPClient.sendAndReceive(request);

            } catch (ServerUnavailableException ex) {
                System.err.println("Попытка подключения не удалась!");
                if (attempt == maxAttempts) {
                    System.err.println("Сервер так и не проснулся!");
                    return null;
                }
                try {
                    Thread.sleep(timeout);
                    timeout *= 2;
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        return null; //если не получилось подключиться
    }
}
// поправить с collection !
// map на клиенте от сервера при подключении !
// dynamic buffer, можно оказывается получать размер пакета до
// понимать, ошибка в коде или в подключении !