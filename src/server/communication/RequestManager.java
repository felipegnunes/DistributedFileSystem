package server.communication;

import com.google.gson.Gson;
import server.domain.Manager;

import java.util.HashMap;
import java.util.Map;

public class RequestManager {

    private static final String OPEN = "ropen";
    private static final String READ = "rread";
    private static final String EOF = "reof";
    private static final String SEEK = "rseek";
    private static final String WRITE = "rwrite";
    private static final String GETPOS = "rgetpos";
    private static final String CLOSE = "rclose";
    private static final String REMOVE = "rremove";


    private Manager manager;
    private int serverId;

    public RequestManager(int serverId, Manager manager) {
        this.serverId = serverId;
        this.manager = manager;
    }

    public int getServerId() {
        return serverId;
    }

    public Map<String, String> receive(Map<String, String> message) {
        System.out.printf("Message received by application at server %d: %s\n", serverId, new Gson().toJson(message));

        HashMap<String, String> reply = new HashMap<>();
        reply.put("destinationAddress", message.get("clientAddress"));
        reply.put("destinationPort", message.get("clientPort"));

        Long num;
        switch (message.get("operation")) {
            case OPEN:
                String rid = manager.open(message.get("filename"),
                        message.get("mode"),
                        message.get("sender"),
                        message.get("timestamp"));
                reply.put("rid", rid);
                break;
            case READ:
                String text = manager.read(message.get("rid"), Integer.valueOf(message.get("count")));
                reply.put("text", String.valueOf(text));
                break;
            case EOF:
                num = manager.close(message.get("rid"));
                reply.put("final", String.valueOf(num));
                break;
            case WRITE:
                num = manager.write(message.get("rid"), message.get("buffer"));
                reply.put("total", String.valueOf(num));
                break;
            case GETPOS:
                num = manager.getpos(message.get("rid"));
                reply.put("getpos", String.valueOf(num));
                break;
            case SEEK:
                num = manager.seek(message.get("rid"), Long.valueOf(message.get("offset")), message.get("origin"));
                reply.put("spos", String.valueOf(num));
                break;
            case CLOSE:
                num = manager.close(message.get("rid"));
                reply.put("close", String.valueOf(num));
                break;
            case REMOVE:
                num = manager.remove(message.get("rid"));
                reply.put("del", String.valueOf(num));
                break;
            default:
                break;
        }

        return reply;
    }
}
