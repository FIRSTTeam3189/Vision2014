package edu.firstteam3189.vision2014.vision;

import com.googlecode.javacv.FFmpegFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

/**
 * This class is used to determine what type of camera capture is happening.
 */
class CameraCapture extends CaptureBase {
	/** The FFMpegFrameGrabber, Grabs the images from the camera. */
	private FFmpegFrameGrabber grabber;

	public CameraCapture() {
		grabber = new FFmpegFrameGrabber("http://10.31.89.11/mjpg/video.mjpg");
	}

	@Override
	public IplImage getImage() throws Exception {
		image = grabber.grab();

		return super.getImage();
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