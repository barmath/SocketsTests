import java.net.*;
import java.io.*;
 
/**
 * This program demonstrates a simple TCP/IP socket client that reads input
 * from the user and prints echoed message from the server.
 *
 * @author www.codejava.net
 */
public class ReverseClient {
    
    private static DataOutputStream output;

    public static void main(String[] args) {
        if (args.length < 2) return;
 
        String hostname = args[0];
        int port = Integer.parseInt(args[1]);
 
        try (Socket socket = new Socket(hostname, port)) {
 
            //OutputStream output = socket.getOutputStream();
            //output = socket.getOutputStream();
            output = new DataOutputStream(socket.getOutputStream());
            PrintWriter writer = new PrintWriter(output, true);

            Console console = System.console();
            String text;
 
            do {
                text = console.readLine("Enter text: ");

                //File file = new File("Arq.txt");
                
                sendFile(text);

                writer.println(text);
 
                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
 
                String time = reader.readLine();
 
                System.out.println(time);
 
            } while (!text.equals("bye"));
 
            socket.close();
 
        } catch (UnknownHostException ex) {
 
            System.out.println("Server not found: " + ex.getMessage());
 
        } catch (IOException ex) {
 
            System.out.println("I/O error: " + ex.getMessage());
        }
    }
    private static void sendFile(String pathName) throws IOException {
        
        FileInputStream fileIn = new FileInputStream(pathName);
        byte[] buf = new byte[Short.MAX_VALUE];
        int bytesRead;        
        while( (bytesRead = fileIn.read(buf)) != -1 ) {
            output.writeShort(bytesRead);
            output.write(buf,0,bytesRead);
        }
        output.writeShort(-1);
        fileIn.close();
    }

}