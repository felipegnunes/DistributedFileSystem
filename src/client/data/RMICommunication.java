package client.data;

import client.domain.Communication;
import server.communication.ServerRMIObject;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;

public class RMICommunication implements Communication {

    private Registry registry;

    public RMICommunication(String clientAddress, int clientPort) throws RemoteException {
        registry = LocateRegistry.getRegistry(clientAddress, clientPort);

        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new Security(System.getSecurityManager()));
        }
    }

    @Override
    public Map<String, String> request(Map<String, String> message) {

        try {
            return ((ServerRMIObject) registry.lookup("request")).request(message);
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }

        return null;
    }

}
