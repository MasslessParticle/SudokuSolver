package puzzle;

public class Tile {
	private int value;
	private int blockID;
	private int columnID;
	private int rowID;
	private boolean[] tileCandidates = {true, true, true, true, true, true, true, true, true};
		
	public Tile(){
		value = 0;
		blockID = 0;
		columnID = 0;
		rowID = 0;
	}
	
	public Tile(int val){
		value = val;
		blockID = 0;
		columnID = 0;
		rowID = 0;
	}
	
	public Tile(int val, int blk, int col, int row){
		value = val;
		blockID = blk;
		columnID = col;
		rowID = row;
	}
	
	public void setValue(int val){
		value = val;
	}
	
	public void setBlock(int blk){
		blockID = blk;
	}
	
	public void setRow(int row){
		rowID = row;
	}
	
	public void setColumn(int col){
		columnID = col;
	}
	
	public int getValue(){
		return value;
	}
	
	public int getBlock(){
		return blockID;
	}
	
	public int getRow(){
		return rowID;
	}
	
	public int getColumn(){
		return columnID;
	}
	
	public boolean[] getCandidates(){
		return tileCandidates;
	}
	
	public boolean hasValue(){
		if (value != 0)
			return true;
		else
			return false;
	}
	
	public void eliminateCandidate(int candidate){
		tileCandidates[candidate - 1] = false;
	}
		
	public boolean findValue(){
		int numTrue = 0, i;
		
		for(i=0; i<9; i++)
			if(tileCandidates[i])
				numTrue++;
			
		if(numTrue == 1)
			for(i=0; i<9; i++)
				if(tileCandidates[i]){
				  value = i+1;
					return true;
				}
		
		return false;
	}
}