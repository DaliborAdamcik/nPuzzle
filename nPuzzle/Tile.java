/**
 * Author: Dalibor Adamèík
 */
package nPuzzle;

public class Tile implements java.io.Serializable {
	private static final long serialVersionUID = -4486137353698498985L;
	private final int value;

	/**
	 * Creates Tile with specified value raises {@link TileValueExc}
	 * 
	 * @param value
	 *            must be intger higher as zero (i>0)
	 */
	public Tile(int value) {
		super();

		if (value < 1)
			throw new nPuzzle.excepts.TileValueExc("An tile value cannot be less than 1");

		this.value = value;
	}

	/**
	 * Gets tile value setted on construct
	 * 
	 * @return (int) tile value
	 */
	public int getValue() {
		return value;
	}
}
