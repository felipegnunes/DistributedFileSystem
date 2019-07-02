package client.data;

import client.domain.Communication;
import client.domain.IOperation;

import java.util.HashMap;
import java.util.Map;

public class Operation implements IOperation {

    private Communication com;

    public Operation(Communication com) {
        this.com = com;
    }

    @Override
    public long ropen(String filename, String mode) {
        Map<String, String> map = new HashMap<>();
        map.put("operation", "ropen");
        map.put("filename", filename);
        map.put("mode", mode);
        Map<String, String> response = this.com.request(map);
        if (response.containsKey("rid")) {
            return Long.valueOf(response.get("rid"));
        }
        return 1;
    }

    @Override
    public long rread(StringBuffer buffer, int sizeBuf, int count, long rid) {
        Map<String, String> map = new HashMap<>();
        map.put("operation", "rread");
        map.put("count", String.valueOf(count));
        map.put("rid", String.valueOf(rid));
        Map<String, String> result = this.com.request(map);
        if (result.containsKey("text")) {
            String text = result.get("text");
            for(int i = 0; i < sizeBuf && i < text.length(); i++) {
                buffer.append(text.charAt(i));
            }
            return text.length();
        }
        return 1;
    }

    @Override
    public long reof(long rid) {
        Map<String, String> map = new HashMap<>();
        map.put("operation", "reof");
        map.put("rid", String.valueOf(rid));
        Map<String, String> result = this.com.request(map);
        if (result.containsKey("final")){
            return Long.valueOf(result.get("final"));
        }
        return 1;
    }

    @Override
    public long rclose(long rid) {
        Map<String, String> map = new HashMap<>();
        map.put("operation", "rclose");
        map.put("rid", String.valueOf(rid));
        Map<String, String> result = this.com.request(map);
        if (result.containsKey("close")){
            return Long.valueOf(result.get("close"));
        }
        return 1;
    }

    public long rremove(Long rid) {
        Map<String, String> map = new HashMap<>();
        map.put("operation", "rremove");
        map.put("filename", String.valueOf(rid));
        Map<String, String> result = this.com.request(map);
        if (result.containsKey("del")){
            return Long.valueOf(result.get("del"));
        }
        return 1;
    }

    @Override
    public long rgetpos(long rid, int pos){
        Map<String, String> map = new HashMap<>();
        map.put("operation", "rgetpos");
        map.put("rid", String.valueOf(rid));
        map.put("pos", String.valueOf(pos));
        Map<String, String> result = this.com.request(map);
        if (result.containsKey("getpos")) {
            return Long.valueOf(result.get("getpos"));
        }
        return 1;
    }

    @Override
    public long rseek(long rid, int offset, String origin) {
        Map<String, String> map = new HashMap<>();
        map.put("operation", "rseek");
        map.put("rid", String.valueOf(rid));
        map.put("offset", String.valueOf(offset));
        map.put("origin", origin);
        Map<String, String> result = this.com.request(map);
        if (result.containsKey("spos")) {
            return Long.valueOf(result.get("spos"));
        }
        return 1;
    }

    @Override
    public long rwrite(StringBuffer buffer, int sizeBuf, int count, long rid) {
        Map<String, String> map = new HashMap<>();
        map.put("operation", "rwrite");
        map.put("rid", String.valueOf(rid));
        map.put("buffer", buffer.toString());
        map.put("size", String.valueOf(sizeBuf));
        map.put("count", String.valueOf(count));
        Map<String, String> result = this.com.request(map);
        if (result.containsKey("total")) {
            return Long.valueOf(result.get("total"));
        }
        return 1;
    }
}
