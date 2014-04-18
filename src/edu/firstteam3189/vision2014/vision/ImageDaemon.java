package edu.firstteam3189.vision2014.vision;

import java.awt.Dimension;

import javax.swing.JFrame;

import team3189.library.Logger.Logger;

import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class ImageDaemon extends Thread {
	/**
	 * The last processed results from the camera
	 */
	private static int lastProcess = 0;

	private static final Logger LOGGER = new Logger(ImageDaemon.class);

	/** This member holds a configuration element used to indicate if camera capture is active or passive. */
	private boolean active = false;

	/** This member holds the camera being used to capture the images. */
	private CaptureBase camera;

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

					debug.setTitle("Original Image is ready");
					debug.showImage(image);

					canvas.setTitle("Processed Image is ready");
					canvas.showImage(imageProcessor.getProcessed());

					camera.done(imageProcessor);
				} else {
					LOGGER.info("Image is null!");
				}

				lastProcess = size;
			}
		} catch (Exception e) {
			LOGGER.error("Error capturing image.", e);
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
