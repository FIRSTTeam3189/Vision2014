package edu.firstteam3189.vision2014.vision;

import java.awt.Dimension;
import java.util.List;

import javax.swing.JFrame;

import team3189.library.Logger.Logger;

import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class ImageDaemon extends Thread {
//	private static final String Address = "10.31.89.2";
	private static final int IMAGE_WIDTH = 640;
	private static final int IMAGE_WIDTH_HALF = IMAGE_WIDTH / 2;
	/**
	 * The last processed results from the camera
	 */
	private static int lastProcess = 0;
	private static final Logger LOGGER = new Logger(ImageDaemon.class);
	private static final String NETWORK_OFF_CENTER = "offcenter";
	private static final String NETWORK_TABLE = "data";

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
	private Dimension minDim = new Dimension(IMAGE_WIDTH, 480);

	private NetworkTable table;

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

					// feedback of where the center of the image is located
					Double offcenter = getOffCenter(imageProcessor);
					if (offcenter != null) {
						table.putNumber(NETWORK_OFF_CENTER, offcenter.doubleValue());
					}

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
		startNetworkClient();

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

	/**
	 * This method returns a signed value of percentage of off centered.
	 * 
	 * @param imageProcessor
	 *            ImageProcessor containing the latest image.
	 */
	private Double getOffCenter(ImageProcessor imageProcessor) {
		Double percentage = null;

		List<ContourDetail> rectangles = imageProcessor.getRectangles();
		if (rectangles != null && !rectangles.isEmpty()) {
			// only use the first one in the list
			ContourDetail rectangle = rectangles.get(0);
			percentage = Double.valueOf((double) (IMAGE_WIDTH_HALF - rectangle.getCenter().getX()) / (double) IMAGE_WIDTH);
		}

		return percentage;
	}

	private void startNetworkClient() {
		// NetworkTable.setClientMode();
		// NetworkTable.setIPAddress(Address);
		table = NetworkTable.getTable(NETWORK_TABLE);
	}
}
