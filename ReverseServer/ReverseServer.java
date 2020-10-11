import java.io.*;
import java.net.*;

/**
 * This program demonstrates a simple TCP/IP socket server that echoes every
 * message from the client in reversed form.
 * This server is single-threaded.
 *
 * @author www.codejava.net
 */
public class ReverseServer {

    private static DataInputStream input;
 
    public static void main(String[] args) {
        if (args.length < 1) return;
 
        int port = Integer.parseInt(args[0]);
 
        try (ServerSocket serverSocket = new ServerSocket(port)) {
 
            System.out.println("Server is listening on port " + port);
 
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected");
 
                input = new DataInputStream(socket.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);
 
                String pathName;
 
                do {
                    pathName = reader.readLine();
                    receiveFile(pathName);
                    //String reverseText = new StringBuilder(text).reverse().toString();
                    System.out.println("This was received from client ");

                    writer.println("Server:" + pathName);
 
                } while (!pathName.equals("bye"));
 
                socket.close();
            }
 
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static void receiveFile(String file) throws IOException {
        FileOutputStream fileOut = new FileOutputStream("novoArq.txt");
        byte[] buf = new byte[Short.MAX_VALUE];
        int bytesSent;        
        while( (bytesSent = input.readShort()) != -1 ) {
            input.readFully(buf,0,bytesSent);
            fileOut.write(buf,0,bytesSent);
        }
        fileOut.close();
    }   
}