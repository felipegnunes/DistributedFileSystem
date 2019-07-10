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
        message.put("clientAddress", clientAddress);
        message.put("clientPort", String.valueOf(clientPort));
        message.put("sourceAddress", clientAddress);
        message.put("sourcePort", String.valueOf(clientPort));

        Map<String, String> serverInfo = requestServerAddress();
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
            System.out.println("Message received: " + serverReply);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return serverReply;
    }

    private void send(Map<String, String> message){
        String destinationAddress = message.get("destinationAddress");
        int destinationPort = Integer.valueOf(message.get("destinationPort"));

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

    private Map<String, String> requestServerAddress(){
        Map<String, String> request = new HashMap<>();
        request.put("operation", "dnsRequest");
        request.put("clientAddress", clientAddress);
        request.put("clientPort", String.valueOf(clientPort));

        try {
            Socket socket = new Socket(DNSAddress, DNSPort);
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader scanner = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bufferedWriter.write(g.toJson(request) + "\n");

            System.out.println("Requesting a server address to the DNS.");

            bufferedWriter.flush();

            String input = scanner.readLine();
            System.out.println("Reply received from the DNS.");
            Map<String, String> reply = (Map<String, String>) g.fromJson(input, Map.class);

            bufferedWriter.close();
            scanner.close();
            socket.close();
            return reply;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
