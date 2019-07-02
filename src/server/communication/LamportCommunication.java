package server.communication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LamportCommunication {

    private RequestManager requestManager;
    private long logicalClock;
    List<Map<String, String>> deliveryBuffer;
    List<Integer> groupProcesses;


    public LamportCommunication(RequestManager requestManager){
        this.requestManager = requestManager;
        logicalClock = 0L;
        this.deliveryBuffer = new ArrayList<>();
        groupProcesses = new ArrayList<>(); // ***
    }

    private void multicast(Map<String, String> message){
    }

    private synchronized List<Map<String, String>> deliver(){
        List<Map<String, String>> replies = new ArrayList<>();
        boolean canDeliver = true;

        while (!deliveryBuffer.isEmpty() && canDeliver){

            int minimumIndex = 0;
            int minimumSender = Integer.valueOf(deliveryBuffer.get(0).get("sender"));
            long mininimumTimestamp = Long.valueOf(deliveryBuffer.get(0).get("timestamp"));

            for (int i = 1; i < deliveryBuffer.size(); i++){
                int sender = Integer.valueOf(deliveryBuffer.get(i).get("sender"));
                long timestamp = Long.valueOf(deliveryBuffer.get(i).get("timestamp"));
                if (sender <= minimumSender && timestamp < mininimumTimestamp){
                    minimumIndex = i;
                    minimumSender = sender;
                    mininimumTimestamp = timestamp;
                }
            }

            int ackCounter = 0;
            for (int i = 0; i < groupProcesses.size(); i++){
                String ack = "ack" + groupProcesses.get(i);
                if (groupProcesses.get(i) != requestManager.getId() && deliveryBuffer.get(minimumIndex).get(ack) == "true")
                    ackCounter++;
            }

            if (ackCounter == groupProcesses.size() - 1)
                replies.addAll(requestManager.receive(deliveryBuffer.remove(minimumIndex)));
            else
                canDeliver = false;
        }

        return replies;
    }

    public List<Map<String, String>> receive(Map<String, String> message){
        System.out.println("A");
        if (message.get("origin").equals("client")) {
            System.out.println("Client incoming!");
            return requestManager.receive(message);
        }

        if (message.get("operation").equals("ack")){
            return receiveAck(message);  // Tarefa 5
        }

        // Tarefa 3
        if (!alreadyReceived(message)){
            logicalClock = Math.max(logicalClock, Long.parseLong(message.get("timestamp")));
            deliveryBuffer.add(message);
            return confirm(message);  // Tarefa 4
        }

        return null;
    }

    private synchronized List<Map<String, String>> receiveAck(Map<String, String> ackMessage){
        logicalClock = Math.max(logicalClock, Long.valueOf(ackMessage.get("timestamp")));

        for (int i = 0; i < deliveryBuffer.size(); i++){
            Map<String, String> message = deliveryBuffer.get(i);
            long messageTimestamp = Long.valueOf(message.get("timestamp"));
            int messageSender = Integer.valueOf(message.get("sender"));
            long confirmedMessageTimestamp = Long.valueOf(ackMessage.get("m.ts"));
            int confirmedMessageSender = Integer.valueOf(ackMessage.get("m.sender"));

            if (messageTimestamp == confirmedMessageTimestamp && messageSender == confirmedMessageSender) {
                if (message.get("ack" + messageSender).equals("false"))
                    message.put("ack" + messageSender, "true");
            }
        }

        return deliver();
    }

    // Tarefa 4
    private synchronized List<Map<String, String>> confirm(Map<String, String> message){
        logicalClock++;
        Map<String, String> ack = new HashMap<>();
        ack.put("operation", "ack");
        ack.put("timestamp", String.valueOf(logicalClock));
        ack.put("sender", String.valueOf(requestManager.getId()));
        ack.put("m.ts", message.get("timestamp"));
        ack.put("m.sender", message.get("sender"));

        for (int processId : groupProcesses){
            ack.put("ack" + processId, "false");
        }
        ack.put("ack" + requestManager.getId(), "true");

        multicast(ack);
        return deliver();
    }

    private synchronized boolean alreadyReceived(Map<String, String> message){
        int messageId = Integer.valueOf(message.get("id"));
        int messageSender = Integer.valueOf(message.get("sender"));

        for (int i = 0; i < deliveryBuffer.size(); i++){
            int bufferedMessageId = Integer.valueOf(deliveryBuffer.get(i).get("id"));
            int bufferedMessageSender = Integer.valueOf(deliveryBuffer.get(i).get("sender"));

            if (bufferedMessageId == messageId && bufferedMessageSender == messageSender)
                return true;
        }
        return false;
    }

    private synchronized void stampMessage(Map<String, String> message){
        message.put("timestamp", String.valueOf(logicalClock++));
        message.put("sender", String.valueOf(requestManager.getId()));
        deliveryBuffer.add(message);
    }

}
