package server.communication;

import com.google.gson.Gson;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;

//classe para lidar com a comunicacao via Socket com os clientes
public class SocketCommunication {

    private LamportCommunication lamportCommunication;
    private String address;
    private int port;
    private Gson gson = new Gson();

    public SocketCommunication(String address, int port, LamportCommunication lamportCommunication) {
        this.address = address;
        this.port = port;
        this.lamportCommunication = lamportCommunication;
    }

    //funcao para criar Thread para atender multiplos clientes
    public void answer() {
        try {
            ServerSocket server = new ServerSocket(port);
            System.out.println("Server socket ready at port " + port + ".");
            while (true) {
                Socket s = server.accept();
                new Thread(() -> process(s)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //funcao para lidar com uma entrada
    //descontroi a mensagem passagem em JSON
    public void process(Socket client) {
        try {
            BufferedReader scanner = new BufferedReader(new InputStreamReader(client.getInputStream()));
            String input = scanner.readLine();
            Map<String, String> message = (Map<String, String>) gson.fromJson(input, Map.class);

            System.out.printf("Message received from (%s, %s) at (%s, %s): %s\n",
                    message.get("sourceAddress"),
                    message.get("sourcePort"),
                    address,
                    String.valueOf(port),
                    input);

            List<Map<String, String>> replies = lamportCommunication.receive(message);

            for (int i = 0; i < replies.size(); i++){
                replies.get(i).put("source", "server");
                replies.get(i).put("id", System.currentTimeMillis() + "@" + address + "@" + port);
                replies.get(i).put("sourceAddress", address);
                replies.get(i).put("sourcePort", String.valueOf(port));
                final int index = i;
                new Thread(() -> send(replies.get(index))).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void send(Map<String, String> message){
        Socket socket = null;
        try {
            String id = message.get("id");
            socket = new Socket(message.get("destinationAddress"), Integer.valueOf(message.get("destinationPort")));
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedWriter.write(gson.toJson(message) + "\n");
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
