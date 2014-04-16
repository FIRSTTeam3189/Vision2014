package edu.firstteam3189.vision2014.vision;

import static com.googlecode.javacv.cpp.opencv_core.cvReleaseImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;

import java.awt.Dimension;
import java.io.File;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.swing.JFrame;

import team3189.library.Logger.Logger;

import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.FFmpegFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

import edu.firstteam3189.vision2014.HttpImageServer;

public class ImageDaemon extends Thread {
	/**
	 * This interface is used to abstract between active camera capture and passive camera capture.
	 */
	private interface Camera {
		void close();

		/**
		 * This method gets the next available image to process.
		 * 
		 * @throws Exception
		 */
		IplImage getImage() throws Exception;

		/**
		 * This method is used to initialize the camera capture process.
		 * 
		 * @throws Exception
		 */
		void init() throws Exception;
	}

	/**
	 * This class is used to determine what type of camera capture is happening.
	 */
	private class CameraCapture implements Camera {
		/** The FFMpegFrameGrabber, Grabs the images from the camera. */
		private FFmpegFrameGrabber grabber;

		public CameraCapture() {
			grabber = new FFmpegFrameGrabber("http://10.31.89.11/mjpg/video.mjpg");
		}

		@Override
		public void close() {
		}

		@Override
		public IplImage getImage() throws Exception {
			return grabber.grab();
		}

		/**
		 * This method is used to initialize the camera capture process.
		 * 
		 * @throws Exception
		 */
		@Override
		public void init() throws Exception {
			grabber.start();
		}
	}

	/**
	 * This class is used to capture images being broadcast by the camera.
	 */
	private class HttpServerCameraCapture implements Camera {
		/** This member is the server that receives images from the camera. */
		private HttpImageServer httpImageServer;

		/** This member holds the list of images to be processed. */
		private BlockingQueue<File> queueImages = new ArrayBlockingQueue<>(20);

		@Override
		public void close() {
			httpImageServer.close();
		}

		@Override
		public IplImage getImage() throws Exception {
			File file = queueImages.take();

			LOGGER.info("Processing image file: " + file);

			return cvLoadImage(file.getAbsolutePath());
		}

		@Override
		public void init() {
			httpImageServer = new HttpImageServer(queueImages);
		}
	}

	/**
	 * The Logger class for the ImageDaemon
	 */
	public static final Logger logger = new Logger(ImageDaemon.class);

	/**
	 * The last processed results from the camera
	 */
	private static int lastProcess = 0;

	private static final Logger LOGGER = new Logger(ImageDaemon.class);

	/** This member holds a configuration element used to indicate if camera capture is active or passive. */
	private boolean active = false;

	/** This member holds the camera being used to capture the images. */
	private Camera camera;

	/**
	 * The Canvas Frame showing the processed contours
	 */
	private CanvasFrame canvas;

	/**
	 * The Canvas Frame showing the raw image from the camera
	 */
	private CanvasFrame debug;

	/**
	 * The Minimum dimensions of the canvas frames
	 */
	private Dimension minDim = new Dimension(640, 480);

	public ImageDaemon() {
		super("Image Daemon");

		canvas = new CanvasFrame("title");
		canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		canvas.setMinimumSize(minDim);
		canvas.setTitle("Loading....");

		debug = new CanvasFrame("title");
		debug.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		debug.setMinimumSize(minDim);
		debug.setTitle("Loading....");
	}

	/**
	 * Returns the amount of contours found on the last process
	 * 
	 * @return
	 */
	public int getLastProcess() {
		return lastProcess;
	}

	public int processImage() {
		int size = 0;
		try {
			camera.init();
			while (true) {
				IplImage image = camera.getImage();
				if (image != null) {
					ImageProcessor imageProcessor = new ImageProcessor(image);

					// Flip the image and process it. Then show frames on canvases.
					// cvFlip(image, image, 1);

					debug.setTitle("Camera is ready");
					debug.showImage(image);

					canvas.setTitle("Camera is ready");
					canvas.showImage(imageProcessor.getProcessed());

					cvReleaseImage(image);
				} else {
					logger.info("Image is null!");
				}

				lastProcess = size;
			}
		} catch (Exception e) {
			logger.error("Error capturing image.", e);
		}
		return size;
	}

	/**
	 * Processes images from the camera
	 */
	@Override
	public void run() {
		// allocate the image capture processes
		if (active) {
			camera = new CameraCapture();
		} else {
			camera = new HttpServerCameraCapture();
		}

		// process the images
		processImage();

		// close the windows that were presumably opened
		camera.close();
		canvas.dispose();
		debug.dispose();
	}

	/**
	 * Sets the amount of contours found on the last process.
	 * 
	 * @param lastProcess
	 */
	public void setLastProcess(int lastProcess) {
		ImageDaemon.lastProcess = lastProcess;
	}
}
