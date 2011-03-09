package patterson.travis.sudoku;

import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 * @author Travis Patterson
 * @copyright 2011
 * Class representing an nxn sudoku puzzle
 */
public class Puzzle {
	private int[][] m_puzzleVals = null;
	private ArrayList<int[][]> m_solutions = new ArrayList<int[][]>();
	private int m_blockLayout[][] = null;
	private int m_size = 0;
	private int m_numBlocks = 0;
	private int m_defaultSize = 9;
	
	/**
	 * Default constructor creates an empty 9x9 puzzle
	 */
	public Puzzle(){
		makePuzzle(emptyPuzzle(), m_defaultSize);
	}
	
	/**
	 * Creates an n x n puzzle from a file representation
	 * @param filePath Path to the file containing the puzzle
	 */
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
	
	/**
	 * Creates a new puzzleSize x puzzleSize puzzle filled with values
	 * from the given array.
	 * @param values Array of values for the puzzle
	 * @param puzzleSize Size of the puzzle
	 */
	public Puzzle(int[][] values, int puzzleSize){
		makePuzzle(values, puzzleSize);
	}
	
	/**
	 * @return The size of the puzzle
	 */
	public int getSize(){
		return m_size;
	}
	
	/**
	 * @param x The X coordinate in the puzzle of the value
	 * @param y The Y coordinate in the puzzle of the value
	 * @return The value at the given X,Y coordinate.
	 */
	public int getValue(int x, int y){
		return m_puzzleVals[x][y];
	}
	
	/**
	 * @return A copy of the value array representing the puzzle.
	 */
	public int[][] getValues(){
		int[][] puzzleVals = new int[m_size][m_size];
		
		for (int i = 0; i < m_size; i++){
			for (int j = 0; j < m_size; j++){
				puzzleVals[i][j] = m_puzzleVals[i][j];
			}
		}
		
		return puzzleVals;
	}
	
	/**
	 * @return A list of all found solutions to this puzzle.
	 */
	public ArrayList<int[][]> getSolutions(){
		return m_solutions;
	}
	
	/**
	 * Sets a value in the puzzle at the given X,Y coordinate
	 * @param x The X coordinate of the value
	 * @param y The Y value of the coordinate.
	 * @param value The value to place at X,Y
	 */
	public void setValue(int x, int y, int value){
		m_puzzleVals[x][y] = value;
	}
	
	/**
	 * Return the block number for a cell at an X,Y coordinate
	 * @param x The X coordinate of the cell
	 * @param y The Y coordinate of the cell
	 * @return The block number of the X,Y coordinate
	 */
	public int getBlockNumber(int x, int y){
		int blockX = x / m_numBlocks;
		int blockY = y / m_numBlocks;
		
		return m_blockLayout[blockX][blockY];	
	}
	
	/**
	 * @param x X coordinate of row
	 * @return All number candidates for the given row number
	 */
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
	
	/**
	 * @param y Y coordinate of row
	 * @return All number candidates for the given column number
	 */
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
	
	/**
	 * @param block Block Number
	 * @return All number candidates for the given block number
	 */
	public int[] blockCandidates(int block){
		int[] candidates = getNewCandidateList();
		Point blockIndices = getBlockIndices(block);
		int blockX = blockIndices.x;
		int blockY = blockIndices.y;
						
		for (int i = blockX; i < blockX + m_numBlocks; i++){
			for (int j = blockY; j < blockY + m_numBlocks; j++){
				int cellValue = m_puzzleVals[i][j];
				if (m_puzzleVals[i][j] != 0){
					candidates[cellValue - 1] = 0;
				}
			}
		}
		
		return candidates;
	}
	
	/**
	 * @param x Cell's X coordinate
	 * @param y Cell's Y coordinate
	 * @return the candidates for the cell at X,Y
	 */
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
	
	/**
	 * @return Whether or not the puzzle is solved.
	 */
	public boolean isSolved(){
		boolean valid = false;
		for (int i = 0; i < m_size; i++){
			valid = validRow(i);
			valid = valid && validColumn(i);
			valid = valid && validBlock(i);
			if (!valid){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Saves the current solution into the solution list
	 */
	public void saveSolution(){
		if(isSolved()){
			m_solutions.add(copyPuzzleVals());
		}
	}
	
	/**
	 * @param y The row number to check
	 * @return true if the given row does not violate any sudoku rules else false
	 */
	public boolean validRow(int y){
		int[] rowVals = getRowValues(y);
		return validRowColumnOrBlock(rowVals);
	}
	
	/**
	 * @param x The column number to check
	 * @return true if the given column does not violate any sudoku rules else false
	 */
	public boolean validColumn(int x){
		int[] colVals = getColumnValues(x);
		return validRowColumnOrBlock(colVals);
	}
	
	/**
	 * @param blockNum The block number to check
	 * @return true if the given block does not violate any sudoku rules else false
	 */
	public boolean validBlock(int blockNum){
		int[] blockVals = getBlockValues(blockNum);
		return validRowColumnOrBlock(blockVals);
	}
	
	/**
	 * @param blockNumber Target block
	 * @return The X,Y coordinate of the upper left cell in the target block number
	 */
	public Point getBlockIndices(int blockNumber){
		int blockX = 0;
		int blockY = 0;
						
		//Find the block indices
		for (int i = 0; i < m_numBlocks; i ++){
			for (int j = 0; j < m_numBlocks; j++){
				if(blockNumber == m_blockLayout[i][j]){
					blockX = i;
					blockY = j;
					return new Point(blockX * m_numBlocks, blockY * m_numBlocks);
				}
			}
		}
		
		return null;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
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
	
	
	/**
	 * @return True if the given candidates do not violate the rules of sudoku
	 */
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
	
	
	/**
	 * @return The puzzle values for a given row: x
	 */
	private int[] getRowValues(int x){
		return m_puzzleVals[x];
	}
	
	/**
	 * @return The puzzle values for a given column: y
	 */
	private int[] getColumnValues(int y){
		int[] vals = new int[m_size];
		
		for (int i = 0; i < m_size; i++){
			vals[i] = m_puzzleVals[i][y];
		}
		
		return vals;
	}
	
	/**
	 * @return The puzzle values for a given block: blockNumber
	 */
	private int[] getBlockValues(int blockNumber){
		int vals[] = new int[m_size];
		Point blockIndices = getBlockIndices(blockNumber);
		int blockX = blockIndices.x;
		int blockY = blockIndices.y;
		int valsIndex = 0;
		
		while (valsIndex < m_size){
			for (int i = blockX; i < blockX + m_numBlocks; i++){
				for (int j = blockY; j < blockY + m_numBlocks; j++){
					vals[valsIndex] = m_puzzleVals[i][j];
					valsIndex++;
				}
			}
		}
		
		return vals;
	}
		
	/**
	 * Initializes a new puzzle with the given values and size.
	 */
	private void makePuzzle(int[][] values, int size){
		m_size = size;
		m_puzzleVals = new int[m_size][m_size];
		for(int i=0; i<m_size; i++){
			for(int j=0; j<m_size; j++){
				m_puzzleVals[i][j] = values[i][j];
			}
		}
		
		setupBlockLayout();
	}
	
	/**
	 * @return New full candidates list
	 */
	private int[] getNewCandidateList(){
		int[] candidates = new int[m_size];

		for (int i = 0; i < m_size; i++){
			candidates[i] = i+1;
		}
		
		return candidates; 
	}
		
	/**
	 * Determines and maps blocks for later other calculations
	 */
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
	
	/**
	 * @return A 9x9 puzzle initialized to 0
	 */
	private int[][] emptyPuzzle(){
		int[][] vals = new int[m_defaultSize][m_defaultSize];
		for (int i = 0; i < m_defaultSize; i++){
			for (int j = 0; j < m_defaultSize; j++){
				vals[i][j] = 0;
			}
		}
		return vals;
	}
		
	/**
	 * @return A copy of the values in the puzzle.
	 */
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