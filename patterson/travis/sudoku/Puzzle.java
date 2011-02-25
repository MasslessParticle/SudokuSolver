package patterson.travis.sudoku;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Puzzle {
	private int[][] m_puzzleVals = null;	
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
			int cellValue = existingValues[i] - 1;
			if (cellValue >= 0){
				if (candidates[cellValue] != 0){
					candidates[cellValue] = 0;
				}
			}
		}
		return candidates;
	}
	
	public int[] columnCandidates(int y){
		int[] candidates = getNewCandidateList();
		
		for (int i = 0; i < m_size; i ++){
			int cellValue = m_puzzleVals[i][y] - 1;
			if (cellValue >= 0){
				if (cellValue != 0){
					candidates[cellValue] = 0;
				}
			}
		}
		return candidates;
	}
	
	public int[] blockCandidates(int block){
		int[] candidates = getNewCandidateList();
		int blockX = 0;
		int blockY = 0;
				
		//Find the block indices
		for (int i = 0; i < m_numBlocks; i ++){
			for (int j = 0; j < m_numBlocks; j++){
				if(block == m_blockLayout[i][j]){
					blockX = i;
					blockY = j;
					break;
				}
			}
		}
		
		//Convert the block indices to puzzle indices
		blockX = blockX * m_numBlocks;
		blockY = blockY * m_numBlocks;
		
		for (int i = blockX; i < blockX + m_numBlocks; i++){
			for (int j = blockX; j < blockX + m_numBlocks; j++){
				int cellValue = m_puzzleVals[i][j] - 1;
				if (cellValue >= 0){
					if (cellValue != 0){
						candidates[cellValue] = 0;
					}
				}
			}
		}
		
		return candidates;
	}
	
	public int[] cellCandidates(int x, int y){
		int[] candidates = new int[m_size];
		int[] rowCandidates = rowCandidates(x);
		int[] colCandidates = columnCandidates(y);
		int[] blockCandidates = blockCandidates(getBlockNumber(x, y));
		
		for (int i = 0; i < candidates.length; i++){
			if (rowCandidates[i] == 0){
				candidates[i] = 0;
			}
			if (colCandidates[i] == 0){
				candidates[i] = 0;
			}
			if (blockCandidates[i] == 0){
				candidates[i] = 0;
			}
		}
				
		return candidates;
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
			candidates[i] = i;
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
}