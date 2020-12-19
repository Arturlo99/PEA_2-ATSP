import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;


public class Graph {
	private int[][] neighborhoodMatrix;
	protected int vertexAmount;

	// Konstruktor Klasy Graph
	public Graph(int vertexAmount) {
		this.vertexAmount = vertexAmount;
		this.neighborhoodMatrix = new int[vertexAmount][vertexAmount];

		for (int i = 0; i < vertexAmount; i++) {
			for (int j = 0; j < vertexAmount; j++) {
				this.neighborhoodMatrix[i][j] = 0;
			}
		}
	}

	// Metoda tworz¹ca krawêdz pomiêdzy danymi wierzcho³kami o podanej wadze
	public boolean addEdge(int v, int w, int weight) {
		// if (weight >= 1000)
		// Waga krawedzi musi byc mniejsza od 1000
		// weight = 999;

		if (this.neighborhoodMatrix[v][w] > 0)
			return false;
		else {
			this.neighborhoodMatrix[v][w] = weight;
			return true;
		}
	}

	public boolean removeEdge(int v, int w) {
		if (this.neighborhoodMatrix[v][w] == -1)
			return false;
		else {
			this.neighborhoodMatrix[v][w] = -1;
			return true;
		}
	}

	// Zwraca wagê krawêdzi
	public int getWeight(int v, int w) {
		return neighborhoodMatrix[v][w];
	}

	// Wypisanie grafu w postaci macierzy s¹siedztwa
	public void displayGraph() {
		for (int i = 0; i < vertexAmount; i++) {
			for (int j = 0; j < vertexAmount; j++) {
				System.out.print(this.neighborhoodMatrix[i][j] + "   ");
			}
			System.out.println();
		}

	}

	public ArrayList<Integer> greedyAlgorithm() {
		Random random = new Random();
		int startVertex = random.nextInt(this.vertexAmount);
		ArrayList<Integer> tour = new ArrayList<Integer>();
		tour.add(startVertex);

		// Rozpatrywanie wierzcho³ka z ktorego chcemy wykonac ruch
		for (int i = 0; i < this.vertexAmount - 1; i++) {
			int minEdgeCost = -1;
			int nextVertex = -1;

			// Rozpatrywanie wierzcho³ka do ktorego chcemy przejsc
			for (int j = 0; j < this.vertexAmount; j++) {
				// Jeœli kolejny wierzcho³ek jest startowym
				// lub droga od niego jest w³aœnie rozpatrywana to odrzucamy

				if (tour.get(tour.size() - 1) == j || tour.get(0) == j)
					continue;

				// Odrzucenie krawedzi do wierzcholka umieszczonego juz na trasie
				boolean vertexUsed = false;
				for (int k = 0; k < tour.size(); k++) {
					if (j == tour.get(k)) {
						vertexUsed = true;
						break;
					}
				}
				if (vertexUsed)
					continue;

				// Znalezienie najkrotszej mozliwej jeszcze do uzycia krawedzi
				int consideredEdgeCost = this.getWeight(tour.get(tour.size() - 1), j);

				if (minEdgeCost == -1 || minEdgeCost > consideredEdgeCost) {
					minEdgeCost = consideredEdgeCost;
					nextVertex = j;
				}
			}

			tour.add(nextVertex);
		}

		// tour.add(startVertex);
		return tour;

	}

	// Algorytm Symulowanego Wy¿arzania z s¹siedztwem zdefiniowanym przez ruch typu swap
	public void simulatedAnnealing(int maxTime, float[] alpha) {

		Random random = new Random();
		double endTime, execTime;
		// Wygenerowanie pocz¹tkowego rozwi¹zania algorytmem zach³annym
		ArrayList<Integer> startTour = greedyAlgorithm();
		// Wyœwietlenie trasy pocz¹tkowej wraz z jej kosztem
		displayTour(startTour);
		ArrayList<Integer> currentTour;
		ArrayList<Integer> minTour;

		// Pêtla iteruj¹ca po wspolczynnikach schladzania
		for (int i = 0; i < alpha.length; i++) {
			// Rozpoczêcie pomiaru czasu
			double startTime = System.nanoTime();
			currentTour = new ArrayList<Integer>(startTour);
			minTour = new ArrayList<Integer>(startTour);
			int minCost = getTourCost(minTour);
			int currentCost = getTourCost(currentTour);
			// 
			double temperature = ((double) getTourCost(startTour) * 3);
			
			System.out.println("#" + i);
			System.out.println("Wspolczynnik zmiany temperatury wynosi: " + alpha[i]);
			System.out.println("Temperatura pocz¹tkowa wynosi: " + temperature);
			
			// Rozpoczêcie dzia³ania algorytmu
			do {
				for (int j = 0; j < this.vertexAmount; j++) {
					// Utworzenie kolejnej trasy poprzez wykonaniu ruchu swap na losowych wierzcho³kach
					ArrayList<Integer> nextTour = swapRandomVertexes(currentTour);
					int nextTourCost = getTourCost(nextTour);
					
					// Je¿eli koszt uzyskanej trasy jest nizszy, to przyjmujemy te rozwiazanie jako aktualne
					if (nextTourCost < currentCost) {
						currentTour = new ArrayList<Integer>(nextTour);
						currentCost = nextTourCost;
						
						//Jeœli rozwi¹zanie jest korzystniejsze od dotychczas najlepszego to je zapamiêtujemy
						if (nextTourCost < minCost) {
							minCost = nextTourCost;
							minTour = new ArrayList<Integer>(nextTour);
						}
					
					// W przeciwnym razie z pewnym prawdopodobieñstwem wybieramy rozwi¹zanie tymczasowo gorsze
					} else if (random.nextFloat() < Math.exp(-((nextTourCost - currentCost) / temperature))) {
						currentTour = new ArrayList<Integer>(nextTour);
						currentCost = nextTourCost;
					}
				}
				// Obni¿enie temperatury
				temperature *= alpha[i];
				endTime = System.nanoTime();
				execTime = (endTime - startTime) / 1000000000;
			} while (execTime <= maxTime);
			System.out.println("Temperatura koncowa wynosi: " + temperature);
			System.out.println("Czas wykonywania wynosi: " + execTime + " s");
			System.out.println("Najlepsze rozwi¹zanie jakie znaleziono: ");
			displayTour(minTour);
		}

	}

	// do skoñczenia!
	public void tabuSearch(int maxTime, int neighbourhoodType) {

		double endTime;
		int lifeTime = this.vertexAmount;
		ArrayList<Integer> currentTour = greedyAlgorithm();
		displayTour(currentTour);
		int divCounter = 0;
		ArrayList<Integer> minTour = new ArrayList<Integer>(currentTour);
		TabuList tabuList = new TabuList(this.vertexAmount); // stworzenie tabu listy
		int minCost = getTourCost(minTour);
		int currentCost = 0;
		int nextTourCost = 0;
		double startTime = System.nanoTime();
		int[] move = new int[2];
		boolean[][] checkedVertexes = new boolean[this.vertexAmount][this.vertexAmount];

		do {

			currentCost = getTourCost(currentTour);
			
			for (int i = 0; i < checkedVertexes.length; i++)
				for (int j = 0; j < checkedVertexes.length; j++)
					checkedVertexes[i][j] = false;

			// Znajdowanie w s¹siedztwie obecnego rozwi¹zania najkorzystniejszego ruchu
			for (int i = 1; i < currentTour.size() - 1; i++) {
				for (int j = 1; j < currentTour.size() - 1; j++) {
					if (i != j && (!checkedVertexes[i][j] && !checkedVertexes[i][j])) {

						// w zaleznosci od wybranej definicji s¹siedztwa wykonujemy ruch na kopii trasy i obliczamy
						// koszt trasy po jego wykonaniu oraz oznaczamy ten ruch jako sprawdzony
						ArrayList<Integer> nextTour = new ArrayList<Integer>(currentTour);
						switch (neighbourhoodType) {
						case 0:
							Collections.swap(nextTour, i, j);
							break;
						case 1:
							insertVertex(nextTour, i, j);
							break;
						case 2:
							invertVertexes(nextTour, i, j);
							break;
						default:
							break;
						}
						nextTourCost = getTourCost(nextTour);
						checkedVertexes[i][j] = true;
						// Porownanie kosztu gdybyœmy wykonali ruch do aktualnego

						if (nextTourCost < currentCost) {
							currentCost = nextTourCost;
							move[0] = i;
							move[1] = j;
						}
					}
				}
			}
			// Sprawdzanie listy ruchów zakazanych z uwzglêdnieniem kryterium aspiracji
			if ((tabuList.tabuList[move[0]][move[1]] == 0 && tabuList.tabuList[move[1]][move[0]] == 0)
					|| currentCost < minCost) {

				switch (neighbourhoodType) {
				case 0:
					Collections.swap(currentTour, move[0], move[1]);
					break;
				case 1:
					insertVertex(currentTour, move[0], move[1]);
					break;
				case 2:
					invertVertexes(currentTour, move[0], move[1]);
					break;
				}



				if (currentCost < minCost) {
					minCost = currentCost;
					minTour = new ArrayList<Integer>(currentTour);
				}
				tabuList.tabuMove(move[0], move[1], lifeTime);
				// Jesli algorytm nie zmienia rozwi¹zania od 20 iteracji to generujemy nowe
				// rozwi¹zanie
				// Dywersyfikacja

				if (divCounter > 19) {
					if (minCost <= currentCost) {
						currentTour = greedyAlgorithm();

					}
					divCounter = 0;
				}

			}
			tabuList.decrementTabu();
			divCounter++;
			endTime = System.nanoTime();

		} while ((endTime - startTime) / 1000000000 <= maxTime);
		System.out.println("Najlepsze rozwiazanie jakie znaleziono: ");
		displayTour(minTour);
	}

	// Metoda definiuj¹ca s¹siedztwo typu invert 
	private void invertVertexes(ArrayList<Integer>tour, int i, int j)
	{
		int bigger = j;
		int smaller = i;
		
		if ( i > j) {
			bigger = i;
			smaller = j;
		}
		for (int k = smaller; k < bigger; k++, bigger--) {
			int temp = tour.get(k);
			tour.set(k,tour.get(bigger));
			tour.set(bigger, temp);
		}
	}
	
	
	
	// Metoda definiuj¹ca s¹siedztwo typu insert (przed j wstatwiamy i)
	private void insertVertex(ArrayList<Integer> tour, int i, int j) {
		int vertexI = tour.get(i);
		if(i < j)
		{
		tour.add(j, vertexI);
		tour.remove(i);
		}
		else {
		tour.add(j, vertexI);
		tour.remove(i+1);
		}
		
	}
	
	
	// Metoda zamieniaj¹ca wierzcho³ki w trasie (tworzy nastêpnie rozpatrywane
	// rozwi¹zanie)
	private ArrayList<Integer> swapRandomVertexes(ArrayList<Integer> tour) {
		ArrayList<Integer> swappedTour = new ArrayList<Integer>(tour);

		Random random = new Random();
		int j = 0, k = 0;
		while (j == k) {
			j = random.nextInt(tour.size());
			k = random.nextInt(tour.size());
		}
		Collections.swap(swappedTour, j, k);

		return swappedTour;
	}

	// Wyswietlenie trasy wraz z jej kosztem
	public void displayTour(ArrayList<Integer> tour) {

		int distFromStart = 0;
		int length = 0;
		for (int i = 0; i < tour.size() - 1; i++) {
			length = this.getWeight(tour.get(i), tour.get(i + 1));
			distFromStart += length;

			System.out.print(tour.get(i));
			System.out.print(" -> ");
		}
		distFromStart += this.getWeight(tour.get(tour.size() - 1), tour.get(0));
		System.out.print(tour.get(0));
		System.out.println();
		System.out.println("Dlugosc trasy: " + distFromStart);
	}

	// Metoda obliczaj¹ca koszt trasy
	public int getTourCost(ArrayList<Integer> tour) {
		int tourCost = 0;
		int length = 0;
		for (int i = 0; i < tour.size() - 1; i++) {
			length = this.getWeight(tour.get(i), tour.get(i + 1));
			tourCost += length;
		}
		tourCost += this.getWeight(tour.get(tour.size() - 1), tour.get(0));
		return tourCost;
	}

	public int[][] getNeighborhoodMatrix() {
		return neighborhoodMatrix;
	}

	public void setNeighborhoodMatrix(int[][] neighborhoodMatrix) {
		this.neighborhoodMatrix = neighborhoodMatrix;
	}

	public int getVertexAmount() {
		return vertexAmount;
	}

	public void setVertexAmount(int vertexAmount) {
		this.vertexAmount = vertexAmount;
	}

}
