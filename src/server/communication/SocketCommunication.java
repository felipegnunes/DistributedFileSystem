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
    private int port;
    private Gson gson = new Gson();

    public SocketCommunication(int port, LamportCommunication lamportCommunication) {
        this.lamportCommunication = lamportCommunication;
        this.port = port;
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
        System.out.println("A new connection!");
        try {
            BufferedReader scanner = new BufferedReader(new InputStreamReader(client.getInputStream()));
            String input = scanner.readLine();
            System.out.println(input);

            Map<String, String> m = (Map<String, String>) gson.fromJson(input, Map.class);
            List<Map<String, String>> replies = lamportCommunication.receive(m);

            for (Map<String, String> reply : replies){
                if (reply.get("destination").equals("server"))
                    new Thread(() -> replyServer(reply)).start();
                else
                    new Thread(() -> replyClient(reply)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reply(Map<String, String> message, String receiverAddress, int receiverPort){
        Socket socket = null;
        try {
            socket = new Socket(receiverAddress, receiverPort);
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedWriter.write(gson.toJson(message) + "\n");
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void replyServer(Map<String, String> message){
        reply(message, message.get("serverAddress"), Integer.valueOf(message.get("serverPort")));
    }

    public void replyClient(Map<String, String> message){
        reply(message, message.get("clientAddress"), Integer.valueOf(message.get("clientPort")));
    }

    //funcao para envio da resposta
    public void replyClient(Map<String, String> map, BufferedWriter writer){
        try {
            System.out.println("response: " + map);
            writer.write(gson.toJson(map) + "\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
