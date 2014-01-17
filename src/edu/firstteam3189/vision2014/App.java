package edu.firstteam3189.vision2014;

import java.io.File;
import java.io.IOException;

import team3189.library.Logger.Logger;

import edu.firstteam3189.vision2014.net.ServerDaemon;
import edu.firstteam3189.vision2014.vision.ImageDaemon;

public class App {
	
	
	
	/**
	 * The Logger for the App class
	 */
	public static final Logger LOGGER = new Logger(App.class);
	
	/**
	 * Is the server still running?
	 */
	private boolean running = true;
	
	private Manager manager;
	
	public App(){
		// load the Constants and start the Server Daemon
		Constants.loadConstants();
		manager = new Manager();
		manager.start();
	}
	
	
	public void kill(){
		running = false;
	}
	
	public boolean isRunning(){
		return running;
	}
	
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
}
