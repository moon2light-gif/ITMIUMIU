package client.exceptions;

public class ServerUnavailableException extends RuntimeException {
    public ServerUnavailableException(String message) {
        super(message);
    }
}
