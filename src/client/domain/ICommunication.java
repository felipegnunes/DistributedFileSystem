package client.domain;

import java.util.Map;

public interface ICommunication {
    Map<String, String> request(Map<String, String> params);
}
