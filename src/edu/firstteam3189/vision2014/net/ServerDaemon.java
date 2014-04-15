package edu.firstteam3189.vision2014.net;

import java.io.IOException;

import team3189.library.Logger.Logger;
import edu.firstteam3189.vision2014.Manager;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class ServerDaemon extends Thread {
	public static final int REQUEST_DEATH_FROM_ROBOT = 666;
	public static final int REQUEST_DISCONNECT_FROM_ROBOT = 14;
	public static final int REQUEST_NOTHING = 99;
	public static final int REQUEST_NUMBER_OF_HOTZONES_FROM_ROBOT = 69;

	/**
	 * The port to listen for connections
	 */
	private static final String Address = "10.31.89.2";
	/**
	 * The Logger class for the ImageDaemon
	 */
	private static final Logger logger = new Logger(ServerDaemon.class);

	private static final String NETWORK_COMMAND = "robot";

	private static final String NETWORK_TABLE = "data";

	private NetworkTable table;

	/**
	 * Creates the server daemon that receives socket connections and sends results
	 * 
	 * @throws IOException
	 */
	public ServerDaemon() {
		super("Server Daemon");
	}

	/**
	 * the running method of the server daemon thread Responds to commands from the robot
	 */
	@Override
	public void run() {
		startClient();
		while (table != null) {
			if (table.isConnected()) {
				// logger.debug("Table is available.");
				if (canReceiveMessage()) {
					int command = receiveMessage();
					switch (command) {
					case REQUEST_NUMBER_OF_HOTZONES_FROM_ROBOT:
						int data = Manager.getHotzones();
						sendMessage(data);
						break;

					case REQUEST_DEATH_FROM_ROBOT:
						System.exit(0);
						break;

					case REQUEST_DISCONNECT_FROM_ROBOT:
						closeClient();
						break;

					case REQUEST_NOTHING:
						break;

					default:
						logger.error("Invalid command (ignored): " + command);
						break;
					}
				}
			} else {
				table = NetworkTable.getTable(NETWORK_TABLE);
			}
		}
	}

	/**
	 * Can we receive messages from the data stream
	 * 
	 * @return if you can
	 * @throws IOException
	 */
	private boolean canReceiveMessage() {
		return table != null;
	}

	/**
	 * close the input and output streams and the client then nullifies all above
	 */
	private void closeClient() {
		if (table != null && table.isConnected()) {
			table = null;
		}
	}

	/**
	 * reads the message from the data input stream, is a blocking call if we can not receive messages
	 * 
	 * @return the returned message
	 * @throws IOException
	 */
	private int receiveMessage() {
		int message = REQUEST_NOTHING;

		if (canReceiveMessage()) {
			message = (int) table.getNumber(NETWORK_COMMAND, REQUEST_NOTHING);
		}

		return message;
	}

	/**
	 * Sends a message through the data output stream
	 * 
	 * @param message
	 *            the string message to send
	 * @throws IOException
	 */
	private void sendMessage(int message) {
		if (table != null) {
			table.putNumber("client", message);
		}
	}

	private void startClient() {
		NetworkTable.setClientMode();
		NetworkTable.setIPAddress(Address);
		table = NetworkTable.getTable(NETWORK_TABLE);
	}
}
