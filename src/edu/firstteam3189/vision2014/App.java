package edu.firstteam3189.vision2014;

import java.io.File;
import java.io.IOException;

import team3189.library.Logger.Logger;

import edu.firstteam3189.vision2014.net.ServerDaemon;
import edu.firstteam3189.vision2014.vision.ImageDaemon;

public class App {
	
	/**
	 * The Image Daemon for the camera
	 */
	private ImageDaemon camera;
	
	/**
	 * The Logger for the App class
	 */
	public static final Logger LOGGER = new Logger(App.class);
	
	/**
	 * Is the server still running?
	 */
	private boolean running = true;
	/**
	 * The Threaded Server Daemon that takes commands from the robot
	 */
	private ServerDaemon server;
	
	public App(){
		// load the Constants and start the Server Daemon
		Constants.loadConstants();
		camera = new ImageDaemon();
		try {
			server = new ServerDaemon();
		} catch (IOException e) {
			e.printStackTrace();
		}
		run();
	}
	
	public void run () {
		camera.start();
		if (server != null) {
			server.start();
		}
	}
	
	public int getHotzones(){
		return camera.getLastProcess();
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
