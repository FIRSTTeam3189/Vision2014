package edu.firstteam3189.vision2014.vision;

public class Point {
	private int x;
	private int y;

	/**
	 * This method constructs a new point representing the origin.
	 */
	public Point() {
		this(0, 0);
	}

	/**
	 * This method constructs a new point at the given coordinates.
	 * 
	 * @param x
	 *            int representing the horizontal movement.
	 * @param y
	 *            int representing the vertical movement.
	 */
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	@Override
	public String toString() {
		return "(" + Integer.toString(x) + ", " + Integer.toString(y) + ")";
	}
}
