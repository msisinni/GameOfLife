import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

class Storage {
	private String path;

	Storage(String path) {
		this.path = path;
	}

	char[][] input(int X, int Y) {
		char[][] input = new char[X][Y];

		try (BufferedReader br = new BufferedReader(new FileReader(path))) {
			int counter = 0;
			while (counter < X) {
				br.read(input[counter]);
				br.readLine();
				counter++;
			}
		} catch (FileNotFoundException e) {
			System.out.println("Cannot find " + path);
		} catch (IOException e) {
			System.out.println("Cannot read " + path);
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

public class Life {

	public static void main(String[] args) throws InterruptedException {
		final String PATH = "input.txt";

		Storage storage = new Storage(PATH);
		int N;
		int X;
		int Y;

		Scanner scanner = new Scanner(System.in);
		try {
			System.out.println("Enter number of mutations:");
			N = scanner.nextInt();
			System.out.println("Enter number of rows:");
			X = scanner.nextInt();
			System.out.println("Enter number of columns:");
			Y = scanner.nextInt();
		} catch (InputMismatchException e) {
			System.out.println("Must input number!");
			scanner.close();
			return;
		}
		scanner.close();

		char[][] input = storage.input(X, Y);

		Actions actions = new Actions();
		char[][] output = new char[X][Y];

		if (N > 0) {
			System.out.println("Mutation number 1:");
			output = actions.output(X, Y, input);
			actions.speakOutput(output);

		}

		for (int i = 1; i < N; i++) {
			Thread.sleep(350);
			output = actions.output(X, Y, output);
			System.out.printf("Mutation number %d:%n", (i+1));
			actions.speakOutput(output);
		}

	}
}
