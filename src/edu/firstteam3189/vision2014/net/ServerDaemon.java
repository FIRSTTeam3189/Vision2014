package edu.firstteam3189.vision2014.net;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import edu.firstteam3189.vision2014.Main;
import team3189.library.Logger.Logger;

public class ServerDaemon extends Thread{
	
	public static final int REQUEST_NUMBER_OF_HOTZONES_FROM_ROBOT = 69;
	public static final int REQUEST_DEATH_FROM_ROBOT = 666;
	public static final int REQUEST_DISCONNECT_FROM_ROBOT = 14;
	
	/**
	 * LOGGER for putting out error messages
	 */
	private static final Logger LOGGER = new Logger(ServerDaemon.class);
	
	/**
	 * The port to listen for connections 
	 */
	private static final int PORT = 1180;
	
	/**
	 * The  server socket to accept sockets
	 */
	private ServerSocket server;
	
	/**
	 * The robot socket
	 */
	private Socket client;
	
	/**
	 * The Data stream to receive input
	 */
	private DataInputStream is;
	
	/**
	 * The Data stream to send results on
	 */
	private DataOutputStream os;
	
	/**
	 * Creates the server daemon that receives socket connections and sends results
	 * @throws IOException
	 */
	public ServerDaemon() throws IOException {
		server = new ServerSocket(PORT);
		client = null;
		is = null;
		os = null;
	}
	
	/**
	 * Sends a message through the data output stream
	 * @param message the string message to send
	 * @throws IOException
	 */
	public void sendMessage(int message) throws IOException {
		if(os != null) {
			os.writeInt(message);
		}
	}
	
	/**
	 * Can we receive messages from the data stream
	 * @return if you can
	 * @throws IOException
	 */
	public boolean canRecieveMessage() throws IOException {
		if(is == null) {
			return false;
		}
		return is.available() > 0;
	}
	
	/**
	 * reads the message from the data input stream, is a blocking call if we can not receive messages
	 * @return the returned message
	 * @throws IOException
	 */
	public int reciveMessage() throws IOException {
		if(is == null) {
			return 0;
		}
		return is.readInt();
	}
	
	/**
	 * the running method of the server daemon thread
	 * Responds to commands from the robot
	 */
	@Override
	public void run() {
		while (server != null && server.isBound()) {
			acceptClient();
			
			try{
				while(client.isConnected()) {
					if(canRecieveMessage()) {
						int command = reciveMessage();
						if(command == REQUEST_NUMBER_OF_HOTZONES_FROM_ROBOT){
							int data = Main.getApp().getHotzones();
							sendMessage(data);
						} else if(command == REQUEST_DEATH_FROM_ROBOT) {
							System.exit(0);
						} else if (command == REQUEST_DISCONNECT_FROM_ROBOT) {
							closeClient();
						} else {
							
						}
						
					}
					
				}
			} catch(IOException e){
				LOGGER.error("Cannot recive command!", e);
				
				closeClient();
				
			}finally {
				closeClient();
			}
		}
	}

	/**
	 * close the input and output streams and the client
	 * then nullifies all above
	 */
	private void closeClient () {
		if (client != null && client.isConnected()) {
			try {
				is.close();
				os.close();
				client.close();
			} catch (IOException e) {
				LOGGER.error("Could not close client!", e);
			}
		}
	}

	/**
	 * accepts the client from server socket
	 * sets up io streams
	 * upon io exception, nullifies client, is, os
	 */
	private void acceptClient() {
		while(client == null || client.isClosed()){
			try {
				client = server.accept();
				is = new DataInputStream(client.getInputStream());
				os = new DataOutputStream(client.getOutputStream());
			} catch (IOException e) {
				LOGGER.error("Could not accept Client!", e);
				client = null;
				is = null;
				os = null;
			}
		}
	}
	
}
