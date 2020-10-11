import java.io.*;
import java.net.*;
import java.util.*;

/**
 * This thread handles connection for each connected client, so the server
 * can handle multiple clients at the same time.
 *
 * @author www.codejava.net
 */
public class UserThread extends Thread {
	private Socket socket;
	private ChatServer server;
	private PrintWriter writer;
	private DataInputStream input;

	public UserThread(Socket socket, ChatServer server) {
		this.socket = socket;
		this.server = server;
	}

	public void run() {
		try {
			InputStream input = socket.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));

			OutputStream output = socket.getOutputStream();
			writer = new PrintWriter(output, true);

			printUsers();
			showOptions();

			String userName = reader.readLine();
			server.addUserName(userName);

			String serverMessage = "New user connected: " + userName;
			server.broadcast(serverMessage, this);

			String clientMessage;

			do {
				clientMessage = reader.readLine();

				if (clientMessage.equals("/sendFile")) {
					serverMessage = "ARQUIVO ENVIADO PARA O SERVER";
					receiveFile(new File("C:\\ArqServer.txt"));
				}
				else
					serverMessage = "[" + userName + "]: " + clientMessage;
				server.broadcast(serverMessage, this);

			} while (!clientMessage.equals("bye"));

			server.removeUser(userName, this);
			socket.close();

			serverMessage = userName + " has quitted.";
			server.broadcast(serverMessage, this);

		} catch (IOException ex) {
			System.out.println("Error in UserThread: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	/**
	 * Sends a list of online users to the newly connected user.
	 */
	void printUsers() {
		if (server.hasUsers()) {
			writer.println("Connected users: " + server.getUserNames());
		} else {
			writer.println("No other users connected");
		}
	}

	/**
	 * Sends a message to the client.
	 */
	void sendMessage(String message) {
		writer.println(message);
	}

	/**
	 * Shows the user options 
	 */
	void showOptions() {
		writer.println("*** Type your messages and press Enter to chat with other users. ***");
		writer.println("*** Use the /sendFile command to upload a file. ***");
	}

	public void receiveFile(File file) throws IOException {
		FileOutputStream fileOut = new FileOutputStream(file);
		input = new DataInputStream(socket.getInputStream());
        byte[] buf = new byte[Short.MAX_VALUE];
        int bytesSent;        
        while( (bytesSent = input.readShort()) != -1 ) {
            input.readFully(buf,0,bytesSent);
            fileOut.write(buf,0,bytesSent);
        }
        fileOut.close();
    }
}
