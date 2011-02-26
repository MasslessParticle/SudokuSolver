package patterson.travis.sudoku;

import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Puzzle {
	private int[][] m_puzzleVals = null;
	private ArrayList<int[][]> m_solutions = new ArrayList<int[][]>();
	private int m_zeroCount = 0;	
	private int m_blockLayout[][] = null;
	private int m_size = 0;
	private int m_numBlocks = 0;
	private int m_defaultSize = 9;
	
	public Puzzle(){
		makePuzzle(emptyPuzzle(), m_defaultSize);
	}
	
	public Puzzle(String filePath){
		
		int[][] vals;
		int size = m_defaultSize;
		try {
			Scanner fin = new Scanner(new File(filePath));
			String puzzleString = "";
			while (fin.hasNext()){
				puzzleString += fin.nextLine() + "\n";
			}
			
			String[] lines = puzzleString.split("\n");
			size = lines.length;
			vals = new int[size][size];
			
			for (int i = 0; i < size; i++){
				for (int j = 0; j < size; j++){
					char val = lines[i].charAt(j);
					vals[i][j] = Character.digit(val, 10);
				}
			}
			
		} catch (FileNotFoundException e) {
			vals = emptyPuzzle();
		}
		
		makePuzzle(vals, size);
	}
	
	public Puzzle(int[][] values, int puzzleSize){
		makePuzzle(values, puzzleSize);
	}
	
	public int getSize(){
		return m_size;
	}
	
	public int getValue(int x, int y){
		return m_puzzleVals[x][y];
	}
	
	public ArrayList<int[][]> getSolutions(){
		return m_solutions;
	}
	
	public void setValue(int x, int y, int value){
		m_puzzleVals[x][y] = value;
	}
	
	public int getBlockNumber(int x, int y){
		int blockX = x / m_numBlocks;
		int blockY = y / m_numBlocks;
		
		return m_blockLayout[blockX][blockY];	
	}
	
	public int[] rowCandidates(int x){
		int[] candidates = getNewCandidateList();
		int[] existingValues = m_puzzleVals[x];
		
		for (int i = 0; i < existingValues.length; i++){
			int cellValue = existingValues[i];
			if (existingValues[i] != 0){
				candidates[cellValue - 1] = 0;
			}
		}
		return candidates;
	}
	
	public int[] columnCandidates(int y){
		int[] candidates = getNewCandidateList();
		
		for (int i = 0; i < m_size; i ++){
			int cellValue = m_puzzleVals[i][y];
			if (m_puzzleVals[i][y] != 0){
				candidates[cellValue - 1] = 0;
			}
		}
		return candidates;
	}
	
	public int[] blockCandidates(int block){
		int[] candidates = getNewCandidateList();
		Point blockIndices = getBlockIndices(block);
		int blockX = blockIndices.x;
		int blockY = blockIndices.y;
		int puzzleX = blockX * m_numBlocks;
		int puzzleY = blockY * m_numBlocks;
				
		for (int i = puzzleX; i < puzzleX + m_numBlocks; i++){
			for (int j = puzzleY; j < puzzleY + m_numBlocks; j++){
				int cellValue = m_puzzleVals[i][j];
				if (m_puzzleVals[i][j] != 0){
					candidates[cellValue - 1] = 0;
				}
			}
		}
		
		return candidates;
	}
	
	public int[] cellCandidates(int x, int y){
		int[] candidates = getNewCandidateList();
		
		if(m_puzzleVals[x][y] != 0){
			Arrays.fill(candidates, 0);
		}else {
			int[] rowCandidates = rowCandidates(x);
			int[] colCandidates = columnCandidates(y);
			int[] blockCandidates = blockCandidates(getBlockNumber(x, y));
			
			for (int i = 0; i < candidates.length; i++){
				if (rowCandidates[i] == 0 || colCandidates[i] == 0 || blockCandidates[i] == 0 ){
					candidates[i] = 0;
				}
			}
		}
		return candidates;
	}
	
	public boolean isSolved(){
		boolean valid = false;
		for (int i = 0; i < m_size; i++){
			int[] rowVals = getRowValues(i);
			int[] colVals = getColumnValues(i);
			int[] blockVals = getBlockValues(i);
			valid = validRowColumnOrBlock(rowVals);
			valid = valid && validRowColumnOrBlock(colVals);
			valid = valid && validRowColumnOrBlock(blockVals);
			if (!valid){
				return false;
			}
		}
		return true;
	}
	
	public void saveSolution(){
		if(isSolved()){
			m_solutions.add(copyPuzzleVals());
		}
	}
	
	public Point getBlockIndices(int blockNumber){
		int blockX = 0;
		int blockY = 0;
						
		//Find the block indices
		for (int i = 0; i < m_numBlocks; i ++){
			for (int j = 0; j < m_numBlocks; j++){
				if(blockNumber == m_blockLayout[i][j]){
					blockX = i;
					blockY = j;
					return new Point(blockX, blockY);
				}
			}
		}
		
		return null;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < m_size; i ++){
			for (int j = 0; j < m_size; j++){
				sb.append(m_puzzleVals[i][j]);
				sb.append(" ");
			}
			sb.append("\n");
		}
		return sb.toString();
	}
	
	private boolean validRowColumnOrBlock(int[] values){
		int[] valueCounts = new int[m_size];
		Arrays.fill(valueCounts, 0);
		
		for (int i = 0; i < valueCounts.length; i ++){
			int cellValue = values[i];
			
			if (cellValue == 0){
				return false;
			} else {
				valueCounts[cellValue - 1]++;
			}
			
			if (valueCounts[cellValue - 1] > 1){
				return false;
			}
		}
		return true;		
	}
	
	private int[] getRowValues(int x){
		return m_puzzleVals[x];
	}
	
	private int[] getColumnValues(int y){
		int[] vals = new int[m_size];
		
		for (int i = 0; i < m_size; i++){
			vals[i] = m_puzzleVals[i][y];
		}
		
		return vals;
	}
	
	private int[] getBlockValues(int blockNumber){
		int vals[] = new int[m_size];
		Point blockIndices = getBlockIndices(blockNumber);
		int blockX = blockIndices.x;
		int blockY = blockIndices.y;
		int puzzleX = blockX * m_numBlocks;
		int puzzleY = blockY * m_numBlocks;
		int valsIndex = 0;
		
		while (valsIndex < 9){
			for (int i = puzzleX; i < puzzleX + m_numBlocks; i++){
				for (int j = puzzleY; j < puzzleY + m_numBlocks; j++){
					vals[valsIndex] = m_puzzleVals[i][j];
					valsIndex++;
				}
			}
		}
		
		return vals;
	}
		
	private void makePuzzle(int[][] values, int size){
		m_size = size;
		m_puzzleVals = new int[m_size][m_size];
		for(int i=0; i<m_size; i++){
			for(int j=0; j<m_size; j++){
				m_puzzleVals[i][j] = values[i][j];

				if(values[i][j] == 0)
					m_zeroCount++;
			}
		}
		
		setupBlockLayout();
	}
	
	private int[] getNewCandidateList(){
		int[] candidates = new int[m_size];
		
		for (int i = 0; i < m_size; i++){
			candidates[i] = i+1;
		}
		
		return candidates; 
	}
		
	private void setupBlockLayout() {
		m_numBlocks = (int)Math.sqrt(m_size);
		m_blockLayout = new int[m_numBlocks][m_numBlocks];
		
		int blockNumber = 0;
		for (int i = 0; i < m_numBlocks; i++){
			for (int j = 0; j < m_numBlocks; j++){
				m_blockLayout[i][j] = blockNumber;
				blockNumber++;
			}
		}
	}
	
	private int[][] emptyPuzzle(){
		int[][] vals = new int[m_defaultSize][m_defaultSize];
		for (int i = 0; i < m_defaultSize; i++){
			for (int j = 0; j < m_defaultSize; j++){
				vals[i][j] = 0;
			}
		}
		return vals;
	}
	
	private int[][] copyPuzzleVals(){
		int[][] puzzleCopy = new int[m_size][m_size];
		for (int i = 0; i < m_size; i++){
			for (int j = 0; j < m_size; j++){
				puzzleCopy[i][j] = m_puzzleVals[i][j];
			}
		}
		return puzzleCopy;
	}
}