package edu.firstteam3189.vision2014;

import java.io.File;

import team3189.library.Logger.Logger;

public class App {
	/**
	 * The Logger for the App class
	 */
	public static final Logger LOGGER = new Logger(App.class);

	/**
	 * This method removes the passed in file.
	 * 
	 * @param file
	 *            File to be removed.
	 */
	public static void remove(File file) {
		if (file.delete()) {
			LOGGER.debug("Deleted old file: " + file);
		} else {
			LOGGER.debug("Unable to delete old file: " + file);
		}
	}

	private Manager manager;

	/**
	 * Is the server still running?
	 */
	private boolean running = true;

	public App() {
		// load the Constants and start the Server Daemon
		Constants.loadConstants();
		manager = new Manager();
		manager.start();
	}

	public boolean isRunning() {
		return running;
	}

	public void kill() {
		running = false;
	}
}
