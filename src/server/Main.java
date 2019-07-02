package server;

import server.communication.LamportCommunication;
import server.communication.RequestManager;
import server.communication.ServerData;
import server.communication.SocketCommunication;
import server.data.FileManager;
import server.domain.Manager;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static final int NUM_SERVERS = 3;
    private static final int INITIAL_SOCKET_PORT = 7777;
    private static final int INITIAL_RMI_PORT = 8888;

    public static void main(String[] args) {
        System.out.println("Starting servers...");

        List<ServerData> serverGroup = new ArrayList<>();

        for (int i = 0; i < NUM_SERVERS; i++){
            serverGroup.add(new ServerData(i, "localhost", INITIAL_SOCKET_PORT));
        }

        for (int i = 0; i < NUM_SERVERS; i++){
            final int index = i;
            new Thread(() -> {
                try {
                    FileManager fileManager = new FileManager();
                    Manager manager = new Manager(fileManager);
                    RequestManager requestManager = new RequestManager(index, manager);
                    LamportCommunication lamportCommunication = new LamportCommunication(requestManager, serverGroup);
                    SocketCommunication socketCommunication = new SocketCommunication(
                            INITIAL_SOCKET_PORT + index,
                            lamportCommunication);
                    socketCommunication.answer();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }

        /*new Thread(() -> {
            try {
                if (System.getSecurityManager() == null) {
                    System.setSecurityManager(new Security(System.getSecurityManager()));
                }
                IRMIObject r = (IRMIObject) UnicastRemoteObject
                        .exportObject(new RMICommunication(requestManager), 0);
                Registry registry = LocateRegistry.createRegistry(6060);
                registry.bind("request", r);
                System.out.println("RMI ready");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();*/

    }
}
