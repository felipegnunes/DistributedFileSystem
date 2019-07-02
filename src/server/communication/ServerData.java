package server.communication;

public class ServerData {

    private int serverId;
    private String serverAddress;
    private int serverPort;

    public ServerData(int serverId, String serverAddress, int serverPort){
        this.serverId = serverId;
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public int getServerId() {
        return serverId;
    }

    public int getServerPort() {
        return serverPort;
    }

    public String getServerAddress() {
        return serverAddress;
    }
}
