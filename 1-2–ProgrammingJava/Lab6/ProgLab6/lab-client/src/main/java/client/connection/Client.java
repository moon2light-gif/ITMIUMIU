package client.connection;

import common.transport.Request;
import common.transport.Response;

public interface Client {
    Response sendAndReceive(Request request);
}
