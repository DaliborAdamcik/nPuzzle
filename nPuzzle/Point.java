package nPuzzle;

public class Point {
	private int x;
	private int y;

	public Point(int x, int y) {
		super();
		setX(x);
		setY(y);
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void deltaX(int x) {
		this.x += x;
	}

	public void deltaY(int y) {
		this.y += y;
	}

	public Point copy() {
		return new Point(getX(), getY());
	}
}
