package patterson.travis.sudoku.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import patterson.travis.sudoku.Puzzle;

public class GamePanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private JTextField[][] m_guiPuzzle = null;
	private Puzzle m_puzzle;
	
	public GamePanel(Puzzle puzzle){
		m_puzzle = puzzle;
		setupPanel();
		update();
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
	
	public void setPuzzle(Puzzle puzzle){
		m_puzzle = puzzle;
		update();
	}
	
	public int[][] getValues(){
		int size = m_puzzle.getSize();
		int[][] values = new int[size][size];
		for (int i = 0; i < size; i ++){
			for (int j = 0; j < size; j ++){
				values[i][j] = Integer.parseInt(m_guiPuzzle[i][j].getText());
			}
		}
		return values;
	}
	
	private JTextField makeNewTextField(int xPos, int yPos){
		JTextField field = new JTextField(1);
		field.setFont(new Font("SansSerif", Font.PLAIN, 36));
		field.setDocument(new JTextFieldLimit(1, xPos, yPos));
		field.setHorizontalAlignment(JTextField.CENTER);
		return field;
	}
	
	public void update(){
		int size = m_puzzle.getSize();
		for(int i = 0; i < size; i++){
			for (int j = 0; j < size; j++){
				String value = Integer.toString(m_puzzle.getValue(i, j));
				m_guiPuzzle[i][j].setText(value);
			}
		}
	}
	
	private class JTextFieldLimit extends PlainDocument {
		private static final long serialVersionUID = 1L;
		private int m_limit;
		private int m_xPos;
		private int m_yPos;
		
		JTextFieldLimit(int limit, int xPos, int yPos) {
			super();
			m_limit = limit;
			m_xPos = xPos;
			m_yPos = yPos;
		}

		public void insertString (int offset, String str, AttributeSet attr) {
			if (str != null){
				int length = getLength() + str.length();
				if (length <= m_limit) {
					try{
						int val = Integer.parseInt(str);
						if (val > 0){
							super.insertString(offset, str, attr);
							m_puzzle.setValue(m_xPos, m_yPos, val);
						}
					}catch (NumberFormatException n){
						return;
					} catch (BadLocationException e) {
						return;
					}
				}
			}
		}
		
		protected void removeUpdate(AbstractDocument.DefaultDocumentEvent chng) {
			m_puzzle.setValue(m_xPos, m_yPos, 0);
		}
	}
}
