package patterson.travis.sudoku;

import patterson.travis.sudoku.gui.GamePanel;


public class Solver implements Runnable{
	private Puzzle m_puzzle;
	private GamePanel m_gamePanel;
	private boolean m_solved = true;
	
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
	
	private void solvePuzzle(){
		//TODO: Implement puzzle Solving
	}
}