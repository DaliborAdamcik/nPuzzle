package nPuzzle;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;

/**
 * 
 * @author Dalibor Adamèík
 * @version 1.0 21.6.2016
 *
 *          NPuzzle game field engine :)
 */
public class NPuzzle implements java.io.Serializable {

	private static final long serialVersionUID = -4957577725789766323L;
	/** Tile[width / x][height / y] an storage for tiles */
	private Tile[][] tiles;
	private boolean isShaked = false;
	private long startTime = 0; // startime timestamp

	/**
	 * Creates field for play a NPuzle game Throws exception
	 * 
	 * @param width
	 * @param height
	 */
	public NPuzzle(int width, int height) {
		super();
		if (width < 4 || height < 4)
			throw new nPuzzle.excepts.SmallPlayField("An game field size cannot be less than 4x4");

		tiles = new Tile[width][height];
		generate();
	}

	/**
	 * Returns tile on specified position Can raise exception "out of bounds"
	 * for invalid coordinates
	 * 
	 * @param x
	 *            position (on 0..width range)
	 * @param y
	 *            position (on 0..height range
	 * @return Tile object on position
	 */
	public Tile getTile(int x, int y) {
		validXYException(x, y);
		return tiles[x][y];
	}

	/**
	 * Sets tile on specified coordinates to value stored in tile Can raise
	 * exception "out of bounds" for invalid coordinates
	 * 
	 * @param x
	 *            position (on 0..width range)
	 * @param y
	 *            position (on 0..height range
	 * @param tile
	 *            an Tile oject or null for hole
	 */
	private void SetTile(int x, int y, Tile tile) {
		validXYException(x, y);
		tiles[x][y] = tile;
	}

	/**
	 * Generates exception out of bounds for non ranged X and Y
	 * 
	 * @param x
	 *            index in field
	 * @param y
	 *            index in field
	 */
	private void validXYException(int x, int y) {
		// if(x<0 || y<0 || x>= getWidth() || y>= getHeight())
		// throw new nPuzzle.excepts.OutOfBounds("Index out of bounds");
		if (x < 0 || x >= getWidth())
			throw new nPuzzle.excepts.OutOfBounds("Index 'x=" + x + "' out of bounds");

		if (y < 0 || y >= getHeight())
			throw new nPuzzle.excepts.OutOfBounds("Index 'y=" + y + "' out of bounds");
	}

	/**
	 * Gets width of field
	 * 
	 * @return width of field
	 */
	public int getWidth() {
		return tiles.length;
	}

	/**
	 * Gets height of field
	 * 
	 * @return height of field
	 */
	public int getHeight() {
		return tiles[0].length;
	}

	/**
	 * Generates field with tiles (numbers)
	 */
	private void generate() {
		int numTiles = 0;
		int maxTiles = getHeight() * getWidth() - 1;

		for (int y = 0; y < getHeight(); y++) // outer interate for height
			for (int x = 0; x < getWidth(); x++) {
				SetTile(x, y, new Tile(numTiles + 1));

				if (++numTiles == maxTiles)
					break;
			}
	}

	/**
	 * Shakes tiles to "random" order (shaked field must by able to solve) Carry
	 * about isShaked flag.
	 */
	public void shake() // OK
	{
		// Theory:
		// 1. shake can be also implemented by "changing numbers on
		// pre-generated tiles" (bad method)
		// 2. but we do shake with objects - exchange (also bad method)
		// 3. this can be also implemented by moving tiles to random sides..
		// like and OK :)

		Random rnd = new Random();

		// W x H x (4 sides on tile) x (5 more times for unreachable move of
		// hole (out of bounds))
		int shakemax = getHeight() * getWidth() * 4 * 5; // shake size to reach,
															// we can add random
															// here to get more
															// fun.
		int shaked = 0; // number of actually sahked obnejcts

		while (shaked++ < shakemax) {
			LongKeys key = LongKeys.values()[LongKeys.UP.ordinal() + rnd.nextInt(4)];
			moveEmptyTile(key);
		}
		isShaked = true;
		// we started to play game now (measure time from this moment)
		startTime = System.currentTimeMillis();
	}

	public String playTimeFmt() {
		if (!isShaked)
			return "<?>"; // we are not shake field, we not play a game

		return String.format("%d s", (System.currentTimeMillis() - startTime) / 1000);
	}

	/**
	 * Checks for game is solved
	 * 
	 * @return true for solved game, false for unsolved game
	 */
	public boolean isSolved() {
		int cnt = 1;
		for (int y = 0; y < getHeight(); y++) // outer interate for height
			for (int x = 0; x < getWidth(); x++) {
				Tile ti = getTile(x, y);

				if (ti == null)
					continue;

				if (ti.getValue() != cnt++)
					break;
			}
		return cnt == getHeight() * getWidth();
	}

	/**
	 * Moves hole into specified direction (UP, DOWN, LEFT, RIGHT) Other
	 * LongKeys casts are unimplemented
	 * 
	 * @param dir
	 *            direction
	 */
	public void moveEmptyTile(LongKeys dir) // OK
	{
		nPuzzle.Point ptOld = emptyTile();
		nPuzzle.Point pt = ptOld.copy();

		switch (dir) {
		case LEFT:
			pt.deltaX(-1);
			break;
		case RIGHT:
			pt.deltaX(1);
			break;
		case DOWN:
			pt.deltaY(1);
			break;
		case UP:
			pt.deltaY(-1);
			break;
		default:
			return;
		}
		doMoveET(ptOld, pt);
	}

	/**
	 * Moves hole from old position to new position method checks positions for
	 * out of bounds (no exception only return)
	 * 
	 * @param oldPos
	 *            old position where is hole
	 * @param newPos
	 *            new position for hole
	 */
	private void doMoveET(nPuzzle.Point oldPos, nPuzzle.Point newPos) // OK
	{
		if (!pointInRange(newPos) || !pointInRange(oldPos))
			return;
		// TODO we can check there an old position is empty (hole / null)

		SetTile(oldPos.getX(), oldPos.getY(), getTile(newPos.getX(), newPos.getY()));
		SetTile(newPos.getX(), newPos.getY(), null);
	}

	/**
	 * Verifies point coordinates are in bounds (0 >= x < Width | 0>= y <
	 * Height)
	 * 
	 * @param pt
	 *            An Point object
	 * @return true = Point in bounds, false = Point out of bounds
	 */
	private boolean pointInRange(nPuzzle.Point pt) {
		return pt.getX() >= 0 && pt.getY() >= 0 && pt.getX() < this.getWidth() && pt.getY() < this.getHeight();
	}

	/**
	 * Search for empty place (hole) in field.
	 * 
	 * @return Hole coordinates (Point)
	 */
	private nPuzzle.Point emptyTile() {
		for (int y = 0; y < getHeight(); y++)
			for (int x = 0; x < getWidth(); x++) {
				if (getTile(x, y) == null) {
					nPuzzle.Point pt = new Point(x, y);
					return pt;
				}
			}
		throw new RuntimeException("Empty tile not found"); // this code may be
															// unreachable (in
															// normal
															// conditions)
	}

	/**
	 * isShaked controls calling of function shake
	 * 
	 * @return true = called method shake, false = sorted by default (as
	 *         solution)
	 */
	public boolean isShaked() {
		return isShaked;
	}

	/**
	 * Saves current state of game into a file
	 */
	public void save() {
		try {
			String fileName = fileName();

			FileOutputStream strm = new FileOutputStream(fileName);
			ObjectOutputStream os = new ObjectOutputStream(strm);
			try {
				long besave = startTime;
				startTime = System.currentTimeMillis() - startTime; // save
																	// delta of
																	// started
																	// game
				os.writeObject(this);
				startTime = besave;
				System.out.println("+ Succesfully saved into: " + fileName()); // TODO
			} finally {
				strm.close();
				os.close();
			}
		} catch (Exception e) {
			System.err.println("** Cant save game: " + e.getMessage());
			// e.printStackTrace();
		}
	}

	/**
	 * Static method used to load game
	 * 
	 * @return on sucess instance of game, otherwise null
	 */
	public static NPuzzle load() {
		try {
			String fileName = fileName();

			File fil = new File(fileName);

			if (!fil.exists())
				return null;

			FileInputStream strm = new FileInputStream(fileName);
			ObjectInputStream os = new ObjectInputStream(strm);
			try {
				NPuzzle lo = (NPuzzle) os.readObject();
				System.out.println("+ Succesfully loaded from: " + fileName()); // TODO
				// correct play time after load
				if (lo.isShaked) {
					lo.startTime = System.currentTimeMillis() - lo.startTime;
				} else
					lo.startTime = 0;

				return lo;
			} finally {
				strm.close();
				os.close();
			}
		} catch (Exception e) {
			// e.printStackTrace();
			System.err.println("** Cant load game: " + e.getMessage());
			return null;
		}
	}

	/**
	 * Path to file to save game
	 * 
	 * @return Path + file name to save current game
	 */
	private static String fileName() {
		try {
			return new File(".").getCanonicalPath() + System.getProperty("file.separator") + "npuzzle.sav";
		} catch (Exception e) {
			return System.getProperty("user.home") + System.getProperty("file.separator") + "npuzzle.sav"; // system
																											// getproperty
																											// must
																											// always
																											// return
																											// path
		}
	}

}
