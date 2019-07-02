package server;

import server.communication.LamportCommunication;
import server.communication.RequestManager;
import server.communication.SocketCommunication;
import server.data.FileManager;
import server.domain.Manager;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Main {

    private static final int NUM_SERVERS = 3;
    private static final int INITIAL_SOCKET_PORT = 7777;
    private static final int INITIAL_RMI_PORT = 8888;

    public static void main(String[] args) {
        System.out.println("Starting servers...");

        LamportCommunication lamportCommunication = new LamportCommunication(
                new RequestManager(
                        0,
                        new Manager(
                                new FileManager())));

        for (int i = 0; i < NUM_SERVERS; i++){
            final int index = i;
            new Thread(() -> {
                try {
                    new SocketCommunication(INITIAL_SOCKET_PORT + index, lamportCommunication).answer();
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
