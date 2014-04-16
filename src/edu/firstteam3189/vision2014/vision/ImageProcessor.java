package edu.firstteam3189.vision2014.vision;

import static com.googlecode.javacpp.Loader.sizeof;
import static com.googlecode.javacv.cpp.opencv_core.CV_RGB;
import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateImage;
import static com.googlecode.javacv.cpp.opencv_core.cvDrawContours;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSeqElem;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSize;
import static com.googlecode.javacv.cpp.opencv_core.cvInRangeS;
import static com.googlecode.javacv.cpp.opencv_core.cvPoint;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BLUR;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_CHAIN_APPROX_SIMPLE;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_POLY_APPROX_DP;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_RETR_CCOMP;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvApproxPoly;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvFindContours;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvSmooth;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import team3189.library.Logger.Logger;

import com.googlecode.javacv.cpp.opencv_core.CvContour;
import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

import edu.firstteam3189.vision2014.Constants;

/**
 * This class is used to process images.
 */
public class ImageProcessor {
	/**
	 * The Logger class for the ImageDaemon
	 */
	public static Logger logger = new Logger(ImageProcessor.class);

	/**
	 * The Draw Color for the contour drawing
	 */
	private static final CvScalar color = CV_RGB(69, 42, 69);

	/** This member holds the processed image. */
	private IplImage processedImage;

	/**
	 * This method constructs a new processor for the given image.
	 * 
	 * @param image
	 *            IplImage to be processed.
	 */
	public ImageProcessor(IplImage image) {
		processedImage = removeColor(image, Constants.base, Constants.end);

		findContours(processedImage);
	}

	/**
	 * This method returns the processed image.
	 */
	public IplImage getProcessed() {
		return processedImage;
	}

	private List<List<Point>> findContours(IplImage image) {
		CvMemStorage mem = CvMemStorage.create();
		CvSeq contours = new CvSeq();
		List<List<Point>> rects = new ArrayList<List<Point>>();
		try {
			cvSmooth(image, image, CV_BLUR, Constants.BLUR);

			cvFindContours(image, mem, contours, sizeof(CvContour.class), CV_RETR_CCOMP, CV_CHAIN_APPROX_SIMPLE,
					cvPoint(0, 0));

			if (!contours.isNull()) {
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
	 * Does an RGB Threshold of the image
	 * 
	 * @param img
	 *            The image to threshold
	 * @param base
	 *            The Start of the color range
	 * @param end
	 *            The End of the color range
	 * @return The Thresholded image
	 */
	private IplImage removeColor(IplImage img, CvScalar base, CvScalar end) {
		IplImage data = cvCreateImage(cvGetSize(img), IPL_DEPTH_8U, 1);
		cvInRangeS(img, base, end, data);
		return data;
	}
}
