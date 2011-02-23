package puzzle;
public class Column {
	private Tile[] members = new Tile[9];
	private int[] candidates = new int[9];
	private int count = 0, columnNumber;
		
	public void setMembers(Tile t){
		members[count] = t;
		count++;
	}
	
	public void setColNumber(){
		columnNumber = members[0].getColumn();
	}
	
	public int getColNumber(){
		return columnNumber;
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
			
	public void eliminateCandidateAll(int can){
		candidates[can - 1] = 0;
		
		for(int i=0; i<9; i++)
			members[i].eliminateCandidate(can);
	}
	
	public void eliminateCandidate(int can, int block){
		for(int i=0; i<9; i++)
			if(members[i].getBlock() != block)
				members[i].eliminateCandidate(can);
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
