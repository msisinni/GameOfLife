package explorationChar;

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

	char[][] input() {
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

		char[][] result = new char[mRows][mCols];

		for (int i = 0; i < mRows; i++) {
			if (list.get(i).length() != mCols) {
				System.err.println("Mismatched columns in input!");
				return null;
			}
			result[i] = list.get(i).toCharArray();
		}
		return result;
	}

	char[][] generatedInput() {
		char[][] result = new char[mRows][mCols];
		for (int i = 0; i < mRows * mCols; i++) {
			for (int j = 0; j < mCols; j++) {
				result[i][j] = (Math.random() * 100 > mLife) ? '.' : '#';
			}
		}

		return result;
	}

}

class Actions {

	char[][] output(char[][] input) {
		int rows = input.length;
		int cols = input[0].length;

		char[][] result = new char[rows][cols];

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				int count = 0;

				for (int k = -1; k <= 1; k++) {
					for (int m = -1; m <= 1; m++) {
						if (input[(i + k + rows) % rows][(j + m + cols) % cols] == '#') {
							count++;
						}
					}
				}
				if (input[i][j] == '#') {
					count--; // fixes when current gets counted above;
				}

				if (count == 2) {
					result[i][j] = input[i][j];
				} else if (count == 3) {
					result[i][j] = '#';
				} else {
					result[i][j] = '.';
				}
			}
		}

		return result;
	}

	void speakOutput(char[][] output) {
		int rows = output.length;
		int cols = output[0].length;
		int totalChars = rows * cols;

		StringBuilder sb = new StringBuilder(totalChars);
		for (char[] currentRow : output) {
			for (char currentValue : currentRow) {
				sb.append(currentValue);
			}
			sb.append("\n");
		}
		System.out.println(sb.toString());
	}
}

public class ExploringLifeChar {

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

		char[][] input;
		if (gameMode == 0) {
			storage.setPath("exploreInput.txt");
			input = storage.input();

			if (input == null) {
				return;
			}
		} else if (gameMode == 2) {
			storage.setRows(40);
			storage.setRows(60);
			storage.setRows(25);
			input = storage.generatedInput();
		} else {
			input = storage.generatedInput();
		}

		char[][] originalInput = new char[input.length][input[0].length];
		for (int i = 0; i < input.length; i++) {
			originalInput[i] = Arrays.copyOf(input[i], input[0].length);
		}

		Actions actions = new Actions();

		System.out.println("Initial input: ");
		actions.speakOutput(originalInput);
		// This governs the size of the queue;
		int pastOutputsLimit = 5;

		Queue<char[][]> pastOutputs = new ArrayDeque<>(pastOutputsLimit);
		pastOutputs.add(input);

		for (int i = 1; i <= 10000; i++) {
			Thread.sleep(325);
			System.out.printf("Mutation number %d:%n", i);
			input = actions.output(input);
			actions.speakOutput(input);

			for (Iterator<char[][]> iterator = pastOutputs.iterator(); iterator
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