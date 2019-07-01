package server.domain;

// Quando um arquivo é aberto por um cliente, um objeto dessa classe guarda todas as informações pertinentes ao acesso.
public class Permission {

    private String filename;
    private String mode;
    private long position;

    public Permission(String filename, String mode, long position) {
        this.filename = filename;
        this.mode = mode;
        this.position = position;
    }

    public String getFilename() {
        return filename;
    }

    public String getMode() {
        return mode;
    }

    public long getPosition(){return position;}

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public void setPosition(long position) {
        this.position = position;
    }
}

