package client.domain;

public interface IOperation {

    String ropen(String filename, String mode);
    long rread(StringBuffer buffer, int sizeBuf, int count, String rid);
    long reof(String rid);
    long rclose(String rid);
    long rremove(String rid);
    long rgetpos(String rid, int pos);
    long rseek(String rid, int offset, String origin);
    long rwrite(StringBuffer buffer, int sizeBuf, int count, String rid);
}
