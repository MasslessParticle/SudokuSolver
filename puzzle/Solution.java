package puzzle;

public class Solution {
	int[][] solutionGrid = null;
	
	public Solution(int[][] g){
		solutionGrid = g;
	}
	
	public boolean equals(Object o){
		Solution s = null;
		
		if(this == o)
			return true;
		else if(!(o instanceof Solution))
			return false;
		else{
			s = (Solution)o;
		
			for(int i=0; i<9; i++)
				for(int j=0; j<9; j++)
					if(solutionGrid[i][j] != s.getGrid()[i][j])
						return false;
			return true;
		}
	}
	
	public int[][] getGrid(){
		return solutionGrid.clone();
	}
	
	public String toString(){
		StringBuffer buff = new StringBuffer();
		
		for(int i=0; i<9; i++){
			for(int j=0; j<9; j++)
				buff.append(solutionGrid[i][j] + " ");
			buff.append("\n");
		}
		
		return buff.toString();
	}
}
