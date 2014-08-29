package explorationBoolean;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;

class Storage {
	private String path;

	public void setPath(String path) {
		this.path = path;
	}

	private int mRows;
	private int mCols;
	private int mLife;

	public void setRows(int rows) {
		mRows = rows;
	}

	public void setCols(int cols) {
		mCols = cols;
	}

	public void setLife(int life) {
		mLife = life;
	}

	boolean[][] input() {
		List<String> list = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(path))) {
			String currentLine;
			while ((currentLine = br.readLine()) != null) {
				list.add(currentLine);
			}
		} catch (FileNotFoundException e) {
			System.out.println("Cannot find " + path);
		} catch (IOException e) {
			System.out.println("Cannot read " + path);
		}

		mRows = list.size();
		mCols = list.get(0).length();

		boolean[][] result = new boolean[mRows][mCols];

		for (int i = 0; i < mRows; i++) {
			if (list.get(i).length() != mCols) {
				System.err.println("Mismatched columns in input!");
				return null;
			}
			for (int j = 0; j < mCols; j++) {
				result[i][j] = (list.get(i).toCharArray()[j] == '.') ? false : true;
			}
		}
		return result;
	}

	boolean[][] generatedInput() {
		boolean[][] result = new boolean[mRows][mCols];
		for (int i = 0; i < mRows; i++) {
			for (int j = 0; j < mCols; j++) {
				result[i][j] = (Math.random() * 100 > mLife) ? false : true;
			}
		}

		return result;
	}

}

class Actions {

	boolean[][] output(boolean[][] input) {
		int rows = input.length;
		int cols = input[0].length;

		boolean[][] result = new boolean[rows][cols];

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				int count = 0;

				for (int k = -1; k <= 1; k++) {
					for (int m = -1; m <= 1; m++) {
						if (input[(i + k + rows) % rows][(j + m + cols) % cols]) {
							count++;
						}
					}
				}
				if (input[i][j]) {
					count--; // fixes when current gets counted above;
				}

				if (count == 2) {
					result[i][j] = input[i][j];
				} else if (count == 3) {
					result[i][j] = true;
				} else {
					result[i][j] = false;
				}
			}
		}

		return result;
	}

	void speakOutput(boolean[][] output) {
		int rows = output.length;
		int cols = output[0].length;
		int totalChars = rows * cols;

		StringBuilder sb = new StringBuilder(totalChars);
		for (boolean[] currentRow : output) {
			for (boolean currentValue : currentRow) {
				sb.append((currentValue)?'#':'.');
			}
			sb.append("\n");
		}
		System.out.println(sb.toString());
	}
}

public class ExploringLifeBoolean {

	public static void main(String[] args) throws InterruptedException {
		Storage storage = new Storage();

		System.out.println("Choose which game to play:");
		System.out.println("Enter (0) for user input.");
		System.out.println("Enter (1) for a random map.");
		System.out.println("Enter (2) for a default randomized map.");
		int gameMode;

		Scanner scanner = new Scanner(System.in);
		try {
			gameMode = scanner.nextInt();
			if (gameMode < 0 || gameMode > 2) {
				System.out.println("Invalid game number!");
				scanner.close();
				return;
			}
			if (gameMode == 1) {
				System.out.println("Enter a number of rows for the board:");
				storage.setRows(scanner.nextInt());
				System.out.println("Enter a number of columns for the board:");
				storage.setCols(scanner.nextInt());
				System.out
						.println("Enter an integer percentage for a tile to contain life:");
				storage.setLife(scanner.nextInt());
			}
		} catch (InputMismatchException e) {
			System.out.println("Input must be a number!");
			scanner.close();
			return;
		}
		scanner.close();

		boolean[][] input;
		if (gameMode == 0) {
			storage.setPath("exploreInput.txt");
			input = storage.input();

			if (input == null) {
				return;
			}
		} else if (gameMode == 2) {
			storage.setRows(40);
			storage.setCols(60);
			storage.setLife(25);
			input = storage.generatedInput();
		} else {
			input = storage.generatedInput();
		}

		boolean[][] originalInput = new boolean[input.length][input[0].length];
		for (int i = 0; i < input.length; i++) {
			originalInput[i] = Arrays.copyOf(input[i], input[0].length);
		}

		Actions actions = new Actions();

		System.out.println("Initial input: ");
		actions.speakOutput(originalInput);
		// This governs the size of the queue;
		int pastOutputsLimit = 5;

		Queue<boolean[][]> pastOutputs = new ArrayDeque<>(pastOutputsLimit);
		pastOutputs.add(input);

		for (int i = 1; i <= 10000; i++) {
			Thread.sleep(325);
			System.out.printf("Mutation number %d:%n", i);
			input = actions.output(input);
			actions.speakOutput(input);

			for (Iterator<boolean[][]> iterator = pastOutputs.iterator(); iterator
					.hasNext();) {
				if (Arrays.deepEquals(input, iterator.next())) {
					System.out.printf(
							"Repeating board found within the last %d maps.%n",
							pastOutputs.size());
					i += 10000;
					break;
				}
			}

			pastOutputs.add(input);

			if (i > pastOutputsLimit - 1) {
				pastOutputs.remove();
			}

		}
		if (gameMode == 1 || gameMode == 2) {
			System.out.println("\nRepeat of the original input:");
			actions.speakOutput(originalInput);
		}
	}
}