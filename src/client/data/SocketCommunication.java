package client.data;

import client.domain.Communication;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

public class SocketCommunication implements Communication {

    private Gson g = new GsonBuilder().create();
    private String url;
    private int port;

    private String clientAddress;
    private int clientPort;

    public SocketCommunication(String clientAddress, int clientPort){
        this.clientAddress = clientAddress;
        this.clientPort = clientPort;
    }

    public Map<String, String> request(Map<String, String> message, String serverAddress, int serverPort) {
        message.put("origin", "client");
        message.put("clientAddress", clientAddress);
        message.put("clientPort", String.valueOf(clientPort));

        try {
            Socket socket = new Socket(serverAddress, serverPort);
            OutputStreamWriter output = new OutputStreamWriter(socket.getOutputStream());
            output.write(g.toJson(message) + "\n");
            output.flush();

            ServerSocket serverSocket = new ServerSocket(clientPort);
            socket = serverSocket.accept();
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Map<String, String> serverReply = g.fromJson(input.readLine(), Map.class);
            input.close();
            serverSocket.close();

            return serverReply;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
