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

/**
 * @author Travis Patterson
 * @copyright 2011
 * Visual Representation of a puzzle.
 */
public class GamePanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private JTextField[][] m_guiPuzzle;
	private Puzzle m_puzzle;
	private JLabel m_numSolutions;
	private JLabel m_solutionSelection;
	private JButton m_startSolving;
	private JButton m_stopSolving;
	
	/**
	 * @param puzzle The puzzle to represent
	 * @param numSolutions The label containing the number of solutions
	 * @param solSelection The label containing the current solution number
	 * @param startSolving The button to start the solver
	 * @param stopSolving The button to stop the solver.
	 */
	public GamePanel(Puzzle puzzle, JLabel numSolutions, JLabel solSelection, JButton startSolving, JButton stopSolving){
		m_puzzle = puzzle;
		m_numSolutions = numSolutions;
		m_solutionSelection = solSelection;
		m_startSolving = startSolving;
		m_stopSolving = stopSolving;

		setupPanel();
		update();
	}
	
	/**
	 * @return The button to start the solver
	 */ 
	public JButton startSolvingButton(){
		return m_startSolving;
	}
	
	/**
	 * @return The button to stop the solver
	 */ 
	public JButton stopSolvingButton(){
		return m_stopSolving;
	}
	
	public void setPuzzle(Puzzle puzzle){
		m_puzzle = puzzle;
		update();
	}
	
	/**
	 * @return The numeric values of the visual representation
	 */
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
	
	/**
	 *  Refresh the visual representation with the current solution
	 */
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
	
	/**
	 * initializes the representation with n x n text fields
	 */
	private void setupPanel() {
		int size = m_puzzle.getSize();
		Color gray = new Color(220, 220, 220);
				
		m_guiPuzzle = new JTextField[size][size];
		setLayout(new GridLayout(9,9));
		
		for (int i = 0; i < size; i ++){
			for (int j = 0; j < size; j++){
				JTextField field = makeNewTextField();
				m_guiPuzzle[i][j] = field;
				
				if (m_puzzle.getBlockNumber(i, j) % 2 != 0){
					field.setBackground(gray);
				}
				add(field);
			}
		}				
	}
	
	/**
	 * @return An initialized text field
	 */
	private JTextField makeNewTextField(){
		JTextField field = new JTextField(1);
		field.setFont(new Font("SansSerif", Font.PLAIN, 36));
		field.setDocument(new JTextFieldLimit(1));
		field.setHorizontalAlignment(JTextField.CENTER);
		return field;
	}
		
	/**
	 * Class to handle TextArea input
	 */
	private class JTextFieldLimit extends PlainDocument {
		private static final long serialVersionUID = 1L;
		private int m_limit;
		
		/**
		 * @param limit The max size of a TextArea in the game representation
		 */
		JTextFieldLimit(int limit) {
			super();
			m_limit = limit;
		}

		/* (non-Javadoc)
		 * @see javax.swing.text.PlainDocument#insertString(int, java.lang.String, javax.swing.text.AttributeSet)
		 */
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
