package server.communication;

import com.google.gson.Gson;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public class RMICommunication implements ServerRMIObject{

    private String address;
    private int port;
    private LamportCommunication lamportCommunication;
    private Gson gson;

    public RMICommunication(LamportCommunication lamportCommunication){
        this.lamportCommunication = lamportCommunication;
    }

    @Override
    public Map<String, String> request(Map<String, String> message) throws RemoteException {
        List<Map<String, String>> replies = lamportCommunication.receive(message);

        for (int i = 0; i < replies.size(); i++){

        }

        return null;
    }


    /*try {
        BufferedReader scanner = new BufferedReader(new InputStreamReader(client.getInputStream()));
        String input = scanner.readLine();
        Map<String, String> message = (Map<String, String>) gson.fromJson(input, Map.class);

        System.out.printf("Message received from (%s, %s) at (%s, %s): %s\n",
                message.get("sourceAddress"),
                message.get("sourcePort"),
                address,
                String.valueOf(port),
                input);

        List<Map<String, String>> replies = lamportCommunication.receive(message);

        for (int i = 0; i < replies.size(); i++){
            replies.get(i).put("source", "server");
            replies.get(i).put("sourceAddress", address);
            replies.get(i).put("sourcePort", String.valueOf(port));
            final int index = i;
            new Thread(() -> send(replies.get(index))).start();
        }
    } catch (Exception e) {
        e.printStackTrace();
    }*/

}
