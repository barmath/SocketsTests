import java.io.*;
import java.net.*;

/**
 * This thread is responsible for reading user's input and send it
 * to the server.
 * It runs in an infinite loop until the user types 'bye' to quit.
 *
 * @author www.codejava.net
 */
public class WriteThread extends Thread {
	private PrintWriter writer;
	private Socket socket;
	private ChatClient client;
	private DataOutputStream output;

	public WriteThread(Socket socket, ChatClient client) {
		this.socket = socket;
		this.client = client;

		try {
			OutputStream output = socket.getOutputStream();
			writer = new PrintWriter(output, true);
		} catch (IOException ex) {
			System.out.println("Error getting output stream: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	public void run() {

		Console console = System.console();

		String userName = console.readLine("\nEnter your name: ");
		client.setUserName(userName);
		writer.println(userName);

		String text;

		try {
			do {
				text = console.readLine("[" + userName + "]: ");
				writer.println(text);
	
				if (text.equals("/sendFile")) 
					sendFile(new File("C:\\Users\\Rapha\\Arq.txt"));
	
			} while (!text.equals("bye"));
			socket.close();
		}
		catch (IOException ex) {

			System.out.println("Error writing to server: " + ex.getMessage());
		}
	}

	public void sendFile(File file) throws IOException {
		FileInputStream fileIn = new FileInputStream(file);
		output = new DataOutputStream(socket.getOutputStream());
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