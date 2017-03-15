/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import ChatMessage.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.PatternSyntaxException;

/**
 * 
 * @author brom
 */
public class ClientConnection extends Thread {

	private Server m_server = null;
	private String m_name = null;
	private String timeOfJoining = null;

	//private InetAddress m_address = null;
	//private int m_port = -1;
	private Socket m_socket = null;
	//PrintWriter out = null;
	//BufferedReader in = null;
	ObjectInputStream in = null;
	ObjectOutputStream out = null;
	
	/*
	 * public ClientConnection(String name, InetAddress address, int port) {
	 * m_name = name; m_address = address; m_port = port; }
	 */

	public ClientConnection(Socket socket, Server server) {
		m_socket = socket;
		m_server = server;
	}

	// Borde detta vara start?!
	public void run() {

		ChatMessage input = null;

		try {
			out = new ObjectOutputStream(m_socket.getOutputStream());//new PrintWriter(m_socket.getOutputStream(), true);
			//in = new BufferedReader(new InputStreamReader(m_socket.getInputStream()));
			in = new ObjectInputStream(m_socket.getInputStream());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		while (!isInterrupted()) {
			try {
				try {
					input = (ChatMessage) in.readObject();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println(input);

				/* Check what type of message it is: */
				if (input.getCommand().equals("/handshake")) {
					if(m_server.requestConnection(input.getSender())){
						fillInfoAboutClient(input.getSender(), input.getTimeStamp());
						m_server.sendPrivateMessage("Server: Welcome user!", m_name);
						m_server.broadcast("Server: Everyone, say hi to: " + m_name);
					}else{
						sendMessage("/taken Sorry this name is already taken.");
						m_server.removeClient(this);
					}
					
				} else if (input.getCommand().equals("/pm")) {
					// Send pm to destination.
					m_server.sendPrivateMessage("(PM) "+ m_name + ": " + getMessageForPM(input.getParameters()), getDestination(input.getParameters()));
					// Send pm to sender so it shows on their screen (lazy as fff)
					m_server.sendPrivateMessage("(PM) "+ m_name + ": " + getMessageForPM(input.getParameters()), m_name);
				} else if(input.getCommand().equals("/list")){
					m_server.sendPrivateMessage(m_server.getListOfClients(), m_name);
				} else if(input.getCommand().equals("/disconnect") || input.getCommand().equals("/leave")){
					sendMessage("/gtfo Server: Awww... sad to see you go. But bye now!");
					m_server.broadcast("Server: "+m_name + " just disconnected. The users last words were: " + input.getParameters());
					m_server.removeClient(this);
				} else if(input.getCommand().equals("/help")){
					String message = null;
					if(input.getParameters().equals("help")){
						message = "/help - displays all commands.";
					} else if(input.getParameters().equals("pm")){
						message = "/pm [name] [message] - Sends a private message to a user.";
					} else if(input.getParameters().equals("list")){
						message = "/list - Displays all connected clients";
					} else if(input.getParameters().equals("leave") || input.getParameters().equals("disconnect")){
						message = "/leave (or /disconnect) - Disconnects user from server.";
					} else if(input.getParameters().equals("time")){
						message = "/time - Shows the time of day.";
					} else if(input.getParameters().equals("isitfridayyet")){
						message = "/isitfridayyet - Shows if it is finally friday.";
					} else{
						message = "help pm list time isitfridayyet leave disconnect";
					}
					
					m_server.sendPrivateMessage(message, m_name);
					
				} else if(input.getCommand().equals("/time")){
					Calendar cal = Calendar.getInstance();
					Date date = new Date();
					cal.setTime(date);
					String message = null;
					String time = String.format("%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
					message = "Time of day: " + time;
					
					m_server.sendPrivateMessage(message, m_name);
				} else if(input.getCommand().equals("/isitfridayyet")){
					Calendar cal = Calendar.getInstance();

					int dayOfTheWeek = cal.get(Calendar.DAY_OF_WEEK);
					String message = null;
					message = "Today it is the: " + dayOfTheWeek + "th day of the week.";
					
					// Amerikans kalender börjar på söndag. Därför är fredag 6e dagen i veckan.
					if(dayOfTheWeek == 6){
						message += " THIS MEANS THAT IT IS FRIDAY!";
					}else {
						message += " Which means that it isnt friday :(";
					}
					
					m_server.sendPrivateMessage(message, m_name);
				} else{
					m_server.broadcast(m_name + ": " + input.getCommand() + " " + input.getParameters());
				}
				System.out.println(input);
				// sendMessage(input);
			} catch (SocketException e) {

				System.out.println("Hello! Socket exception bitches.");

				// kalla på att ta bort objektet
				m_server.removeClient(this);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	public void sendMessage(String message) {
		// TODO: send a message to this client using chatmessage.
		
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
		ChatMessage cm = new ChatMessage(command, parameters, m_name);

		// * send a chat message to the server
		//out.println(cm.getJsonString());
		try {
			out.writeObject(cm);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean hasName(String testName) {
		return testName.equals(m_name);
	}
	
	public String joinedAt(){
		return timeOfJoining;
	}
	
	public String getNameOfClient(){
		return m_name;
	}

	private void fillInfoAboutClient(String name, String time) {
		m_name = name;
		timeOfJoining = time;
	}

	private String getDestination(String parameters) {
		// Splitting the string into parts where there is a space.
		String[] splitArray = null;
		try {
			splitArray = parameters.split("\\s+");
		} catch (PatternSyntaxException e) {
			//
			System.out.println(e.getMessage());
		}

		System.out.println(splitArray[0]);
		return splitArray[0];
	}
	
	private String getMessageForPM(String parameters) {
		String message = null;
		// Splitting the string into parts where there is a space.
		String[] splitArray = null;
		try {
			splitArray = parameters.split("\\s+");
		} catch (PatternSyntaxException e) {
			//
			System.out.println(e.getMessage());
		}

		// Get only parameters.
		splitArray = Arrays.copyOfRange(splitArray, 1, splitArray.length);
		message = String.join(" ", splitArray);
		System.out.println(message);
		return message;
	}

}
