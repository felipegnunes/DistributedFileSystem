package client;

import client.data.Operation;
import client.data.SocketCommunication;
import client.domain.ClientManager;
import client.presentation.Terminal;
import com.google.gson.Gson;

public class Main {

    private static final String clientAddress = "localhost";
    private static final int clientPort = 5555;
    private static final String DNS_ADDRESS = "localhost";
    private static final int DNS_SOCKET_PORT = 8888;
    private static final Gson gson = new Gson();

    public static void main(String[] args) {
        System.out.println("Starting client...");

        // Ask DNS for server address

        String serverAddress = "localhost";
        int serverPort = 7777;

        new Terminal(
                new ClientManager(
                        new Operation(
                                new SocketCommunication(clientAddress, clientPort, DNS_ADDRESS, DNS_SOCKET_PORT)
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
