package exploration;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;

class Storage {
	private String path;

	public void setPath(String path) {
		this.path = path;
	}

	private int[] importedBoardSize = new int[2];

	public int[] getBoardSize() {
		return importedBoardSize;
	}

	String input() {
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

		int rows = list.size();
		int cols = list.get(0).length();

		importedBoardSize[0] = rows;
		importedBoardSize[1] = cols;

		StringBuilder sb = new StringBuilder(rows * cols);

		for (int i = 0; i < rows; i++) {
			if (list.get(i).length() != cols) {
				System.err.println("Mismatched columns in input!");
				return null;
			}
			sb.append(list.get(i));
		}
		return sb.toString();
	}

	String generatedInput(int[] boardSize, int life) {
		int rows = boardSize[0];
		int cols = boardSize[1];
		StringBuilder sb = new StringBuilder(rows * cols);

		for (int i = 0; i < rows * cols; i++) {
			sb.append((Math.random() * 100 > life) ? '.' : '#');
		}

		return sb.toString();
	}

}

class Actions {

	String output(String input, int[] boardSize) {
		int rows = boardSize[0];
		int cols = boardSize[1];

		int totalChars = rows * cols;

		StringBuilder sb = new StringBuilder(totalChars);

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				int count = 0;

				for (int k = -1; k <= 1; k++) {
					for (int m = -1; m <= 1; m++) {
						if (input.charAt(((i + k) * cols + ((j + m) + cols)
								% cols + totalChars)
								% totalChars) == '#') {
							count++;
						}
					}
				}
				if (input.charAt(i * cols + j) == '#') {
					count--; // fixes when current gets counted above;
				}

				if (count == 2) {
					sb.append(input.charAt(i * cols + j));
				} else if (count == 3) {
					sb.append('#');
				} else {
					sb.append('.');
				}
			}
		}

		return sb.toString();
	}

	void speakOutput(String output, int[] boardSize) {
		int rows = boardSize[0];
		int cols = boardSize[1];
		for (int i = 0; i < rows; i++) {
			System.out.println(output.substring(i * cols, i * cols + cols));
		}
	}
}

public class ExploringLife {

	public static void main(String[] args) throws InterruptedException {
		Storage storage = new Storage();

		System.out.println("Choose which game to play:");
		System.out.println("Enter (0) for user input.");
		System.out.println("Enter (1) for a random map.");
		int gameMode;
		int[] boardSize = new int[2];
		int life = 0;
		Scanner scanner = new Scanner(System.in);
		try {
			gameMode = scanner.nextInt();
			if (gameMode != 0 && gameMode != 1) {
				System.out.println("Invalid game number!");
				scanner.close();
				return;
			}
			if (gameMode == 1) {
				System.out.println("Enter a number of rows for the board:");
				boardSize[0] = scanner.nextInt();
				System.out.println("Enter a number of columns for the board:");
				boardSize[1] = scanner.nextInt();
				System.out
						.println("Enter an integer percentage for a tile to contain life:");
				life = scanner.nextInt();
			}
		} catch (InputMismatchException e) {
			System.out.println("Input must be a number!");
			scanner.close();
			return;
		}
		scanner.close();

		String input;
		if (gameMode == 0) {
			storage.setPath("exploreInput.txt");

			boardSize = storage.getBoardSize();
			input = storage.input();

			if (input == null) {
				return;
			}
		} else {
			input = storage.generatedInput(boardSize, life);
		}
		String originalInput = new String(input);
		Actions actions = new Actions();
		
		System.out.println("Initial input: ");
		actions.speakOutput(originalInput, boardSize);
		
		int pastOutputsLimit = 5;

		Queue<String> pastOutputs = new ArrayDeque<>(pastOutputsLimit);
		pastOutputs.add(input);

		for (int i = 1; i <= 10000; i++) {
			Thread.sleep(325);
			System.out.printf("Mutation number %d:%n", i);
			input = actions.output(input, boardSize);
			actions.speakOutput(input, boardSize);

			if (pastOutputs.contains(input)) {
				System.out.printf(
						"Repeating board found within the last %d maps.",
						pastOutputs.size());
				break;
			}

			pastOutputs.add(input);

			if (i > pastOutputsLimit - 1) {
				pastOutputs.remove();
			}
			
		}
		if (gameMode == 1) {
			System.out.println("\nRepeat of the original input:");
			actions.speakOutput(originalInput, boardSize);
		}
	}
}