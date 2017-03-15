package Server;

import java.io.IOException;

//
// Source file for the server side. 
//
// Created by Sanny Syberfeldt
// Maintained by Marcus Brohede
//

import java.net.*;
//import java.io.*;

import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JOptionPane;

public class Server {

	private ArrayList<ClientConnection> m_connectedClients = new ArrayList<ClientConnection>();
	private ServerSocket m_socket;

	public static void main(String[] args) {

		try {
			String port = JOptionPane.showInputDialog("Please input the port for the server!");

			Server instance = new Server(Integer.parseInt(port));
			instance.listenForClientMessages();
		} catch (NumberFormatException e) {
			System.err.println("Error: port number must be an integer.");
			System.exit(-1);
		}
	}

	private Server(int portNumber) {
		// TODO: create a socket, attach it to port based on portNumber, and
		// assign it to m_socket
		try {
			m_socket = new ServerSocket(portNumber);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void listenForClientMessages() {
		System.out.println("Waiting for client messages... ");

		do {

			try {
				ClientConnection newConnect = new ClientConnection(m_socket.accept(), this);
				newConnect.start();
				m_connectedClients.add(newConnect);
			} catch (SocketException e) {
				System.out.println("SocketException");
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			System.out.println("After a client has connected");

			// TODO: Listen for client messages.
			// On reception of message, do the following:
			// * Unmarshal message
			// * Depending on message type, either
			// - Try to create a new ClientConnection using addClient(), send
			// response message to client detailing whether it was successful
			// - Broadcast the message to all connected users using broadcast()
			// - Send a private message to a user using sendPrivateMessage()
		} while (true);
	}

	public void removeClient(ClientConnection cc) {

		for (int i = 0; i < m_connectedClients.size(); i++) {
			if (m_connectedClients.get(i) == cc) {
				// Behöver fixas bättre.
				m_connectedClients.get(i).interrupt();
				m_connectedClients.remove(i);
			}
		}

	}

	public boolean requestConnection(String name) {
		ClientConnection c;
		for (Iterator<ClientConnection> itr = m_connectedClients.iterator(); itr.hasNext();) {
			c = itr.next();
			if (c.hasName(name)) {
				return false; // Already exists a client with this name
			}
		}
		return true;
	}

	public void sendPrivateMessage(String message, String name) {
		ClientConnection c;
		for (Iterator<ClientConnection> itr = m_connectedClients.iterator(); itr.hasNext();) {
			c = itr.next();
			if (c.hasName(name)) {
				c.sendMessage(message/* , m_socket */);
			}
		}
	}

	public void broadcast(String message) {
		for (Iterator<ClientConnection> itr = m_connectedClients.iterator(); itr.hasNext();) {
			itr.next().sendMessage(message/* , m_socket */);
		}
	}

	public String getListOfClients() {

		String message = "Connected Clients: \n\n";

		for (int i = 0; i < m_connectedClients.size(); i++) {
			message += m_connectedClients.get(i).getNameOfClient() + " - " + m_connectedClients.get(i).joinedAt()
					+ "\n";
		}
		return message;
	}
}
