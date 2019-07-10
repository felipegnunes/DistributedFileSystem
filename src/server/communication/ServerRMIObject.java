package server.communication;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface ServerRMIObject extends Remote {
    Map<String, String> request(Map<String, String> message) throws RemoteException;
}
