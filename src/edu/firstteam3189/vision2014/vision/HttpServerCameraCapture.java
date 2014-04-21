package edu.firstteam3189.vision2014.vision;

import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvSaveImage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import team3189.library.Logger.Logger;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

import edu.firstteam3189.vision2014.HttpImageServer;

/**
 * This class is used to capture images being broadcast by the camera.
 */
class HttpServerCameraCapture extends CaptureBase {
	private static final String FILE_SUFFIX = "p";
	private static final Logger LOGGER = new Logger(HttpServerCameraCapture.class);

	/** This member holds the file information of the last image returned. */
	private File fileImage;

	/** This member is the server that receives images from the camera. */
	private HttpImageServer httpImageServer;

	/** This member holds the list of images to be processed. */
	private BlockingQueue<File> queueImages = new ArrayBlockingQueue<>(20);

	@Override
	public void close() {
		httpImageServer.close();
	}

	/**
	 * This method is called once the processing is complete.
	 * 
	 * @param imageProcessor
	 *            ImageProcessor that contains the processed image.
	 */
	@Override
	public void done(ImageProcessor imageProcessor) {
		// write out the processed file
		IplImage processed = imageProcessor.getProcessed();
		String strProcessed = getProcessedName(fileImage.getName());
		File fileProcessed = new File(fileImage.getParentFile(), strProcessed);
		cvSaveImage(fileProcessed.getAbsolutePath(), processed);

		// write out the details of the found rectangles
		writeDetails(imageProcessor);

		super.done(imageProcessor);
	}

	@Override
	public IplImage getImage() throws Exception {
		fileImage = queueImages.take();

		LOGGER.info("Processing image file: " + fileImage);

		image = cvLoadImage(fileImage.getAbsolutePath());

		return super.getImage();
	}

	@Override
	public void init() {
		httpImageServer = new HttpImageServer(queueImages);
	}

	/**
	 * This method returns the passed in file name with the extension changed to the passed in extension.
	 * 
	 * @param fileName
	 *            String containing the original file name.
	 * @param extension
	 *            String containing the new extension.
	 */
	private String changeExtension(String fileName, String extension) {
		int index = fileName.lastIndexOf('.');
		if (index >= 0) {
			fileName = fileName.substring(0, index);
		}
		fileName = fileName + extension;

		return fileName;
	}

	/**
	 * This method takes the passed in filename and adds a suffix right before the extension.
	 * 
	 * @param fileName
	 *            String containing the original filename.
	 */
	private String getProcessedName(String fileName) {
		int index = fileName.lastIndexOf('.');
		if (index > 0) {
			fileName = fileName.substring(0, index) + FILE_SUFFIX + fileName.substring(index);
		}

		return fileName;
	}

	/**
	 * This method writes out the details of the processed file.
	 * 
	 * @param imageProcessor
	 *            ImageProcessor that contains the processed image.
	 */
	private void writeDetails(ImageProcessor imageProcessor) {
		// write out the processed file
		IplImage processed = imageProcessor.getProcessed();
		String strProcessed = getProcessedName(fileImage.getName());
		File fileProcessed = new File(fileImage.getParentFile(), strProcessed);
		cvSaveImage(fileProcessed.getAbsolutePath(), processed);

		String strProcessedDetails = changeExtension(strProcessed, ".txt");

		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter(new File(fileImage.getParentFile(), strProcessedDetails)));

			out.write("Image: ");
			out.write(fileImage.toString());
			out.newLine();

			List<ContourDetail> details = imageProcessor.getRectangles();
			int index = 0;
			for (ContourDetail detail : details) {
				out.write("Contour ");
				out.write(Integer.toString(index++));
				out.newLine();

				out.write(detail.toString());

				Iterator<Point> iterPoints = detail.getPoints();
				if (iterPoints != null && iterPoints.hasNext()) {
					out.write("Points: ");

					while (iterPoints.hasNext()) {
						Point point = iterPoints.next();
						out.write(point.toString());

						if (iterPoints.hasNext()) {
							out.write(" ");
						}
					}
				}
				out.newLine();
			}
		} catch (IOException e) {
			LOGGER.error("Error writing details.", e);
		} finally {
			if (out != null) {
				try {
					out.flush();
					out.close();
				} catch (IOException e) {
					LOGGER.error("Illogical closing error writing details.", e);
				}
			}
		}
	}
}