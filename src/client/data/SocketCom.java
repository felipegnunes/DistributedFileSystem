package client.data;

import client.domain.ICommunication;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class SocketCom implements ICommunication {

    private Socket socket;
    private OutputStreamWriter output;
    private BufferedReader input;
    private Gson g = new GsonBuilder().create();
    private String url;
    private int port;

    public SocketCom(String url, int port) {
        this.url = url;
        this.port = port;
    }

    public Map<String, String> request(Map<String, String> params) {
        Map<String, String> empty = new HashMap<>();
        try {
            socket = new Socket(url, port);
            output = new OutputStreamWriter(socket.getOutputStream());
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output.write(g.toJson(params) + "\n");
            output.flush();
            Thread.sleep(500);
            if(input.ready()) {
                String response = input.readLine();
                return g.fromJson(response, Map.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return empty;
    }


}
