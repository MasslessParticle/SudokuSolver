package patterson.travis.sudoku;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

import patterson.travis.sudoku.gui.GamePanel;


public class Solver implements Runnable{
	private Puzzle m_puzzle;
	private GamePanel m_gamePanel;
	private boolean m_solved = false;
	private volatile boolean m_isSolving = false;
	
	public Solver(Puzzle puzzle, GamePanel gamePanel){
		m_puzzle = puzzle;
		m_gamePanel = gamePanel;
		initializePuzzle();
	}
	
	private void initializePuzzle() {
		int size = m_puzzle.getSize();
		int[][] values = m_gamePanel.getValues();
		
		for (int i = 0; i < size; i++){
			for (int j = 0; j < size; j++){
				m_puzzle.setValue(i, j, values[i][j]);
			}
		}
	}

	public void run() {
		solvePuzzle();
	}
	
	public boolean isSolved(){
		return m_solved;
	}
	
	public void stopSolving(){
		m_isSolving = false;
	}
	
	private void solvePuzzle(){
		m_isSolving = true;
		while (m_isSolving && !m_solved){
			m_solved = !tryNakedSingle();
			m_solved = m_solved && !tryHiddenSingle();			
		}
		
		if(m_isSolving){
			recursiveBacktrack(0,0, m_puzzle.getSize());
		}
		
		if(m_puzzle.isSolved()){
			m_gamePanel.update();
		}
		
		m_gamePanel.startSolvingButton().setEnabled(true);
		m_gamePanel.stopSolvingButton().setEnabled(false);
	}
	
	private boolean tryNakedSingle() {
		int puzzleSize = m_puzzle.getSize();
		boolean foundValue = false;
		
		for (int i = 0; i < puzzleSize; i++){
			for (int j = 0; j < puzzleSize; j++){
				int[] cellCandidates = m_puzzle.cellCandidates(i, j);
				Integer[] strippedCandidates = stripZeros(cellCandidates);
				if(strippedCandidates.length == 1){
					int value = strippedCandidates[0];
					m_puzzle.setValue(i, j, value);
					m_gamePanel.update();
					foundValue = true;
				}
			}
		}
		return foundValue;
	}
	
	private boolean tryHiddenSingle() {
		int size = m_puzzle.getSize();
		boolean foundValue = false;
		for (int i = 0; i < size; i ++){
			foundValue = findHiddinSingleRow(size, i);
			foundValue = foundValue || findHiddinSingleColumn(size, i);
			foundValue = foundValue || findHiddinSingleBlock(size, i);
		}
		return foundValue;
	}
	
	private boolean findHiddinSingleColumn(int size, int y) {
		ArrayList<int[]> allCandidates = new ArrayList<int[]>();
		boolean valueSet = false;
		
		for(int i = 0; i < size; i++){
			allCandidates.add(m_puzzle.cellCandidates(i, y));
		}
		
		for (int i = 0; i < size; i ++){
			int count = 0;
			int lastCandidateLocation = -1;
			for (int j = 0; j < size; j++){
				if (allCandidates.get(j)[i] > 0){
					lastCandidateLocation = j;
					count++;
				}
			}
			if (count == 1){
				m_puzzle.setValue(lastCandidateLocation, y, i+1);
				m_gamePanel.update();
				valueSet = true;
			}
		}
		return valueSet;
	}

	private boolean findHiddinSingleRow(int size, int x) {
		ArrayList<int[]> allCandidates = new ArrayList<int[]>();
		boolean valueSet = false;
		
		for(int i = 0; i < size; i++){
			allCandidates.add(m_puzzle.cellCandidates(x, i));
		}
		
		for (int i = 0; i < size; i ++){
			int count = 0;
			int lastCandidateLocation = -1;
			for (int j = 0; j < size; j++){
				if (allCandidates.get(j)[i] > 0){
					lastCandidateLocation = j;
					count++;
				}
			}
			if (count == 1){
				m_puzzle.setValue(x, lastCandidateLocation, i+1);
				m_gamePanel.update();
				valueSet = true;
			}
		}
		return valueSet;
	}

	private boolean findHiddinSingleBlock(int size, int blockNumber) {
		HashMap<Point,int[]> allCandidates = new HashMap<Point,int[]>();
		boolean valueSet = false;
		int blockSize = (int)Math.sqrt(size);
		Point blockIndices = m_puzzle.getBlockIndices(blockNumber);
		int blockX = blockIndices.x;
		int blockY = blockIndices.y;
		
		for(int i = blockX; i < blockX + blockSize; i++){
			for (int j = blockY; j < blockY + blockSize; j++){
				allCandidates.put(new Point(i,j), m_puzzle.cellCandidates(i, j));
			}
		}
		
		Point[] blockCells = allCandidates.keySet().toArray(new Point[0]);
		for (int i = 0; i < size; i ++){
			int count = 0;
			int lastCandidateLocation = -1;
			for (int j = 0; j < size; j++){
				Point blockCell = blockCells[j];
				if (allCandidates.get(blockCell)[i] > 0){
					lastCandidateLocation = j;
					count++;
				}
			}
			if (count == 1){
				Point blockCell = blockCells[lastCandidateLocation];
				m_puzzle.setValue(blockCell.x, blockCell.y, i+1);
				m_gamePanel.update();
				valueSet = true;
			}
		}
		return valueSet;
	}

	private void recursiveBacktrack(int x, int y, int size) {
		Integer[] candidates = stripZeros(m_puzzle.cellCandidates(x, y));
				
		if(m_isSolving){
			Point nextCell = getNextCell(x, y, size);
			
			if (m_puzzle.getValue(x, y) == 0){		
				for (Integer candidate : candidates) {
					m_puzzle.setValue(x, y, candidate);
															
					if(nextCell != null){
						recursiveBacktrack(nextCell.x, nextCell.y, size);
					}
					
					if(!m_isSolving){
						return;
					}
					
					if(m_puzzle.isSolved()){
						m_puzzle.saveSolution();
						m_gamePanel.update();
					}
				}
				
				if(m_isSolving){
					m_puzzle.setValue(x, y, 0);
				}
			} else {
				if(nextCell != null){
					recursiveBacktrack(nextCell.x, nextCell.y, size);
				}
			}
		}
	}
		
	private Point getNextCell(int x, int y, int size) {
		if (y + 1 < size){
			return new Point(x, y + 1);
		} else if (x + 1 < size){
			return new Point(x + 1, 0);
		} else {
			return null;
		}		
	}

	private Integer[] stripZeros(int[] values){
		ArrayList<Integer> strippedValueList = new ArrayList<Integer>();
				
		for (int i = 0; i < values.length; i++){
			if (values[i] != 0){
				strippedValueList.add(values[i]);
			}
		}
		
		return strippedValueList.toArray(new Integer[0]);
	}
}