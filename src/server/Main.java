package server;

import server.communication.LamportCommunication;
import server.communication.RequestManager;
import server.communication.ServerData;
import server.communication.SocketCommunication;
import server.data.FileManager;
import server.domain.Manager;

import java.util.ArrayList;
import java.util.List;

public class Main {

    private static final String RESOURCES_PATH = "/home/felipe/DistributedFileSystem/resources/";
    private static final int NUM_SERVERS = 3;
    private static final int INITIAL_SOCKET_PORT = 7777;
    private static final int INITIAL_RMI_PORT = 8888;

    public static void main(String[] args) {
        initializeServer(2);
    }

    public static void initializeServer(int serverId) {
        System.out.println("Starting server " + serverId + "...");

        List<ServerData> serverGroup = new ArrayList<>();
        for (int i = 0; i < NUM_SERVERS; i++) {
            serverGroup.add(new ServerData(i, "localhost", INITIAL_SOCKET_PORT + i));
        }

        final int id = serverId;

        FileManager fileManager = new FileManager( RESOURCES_PATH + "server" + id + "/");
        Manager manager = new Manager(fileManager);
        RequestManager requestManager = new RequestManager(id, manager);
        LamportCommunication lamportCommunication = new LamportCommunication(requestManager, serverGroup);

        new Thread(() -> {
            try {
                SocketCommunication socketCommunication = new SocketCommunication(
                        "localhost",
                        INITIAL_SOCKET_PORT + serverId,
                        lamportCommunication);
                socketCommunication.answer();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

    }

}
