package client.domain;

public class ClientManager {

    private IOperation communication;

    public ClientManager(IOperation communication) {
        this.communication = communication;
    }

    public String rread(String rid) {
        int bufSize = 10000;
        StringBuffer buffer = new StringBuffer(bufSize);
        if(communication.rread(buffer, bufSize, 10000, rid) == 0) {
            return "Arquivo vazio.";
        }
        return "Conteúdo: " + buffer.toString();
    }

    public String ropen(String filename, String mode) {
        return communication.ropen(filename, mode);
    }

    public long reof(String rid) {
        return communication.reof(rid);
    }

    public long rclose(String rid) {
        return communication.rclose(rid);
    }

    public long rremove(String rid) {
        return communication.rremove(rid);
    }

    public long rgetpos(String rid, int pos) {
        return communication.rgetpos(rid, pos);
    }

    public long rseek(String rid, int offset, String origin) {
        if(origin.equals("SEEK_SET") || origin.equals("SEEK_CUR") || origin.equals("SEEK_END")){
            return communication.rseek(rid, offset, origin);
        }
        return 1;
    }
    public  long rwrite(StringBuffer buffer, int size, int count, String rid){
        return communication.rwrite(buffer, size, count, rid);
    }
}
