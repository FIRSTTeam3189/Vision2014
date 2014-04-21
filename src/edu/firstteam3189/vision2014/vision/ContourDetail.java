package edu.firstteam3189.vision2014.vision;

import static com.googlecode.javacv.cpp.opencv_core.cvPoint;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import com.googlecode.javacv.cpp.opencv_core.CvRect;

/**
 * This class holds the details for any given found object.
 */
public class ContourDetail {
	private static final double ACCEPTABLE_TOLERANCE = 2.00;
	private static final double STRIP_RATIO = 4.0 / 32.0;
	private Point boundingLowerRight;
	private Point boundingUpperLeft;
	private Point center;
	private List<Point> contourPoints = new ArrayList<Point>();
	private double sizeRatio;

	public void addContourPoint(Point point) {
		contourPoints.add(point);
	}

	public CvPoint getBoundingLowerRight() {
		return cvPoint(boundingLowerRight.getX(), boundingLowerRight.getY());
	}

	public CvPoint getBoundingUpperLeft() {
		return cvPoint(boundingUpperLeft.getX(), boundingUpperLeft.getY());
	}

	public Point getCenter() {
		return center;
	}

	public Iterator<Point> getPoints() {
		return contourPoints.iterator();
	}

	/**
	 * This method returns if the contour appears to be within the approximate size ratio (looks like a shape of
	 * interest).
	 */
	public boolean isAcceptable() {
		double percentageOfAcceptable = Math.abs(sizeRatio - STRIP_RATIO) / STRIP_RATIO;

		return percentageOfAcceptable < ACCEPTABLE_TOLERANCE;
	}

	/**
	 * This method sets the bounding rectangle for the contour.
	 * 
	 * @param cvBoundingRect
	 *            CvRect containing the bounding rectangle for this contour.
	 */
	public void setBoundingRectangle(CvRect boundingRect) {
		boundingUpperLeft = new Point(boundingRect.x(), boundingRect.y());
		boundingLowerRight = new Point(boundingRect.x() + boundingRect.width(), boundingRect.y()
				+ boundingRect.height());
		sizeRatio = (double) boundingRect.width() / (double) boundingRect.height();
		center = new Point((boundingUpperLeft.getX() + boundingLowerRight.getX()) / 2,
				(boundingUpperLeft.getY() + boundingLowerRight.getY()) / 2);
	}

	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();

		buffer.append("Corners: ").append(boundingUpperLeft).append(' ').append(boundingLowerRight)
				.append(System.lineSeparator());
		buffer.append("Ratio: ").append(boundingLowerRight.getX() - boundingUpperLeft.getX()).append(" / ")
				.append(boundingLowerRight.getY() - boundingUpperLeft.getY()).append(" (").append(sizeRatio)
				.append(")").append(System.lineSeparator());
		buffer.append("Center: ").append(center).append(System.lineSeparator());

		return buffer.toString();
	}
}