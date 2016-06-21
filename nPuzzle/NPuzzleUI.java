package nPuzzle;

public class NPuzzleUI {
	private NPuzzle puz; // an instance of NPuzzle field
	private ConsoleInput csl = new ConsoleInput(true);

	public NPuzzleUI() {
		super();
	}

	public void uiRun() {
		System.out.println("Welcome to N-Puzzle!!\n"); // Welcome
		puz = NPuzzle.load(); // try to load saved game

		if (puz == null) // saved game cant be loaded, ask for new game
							// parameters
		{
			puz = askNewGame();
		}
		do {
			doPaint();
			doPlay(userInput());
		} while (!puz.isSolved() || !puz.isShaked());
		System.out.printf(
				"\n\nYou solved %d x %d puzzle for %s\n!!! Y O U W O N !!!\n" + " ** Thanx for playing :-)\n\n",
				puz.getWidth(), puz.getHeight(), puz.playTimeFmt());
	}

	/**
	 * Aks user for playground size
	 * 
	 * @return new initialized gamefield object
	 */
	private NPuzzle askNewGame() {
		System.out.println("\n****************************\nPlease, specify game field size:"); // we
																								// specified
																								// size
																								// of
																								// 4x4
																								// as
																								// minimuim
																								// size
		int width = csl.readInt("Width", val -> val > 3 && val < 20);
		int height = csl.readInt("Height", val -> val > 3 && val < 20);
		return new NPuzzle(width, height);
	}

	private String bl2yn(Boolean b) {
		return (b ? "Y" : "N");
	}

	/**
	 * Prints gamefield to srtandard output
	 */
	private void doPaint() {
		System.out.printf("\n------------------------ Solved: %s Shaked: %s time: %s\n", bl2yn(puz.isSolved()),
				bl2yn(puz.isShaked()), puz.playTimeFmt());

		for (int y = 0; y < puz.getHeight(); y++) {
			for (int x = 0; x < puz.getWidth(); x++) {
				Tile ti = puz.getTile(x, y);
				if (ti != null)
					System.out.printf(" %2d ", ti.getValue());
				else
					System.out.printf("    ");
			}
			System.out.println();
		}
	}

	// input from user,
	private enum Shortkeys {
		N, E, L, V, W, S, A, D, K
	};

	private String regexBuilder() {
		if (Shortkeys.values().length != LongKeys.values().length)
			throw new RuntimeException("Cannot generate regex string for menu options.");

		StringBuilder regex = new StringBuilder("[");
		for (Shortkeys s : Shortkeys.values()) {
			regex.append(s.toString());
		}
		regex.append("]|(");

		for (LongKeys s : LongKeys.values()) {
			regex.append(s.toString() + "|");
		}
		regex.deleteCharAt(regex.length() - 1);
		regex.append(")");
		// System.err.println(regex.toString()); // commented out, only for
		// debug purposes

		return regex.toString();
	}

	private LongKeys parseKeys(String cmd) {
		if (cmd.length() == 1)
			return LongKeys.values()[Shortkeys.valueOf(cmd).ordinal()];

		return LongKeys.valueOf(cmd);
	}

	private LongKeys userInput() {
		String cmd = csl.readStr("move[WSAD] Exit saVe Load New shaKe",
				val -> val.toUpperCase().matches(regexBuilder()));
		LongKeys key = parseKeys(cmd.toUpperCase());
		// System.err.println(key); // commented out
		return key;
	}

	private void doPlay(LongKeys key) {
		System.out.println("Your command: " + key.toString());

		switch (key) {
		case DOWN:
		case LEFT:
		case RIGHT:
		case UP:
			puz.moveEmptyTile(key);
			break;
		case SHAKE:
			puz.shake();
			break;
		case LOAD:
			puz = NPuzzle.load();
			break;
		case EXIT:
			puz.save();
			System.out.println("Bye bye"); // TODO ask for save on exit or
											// automatic save?
			System.exit(0);
			break;
		case NEW:
			puz = askNewGame();
			break;
		case SAVE:
			puz.save();
			break;
		default:
			throw new RuntimeException("Unimplented " + key);
		}
	}
}
