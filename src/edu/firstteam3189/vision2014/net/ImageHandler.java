package edu.firstteam3189.vision2014.net;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.util.List;

import team3189.library.Logger.Logger;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import edu.firstteam3189.vision2014.Constants;

/**
 * Unused!
 */
public class ImageHandler implements HttpHandler{
	private static final Logger LOGGER = new Logger(ImageHandler.class);
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		String requestMethod = exchange.getRequestMethod();
		
		try{
			if("GET".equals(requestMethod)){
				// Close it saying that we dont do GET
				close(exchange, "Please POST to this site!");
			} else if ("POST".equals(requestMethod)){
				// Lets process this request
				Headers headers = exchange.getRequestHeaders();
				List<String> fullFileNames = headers.get(Constants.HEADER_FILENAME);
				
				if (fullFileNames != null && !fullFileNames.isEmpty()){
					// Lets grab the filename of the PIC
					String fullFileName = fullFileNames.get(0);
					String fileName = fullFileName.substring(fullFileName.indexOf('"') + 1,
                            fullFileName.lastIndexOf('"'));
					File imageFile = new File(fileName);
					LOGGER.info("Creating an image file of name: " + fileName + "on thread " + Thread.currentThread().getName());
					
					// Write the image to a file
					InputStream body = exchange.getRequestBody();
					copy(body, imageFile);
					
					// TODO Process Image
				}
				
				close(exchange, "Finished Processing");
				
			}
		}finally{
			// We will always want to close the exchange
			exchange.close();
		}
	}

	
	private void close(HttpExchange exchange, String message) throws IOException{
		exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, message.length());
		
		OutputStreamWriter out = new OutputStreamWriter(exchange.getResponseBody());
		
		out.write(message);
		
		out.close();
	}
	
	/**
     * This method copies the input stream to the named file.
     * 
     * @throws IOException
     */
    private void copy(InputStream inputStream, File fileName) throws IOException {
            copy(inputStream, new FileOutputStream(fileName));
    }

    /**
     * This method copies the input stream to the output stream.
     * 
     * @throws IOException
     */
    private void copy(InputStream inputStream, OutputStream outputStream) throws IOException {
            byte[] buffer = new byte[2048];
            int bytesRead;
            try {
                    while ((bytesRead = inputStream.read(buffer)) > 0) {
                            outputStream.write(buffer, 0, bytesRead);
                    }
            } finally {
                    inputStream.close();
                    outputStream.close();
            }
    }
}
