package edu.firstteam3189.vision2014.vision;

import static com.googlecode.javacv.cpp.opencv_core.cvReleaseImage;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

/**
 * This base class is used to for both active camera capture and passive camera capture.
 */
abstract class CaptureBase {
	/** This member holds the last image returned. */
	protected IplImage image;

	public void close() {
	}

	/**
	 * This method is called once the processing is complete.
	 * 
	 * @param imageProcessor
	 *            ImageProcessor that contains the processed image.
	 */
	public void done(ImageProcessor imageProcessor) {
		cvReleaseImage(image);

		image = null;
	}

	/**
	 * This method gets the next available image to process.
	 * 
	 * @throws Exception
	 */
	public IplImage getImage() throws Exception {
		return image;
	}

	/**
	 * This method is used to initialize the camera capture process.
	 * 
	 * @throws Exception
	 */
	public void init() throws Exception {
	}
}
