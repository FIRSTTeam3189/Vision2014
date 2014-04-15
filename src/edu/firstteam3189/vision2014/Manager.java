package edu.firstteam3189.vision2014;

import edu.firstteam3189.vision2014.net.ServerDaemon;
import edu.firstteam3189.vision2014.vision.ImageDaemon;

public class Manager extends Thread {
	/**
	 * This interface is used to abstract between active camera capture and passive camera capture.
	 */
	private interface Camera {
		/**
		 * This method is used to initialize the camera capture process.
		 */
		void init();

		/**
		 * This method gets called to periodically wake up the camera.
		 */
		void ping();
	}

	/**
	 * This class is used to determine what type of camera capture is happening.
	 */
	private class CameraCapture implements Camera {
		/**
		 * The Image Daemon for the camera
		 */
		private ImageDaemon camera;

		public CameraCapture() {
			camera = new ImageDaemon();
		}

		/**
		 * This method is used to initialize the camera capture process.
		 */
		@Override
		public void init() {
			camera.start();
		}

		/**
		 * This method gets called to periodically wake up the camera.
		 */
		@Override
		public void ping() {
			if (!camera.isAlive()) {
				camera = new ImageDaemon();
				camera.start();
			}
		}
	}

	/**
	 * This class is used to capture images being broadcast by the camera.
	 */
	private class HttpServerCameraCapture implements Camera {
		/** This member is the server that receives images from the camera. */
		private HttpImageServer httpImageServer;

		@Override
		public void init() {
			httpImageServer = new HttpImageServer();
		}

		@Override
		public void ping() {
		}
	}

	/**
	 * The Threaded Server Daemon that takes commands from the robot
	 */
	private static ServerDaemon server;

	public static int getHotzones() {
		return 0; // TODO
		// return camera.getLastProcess();
	}

	/** This member holds a configuration element used to indicate if camera capture is active or passive. */
	private boolean active = false;

	/** This member holds the camera being used to capture the images. */
	private Camera camera;

	public Manager() {
		super("Manager");

		if (active) {
			camera = new CameraCapture();
		} else {
			camera = new HttpServerCameraCapture();
		}
		server = new ServerDaemon();
	}

	@Override
	public void run() {
		camera.init();
		if (server != null) {
			server.start();
		}
		while (true) {
			camera.ping();
			try {
				sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
