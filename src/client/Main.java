package client;

import client.data.Operation;
import client.data.SocketCommunication;
import client.domain.ClientManager;
import client.presentation.Terminal;

public class Main {

    public static void main(String[] args) {
        System.out.println("Starting client...");

        // Ask DNS for server address

        String clientAddress = "localhost";
        int clientPort = 9999;

        String serverAddress = "localhost";
        int serverPort = 7777;

        new Terminal(
                new ClientManager(
                        new Operation(
                                new SocketCommunication(clientAddress, clientPort),
                                serverAddress,
                                serverPort
                        )
                )
        ).start();

        /*try {
            new Terminal(
                    new ClientManager(
                            new Operation(
                                    new RMICom("127.0.0.1", 6060)
                            )
                    )
            ).start();

        } catch (RemoteException e) {
            e.printStackTrace();
        }*/
    }
}
