package server.communication;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LamportCommunication {

    private RequestManager requestManager;
    private long logicalClock;
    List<Map<String, String>> deliveryBuffer;
    List<Integer> groupProcesses;

    public LamportCommunication(RequestManager requestManager){
        this.requestManager = requestManager;
        logicalClock = 0L;  // LC = 0
        this.deliveryBuffer = new ArrayList<>();  // B = {}
        groupProcesses = new ArrayList<>();  // G = {}
    }

    private void deliver(){
        if (!deliveryBuffer.isEmpty()){
            int minimumIndex = 0;
            long mininimumTimestamp = Long.valueOf(deliveryBuffer.get(0).get("timestamp"));
            int minimumSender = Integer.valueOf(deliveryBuffer.get(0).get("sender"));


        }
    }

    public Map<String, String> process(Map<String, String> message){
        // Receive
        if (!alreadyBuffered(message)){
            receive(message);
        }



        Map<String, String> reply = requestManager.process(message);


        // Send
        stampMessage(reply);

        if (reply.get("multicast").equals("yes")){
        }

        return reply;

    }

    private synchronized void receive(Map<String, String> message){
        logicalClock = Math.max(logicalClock, Long.parseLong(message.get("timestamp")));
        deliveryBuffer.add(message);
    }

    private synchronized void confirm(){
        logicalClock++;
    }

    private synchronized boolean alreadyBuffered(Map<String, String> message){
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

    private synchronized void tick(){
        logicalClock++;
    }


}
