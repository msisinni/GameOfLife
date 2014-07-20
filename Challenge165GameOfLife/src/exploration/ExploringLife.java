package exploration;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Storage {
	private String path;

	Storage(String path) {
		this.path = path;
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
				return new char[0][0];
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
				input[i][j] = (Math.random()*100 > life) ? '.' : '#'; 
			}
		}
		
		return input;
	}
	
	
	
}

class Actions {

	char[][] output(int X, int Y, char[][] in) {

		char[][] output = new char[X][Y];

		char oct = '#';

		for (int i = 0; i < X; i++) {
			for (int j = 0; j < Y; j++) {
				int count = 0;
				if (in[(i - 1 + X) % X][(j - 1 + Y) % Y] == oct) {
					count++;
				}
				if (in[(i - 1 + X) % X][j] == oct) {
					count++;
				}
				if (in[(i - 1 + X) % X][(j + 1 + Y) % Y] == oct) {
					count++;
				}
				if (in[i][(j - 1 + Y) % Y] == oct) {
					count++;
				}
				if (in[i][(j + 1 + Y) % Y] == oct) {
					count++;
				}
				if (in[(i + 1 + X) % X][(j - 1 + Y) % Y] == oct) {
					count++;
				}
				if (in[(i + 1 + X) % X][j] == oct) {
					count++;
				}
				if (in[(i + 1 + X) % X][(j + 1 + Y) % Y] == oct) {
					count++;
				}

				if (count == 2) {
					output[i][j] = in[i][j];
				} else if (count == 3) {
					output[i][j] = '#';
				} else {
					output[i][j] = '.';
				}
			}
		}

		return output;
	}

	void speakOutput(char[][] output) throws InterruptedException {
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
				System.out.println("Enter an integer percentage for a tile to contain life:");
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
		} else {
			input = storage.generatedInput(boardSize);
		}
		
		System.out.println("\nInitial conditions:");
		for (char[] row : input) {
			for (char current : row) {
				System.out.print(current);
			}
			System.out.println();
		}
		int N = 100;
		Actions actions = new Actions();
		
		int X = input.length;
		int Y = input[0].length;
		
		char[][] output = new char[X][Y];
		if (N > 0) {
			System.out.println("Mutation number 1:");
			output = actions.output(X, Y, input);
			actions.speakOutput(output);

		}

		for (int i = 1; i < N; i++) {
			Thread.sleep(325);
			output = actions.output(X, Y, output);
			System.out.printf("Mutation number %d:%n", (i+1));
			actions.speakOutput(output);
		}
		
	}
}

// to do:
// check previous boards - previous 2 seems like a good number
// use queue for that?
//
// near infinite loop - what value for a kill switch?
