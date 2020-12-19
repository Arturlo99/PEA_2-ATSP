import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Menu {

	@SuppressWarnings("resource")
	public static Graph readGraphFromFile() throws FileNotFoundException {

		Scanner sc = new Scanner(System.in);
		File file = null;
		int selected = sc.nextInt();
		switch (selected) {
		case 1:
			file = new File("ftv47.atsp");
			break;
		case 2:
			file = new File("ftv170.atsp");
			break;
		case 3:
			file = new File("rbg403.atsp");
			break;
		default:
			break;
		}
		Scanner scanner = new Scanner(file);

		for (int i = 0; i < 3; i++) {
			if (scanner.hasNextLine()) {
				scanner.nextLine();
			}
		}

		if (scanner.hasNext()) {
			scanner.next();
		}

		int dimension = scanner.nextInt();
		for (int i = 0; i < 4; i++) {
			if (scanner.hasNextLine()) {
				scanner.nextLine();
			}
		}

		Graph graph = new Graph(dimension);
		for (int i = 0; i < dimension; i++) {
			for (int j = 0; j < dimension; j++) {
				graph.addEdge(i, j, scanner.nextInt());
			}
		}
		return graph;
	}

	@SuppressWarnings("resource")
	public static void main(String[] args) throws FileNotFoundException {
		int neighbourhoodType = 0;
		float[] alpha = {0.999f, 0.9999f, 0.99999f}; // wspolczynnik zmiany temperatury do SW
		int maxTime = 0; // Kryterium stopu
		int selection;
		Graph graph = null;
		do {
			System.out.println("1. Wczytanie danych z pliku");
			System.out.println("2. Wprowadzenie kryterium stopu");
			System.out.println("3. Wybór s¹siedztwa");
			System.out.println("4. Uruchomienie algorytmu TS dla wczytanych danych i ustawieñ z wyœwietleniem wynikow");
			System.out.println("5. Ustawienie wspólczynnikow zmiany temperatury dla SW");
			System.out.println("6. Uruchomianie algorytmu SW dla wczytanych danych i ustawieñ z wyœwietleniem wynikow");
			//System.out.println("7. Pomiary");
			System.out.println("Aby zakonczyc - 0");
			System.out.println("Wprowadz liczbê: ");
			Scanner sc = new Scanner(System.in);
			selection = sc.nextInt();
			switch (selection) {

			// Wczytanie danych z pliku
			case 1: {
				System.out.println("Wybierz plik, z ktorego wczytaæ dane: ");
				System.out.println("1. ftv47.atsp");
				System.out.println("2. ftv170.atsp");
				System.out.println("3. rbg403.atsp");
				graph = readGraphFromFile();
				//graph.displayGraph();

			}
				break;

			case 2: {

				System.out.println("Wprowad¿ czas jako kryterium stopu w sekundach: ");
				maxTime = sc.nextInt();

			}
				break;


			case 3: {
				Scanner scanner = new Scanner(System.in);
				System.out.println("Wybierz rodzaj s¹siedztwa: ");
				System.out.println("0. Swap");
				System.out.println("1. Insert");
				System.out.println("2. Invert");
				neighbourhoodType = scanner.nextInt();
			} 
			break;
			
			case 4: {
				graph.tabuSearch(maxTime, neighbourhoodType);
			}
			break;
			

			case 5: {
				System.out.println("Wprowadz wspolczyniki zmiany temperatury < 1: ");
				
					alpha[0] = sc.nextFloat();
					alpha[1] = sc.nextFloat();
					alpha[2] = sc.nextFloat();
			}
				break;

			case 6: {
				if (graph != null) {
					graph.simulatedAnnealing(maxTime, alpha);
				}
			}
				break;

			case 0: {
			}
				break;
			default: {
				System.out.println("Nieprawidlowy wybor");
			}
			}
		} while (selection != 0);

	}

}
