package exploration;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;

class Storage {
	private String path;

	Storage(String path) {
		this.path = path;
	}
	


	private int[] importedBoardSize = new int[2];
	
	public int[] getBoardSize() {
		return importedBoardSize;
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

		int rows = list.size();
		int cols = list.get(0).length();

		char[][] input = new char[rows][cols];

		for (int i = 0; i < rows; i++) {
			if (list.get(i).length() != cols) {
				System.err.println("Mismatched columns in input!");
				return null;
			}
			input[i] = list.get(i).toCharArray();
		}
		return input;
	}

	char[][] generatedInput(int[] boardSize) {
		int rows = boardSize[0];
		int cols = boardSize[1];
		int life = boardSize[2];

		char[][] input = new char[rows][cols];

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				input[i][j] = (Math.random() * 100 > life) ? '.' : '#';
			}
		}

		return input;
	}

}

class Actions {

	char[][] output(char[][] input) {
		int rows = input.length;
		int cols = input[0].length;

		char[][] output = new char[rows][cols];

		char oct = '#';

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				int count = 0;

				for (int k = -1; k <= 1; k++) {
					for (int m = -1; m <= 1; m++) {
						if (input[(i+k+rows)%rows][(j+m+cols)%cols] == oct) {
							count++;
						}
					}
				}
				if (input[i][j] == oct) {
					count--; // fixes when current gets counted above;
				}
				
				if (count == 2) {
					output[i][j] = input[i][j];
				} else if (count == 3) {
					output[i][j] = '#';
				} else {
					output[i][j] = '.';
				}
			}
		}

		return output;
	}

	void speakOutput(char[][] output) {
		for (char[] line : output) {
			for (char current : line) {
				System.out.print(current);
			}
			System.out.println();
		}
		System.out.println();
	}
}

public class ExploringLife {

	public static void main(String[] args) throws InterruptedException {
		final String PATH = "exploreInput.txt";

		Storage storage = new Storage(PATH);

		System.out.println("Choose which game to play:");
		System.out.println("Enter (0) for user input.");
		System.out.println("Enter (1) for a random map.");
		int gameMode;
		int[] boardSize = new int[3];
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
				boardSize[2] = scanner.nextInt();
			}

		} catch (NumberFormatException e) {
			System.out.println("Input must be a number!");
			scanner.close();
			return;
		}
		scanner.close();

		char[][] input;
		if (gameMode == 0) {
			input = storage.input();
			if (input == null) {
				return;
			}
		} else {
			input = storage.generatedInput(boardSize);
		}
		Actions actions = new Actions();
		System.out.println("Mutation number 1:");
		char[][] output = actions.output(input);
		actions.speakOutput(output);

		Queue<char[][]> pastOutputs = new ArrayBlockingQueue<>(3);
		pastOutputs.add(input);
		System.out.println(pastOutputs.size());
		
		for (int i = 2; i < 1001; i++) {
			Thread.sleep(325);
			System.out.printf("Mutation number %d:%n", i);
			output = actions.output(output);
			actions.speakOutput(output);
			
			
			
		}

	}
}
// to do:
// check previous boards - previous 2 seems like a good number
// use queue for that?