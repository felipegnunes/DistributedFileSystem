package dns;

import com.google.gson.Gson;

import server.communication.ServerData;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    private static final String DNS_ADDRESS = "localhost";
    private static final int SOCKET_PORT = 8888;
    //private static final int RMI_PORT = 8889;
    private static final List<ServerData> serverList = new ArrayList<>();
    private static final Gson gson = new Gson();

    public static void main(String[] args) {
        serverList.add(new ServerData(0, "localhost", 7777));
        serverList.add(new ServerData(1, "localhost", 7778));
        serverList.add(new ServerData(2, "localhost", 7779));

        listen();
    }

    private static void listen(){
        socketListen();
        //new Thread(() -> RMIListen()).start();
    }

    private static void socketListen(){
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(SOCKET_PORT);
            System.out.printf("Socket DNS ready at port %s.\n", SOCKET_PORT);
            while (true){
                Socket socket = serverSocket.accept();
                new Thread(() -> socketReply(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void socketReply(Socket socket){
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader scanner = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String input = scanner.readLine();
            Map<String, String> message = (Map<String, String>) gson.fromJson(input, Map.class);

            System.out.printf("Message received from (%s, %s): %s\n",
                    message.get("clientAddress"),
                    message.get("clientPort"),
                    input);


            Map<String, String> reply = new HashMap<>();
            reply.put("operation", "dnsReply");
            reply.put("sourceAddress", DNS_ADDRESS);
            reply.put("sourcePort", String.valueOf(SOCKET_PORT));
            ServerData selectedServer = serverList.get((int)(serverList.size() * Math.random()));
            reply.put("serverAddress", selectedServer.getServerAddress());
            reply.put("serverPort", String.valueOf(selectedServer.getServerPort()));

            bufferedWriter.write(gson.toJson(reply) + "\n");
            bufferedWriter.flush();

            bufferedWriter.close();
            scanner.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // private void RMIListen();
    // private void RMIReply();
}
