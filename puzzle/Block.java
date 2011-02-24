package puzzle;

import patterson.travis.sudoku.Puzzle;

public class Block {
	private Tile[] members = new Tile[9];
	private int[] candidates = new int[]{0,0,0,0,0,0,0,0,0};
	private int count = 0, blockNumber;
	private Puzzle puzzleIBelongTo;
	
	public Block(Puzzle p){
		puzzleIBelongTo = p;
	}
		
	public void setMembers(Tile t){
		members[count] = t;
		count++;
	}
	
	public void setBlockNumber(){
		blockNumber = members[0].getBlock();
	}
	
	public void refreshCandidates(){
		for(int i=0; i<9; i++)
			candidates[i] = 0;
			
		for(int i=0; i<9; i++)
			for(int j=0; j<9; j++)
				if(members[i].getCandidates()[j])
					candidates[j]++;
	}
			
	public int[] getCandidates(){
		return candidates;
	}
	
	public int getBlockNumber(){
		return blockNumber;
	}
		
	public Tile getMember(int index){
		return members[index];
	}
		
	public boolean findHiddenSingle(){
		for(int i=0; i<9; i++)
			if(candidates[i] == 1)
				for(int j=0; j<9; j++)
					if(members[j].getCandidates()[i]){
						members[j].setValue(i+1);
						return true;
					}
		return false;
	}
			
	public void eliminateCandidate(int can){
		candidates[can - 1] = 0;
		
		for(int i=0; i<9; i++)
			members[i].eliminateCandidate(can);
	}
	
	public boolean cbInteraction(){
		boolean[][] candidates = new boolean[9][3];
		boolean[] elimination = new boolean[]{false, false, false, false, false, false, false, false, false};
		Tile[][] block = new Tile[3][3];
		boolean done = false;
		
		//initilizes candidate array to false
		for(int i=0; i<9; i++)
			for(int j=0; j<3; j++)
				candidates[i][j] = false;
		
		//converts members array to a 3x3 array
		for(int i=0; i<9; i++)
			if(i<3)
				block[0][i] = members[i];
			else if (i<6)
				block[1][i-3] = members[i];
			else
				block[2][i-6] = members[i];
		
		//condenses 3x3 array candidates to an array representing candidates by column	
		for(int i=0; i<3; i++)
			for(int j=0; j<3; j++)
				for(int k=0; k<9; k++)
					if(block[j][i].getCandidates()[k])
						candidates[k][i] = true;
		
		//condenses candidate array to single list of possible elimination					
		for(int i=0; i<9; i++)
			if(((!candidates[i][0]) && (!candidates[i][1]) && (candidates[i][2])) || ((!candidates[i][0]) && (candidates[i][1]) && (!candidates[i][2])) || ((candidates[i][0]) && (!candidates[i][1]) && (!candidates[i][2])))
				elimination[i] = true;
				
		//checks for and performs final elimination		
		for(int i=0; i<3; i++)
			for(int j=0; j<3; j++)
				for(int k=0; k<9; k++)
					for(int l=0; l<9; l++)
						if((block[i][j].getCandidates()[k]) && (elimination[k]))
							if(puzzleIBelongTo.getCols()[block[i][j].getColumn()].getMember(l).getBlock() != block[i][j].getBlock())
								if(puzzleIBelongTo.getCols()[block[i][j].getColumn()].getMember(l).getCandidates()[k]){
									puzzleIBelongTo.getCols()[block[i][j].getColumn()].eliminateCandidate(k+1, block[i][j].getBlock());
									elimination[k] = false;
									done = true;
								}
		return done;			
	}					

	public boolean rbInteraction(){
		boolean[][] candidates = new boolean[3][9];
		boolean[] elimination = {false, false, false, false, false, false, false, false, false};
		Tile[][] block = new Tile[3][3];
		boolean done = false;
		
		//initilizes candidate array to false
		for(int i=0; i<9; i++)
			for(int j=0; j<3; j++)
				candidates[j][i] = false;
		
		//converts members array to a 3x3 array
		for(int i=0; i<9; i++)
			if(i<3)
				block[0][i] = members[i];
			else if (i<6)
				block[1][i-3] = members[i];
			else
				block[2][i-6] = members[i];
		
		//condenses 3x3 array candidates to an array representing candidates by column	
		for(int i=0; i<3; i++)
			for(int j=0; j<3; j++)
				for(int k=0; k<9; k++)
					if(block[i][j].getCandidates()[k])
						candidates[i][k] = true;
		
		//condenses candidate array to single list of possible elimination					
		for(int i=0; i<9; i++)
			if(((!candidates[0][i]) && (!candidates[1][i]) && (candidates[2][i])) || ((!candidates[0][i]) && (candidates[1][i]) && (!candidates[2][i])) || ((candidates[0][i]) && (!candidates[1][i]) && (!candidates[2][i])))
				elimination[i] = true;
				
		//checks for and performs final elimination		
		for(int i=0; i<3; i++)
			for(int j=0; j<3; j++)
				for(int k=0; k<9; k++)
					for(int l=0; l<9; l++)
						if((block[i][j].getCandidates()[k]) && (elimination[k]))
							if(puzzleIBelongTo.getRows()[block[i][j].getRow()].getMember(l).getBlock() != block[i][j].getBlock())
								if(puzzleIBelongTo.getRows()[block[i][j].getRow()].getMember(l).getCandidates()[k]){
									puzzleIBelongTo.getRows()[block[i][j].getRow()].eliminateCandidate(k+1, block[i][j].getBlock());
									elimination[k] = false;
									done = true;
								}
		return done;			
	}
	
	public boolean isValid(){
		int sum = 0;
		
		for(int i=0; i<9; i++)
			sum += members[i].getValue();
	
		if(sum == 45)
			return true;
		else
			return false;
	}
	
	public void printCandidates(){
		for(int i=0; i<9; i++)
			System.out.print(candidates[i] + " ");
	}
	
	public String toString(){
		String temp = "";
		for(int i=0; i<9; i++)
			temp += members[i].getValue() + " ";
		return temp;
	}
	
	public boolean isPossibleCandidate(int proposedCandidate){
		for(int i=0; i<candidates.length; i++)
			if(members[i].getValue() == proposedCandidate)
				return false;
		
		return true;
	}
}
