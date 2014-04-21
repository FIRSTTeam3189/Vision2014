package edu.firstteam3189.vision2014.net;

import java.io.IOException;

import team3189.library.Logger.Logger;
import edu.firstteam3189.vision2014.Manager;
import edu.firstteam3189.vision2014.vision.ImageDaemon;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.first.wpilibj.tables.ITableListener;

public class ServerDaemon extends Thread implements ITableListener {
	public static final int REQUEST_DEATH_FROM_ROBOT = 666;
	public static final int REQUEST_DISCONNECT_FROM_ROBOT = 14;
	public static final int REQUEST_NOTHING = 99;
	public static final int REQUEST_NUMBER_OF_HOTZONES_FROM_ROBOT = 69;

	private static final Logger LOGGER = new Logger(ServerDaemon.class);
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
		table = NetworkTableAccess.getInstance().getTable(NETWORK_TABLE);
		table.addTableListener(ImageDaemon.NETWORK_OFF_CENTER_ON, this, true);
		while (table != null) {
			if (table.isConnected()) {
				// LOGGER.debug("Table is available.");
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
						LOGGER.error("Invalid command (ignored): " + command);
						break;
					}
				}
			} else {
				table = NetworkTable.getTable(NETWORK_TABLE);
			}
		}
	}

	/**
	 * This method is used to detect a change in the network table.
	 */
	@Override
	public void valueChanged(ITable source, String key, Object value, boolean isNew) {
		if (table == source) {
			if (ImageDaemon.NETWORK_OFF_CENTER_ON.equals(key) && value instanceof Boolean) {
				ImageHandler.setCollectionActive(((Boolean) value).booleanValue());
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

			// got the message, so replace it with the do nothing request so as not to repeat the request
			table.putNumber(NETWORK_COMMAND, REQUEST_NOTHING);
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
}
