package puzzle;
import gui.Solver;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Puzzle {
	private Tile[][] puzzle = new Tile[9][9];	
	private Block[] blocks = new Block[9];
	private Row[] rows = new Row[9];
	private Column[] columns = new Column[9];
	private int zeroCount = 0;	
	private volatile boolean isDone = false;
	private Solver mySolver;
	private int numSolutions;
	
	public Puzzle(int[][] values){
		for(int i=0; i<9; i++){
			for(int j=0; j<9; j++){
				puzzle[i][j] = new Tile(values[i][j], Puzzle.getBlockNumber(i, j), j, i);

				if(values[i][j] == 0)
					zeroCount++;
			}
		}
		
//	fills the rows columns and blocks with the tiles just added to the puzzle
		for(int i=0; i<9; i++){
			columns[i] = new Column();
			blocks[i] = new Block(this);
			rows[i] = new Row();
		}
		for(int i=0; i<9; i++)	
			for(int j=0; j<9; j++){
				columns[puzzle[i][j].getColumn()].setMembers(puzzle[i][j]);
				rows[puzzle[i][j].getRow()].setMembers(puzzle[i][j]);
				blocks[puzzle[i][j].getBlock()].setMembers(puzzle[i][j]);
			}

		//fills the candidate arrays of the rows/blocks/columns
		updatePuzzle();

		for(int i=0; i<9; i++){
			columns[i].refreshCandidates();
			rows[i].refreshCandidates();
			blocks[i].refreshCandidates();
		}

		for(int i=0; i<9; i++){
			blocks[i].setBlockNumber();
			columns[i].setColNumber();
			rows[i].setRowNumber();
		}

		updatePuzzle();
	}

	public Puzzle(String fileName){
		Scanner fin = null;

		try{
			File file = new File(fileName);
			fin = new Scanner(file);
		}
		catch(IOException ex){System.out.println("File not Found");}

		//fills the puzzle with the tile's value. block, col, and row index start at 0
		for(int i=0; fin.hasNext(); i++){
			String row = fin.nextLine();
			for(int j=0; j<9; j++){
				puzzle[i][j] = new Tile(row.charAt(j)%48, Puzzle.getBlockNumber(i, j), j, i);

				if(row.charAt(j)%48 == 0)
					zeroCount++;
			}
		}
		//fills the rows columns and blocks with the tiles just added to the puzzle
		for(int i=0; i<9; i++){
			columns[i] = new Column();
			blocks[i] = new Block(this);
			rows[i] = new Row();
		}
		for(int i=0; i<9; i++)	
			for(int j=0; j<9; j++){
				columns[puzzle[i][j].getColumn()].setMembers(puzzle[i][j]);
				rows[puzzle[i][j].getRow()].setMembers(puzzle[i][j]);
				blocks[puzzle[i][j].getBlock()].setMembers(puzzle[i][j]);
			}

		//fills the candidate arrays of the rows/blocks/columns
		updatePuzzle();

		for(int i=0; i<9; i++){
			columns[i].refreshCandidates();
			rows[i].refreshCandidates();
			blocks[i].refreshCandidates();
		}

		for(int i=0; i<9; i++){
			blocks[i].setBlockNumber();
			columns[i].setColNumber();
			rows[i].setRowNumber();
		}

		updatePuzzle();
	}

	private void updatePuzzle(){
		for(int i=0; i<9; i++)
			for(int j=0; j<9; j++)
				if(!(puzzle[i][j].hasValue())){
					for(int k=0; k<9; k++)
						for(int l=0; l<9; l++)
							if(puzzle[i][j].equals(puzzle[k][l]) && puzzle[k][l].hasValue())
								puzzle[i][j].eliminateCandidate(puzzle[k][l].getValue());
				}
				else
					for(int m=0; m<9; m++)
						puzzle[i][j].eliminateCandidate(m+1);

		for(int i=0; i<9; i++){
			columns[i].refreshCandidates();
			rows[i].refreshCandidates();
			blocks[i].refreshCandidates();
		}
	}

	public void setSolver(Solver s){
		mySolver = s;
	}
	
	private boolean isSolved(){
		for(int i=0; i<9; i++)
			if((!rows[i].isValid()) || (!columns[i].isValid()) || (!blocks[i].isValid()))
				return false;

		return true;
	}

	private void recursiveSolve(int x, int y){
		Tile t = null;		
		if(x < 9){
			t = puzzle[x][y];

			if(t.hasValue()){
				pickNextToSolve(t);
			}
			else{
				for(int i=1; i<10; i++){
					if(t.getCandidates()[i-1]){
						if(rows[t.getRow()].isPossibleCandidate(i) && columns[t.getColumn()].isPossibleCandidate(i) && blocks[t.getBlock()].isPossibleCandidate(i)){
							t.setValue(i);
							pickNextToSolve(t);
						}
					}
				}
				t.setValue(0);
			}
		}
	}

	private void pickNextToSolve(Tile t){
		if(isSolved()){
			saveSolution();
		}

		if(t.getColumn() == 8 && t.getRow() < 9)
			recursiveSolve(t.getRow()+1, 0);
		else if(t.getColumn() == 9 && t.getRow() == 9)
			;
		else
			recursiveSolve(t.getRow(), t.getColumn()+1);
	}

	private void saveSolution(){
		Solution s;
		int[][] temp = new int[9][9];
		
		for(int i=0; i<9; i++)
			for(int j=0; j<9; j++)
				temp[i][j] = puzzle[i][j].getValue();

		s = new Solution(temp);
		
		mySolver.addSolution(s);
		numSolutions++;
	}

	Row[] getRows(){
		return rows;
	}

	Column[] getCols(){
		return columns;
	}

	public void solve(){
		boolean done = false; //flags if the puzzle is solved as far as it can be
		boolean nsDone = false; //flags if all naked singles have been found for now
		boolean hsDone = false; //flags if all hidden singles have been found for now
		numSolutions = 0;
		
		//solving techniques
		while(!done){
			done = true;
			nsDone = hsDone = false;
			//Naked single
			while(!nsDone){
				nsDone = true;
				for(int i=0; i<9; i++)
					for(int j=0; j<9; j++)
						if(puzzle[i][j].findValue()){ 
							done = false;
							nsDone = false;
							updatePuzzle();
						}
			}
			//Hidden Single
			while(!hsDone){
				hsDone = true;
				for(int i=0; i<9; i++)
					if(rows[i].findHiddenSingle() || columns[i].findHiddenSingle() || blocks[i].findHiddenSingle()){
						done = false;
						hsDone = false;
						updatePuzzle();
					}
			}
			// row/col block interaction
			if(!(isSolved())){
				for(int i=0; i<9; i++)
					if(blocks[i].cbInteraction() || blocks[i].rbInteraction())
						done = false;
				updatePuzzle();
			}
		}

		recursiveSolve(0,0);
		
		if(numSolutions == 0)
			mySolver.addSolution(null);
	}

	public int[][] getPuzzle(){
		int[][] temp = new int[9][9];

		for(int i=0; i<9; i++)
			for(int j=0; j<9; j++)
				temp[i][j] = puzzle[i][j].getValue();

		return temp;
	}

	public boolean getIsDone(){
		return isDone;
	}
		
	public static int getBlockNumber(int i, int j){
		if(j<3 && i<3){
			return 0;
		} else if(j<6 && i<3){
			return 1;
		} else if(j<9 && i<3){
			return 2;
		} else if(j<3 && i<6){
			return 3;
		} else if(j<6 && i<6){
			return 4;
		} else if(j<9 && i<6){
			return 5;
		} else if(j<3 && i<9){
			return 6;
		} else if(j<6 && i<9){
			return 7;
		} else
			return 8;
	}
	
	public String toString(){
		int[][] puzz = getPuzzle();
		StringBuilder sb = new StringBuilder(); 
		
		for (int i = 0; i < 9; i ++) {
			for (int j = 0; j < 9; j ++) {
				sb.append(puzz[i][j]);
				sb.append(" ");
			}
			sb.append("\n");
		}
				
		return sb.toString();		
	}
}