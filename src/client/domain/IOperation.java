package client.domain;

public interface IOperation {

    long ropen(String filename, String mode);
    long rread(StringBuffer buffer, int sizeBuf, int count, long rid);
    long reof(long rid);
    long rclose(long rid);
    long rremove(Long rid);
    long rgetpos(long rid, int pos);
    long rseek(long rid, int offset, String origin);
    long rwrite(StringBuffer buffer, int sizeBuf, int count, long rid);
}
