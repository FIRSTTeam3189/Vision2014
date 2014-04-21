package edu.firstteam3189.vision2014.net;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

/**
 * This class provides access to the network table. It make sure it is initialized only once.
 */
public class NetworkTableAccess {
	private static NetworkTableAccess instance;
	private static final String ROBOT_ADDRESS = "10.31.89.2";

	static {
		instance = new NetworkTableAccess();
	}

	/**
	 * This method returns the singleton instance of this class.
	 */
	public static NetworkTableAccess getInstance() {
		return instance;
	}

	/**
	 * This method constructs a new network table access and initializes it to a client.
	 */
	private NetworkTableAccess() {
		NetworkTable.setClientMode();
		NetworkTable.setIPAddress(ROBOT_ADDRESS);
	}

	/**
	 * This method returns the named access point.
	 */
	public NetworkTable getTable(String name) {
		return NetworkTable.getTable(name);
	}
}
