package client.domain;

import java.util.Map;

public interface Communication {
    Map<String, String> request(Map<String, String> params, String serverAddress, int serverPort);
}
