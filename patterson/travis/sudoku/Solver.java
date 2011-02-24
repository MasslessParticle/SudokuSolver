package patterson.travis.sudoku;

import javax.swing.JButton;


public class Solver implements Runnable{
	private Puzzle m_puzzle;
	private JButton solveButton;
	
	public Solver(Puzzle p, JButton s){
		solveButton = s;
		m_puzzle = p;
	}
	
	public void run() {
		solveButton.setText("Solving");
		solveButton.setEnabled(false);
		solvePuzzle();
		solveButton.setText("Solve");
		solveButton.setEnabled(true);
	}
	
	private void solvePuzzle(){
		//TODO: Implement puzzle Solving
	}
}