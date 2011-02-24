package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class BlockPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private JTextField[][] numbers = new JTextField[3][3];
	private int place = 0;
	
	public BlockPanel(){
		super();
		this.setLayout(new GridLayout(3,3));
		this.setBackground(Color.BLACK);
				
		for(int i=0; i<3; i++)
			for(int j=0; j<3; j++){
				numbers[i][j] = new JTextField();
				numbers[i][j].setFont(new Font("SansSerif", Font.PLAIN, 36));
				numbers[i][j].setDocument(new JTextFieldLimit(1));
				numbers[i][j].setHorizontalAlignment(JTextField.CENTER);
				this.add(numbers[i][j]);
			}
	}
	
	public void setTextBoxColor(Color c){
		for(int i=0; i<3; i++)
			for(int j=0; j<3; j++)
				numbers[i][j].setBackground(c);
	}
	
	public int[][] getBlockValues(){
		int[][] temp = new int[3][3];
		
		for(int i=0; i<3; i++)
			for(int j=0; j<3; j++)
				if(!numbers[i][j].getText().equals(""))
					temp[i][j] = Integer.parseInt(numbers[i][j].getText());
				else
					temp[i][j] = 0;
		
		return temp;
	}
	
	public void setValue(int value){
		int count = 0;
		
		for(int i=0; i<3; i++){
			for(int j=0; j<3; j++){
				if(count == place){
					numbers[i][j].setText(Integer.toString(value));
					break;
				}
				count++;
			}
		}
		place++;
	}
	
	public void reset(){
	for(int i=0; i<3; i++)
		for(int j=0; j<3; j++)
			numbers[i][j].setText("");
		place = 0;
	}
	
	private class JTextFieldLimit extends PlainDocument {
		private static final long serialVersionUID = 1L;
		private int m_limit;

		JTextFieldLimit(int limit) {
			super();
			m_limit = limit;
		}

		public void insertString
		(int offset, String  str, AttributeSet attr)
		throws BadLocationException {
						
			if (str == null) return;

			if ((getLength() + str.length()) <= m_limit) {
				try{
					if(Integer.parseInt(str) > 0)
						super.insertString(offset, str, attr);
				}catch (NumberFormatException n){
					
				}
			}
		}
	}
}
