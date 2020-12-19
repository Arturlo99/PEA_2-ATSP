
public class TabuList {

    int [][] tabuList ;

	
    public TabuList(int vertexAmount){
        tabuList = new int[vertexAmount][vertexAmount];
    }
    
    //Ruch tabu
    public void tabuMove(int city1, int city2, int lifeTime){ 
        tabuList[city1][city2]+= lifeTime;
        tabuList[city2][city1]+= lifeTime;
    }

    public void decrementTabu(){
        for(int i = 0; i < tabuList.length; i++){
            for(int j = 0; j < tabuList.length; j++){
                if(tabuList[i][j]  > 0) {
                    tabuList[i][j]--;
                }
            }
        }
    }

    public void printTabu(){
        for(int i = 0; i < tabuList.length; i++){
            for(int j = 0; j < tabuList.length; j++){
                System.out.print(tabuList[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
	
	
	
}
