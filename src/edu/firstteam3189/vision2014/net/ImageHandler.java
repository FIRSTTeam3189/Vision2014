package edu.firstteam3189.vision2014.net;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import team3189.library.Logger.Logger;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class ImageHandler implements HttpHandler {
	private static final String HEADER_FILENAME = "Content-disposition";
	private static final Logger LOGGER = new Logger(ImageHandler.class);

	private File imageDirectory;

	/** This member holds the list of images to be processed. */
	private BlockingQueue<File> queueImages;

	/**
	 * This method constructs a new instance, initializing storage folder based on the current date.
	 * 
	 * @param queueImages
	 */
	public ImageHandler(BlockingQueue<File> queueImages) {
		this.queueImages = queueImages;
		Date now = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
		imageDirectory = new File("images", formatter.format(now));
		imageDirectory.mkdirs();
		LOGGER.info("Storing images in " + imageDirectory.getAbsolutePath());
	}

	/**
	 * This method processes the HTTP requests.
	 */
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		String requestMethod = exchange.getRequestMethod();
		// LOGGER.info("Image server getting a request of type " + requestMethod);

		try {
			if ("GET".equals(requestMethod)) {
				// close the exchange and redirect to post
				close(exchange, "Please POST to this site!");
			} else if ("POST".equals(requestMethod)) {
				// save off the image
				Headers headers = exchange.getRequestHeaders();
				List<String> fullFileNames = headers.get(HEADER_FILENAME);

				if (fullFileNames != null && !fullFileNames.isEmpty()) {
					// grab the filename
					String fullFileName = fullFileNames.get(0);
					String fileName = fullFileName.substring(fullFileName.indexOf('"') + 1,
							fullFileName.lastIndexOf('"'));
					File imageFile = new File(imageDirectory, fileName);
					LOGGER.info("Creating an image file of name: " + imageFile + " on thread "
							+ Thread.currentThread().getName());

					// write the image to a file
					InputStream body = exchange.getRequestBody();
					copy(body, imageFile);

					queueImages.put(imageFile);
				}

				close(exchange, "Finished processing.");
			} else if ("HEAD".equals(requestMethod)) {
				// just respond with an ok
				close(exchange);
			}
		} catch (InterruptedException e) {
			LOGGER.error("Error adding image to queue.", e);
		} finally {
			// will always want to close the exchange
			exchange.close();
		}
	}

	/**
	 * This method close the exchange with a success.
	 * 
	 * @param exchange
	 *            HttpExchange containing the interaction.
	 * @throws IOException
	 */
	private void close(HttpExchange exchange) throws IOException {
		close(exchange, "");
	}

	/**
	 * This method writes the message to the response and closes everything.
	 * 
	 * @param exchange
	 *            HttpExchange containing the interaction.
	 * @param message
	 *            String containing the message.
	 * @throws IOException
	 */
	private void close(HttpExchange exchange, String message) throws IOException {
		exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, message.length());

		if (message.length() > 0) {
			// add the message to the body
			OutputStreamWriter out = new OutputStreamWriter(exchange.getResponseBody());

			out.write(message);

			out.close();
		}
	}

	/**
	 * This method produces the named file the the body contents.
	 * 
	 * @param body
	 *            InputStream containing the content of the image.
	 * @param imageFile
	 *            File containing the file to write.
	 * @throws IOException
	 */
	private void copy(InputStream body, File imageFile) throws IOException {
		copy(body, new FileOutputStream(imageFile));
	}

	private void copy(InputStream inputStream, OutputStream outputStream) throws IOException {
		byte[] buffer = new byte[1024];
		int byteRead;

		try {
			while ((byteRead = inputStream.read(buffer)) > 0) {
				outputStream.write(buffer, 0, byteRead);
			}
		} finally {
			inputStream.close();
			outputStream.close();
		}
	}
}
