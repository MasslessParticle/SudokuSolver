package patterson.travis.sudoku.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import patterson.travis.sudoku.Puzzle;

public class GamePanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private JTextField[][] m_guiPuzzle;
	private Puzzle m_puzzle;
	private JLabel m_numSolutions;
	private JLabel m_solutionSelection;
	private JButton m_startSolving;
	private JButton m_stopSolving;
	
	public GamePanel(Puzzle puzzle, JLabel numSolutions, JLabel solSelevtion, JButton startSolving, JButton stopSolving){
		m_puzzle = puzzle;
		m_numSolutions = numSolutions;
		m_solutionSelection = solSelevtion;
		m_startSolving = startSolving;
		m_stopSolving = stopSolving;

		setupPanel();
		update();
	}
	
	public JButton startSolvingButton(){
		return m_startSolving;
	}
	
	public JButton stopSolvingButton(){
		return m_stopSolving;
	}
	
	public void setPuzzle(Puzzle puzzle){
		m_puzzle = puzzle;
		update();
	}
	
	public int[][] getValues(){
		int size = m_puzzle.getSize();
		int[][] values = new int[size][size];
		for (int i = 0; i < size; i ++){
			for (int j = 0; j < size; j ++){
				try {
					values[i][j] = Integer.parseInt(m_guiPuzzle[i][j].getText());
				} catch (NumberFormatException e){
					values[i][j] = 0;
				}
			}
		}
		return values;
	}
	
	public void update(){
		int size = m_puzzle.getSize();
		int[][] displaySolution = null;
		ArrayList<int[][]> solutions = m_puzzle.getSolutions();
		
		if (solutions.size() > 0){
			String solString = m_solutionSelection.getText();
			if(!solString.equals("")){ 
				int solutionNum = Integer.parseInt(solString);
				if (solutionNum == 0) { //make sure the displayed number is 1 indexed.
					solutionNum++;
					String displayNumber = Integer.toString(solutionNum);
					m_solutionSelection.setText(displayNumber);
				}
				
				solutionNum -= 1; //convert to 0 index
				String numSolutions = Integer.toString(solutions.size());
				m_numSolutions.setText(numSolutions);
				displaySolution = solutions.get(solutionNum);
			} 
		} else if(m_puzzle.isSolved()){
			m_puzzle.saveSolution();
			update();
			return;
		} else {
			displaySolution = m_puzzle.getValues();
		}
		
		for(int i = 0; i < size; i++){
			for (int j = 0; j < size; j++){
				String value = Integer.toString(displaySolution[i][j]);
				m_guiPuzzle[i][j].setText(value);
			}
		}
	}
	
	private void setupPanel() {
		int size = m_puzzle.getSize();
		Color gray = new Color(220, 220, 220);
				
		m_guiPuzzle = new JTextField[size][size];
		setLayout(new GridLayout(9,9));
		
		for (int i = 0; i < size; i ++){
			for (int j = 0; j < size; j++){
				JTextField field = makeNewTextField(i,j);
				m_guiPuzzle[i][j] = field;
				
				if (m_puzzle.getBlockNumber(i, j) % 2 != 0){
					field.setBackground(gray);
				}
				add(field);
			}
		}				
	}
		
	private JTextField makeNewTextField(int xPos, int yPos){
		JTextField field = new JTextField(1);
		field.setFont(new Font("SansSerif", Font.PLAIN, 36));
		field.setDocument(new JTextFieldLimit(1));
		field.setHorizontalAlignment(JTextField.CENTER);
		return field;
	}
		
	private class JTextFieldLimit extends PlainDocument {
		private static final long serialVersionUID = 1L;
		private int m_limit;
		
		JTextFieldLimit(int limit) {
			super();
			m_limit = limit;
		}

		public void insertString (int offset, String str, AttributeSet attr) {
			if (str != null){
				int length = getLength() + str.length();
				if (length <= m_limit) {
					try{
						int val = Integer.parseInt(str);
						if (val > 0){
							super.insertString(offset, str, attr);
						}
					}catch (NumberFormatException n){
						return;
					} catch (BadLocationException e) {
						return;
					}
				}
			}
		}
	}
}
