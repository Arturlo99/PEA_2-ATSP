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

		//tour.add(startVertex);
		return tour;

	}

	// Algorytm Symulowanego Wy¿arzania z s¹siedztwem typu swap
	public void simulatedAnnealing(int maxTime, float[] alpha) {
		
		Random random = new Random();
		double endTime, execTime;
		ArrayList<Integer> startTour = greedyAlgorithm();
		displayTour(startTour);
		ArrayList<Integer> currentTour;
		ArrayList<Integer> minTour;
		for (int i = 0; i < alpha.length; i++)
		{
			System.out.println("#"+ i);
			double startTime = System.nanoTime();
			currentTour = new ArrayList<Integer>(startTour);
			minTour = new ArrayList<Integer>(startTour);
			int minCost = getTourCost(minTour);
			int currentCost = getTourCost(currentTour);
			double temperature = ((double) getTourCost(startTour) * 3);
			System.out.println("Wspolczynnik zmiany temperatury wynosi: " + alpha[i]);
			System.out.println("Temperatura pocz¹tkowa wynosi: " + temperature);
			do {
				for (int j = 0; j < this.vertexAmount; j++) 
				{
					ArrayList<Integer> swappedTour = swapVertexes(currentTour);
					int nextTourCost = getTourCost(swappedTour);

					if (nextTourCost < currentCost) 
					{
						currentTour = new ArrayList<Integer>(swappedTour);
						currentCost = nextTourCost;
						
						if(nextTourCost < minCost) 
						{
							minCost = nextTourCost;
							minTour = new ArrayList<Integer>(swappedTour);
						}
						
					} 
					else if (random.nextFloat() < Math.exp(-((nextTourCost - currentCost) / temperature))) 
					{
						currentTour = new ArrayList<Integer>(swappedTour);
						currentCost = nextTourCost;
					}
				}
				temperature *= alpha[i];
				endTime = System.nanoTime();
				execTime = (endTime - startTime) / 1000000000;
			} while (execTime <= maxTime);
			System.out.println("Temperatura koncowa wynosi: " + temperature);
			System.out.println("Czas wykonywania wynosi: " + execTime + " s");
			System.out.println("Trasa: " );
			displayTour(minTour);
		}

	}

	// Metoda zamieniaj¹ca wierzcho³ki w trasie (tworzy nastêpnie rozpatrywane
	// rozwi¹zanie)
	private ArrayList<Integer> swapVertexes(ArrayList<Integer> tour) {
		ArrayList<Integer> swappedTour = new ArrayList<Integer>(tour);

		int v, w;
		Random random = new Random();

		do {
			v = random.nextInt(tour.size());
			w = random.nextInt(tour.size());

		} while (v == w);
		Collections.swap(swappedTour, v, w);
		
		return swappedTour;
	}

	// Wyswietlenie trasy wraz z jej kosztem
	public void displayTour(ArrayList<Integer> tour) {

		int distFromStart = 0;
		int length = 0;
		for (int i = 0; i < tour.size() - 1; i++) {
			length = this.getWeight(tour.get(i), tour.get(i+1));
			distFromStart += length;

			System.out.print(tour.get(i));
			System.out.print(" -> ");
		}
		distFromStart += this.getWeight(tour.get(tour.size()-1), tour.get(0));
		System.out.print(tour.get(0));
		System.out.println();
		System.out.println("Dlugosc trasy: " + distFromStart);
	}

	// Metoda obliczaj¹ca koszt trasy
	public int getTourCost(ArrayList<Integer> tour) {
		int tourCost = 0;
		int length = 0;
		for (int i = 0; i < tour.size() - 1; i++) {
			length = this.getWeight(tour.get(i), tour.get(i+1));
			tourCost += length;
		}
		tourCost += this.getWeight(tour.get(tour.size() -1 ), tour.get(0));
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
