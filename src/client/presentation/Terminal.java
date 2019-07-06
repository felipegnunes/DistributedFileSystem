package client.presentation;

import client.domain.ClientManager;

import java.util.Scanner;

//classe responsavel para lidar com as interacoes IO com o terminal de comando
public class Terminal {

    private static final String OPEN = "ropen";
    private static final String READ = "rread";
    private static final String EOF = "reof";
    private static final String SEEK = "rseek";
    private static final String WRITE = "rwrite";
    private static final String GETPOS = "rgetpos";
    private static final String CLOSE = "rclose";
    private static final String REMOVE = "rremove";

    private ClientManager manager;

    public Terminal(ClientManager manager) {
        this.manager = manager;
    }

    //funcao para interpretar a entrada e repassar para a camada lógica o que foi requisitado
    public void start() {
        Scanner scanner = new Scanner(System.in);
        String lastRid = "0";
        while(true) {
            /*
            param[0] é a funcao desejada e os proximos comandos
            sao as entradas padroes definidas para cada funcao
             */
            String input = scanner.nextLine();
            String[] params = input.split(" ");
            String operation = params[0];
            switch (operation) {
                case OPEN:
                    lastRid = manager.ropen(params[1], params[2]);
                    System.out.println(lastRid);
                    break;
                case READ:
                    System.out.println(manager.rread(params[1]));
                    break;
                case WRITE:
                    StringBuffer wBuffer = new StringBuffer(params[1]);
                    System.out.println(manager.rwrite(wBuffer, Integer.valueOf(params[2]), Integer.valueOf(params[3]), params[4]));
                    break;
                case EOF:
                    System.out.println(manager.reof(params[1]));
                    break;
                case SEEK:
                    System.out.println(manager.rseek(params[1], Integer.valueOf(params[2]), params[3]));
                    break;
                case CLOSE:
                    System.out.println(manager.rclose(params[1]));
                    break;
                case GETPOS:
                    System.out.println(manager.rgetpos(params[1], Integer.valueOf(params[2])));
                    break;
                case REMOVE:
                    System.out.println(manager.rremove(params[1]));
                    break;
                default:
                    break;
            }
        }
    }
}
