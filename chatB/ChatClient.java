import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ChatClient {

    private Socket socket;
    private Scanner console;
    private DataOutputStream output;
    private BufferedReader reader;

    public ChatClient(String serverName, int serverPort) {
        try {
            System.out.println("LiveChat Client 1.1 start.");
            System.out.println("Trying to connect to " + serverName + " on port " + serverPort + "...");
            socket = new Socket(serverName, serverPort);
            System.out.println("Success!");

            console = new Scanner(System.in);
            output = new DataOutputStream(socket.getOutputStream());
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            Thread t1 = new Thread(){
                @Override
                public void run() {
                    String line = "";

                    while(!line.equals(".bye")) {
                        try {
                            System.out.print("me: ");
                            line = console.nextLine();
                            output.writeUTF(line);
                            output.flush();
                        } catch(IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };

            Thread t2 = new Thread(){
                @Override
                public void run() {
                    String line = "";

                    try {
                        while(!(line = reader.readLine()).equals(".bye")) {
                            System.out.print("\nhe: " + line + "\nme: ");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };

            t1.start();
            t2.start();
        } catch(UnknownHostException e) {
            System.err.println("Unknown host: " + e.getMessage());
        } catch(IOException e) {
            System.err.println("Exception ocurred: " + e.getMessage());
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    @SuppressWarnings({ "unused", "resource" })
    public static void main(String[] args) throws IOException {
        File hostsFile = new File("hosts.dat");

        if(!hostsFile.exists()) {
            hostsFile.createNewFile();
            FileController.writeFile(hostsFile.getPath(), "localhost\n");
        }

        String hosts = FileController.loadFile("hosts.dat");
        String[] h = hosts.split("\n");

        System.out.println("Select host by it number, or insert a new one.");
        System.out.println("Currently avaliable hosts: ");

        for(int i = 0; i < h.length; i++) {
            System.out.println(i + ": " + h[i]);
        }

        System.out.print("Please provide the IP Address of the server: ");
        Scanner s = new Scanner(System.in);
        String hostName = s.nextLine();

        if(isInteger(hostName)) {
            int i = Integer.parseInt(hostName);
            hostName = h[i];
        } else {
            FileController.writeFile(hostsFile.getPath(), hostName + "\n");
        }

        ChatClient client = new ChatClient("localhost", 9081);
    }

    private static boolean isInteger(String str) {
        boolean is = false;

        try {
            Integer.parseInt(str);
            is = true;
        } catch(Exception e) {
            is = false;
        }

        return is;
    }
}