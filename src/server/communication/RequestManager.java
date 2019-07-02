package server.communication;

import server.domain.Manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private int id;

    public RequestManager(int id, Manager manager) {
        this.id = id;
        this.manager = manager;
    }

    public int getId() {
        return id;
    }

    public List<Map<String, String>> receive(Map<String, String> m) {
        List<Map<String, String>> replies = new ArrayList<>();

        HashMap<String, String> response = new HashMap<>();
        response.put("destination", "client");
        response.put("clientAddress", m.get("clientAddress"));
        response.put("clientPort", m.get("clientPort"));

        HashMap<String, String> updateRequest = ;

        switch (m.get("operation")) {
            case OPEN:
                Long num = manager.open(m.get("filename"), m.get("mode"));
                response.put("rid", String.valueOf(num));
                break;
            case READ:
                String text = manager.read(
                        Long.valueOf(m.get("rid")),
                        Integer.valueOf(m.get("count"))
                );
                response.put("text", String.valueOf(text));
                break;
            case EOF:
                num = manager.close(Long.valueOf(m.get("rid")));
                response.put("final", String.valueOf(num));
                break;
            case WRITE:
                System.out.println("entrei aqui " + m.get("buffer"));
                num = manager.write(Long.valueOf(m.get("rid")), m.get("buffer"));
                response.put("total", String.valueOf(num));
                break;
            case GETPOS:
                num = manager.getpos(Long.valueOf(m.get("rid")));
                response.put("getpos", String.valueOf(num));
                break;
            case SEEK:
                num = manager.seek(Long.valueOf(m.get("rid")), Long.valueOf(m.get("offset")), m.get("origin"));
                response.put("spos", String.valueOf(num));
                break;
            case CLOSE:
                num = manager.close(Long.valueOf(m.get("rid")));
                response.put("close", String.valueOf(num));
                break;
            case REMOVE:
                num = manager.remove(Long.valueOf(m.get("rid")));
                response.put("del", String.valueOf(num));
                break;
            default:
                break;
        }

        replies.add(response);
        return replies;
    }
}
