package server.domain;

import server.data.FileManager;

import java.util.*;

//Classe lógica para lidar com as requisicoes
//e criar permissoes de acesso a um arquivo e manutencao de quais estao aberto
public class Manager {

    private static final String READ = "r";
    private static final String WRITE = "w";
    private static final String APPEND = "a";
    private static final String READ_PLUS = "r+";
    private static final String WRITE_CREATE = "w+";
    private static final String APPEND_CREATE = "a+";
    private static final List<String> MODES = Arrays.asList(
            READ, WRITE, APPEND, READ_PLUS, WRITE_CREATE, APPEND_CREATE
    );

    private FileManager fileManager;
    private Map<String, Permission> permissions = Collections.synchronizedMap(new HashMap<>());

    public Manager(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    public synchronized String open(String filename, String mode, String messageSender, String messageTimestamp) {
        String rid = "0";
        if(filename != null && mode != null) {
            ArrayList<Permission> usages = filenameUsage(filename);
            if (!MODES.contains(mode)) {
                // Invalid mode
                return rid;
            }
            if (usages.size() == 0) {
                // Nobody is using this file
                rid = messageSender + "@" + messageTimestamp;
                createPermission(filename, mode, rid);
            } else if (!isBeingWritten(usages.get(0)) && mode.contains(READ)) {
                // If someone is reading, anyone else can read.
                rid = messageSender + "@" + messageTimestamp;
                createPermission(filename, mode, rid);
            }
        }
        return rid;
    }

    public String read(String rid, int count) {
        String result = "";
        if (permissions.containsKey(rid)) {
            Permission permission = permissions.get(rid);
            result = fileManager.read(permission.getFilename(), permission.getPosition(), count);
            permission.setPosition(permission.getPosition() + result.length());
        }
        return result;
    }

    public long eof(String rid){
        if (permissions.containsKey((rid))) {
            Permission permission = permissions.get(rid);
            return fileManager.eof(permission.getFilename(), permission.getPosition()) ? 1 : 0;
        }
        return 0;
    }

    public long remove(String rid) {
        long result = 1;
        if (permissions.containsKey((rid))) {
            Permission permission = permissions.get(rid);
            result = fileManager.remove(permission.getFilename());
            permissions.remove(rid);
        }
        return result;
    }

    public long write(String rid, String text){
        long result=0;
        if(permissions.containsKey(rid)){
            Permission permission = permissions.get(rid);
            return fileManager.write(text, permission.getFilename(), permission.getMode(), permission.getPosition());
        }
        return result;
    }

    public long seek(String rid, long offset, String origin){
        long result=1;
        if(permissions.containsKey(rid)){
            Permission permission = permissions.get(rid);
            result = fileManager.seek(permission.getFilename(), permission.getPosition(), offset, origin);
            if(result != -1) {
                permission.setPosition(result);
                return 0;
            }
        }
        return result;
    }

    public long close(String rid){
        long result=0;
        if(permissions.containsKey(rid)){
            Permission permission = permissions.get(rid);
            //result = fileManager.remove(permission.getFilename());
            permissions.remove(rid);
        }
        return result;
    }

    public long getpos(String rid){
        long result=0;
        if(permissions.containsKey(rid)){
            Permission permission = permissions.get(rid);
            result = fileManager.getpos(permission.getPosition());
            result = 1;
        }
        return result;
    }

    private synchronized void createPermission(String filename, String mode, String rid) {
        Permission permission = new Permission(filename, mode,0);
        permissions.put(rid, permission);
    }

    private boolean isBeingWritten(Permission permission) {
        String mode = permission.getMode();
        return mode.contains(WRITE) || mode.contains(APPEND);
    }

    private ArrayList<Permission> filenameUsage(String filename) {
        ArrayList<Permission> permissions = new ArrayList<>();
        this.permissions.forEach((rid, permission) -> {
            if (permission.getFilename().equals(filename)) {
                permissions.add(permission);
            }
        });
        return permissions;
    }
}
