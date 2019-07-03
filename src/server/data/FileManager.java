package server.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.Scanner;

public class FileManager {

    // Caminho absoluto para o diretório de arquivos.
    // Modificar PATH para um caminho de um diretório no servidor.
    private static final String PATH = "/home/felipeguimaraes/DistributedFileSystem/resources/";

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
        String fPath = PATH + filename;
        System.out.println(PATH);
        String acum="";
        if(fileExists(fPath)) {
            System.out.println("arquivo existe");
            String data = fileToString(PATH + filename);
            acum = data.substring((int) start, Math.min((int) (start + count), data.length()));
        }
        return acum;
    }

    public boolean eof(String filename, long position) {
        String data = fileToString(PATH + filename);
        return data.length() == (int)position;
    }

    public int write(String buffer, String filename, String mode, long position){
        int result=0;
        String file = PATH + filename;
        boolean append = mode.contains("a");
        System.out.println("buffer " + buffer);
        try {
            FileWriter fWriter = new FileWriter(file, append);
            fWriter.write(buffer);
            fWriter.close();
            System.out.println("escrevi");
        } catch (Exception e){
            result=0;
        }

        return result;
    }

    public long seek(String filename, long position, long offset, String origin) {
        long fileSize = fileSize(PATH + filename);
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
        String f = PATH + filename;
        File deleteFile = new File(f);
        if(deleteFile.delete()){
            return 0;
        }
        return 1;
    }
}