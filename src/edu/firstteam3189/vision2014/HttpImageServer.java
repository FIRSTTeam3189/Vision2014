package edu.firstteam3189.vision2014;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;

import team3189.library.Logger.Logger;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import edu.firstteam3189.vision2014.net.ImageHandler;

/**
 * This class is used to act as an end point for the camera to dump images to. It starts a simple HTTP server that
 * listens at the root of the server to get images.
 */
public class HttpImageServer {
	private static final String LOCAL_ADDRESS = "10.31.89.5"; // Wireless
	// private static final String LOCAL_ADDRESS = "10.31.89.6"; // Hardwired
	private static final int LOCAL_PORT = 80;
	private static final Logger LOGGER = new Logger(HttpImageServer.class);

	/** This member holds the actual server instance. */
	private HttpServer httpServer;

	/**
	 * This method constructs a new image server.
	 * 
	 * @param queueImages
	 */
	public HttpImageServer(BlockingQueue<File> queueImages) {
		startImageServer(queueImages);
	}

	public void close() {
		httpServer.stop(5);
	}

	/**
	 * This method starts an HTTP server to process images being sent to the server on the root.
	 * 
	 * @param queueImages
	 */
	private void startImageServer(BlockingQueue<File> queueImages) {
		try {
			// determine the address on which the server is going to be listening
			InetAddress inetAddress = InetAddress.getByName(LOCAL_ADDRESS);
			InetSocketAddress address = new InetSocketAddress(inetAddress, LOCAL_PORT);
			LOGGER.info("Listening for images on " + address);

			// allocate the server with a thread pool
			httpServer = HttpServer.create(address, 10);
			httpServer.setExecutor(Executors.newCachedThreadPool());

			// add a handler for the root context (i.e. all traffic)
			HttpHandler imageHandler = new ImageHandler(queueImages);
			httpServer.createContext("/", imageHandler);

			// fire up the server
			httpServer.start();
		} catch (UnknownHostException e) {
			LOGGER.error("Unable to determine local host.", e);
		} catch (IOException e) {
			LOGGER.error("Error starting the server.", e);
		}
	}
}
