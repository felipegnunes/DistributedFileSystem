package server.communication;

import com.google.gson.Gson;

import java.util.*;

public class LamportCommunication {

    // Locks
    //ReentrantLock requestManagerLock;
    //ReentrantLock logicalClockLock;
    //ReentrantLock deliveryBufferLock;
    //ReentrantLock groupLock;

    private RequestManager requestManager;
    private long logicalClock;
    List<Map<String, String>> deliveryBuffer;
    List<ServerData> group;


    public LamportCommunication(RequestManager requestManager, List<ServerData> group){
        this.requestManager = requestManager;
        logicalClock = 0L;
        this.deliveryBuffer = new ArrayList<>();
        this.group = group;
    }

    public synchronized List<Map<String, String>> receive(Map<String, String> message){
        List<Map<String, String>> replies = new ArrayList<>();

        if (message.get("source").equals("client")){
            System.out.println("Received client message.");
            logicalClock++;
            message.put("timestamp", String.valueOf(logicalClock));
            message.put("sender", String.valueOf(requestManager.getServerId()));
            deliveryBuffer.add(message);
            return multicast(message);
        }

        if (message.get("operation").equals("ack")){
            System.out.println("Received ack message.");
            replies.addAll(receiveAck(message));  // Tarefa 5
        }

        if (!alreadyReceived(message)){

            long messageTimestamp = Long.valueOf(message.get("timestamp"));
            logicalClock = Math.max(logicalClock, messageTimestamp);
            message.put("ack" + requestManager.getServerId(), "true");
            deliveryBuffer.add(message);
            replies.addAll(confirm(message));  // Tarefa 4
        }

        return send(replies);
    }

    private List<Map<String, String>> send(List<Map<String, String>> replies){
        List<Map<String, String>> newReplies = new ArrayList<>();

        for (int i = 0; i < replies.size(); i++){
            logicalClock++;
            Map<String, String> message = replies.get(i);
            message.put("timestamp", String.valueOf(logicalClock));
            message.put("sender", String.valueOf(requestManager.getServerId()));
            deliveryBuffer.add(message);
            newReplies.addAll(multicast(message));
        }

        return newReplies;
    }

    private boolean isFullyAcknowledged(Map<String, String> message){
        int sender = Integer.valueOf(message.get("sender"));

        for (ServerData serverData : group){
            String serverAck = "ack" + serverData.getServerId();
            if (serverData.getServerId() != sender && message.getOrDefault(serverAck,"false").equals("false")){
                return false;
            }
        }
        return true;
    }

    private void sortDeliveryBuffer(){
        Collections.sort(deliveryBuffer, (messageA, messageB) -> {
            long timestampA = Long.valueOf(messageA.get("timestamp"));
            long timestampB = Long.valueOf(messageB.get("timestamp"));
            int senderA = Integer.valueOf(messageA.get("sender"));
            int senderB = Integer.valueOf(messageB.get("sender"));

            if (timestampA < timestampB || timestampA == timestampB && senderA < senderB)
                return -1;
            if (timestampA == timestampB && senderA == senderB)
                return 0;
            return 1;
        });
    }

    private List<Map<String, String>> deliver(){
        List<Map<String, String>> replies = new ArrayList<>();
        boolean canDeliver = true;

        if (!deliveryBuffer.isEmpty()){
            sortDeliveryBuffer();

            while (!deliveryBuffer.isEmpty() && isFullyAcknowledged(deliveryBuffer.get(0))){
                replies.addAll(requestManager.receive(deliveryBuffer.remove(0)));
            }
        }

        return replies;
    }

    private List<Map<String, String>> receiveAck(Map<String, String> ackMessage){
        long ackTimestamp = Long.valueOf(ackMessage.get("timestamp"));
        logicalClock = Math.max(logicalClock, ackTimestamp);

        for (int i = 0; i < deliveryBuffer.size(); i++){
            Map<String, String> message = deliveryBuffer.get(i);

            long messageTimestamp = Long.valueOf(message.get("timestamp"));
            int messageSender = Integer.valueOf(message.get("sender"));

            long confirmedMessageTimestamp = Long.valueOf(ackMessage.get("messageTimestamp"));
            int confirmedMessageSender = Integer.valueOf(ackMessage.get("messageSender"));

            if (messageTimestamp == confirmedMessageTimestamp && messageSender == confirmedMessageSender) {
                String ack = "ack" + confirmedMessageSender;
                if (message.getOrDefault(ack, "false").equals("false"))
                    message.put(ack, "true");
            }
        }

        return deliver();
    }

    // Tarefa 4
    private List<Map<String, String>> confirm(Map<String, String> message){
        logicalClock++;
        Map<String, String> ack = new HashMap<>();
        ack.put("operation", "ack");
        ack.put("timestamp", String.valueOf(logicalClock));
        ack.put("sender", String.valueOf(requestManager.getServerId()));
        ack.put("messageTimestamp", message.get("timestamp"));
        ack.put("messageSender", message.get("sender"));

        //ack.put("ack" + requestManager.getServerId(), "true");

        List<Map<String, String>> replies = new ArrayList<>();
        replies.addAll(multicast(ack));
        replies.addAll(deliver());

        return replies;
    }

    private boolean alreadyReceived(Map<String, String> message){

        for (Map<String, String> bufferedMessage : deliveryBuffer){
            System.out.println(new Gson().toJson(bufferedMessage));
            String bufferedMessageId = bufferedMessage.get("id");
            String messageId = message.get("id");
            if (bufferedMessageId == null)
                System.out.printf("Erro no buffer de %d: %s\n", requestManager.getServerId(), new Gson().toJson(bufferedMessage));
            if (messageId == null)
                System.out.println("Erro na mensagem recebida");
            if (bufferedMessageId.equals(messageId))
                return true;
        }

        return false;
    }

    private List<Map<String, String>> multicast(Map<String, String> message){
        List<Map<String, String>> copies = new ArrayList<>();

        for (ServerData serverData : group){
            if (serverData.getServerId() != requestManager.getServerId()) {
                Map<String, String> copy = deepCopy(message);
                copy.put("destinationAddress", serverData.getServerAddress());
                copy.put("destinationPort", String.valueOf(serverData.getServerPort()));
                copies.add(copy);
            }
        }

        return copies;
    }

    private Map<String, String> deepCopy(Map<String, String> message){
        Gson gson = new Gson();
        String jsonString = gson.toJson(message);
        Map<String, String> copy = gson.fromJson(jsonString, Map.class);
        return copy;
    }

}
