package nPuzzle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleInput {
	private BufferedReader input = new BufferedReader(new InputStreamReader(System.in)); // input
																							// reader
																							// /
																							// coppied
																							// from
																							// minesweeper
	private boolean dbgMode;

	public ConsoleInput(boolean dbgmode) {
		super();
		this.dbgMode = dbgmode;
	}

	/**
	 * an interface to test acceptable range of integer for function readInt
	 */
	public interface IrdIntTest {
		public boolean AcceptValue(int val);
	}

	/**
	 * an interface to test string from input
	 */
	public interface IrdStrTest {
		public boolean AcceptValue(String val);
	}

	/**
	 * Reads line of text from the reader. (method from minesweeper)
	 * 
	 * @return line as a string
	 */
	private String readLine() {
		try {
			return input.readLine();
		} catch (IOException e) {
			System.err.println("Cannot read from console: " + e.getMessage());
			return null;
		}
	}

	private void printMsg(String message) {
		System.out.printf(">%s: ", message);
	}

	private void prinErr(String message) {
		if (dbgMode)
			System.err.printf("**\tCslError: %s\n", message);
	}

	/**
	 * Read integer from console input On conversion error (input string is nod
	 * valid numeric characters) returns min(int)
	 * 
	 * @return Entered integer in console, or error value (described up)
	 */

	private int readIntCsl() {
		String src = readLine(); // mozno osetrit null
		try {
			return Integer.parseInt(src);
		} catch (Exception e) {
			prinErr("readInt " + e.getMessage());
			return Integer.MIN_VALUE;
		}
	}

	public int readInt(String message, IrdIntTest test) {
		int retval;
		do {
			printMsg(message);
			retval = readIntCsl();
		} while (retval == Integer.MIN_VALUE || !test.AcceptValue(retval));
		return retval;
	}

	public String readStr(String message, IrdStrTest test) {
		String retval;
		do {
			printMsg(message);
			retval = readLine();
		} while (retval == null || !test.AcceptValue(retval));
		return retval;
	}

}
