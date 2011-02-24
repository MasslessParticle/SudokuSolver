package gui;

import javax.swing.JButton;

import puzzle.Puzzle;

public class Solve implements Runnable{
	private Puzzle puzz;
	private JButton solveButton;
	
	public Solve(Puzzle p, JButton s){
		solveButton = s;
		puzz = p;
	}
	
	public void run() {
		solveButton.setText("Solving");
		solveButton.setEnabled(false);
		//TODO: IMPLEMENT Puzzle Solving on this thread		
		//puzz.solve();
		solveButton.setText("Solve");
		solveButton.setEnabled(true);
	}
}