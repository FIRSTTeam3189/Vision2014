package edu.firstteam3189.vision2014.net;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import edu.firstteam3189.vision2014.Main;
import edu.firstteam3189.vision2014.Manager;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
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
	private static final String Adress = "10.31.89.2";
        
        private NetworkTable table;
	
	/**
	 * Creates the server daemon that receives socket connections and sends results
	 * @throws IOException
	 */
	public ServerDaemon() throws IOException {
		
	}
	
	/**
	 * Sends a message through the data output stream
	 * @param message the string message to send
	 * @throws IOException
	 */
	public void sendMessage(int message) {
		if(table != null) {
			table.putNumber("client", message);
		}
	}
	
	/**
	 * Can we receive messages from the data stream
	 * @return if you can
	 * @throws IOException
	 */
	public boolean canRecieveMessage() {
		if(table == null) {
			return false;
		}
                
		return true;
	}
	
	/**
	 * reads the message from the data input stream, is a blocking call if we can not receive messages
	 * @return the returned message
	 * @throws IOException
	 */
	public int reciveMessage() {
		if(table == null) {
			return 0;
		}
		return (int) table.getNumber("robot");
	}
	
	/**
	 * the running method of the server daemon thread
	 * Responds to commands from the robot
	 */
	@Override
	public void run() {
            startClient();
		while (table != null) {
                    
                    if(table.isConnected()){
                        try {
                            System.out.println("asdfghj");
                                if(canRecieveMessage()) {
                                    int command = reciveMessage();

                                    if(command == REQUEST_NUMBER_OF_HOTZONES_FROM_ROBOT){
                                            int data = Manager.getHotzones();
                                            sendMessage(data);
                                    } else if(command == REQUEST_DEATH_FROM_ROBOT) {
                                            System.exit(0);
                                    } else if (command == REQUEST_DISCONNECT_FROM_ROBOT) {
                                            closeClient();
                                    } else {

                                    }

                                }

                        }finally{
                        
                        }
		}else{
                       table = NetworkTable.getTable("data");
                    }
                

}
	}

	/**
	 * close the input and output streams and the client
	 * then nullifies all above
	 */
	private void closeClient () {
		if (table != null && table.isConnected()) {
			table = null;
		}
	}
        
        private void startClient(){
            NetworkTable.setClientMode();
            NetworkTable.setIPAddress(Adress);
            table = NetworkTable.getTable("data");
        }
	
}
