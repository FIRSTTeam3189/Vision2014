package edu.firstteam3189.vision2014.vision;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import team3189.library.Logger.Logger;

import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.FFmpegFrameGrabber;
import com.googlecode.javacv.FrameGrabber.Exception;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

import edu.firstteam3189.vision2014.Constants;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacpp.Loader.sizeof;
import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BLUR;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_CHAIN_APPROX_SIMPLE;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_POLY_APPROX_DP;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_RETR_CCOMP;

public class ImageDaemon extends Thread{
	
	/**
	 * The Logger class for the ImageDaemon
	 */
	public static Logger logger = new Logger(ImageDaemon.class);
	
	/**
	 * The last processed results from the camera
	 */
	private int lastProcess = 0;
	
	/**
	 * The Canvas Frame showing the processed contours
	 */
	CanvasFrame canvas;
	
	/**
	 * The Canvas Frame showing the raw image from the camera
	 */
	CanvasFrame debug;
	
	/**
	 * The Minimum dimensions of the canvas frames
	 */
	Dimension minDim = new Dimension(640, 480);
	
	/**
	 * The FFMpegFrameGrabber, Grabs the images from the camera
	 */
	FFmpegFrameGrabber grabber;
	
	/**
	 * The Draw Color for the contour drawing
	 */
	private static final CvScalar color = CV_RGB(69, 42, 69);
	
	public ImageDaemon(){
		canvas = new CanvasFrame("title");
		canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		canvas.setMinimumSize(minDim);
		canvas.setTitle("Loading....");
		debug = new CanvasFrame("title");
		debug.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		debug.setMinimumSize(minDim);
		debug.setTitle("Loading....");
		grabber = new FFmpegFrameGrabber("http://10.31.89.11/mjpg/video.mjpg");
	}
	
	public int processImage(){
		int size = 0;
		try {
			grabber.start();
			
			while(true){
				
				IplImage image = null;
				image = grabber.grab();
				
				canvas.setSize(grabber.getImageWidth(), grabber.getImageHeight());
				debug.setSize(grabber.getImageWidth(), grabber.getImageHeight());
				
				if (image != null){
					// Flip the image and process it. Then show frames on canvases.
					cvFlip(image, image, 1);
					canvas.setTitle("Camera is ready");
					debug.setTitle("Camera is ready");
					
					debug.showImage(image);
					
					image = removeColor(image, Constants.base, Constants.end);
					
					size = findContours(image).size();
					canvas.showImage(image);
					image.release();
				} else {
					System.out.println("Image is null!");
				}
				
				lastProcess = size;
			}
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return size;
	}
	
	public static List<List<Point>> findContours(IplImage image) {
		CvMemStorage mem = CvMemStorage.create();
		CvSeq contours = new CvSeq();
		List<List<Point>> rects = new ArrayList<List<Point>>();
		try {
			cvSmooth(image, image, CV_BLUR, Constants.BLUR);

			cvFindContours(image, mem, contours, sizeof(CvContour.class), CV_RETR_CCOMP, CV_CHAIN_APPROX_SIMPLE,
					cvPoint(0, 0));

			if(!contours.isNull()){
				for (; contours != null; contours = contours.h_next()) {
					cvDrawContours(image, contours, color, color, -1, 1, 8);
					CvSeq r = cvApproxPoly(contours, sizeof(CvContour.class), mem, CV_POLY_APPROX_DP, 2, 0);
					List<Point> points = new ArrayList<Point>();
					rects.add(points);
					for (int p = 0; p < r.total(); ++p) {
						// create a point from the pointer, because you have to
						CvPoint vPoint = new CvPoint(cvGetSeqElem(r, p));

						// convert to a local point type
						points.add(new Point(vPoint.x(), vPoint.y()));
					}
					r.deallocate();
				}
			}
		} catch (Throwable t) {
			logger.error("Image Processor: Failed...", t);
		}
		mem.release();
		return rects;
	}

	/**
	 * Returns the amount of contours found on the last process
	 * @return
	 */
	public int getLastProcess() {
		return lastProcess;
	}

	/**
	 * Sets the amount of countours found on the last process.
	 * @param lastProcess
	 */
	public void setLastProcess(int lastProcess) {
		this.lastProcess = lastProcess;
	}

	/**
	 * Does an RGB Threshold of the image
	 * @param img The image to threshold
	 * @param base The Start of the color range
	 * @param end The End of the color range
	 * @return The Thresholded image
	 */
	public static IplImage removeColor(IplImage img, CvScalar base, CvScalar end) {
		IplImage data = cvCreateImage(cvGetSize(img), IPL_DEPTH_8U, 1);
		cvInRangeS(img, base, end, data);
		img.release();
		return data;
	}

	/**
	 * Processes images from the camera
	 */
	@Override
	public void run() {
		processImage();
	}
}
