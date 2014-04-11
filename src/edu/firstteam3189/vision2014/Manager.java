package edu.firstteam3189.vision2014;

import java.io.IOException;

import edu.firstteam3189.vision2014.net.ServerDaemon;
import edu.firstteam3189.vision2014.vision.ImageDaemon;

public class Manager extends Thread {
	/**
	 * The Image Daemon for the camera
	 */
	private static ImageDaemon camera;

	/**
	 * The Threaded Server Daemon that takes commands from the robot
	 */
	private static ServerDaemon server;

	public static int getHotzones() {
		return camera.getLastProcess();
	}

	public Manager() {
		camera = new ImageDaemon();
		try {
			server = new ServerDaemon();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		camera.start();
		if (server != null) {
			server.start();
		}
		while (true) {
			if (!camera.isAlive()) {
				camera = new ImageDaemon();
				camera.start();
			}
			try {
				sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
