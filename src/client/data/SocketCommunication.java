package client.data;

import client.domain.Communication;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class SocketCommunication implements Communication {

    private Gson g = new GsonBuilder().create();
    private String url;
    private int port;

    private String clientAddress;
    private int clientPort;
    private String DNSAddress;
    private int DNSPort;
    private Map<String, Boolean> messageHistory;

    public SocketCommunication(String clientAddress, int clientPort, String DNSAddress, int DNSPort){
        this.clientAddress = clientAddress;
        this.clientPort = clientPort;
        this.DNSAddress = DNSAddress;
        this.DNSPort = DNSPort;
        this.messageHistory = new HashMap<>();
    }

    public Map<String, String> request(Map<String, String> message) {
        message.put("source", "client");
        message.put("sourceAddress", clientAddress);
        message.put("sourcePort", String.valueOf(clientPort));

        Map<String, String> serverInfo = requestServerInfo();
        message.put("destinationAddress", serverInfo.get("serverAddress"));
        message.put("destinationPort", serverInfo.get("serverPort"));

        send(message);
        return receive();

    }

    private Map<String, String> receive(){
        Map<String, String> serverReply = null;
        try {
            ServerSocket serverSocket = new ServerSocket(clientPort);
            Socket socket = serverSocket.accept();
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            serverReply = g.fromJson(input.readLine(), Map.class);
            input.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return serverReply;
    }

    private void send(Map<String, String> message){
        String destinationAddress = message.get("destinationAddress");
        int destinationPort = Integer.valueOf(message.get("destinationPort"));

        message.put("id", System.currentTimeMillis() + "@" + clientAddress + "@" + clientPort);

        Socket socket = null;
        try {
            socket = new Socket(destinationAddress, destinationPort);
            OutputStreamWriter output = new OutputStreamWriter(socket.getOutputStream());
            output.write(g.toJson(message) + "\n");
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Map<String, String> requestServerInfo(){
        Map<String, String> request = new HashMap<>();
        request.put("operation", "dnsRequest");
        request.put("sourceAddress", clientAddress);
        request.put("sourcePort", String.valueOf(clientPort));

        Socket socket = null;
        try {
            socket = new Socket(DNSAddress, DNSPort);
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedWriter.write(g.toJson(request) + "\n");
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Asking the DNS for a server address.");

        String input = null;
        try {
            ServerSocket serverSocket = new ServerSocket(clientPort);
            socket = serverSocket.accept();
            BufferedReader scanner = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            input = scanner.readLine();

            scanner.close();
            serverSocket.close();
            System.out.println("Reply from DNS received.");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, String> reply = (Map<String, String>) g.fromJson(input, Map.class);
        return reply;
    }


}
