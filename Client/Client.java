package Client;

import java.awt.event.*;

import javax.swing.*;

//import java.io.*;

public class Client implements ActionListener {

	private String m_name = null;
	private final ChatGUI m_GUI;
	private ServerConnection m_connection = null;
	
	private boolean active = true;

	public static void main(String[] args) {
		/*if (args.length < 3) {
			System.err.println("Usage: java Client serverhostname serverportnumber username");
			System.exit(-1);
		}*/

		try {

			String name = JOptionPane.showInputDialog("Please input a name!");
			String ip = JOptionPane.showInputDialog("Please input the ip to the server!");
			String port = JOptionPane.showInputDialog("Please input the port for the server!");
			
			Client instance = new Client(name);
			instance.connectToServer(ip, Integer.parseInt(port));
		} catch (NumberFormatException e) {
			System.err.println("Error: port number must be an integer.");
			System.exit(-1);
		}
	}

	private Client(String userName) {
		
		
		
		m_name = userName;

		// Start up GUI (runs in its own thread)
		m_GUI = new ChatGUI(this, m_name);
	}

	private void connectToServer(String hostName, int port) {
		// Create a new server connection
		m_connection = new ServerConnection(hostName, port, this);
		m_connection.handshake(m_name);
		
		listenForServerMessages();
	}

	private void listenForServerMessages() {
		// Use the code below once m_connection.receiveChatMessage() has been
		// implemented properly.
		do {
			m_GUI.displayMessage(m_connection.receiveChatMessage());
		} while (active);
	}

	// Sole ActionListener method; acts as a callback from GUI when user hits
	// enter in input field
	@Override
	public void actionPerformed(ActionEvent e) {
		// Since the only possible event is a carriage return in the text input
		// field,
		// the text in the chat input field can now be sent to the server.
		if(active){
			m_connection.sendChatMessage(m_GUI.getInput(), m_name);
			m_GUI.clearInput();
		}else{
			m_GUI.displayMessage("No connection detected. Please restart.");
		}
		
	}
	
	public void stopClient(){
		active = false;
		m_GUI.displayMessage("Disconnected from server.");
	}
	
	public void showMessage(String message){
		m_GUI.displayMessage(message);
	}
	
}
