package edu.firstteam3189.vision2014;

import edu.firstteam3189.vision2014.net.ServerDaemon;
import edu.firstteam3189.vision2014.vision.ImageDaemon;

public class Manager extends Thread {
	/**
	 * The Image Daemon for the camera
	 */
	private static ImageDaemon image;

	/**
	 * The Threaded Server Daemon that takes commands from the robot
	 */
	private static ServerDaemon server;

	public static int getHotzones() {
		return 0; // TODO
		// return camera.getLastProcess();
	}

	public Manager() {
		super("Manager");
		image = new ImageDaemon();
		server = new ServerDaemon();
	}

	@Override
	public void run() {
		image.start();
		if (server != null) {
			server.start();
		}
		while (true) {
			if (!image.isAlive()) {
				image = new ImageDaemon();
				image.start();
			}
			try {
				sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
