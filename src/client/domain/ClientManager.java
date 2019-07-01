package client.domain;

public class ClientManager {

    private IOperation communication;

    public ClientManager(IOperation communication) {
        this.communication = communication;
    }

    public String rread(long rid) {
        int bufSize = 10000;
        StringBuffer buffer = new StringBuffer(bufSize);
        if(communication.rread(buffer, bufSize, 10000, rid) == 0) {
            return "Nada para ler";
        }
        return "Conteúdo: " + buffer.toString();
    }

    public long ropen(String filename, String mode) {
        return communication.ropen(filename, mode);
    }

    public long reof(long rid) {
        return communication.reof(rid);
    }

    public long rclose(long rid) {
        return communication.rclose(rid);
    }

    public long rremove(Long rid) {
        return communication.rremove(rid);
    }

    public long rgetpos(long rid, int pos) {
        return communication.rgetpos(rid, pos);
    }

    public long rseek(long rid, int offset, String origin) {
        if(origin.equals("SEEK_SET") || origin.equals("SEEK_CUR") || origin.equals("SEEK_END")){
            return communication.rseek(rid, offset, origin);
        }
        return 1;
    }
    public  long rwrite(StringBuffer buffer, int size, int count, long rid){
        return communication.rwrite(buffer, size, count, rid);
    }
}
