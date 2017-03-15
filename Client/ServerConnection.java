/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.Arrays;
import java.util.regex.PatternSyntaxException;

import ChatMessage.*;

/**
 *
 * @author brom
 */
public class ServerConnection {

	private Socket m_socket = null;
	private InetAddress m_serverAddress = null;
	private int m_serverPort = -1;
	private Client m_client = null;
	//PrintWriter out = null;
	ObjectOutputStream out = null;
	ObjectInputStream in = null;
	//BufferedReader in = null;

	public ServerConnection(String hostName, int port, Client client) {
		m_serverPort = port;
		m_client = client;
		// TODO:
		// * get address of host based on parameters and assign it to
		// m_serverAddress

		try {
			m_serverAddress = InetAddress.getByName(hostName);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// * set up socket and assign it to m_socket

		try {
			m_socket = new Socket(m_serverAddress, m_serverPort);
			out = new ObjectOutputStream(m_socket.getOutputStream());//new PrintWriter(m_socket.getOutputStream(), true);
			in = new ObjectInputStream(m_socket.getInputStream());
			//in = new BufferedReader(new InputStreamReader(m_socket.getInputStream()));
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host " + hostName);
			System.exit(1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void handshake(String name) {
		sendChatMessage("/handshake", name);
	}

	public String receiveChatMessage() {
		ChatMessage fromServer = null;
		String message = null;

		try {
			try {
				if ((fromServer = (ChatMessage) in.readObject()) != null) {
					System.out.println("Server: " + fromServer);
					
					
					String command = null;
					// Splitting the string into parts where there is a space.
					/*String[] splitArray = null;
					try {
						splitArray = fromServer.split("\\s+");
					} catch (PatternSyntaxException e) {
						//
						System.out.println(e.getMessage());
					}*/
					
					// Get only command from message.
					command = fromServer.getCommand();
					if(command.equals("/taken")){
						message = fromServer.getParameters();
						m_client.stopClient();
					}else if(command.equals("/gtfo")){
						message = fromServer.getParameters();
						m_client.stopClient();
					}else{
						message = fromServer.getCommand() + " " + fromServer.getParameters();
					}
				} else {

				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (SocketException e) {
			// TODO Auto-generated catch block
			System.out.println("WE HAVE A SOCKET EXCEPTION BITCHES");
			m_client.showMessage("The servers seems to have shutdown.");
			m_client.stopClient();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO:
		// * receive message from server
		// * unmarshal message if necessary

		// Note that the main thread can block on receive here without
		// problems, since the GUI runs in a separate thread

		// Update to return message contents
		return message;
	}

	public void sendChatMessage(String message, String sender) {

		// Split string.
		// Make new chat message.
		// Put first one as command.
		// .rest as message.
		// Set sender as sender.
		// Serialize object as string.
		// Send string.
		
		String parameters = new String();
		String command = new String();

		// Splitting the string into parts where there is a space.
		String[] splitArray = null;
		try {
			splitArray = message.split("\\s+");
		} catch (PatternSyntaxException e) {
			//
			System.out.println(e.getMessage());
		}
		
		// Get only command from message.
		command = splitArray[0];
		
		// Get only parameters.
		splitArray = Arrays.copyOfRange(splitArray, 1, splitArray.length);
		parameters = String.join(" ", splitArray);
		
		// Create a new chatmessage.
		ChatMessage cm = new ChatMessage(command, parameters, sender);

		// * send a chat message to the server
		try {
			out.writeObject(cm);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//out.println(cm.getJsonString());
		//out.println(message);
	}

}
