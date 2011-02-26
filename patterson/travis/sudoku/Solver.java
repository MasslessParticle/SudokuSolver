package patterson.travis.sudoku;

import java.util.ArrayList;

import patterson.travis.sudoku.gui.GamePanel;


public class Solver implements Runnable{
	private Puzzle m_puzzle;
	private GamePanel m_gamePanel;
	private boolean m_solved = false;
	private volatile boolean m_isSolving = false;
	
	public Solver(Puzzle puzzle, GamePanel gamePanel){
		m_puzzle = puzzle;
		m_gamePanel = gamePanel;
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
		
		if(!m_puzzle.isSolved()){
			m_solved = false;
			recursiveBacktrack();
			m_solved = true;
		}
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
		for (int i = 0; i < size; i ++){
			findHiddinSingleRow(size, i);
			findHiddinSingleColumn(size, i);
			findHiddinSingleBlock(size, i);
		}
		return false;
	}
	
	private boolean findHiddinSingleColumn(int size, int y) {
		ArrayList<int[]> allCandidates = new ArrayList<int[]>();
		boolean valueSet = false;
		
		for(int i = 0; i < size; i++){
			allCandidates.add(m_puzzle.cellCandidates(i, y));
		}
		
		for (int i = 0; i < size; i ++){
			int count = 0;
			for (int j = 0; j < size; j++){
				if (allCandidates.get(j)[i] > 0){
					count++;
				}
			}
			if (count == 1){
				m_puzzle.setValue(i, y, i+1);
				m_gamePanel.update();
				valueSet = true;
			}
		}
		return valueSet;
	}

	private void findHiddinSingleRow(int size, int x) {
		// TODO Auto-generated method stub
		
	}

	private void findHiddinSingleBlock(int size, int blockNumber) {
		// TODO Auto-generated method stub
		
	}

	private void recursiveBacktrack() {
		// TODO Auto-generated method stub
		
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