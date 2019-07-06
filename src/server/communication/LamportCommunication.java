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
    private List<Map<String, String>> deliveryBuffer;
    private List<Map<String, String>> storedAcks;
    private List<ServerData> group;


    public LamportCommunication(RequestManager requestManager, List<ServerData> group){
        this.requestManager = requestManager;
        logicalClock = 0L;
        this.deliveryBuffer = new ArrayList<>();
        storedAcks = new ArrayList<>();
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
        } else if (message.get("operation").equals("ack")){
            System.out.println("Received ack message.");
            receiveAck(message);  // Tarefa 5
            return deliver();
        } else if (!alreadyReceived(message)){
            long messageTimestamp = Long.valueOf(message.get("timestamp"));
            logicalClock = Math.max(logicalClock, messageTimestamp);
            message.put("ack" + message.get("sender"), "true");
            deliveryBuffer.add(message);
            return confirm(message);  // Tarefa 4
        }

        return replies;
    }

    private boolean isFullyAcknowledged(Map<String, String> message){
        int sender = Integer.valueOf(message.get("sender"));

        for (ServerData serverData : group){
            String serverAck = "ack" + serverData.getServerId();

            if (serverData.getServerId() != requestManager.getServerId() && message.getOrDefault(serverAck,"false").equals("false")){
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

        storedAcks.removeIf(ackMessage -> receiveAck(ackMessage));

        ListIterator<Map<String, String>> iter = storedAcks.listIterator();
        while(iter.hasNext()){
            if(receiveAck(iter.next())){
                iter.remove();
            }
        }

        if (!deliveryBuffer.isEmpty()){
            sortDeliveryBuffer();

            System.out.println();
            System.out.println("Server" + requestManager.getServerId() + "'s delivery buffer");
            System.out.println(new Gson().toJson(deliveryBuffer.get(0)));
            System.out.println();

            while (!deliveryBuffer.isEmpty() && isFullyAcknowledged(deliveryBuffer.get(0))){
                replies.add(requestManager.receive(deliveryBuffer.remove(0)));
            }
        }

        return replies;
    }

    private boolean receiveAck(Map<String, String> ackMessage){
        long ackTimestamp = Long.valueOf(ackMessage.get("timestamp"));
        logicalClock = Math.max(logicalClock, ackTimestamp);
        boolean found = false;

        for (int i = 0; i < deliveryBuffer.size(); i++){
            Map<String, String> bufferedMessage = deliveryBuffer.get(i);

            long bufferedMessageTimestamp = Long.valueOf(bufferedMessage.get("timestamp"));
            int bufferedMessageSender = Integer.valueOf(bufferedMessage.get("sender"));

            long confirmedMessageTimestamp = Long.valueOf(ackMessage.get("messageTimestamp"));
            int confirmedMessageSender = Integer.valueOf(ackMessage.get("messageSender"));

            if (bufferedMessageTimestamp == confirmedMessageTimestamp && bufferedMessageSender == confirmedMessageSender) {
                found = true;
                String ack = "ack" + ackMessage.get("sender");
                if (bufferedMessage.getOrDefault(ack, "false").equals("false"))
                    bufferedMessage.put(ack, "true");
            }
        }

        if (!found) {
            System.out.println("Ack received before the message to be confirmed.");
            storedAcks.add(ackMessage);
            return false;
        }

        return true;
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

        List<Map<String, String>> replies = new ArrayList<>();
        replies.addAll(multicast(ack));
        replies.addAll(deliver());

        return replies;
    }

    private boolean alreadyReceived(Map<String, String> receivedMessage){
        String receivedMessageTimestamp = receivedMessage.get("timestamp");
        String receivedMessageSender = receivedMessage.get("sender");

        for (Map<String, String> bufferedMessage : deliveryBuffer){
            System.out.println(new Gson().toJson(bufferedMessage));

            String bufferedMessageTimestamp = bufferedMessage.get("timestamp");
            String bufferedMessageSender = bufferedMessage.get("sender");

            if (receivedMessageTimestamp.equals(bufferedMessageTimestamp) && receivedMessageSender.equals(bufferedMessageSender))
                return true;
        }

        return false;
    }

    private List<Map<String, String>> multicast(Map<String, String> message){
        List<Map<String, String>> copies = new ArrayList<>();

        for (ServerData serverData : group){
            if (serverData.getServerId() != requestManager.getServerId()) {
                Map<String, String> copy = deepCopy(message);
                copy.put("source", "server");
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
