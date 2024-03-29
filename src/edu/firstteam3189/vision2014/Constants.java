package edu.firstteam3189.vision2014;

import static com.googlecode.javacv.cpp.opencv_core.cvScalar;
import team3189.library.Logger.Logger;

import com.googlecode.javacv.cpp.opencv_core.CvScalar;

import edu.firstteam3189.vision2014.vision.Utility;

public class Constants {
	public static CvScalar base = cvScalar(Constants.BLUE_LOW, Constants.GREEN_LOW, Constants.RED_LOW, 0);
	public static int BLUR = 20;
	public static CvScalar end = cvScalar(Constants.BLUE_HIGH, Constants.GREEN_HIGH, Constants.RED_HIGH, 0);
	public static float GOAL_ONE_RATIO = 1.1f;
	public static float GOAL_THREE_RATIO = 3.1f;
	public static float GOAL_TOLERANCE = .5f;
	public static float GOAL_TWO_RATIO = 2.1f;
	public static String HEADER_FILENAME = "Content-disposition";
	public static String HEADER_SIZE = "Content-length";
	/**
	 * 90 degrees in radians
	 */
	public static double NINE_D = Math.PI / 2;
	public static int RECTANGLE_SAVE_LIMIT = 100;
	public static int SCREEN_HEIGHT = 480 / 2;
	public static int SCREEN_WIDTH = 640 / 2;
	/**
	 * Angle Tolerance in radians
	 */
	public static double TOLERANCE = Math.PI / 70;

	public static int WRITING_PROCESSED_IMAGES = 0;
	private static int BLUE_HIGH = 90;

	private static int BLUE_LOW = 0;
	private static int GREEN_HIGH = 255;

	private static int GREEN_LOW = 120;
	private static final Logger LOGGER = new Logger(Constants.class);

	private static int RED_HIGH = 40;
	private static int RED_LOW = 0;

	public static void loadConstants() {
		RED_LOW = Utility.getIntFromProperty("redLow", RED_LOW);
		RED_HIGH = Utility.getIntFromProperty("redHigh", RED_HIGH);
		GREEN_LOW = Utility.getIntFromProperty("greenLow", GREEN_LOW);
		GREEN_HIGH = Utility.getIntFromProperty("greenHigh", GREEN_HIGH);
		BLUE_LOW = Utility.getIntFromProperty("blueLow", BLUE_LOW);
		BLUE_HIGH = Utility.getIntFromProperty("blueHigh", BLUE_HIGH);

		base = cvScalar(Constants.BLUE_LOW, Constants.GREEN_LOW, Constants.RED_LOW, 0);
		end = cvScalar(Constants.BLUE_HIGH, Constants.GREEN_HIGH, Constants.RED_HIGH, 0);

		TOLERANCE = Utility.getDoubleFromProperty("tolerance", TOLERANCE);
		BLUR = Utility.getIntFromProperty("blur", BLUR);
		RECTANGLE_SAVE_LIMIT = Utility.getIntFromProperty("rectangle.save.limit", RECTANGLE_SAVE_LIMIT);
		WRITING_PROCESSED_IMAGES = Utility.getIntFromProperty("writing.processed.images", WRITING_PROCESSED_IMAGES);

		LOGGER.info("RedLow: " + RED_LOW);
		LOGGER.info("blueLow: " + BLUE_LOW);
		LOGGER.info("greenLow: " + GREEN_LOW);
		LOGGER.info("redHigh: " + RED_HIGH);
		LOGGER.info("blueHigh: " + BLUE_HIGH);
		LOGGER.info("greenHigh: " + GREEN_HIGH);
		LOGGER.info("blur: " + BLUR);
		LOGGER.info("tolerance: " + TOLERANCE);
	}
}
