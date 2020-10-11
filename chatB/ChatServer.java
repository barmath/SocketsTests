import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer {

    private Socket[] sockets;
    private ServerSocket server;
    private DataInputStream[] ins;
    private PrintStream[] outs;

    private String ln1;
    private String ln2;

    public ChatServer(int port) {
        try {
            System.out.println("LiveChat Server 0.9 start.");
            System.out.println("Trying to open port " + port + "...");
            server = new ServerSocket(port);
            System.out.println("Server " + server.getInetAddress().getHostName() + " successfully started!");
            System.out.println("Instantiating input and output streams...");
            ins = new DataInputStream[2];
            outs = new PrintStream[2];
            System.out.println("Success!");
            System.out.println("Instantiating sockets...");
            sockets = new Socket[2];
            System.out.println("Success!");
            System.out.println("Waiting socket 1 to connect...");
            sockets[0] = server.accept();
            System.out.println("Success!");
            System.out.println("Waiting socket 2 to connect...");
            sockets[1] = server.accept();
            System.out.println("Success!");
            System.out.println("Opening input and output streams...");
            open();
            System.out.println("Success!");
            System.out.println("Initializing input strings...");
            ln1 = "";
            ln2 = "";
            System.out.println("Success!");

            Thread r1 = new Thread() {
                @Override
                public void run() {
                    try {
                        while(!ln1.equals(".bye")) {
                            ln1 = ins[0].readUTF();
                            System.out.println("1: " +ln1);
                            outs[1].println(ln1);
                        }

                        System.out.println("Socket 1 disconnect!");
                        sockets[0].close();
                    } catch(IOException e) {
                        e.printStackTrace();
                    }
                }
            };

            Thread r2 = new Thread() {
                @Override
                public void run() {
                    try {
                        while(!ln2.equals(".bye")) {
                            ln2 = ins[1].readUTF();
                            System.out.println("2: " + ln2);
                            outs[0].println(ln2);
                        }

                        System.out.println("Socket 2 disconnect!");
                        sockets[1].close();
                    } catch(IOException e) {
                        e.printStackTrace();
                    }
                }
            };

            r1.start();
            r2.start();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void open() throws IOException {
        ins[0] = new DataInputStream(new BufferedInputStream(sockets[0].getInputStream()));
        ins[1] = new DataInputStream(new BufferedInputStream(sockets[1].getInputStream()));
        outs[0] = new PrintStream(sockets[0].getOutputStream());
        outs[1] = new PrintStream(sockets[1].getOutputStream());
    }

    @SuppressWarnings("unused")
    public static void main(String[] args) {
        ChatServer chat = new ChatServer(9081);
    }
}