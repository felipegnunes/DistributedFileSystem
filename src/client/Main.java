package client;

import client.data.Operation;
import client.data.SocketCom;
import client.domain.ClientManager;
import client.presentation.Terminal;

public class Main {

    public static void main(String[] args) {
        System.out.println("Starting client...");

        new Terminal(
                new ClientManager(
                        new Operation(
                                new SocketCom("127.0.0.1", 7777)
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
