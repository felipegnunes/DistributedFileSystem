package server.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.Scanner;

public class FileManager {

    private String resourcesPath;

    public FileManager(String resourcesPath){
        this.resourcesPath = resourcesPath;

        // Cria diretório para armazenamento de arquivos no servidor caso ele não exista.
        File directory = new File(resourcesPath);
        if (!directory.exists())
            directory.mkdir();
    }

    private String fileToString(String filename) {
        String fileContent = "";
        try {
            File f = new File(filename);
            Scanner reader = new Scanner(f);
            while (reader.hasNextLine()) {
                fileContent += reader.nextLine() + "\n";
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return fileContent;
    }

    private long fileSize(String filename) {
        long acum = -1;
        try {
            File f = new File(filename);
            acum = f.length();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return acum;
    }

    private boolean fileExists(String filename) {
        try {
            File f = new File(filename);
            return f.exists();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String read(String filename, long start, long count) {
        String fPath = resourcesPath + filename;
        String acum="";

        if(fileExists(fPath)) {
            String data = fileToString(resourcesPath + filename);
            acum = data.substring((int) start, Math.min((int) (start + count), data.length()));
        } else {
            System.out.println("Arquivo nao existe*-");
        }
        return acum;
    }

    public boolean eof(String filename, long position) {
        String data = fileToString(resourcesPath + filename);
        return data.length() == (int)position;
    }

    public int write(String buffer, String filename, String mode, long position){
        int result=0;
        String file = resourcesPath + filename;
        boolean append = mode.contains("a");
        try {
            FileWriter fWriter = new FileWriter(file, append);
            fWriter.write(buffer);
            fWriter.close();
            result = buffer.length();
        } catch (Exception e){
            e.printStackTrace();
            result=0;
        }
        return result;
    }

    public long seek(String filename, long position, long offset, String origin) {
        long fileSize = fileSize(resourcesPath + filename);
        switch(origin) {
            case "SEEK_SET":
                if(offset <= fileSize) {
                    return offset;
                }
                return -1;
            case "SEEK_CUR":
                if(position + offset <= fileSize) {
                    return position + offset;
                }
                return -1;
            case "SEEK_END":
                if(fileSize + offset <= fileSize) {
                    return position + offset;
                }
                return -1;
        }
        return position;
    }

    public long getpos(long position){
        return position;
    }

    public int remove(String filename){
        String f = resourcesPath + filename;
        File deleteFile = new File(f);
        if(deleteFile.delete()){
            return 0;
        }
        return 1;
    }
}