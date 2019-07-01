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

    public static void main(String[] args) {
        System.out.println("Starting server...");

        new Thread(() -> {
            try {
                new SocketCommunication(7777,
                        new LamportCommunication(
                            new RequestManager(
                               0,
                                new Manager(
                                    new FileManager())
                            )
                        )
                ).answer();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

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
