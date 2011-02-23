package gui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.plaf.basic.BasicArrowButton;

import puzzle.Puzzle;
import puzzle.Solution;

public class Solver extends JFrame{
	private static final long serialVersionUID = 1L;
	private Puzzle puzzle;
	private JButton solve = new JButton("Solve");
	private JToolBar toolBar = new JToolBar();
	private JButton openFile = new JButton("Open File");
	private JPanel gamePanel = new JPanel();
	private BlockPanel[] blocks = new BlockPanel[9];
	private JFileChooser findPuzzle = new JFileChooser();
	private JFrame chooseFrame = new JFrame();
	private ArrayList<Solution> solutions = new ArrayList<Solution>();
	private JLabel viewing = new JLabel("Viewing soluion ");
	private JLabel of = new JLabel(" of " + solutions.size());
	private JTextField selectSolution = new JTextField("0");
	private int viewingSolution = 0;
	
	public void start(){
		this.setTitle("Travis' Sudoku Solver");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setPreferredSize(new Dimension(480, 640));
		this.setLayout(new BorderLayout());
		this.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width/2) - (480/2), (Toolkit.getDefaultToolkit().getScreenSize().height/2) - (640/2));
		this.setIconImage(Toolkit.getDefaultToolkit().createImage(Solver.class.getResource("untitled.GIF")));
				
		setupChooseFrame();
				
		openFile.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				chooseFrame.setVisible(true);
			}
		});
		toolBar.add(openFile);
		this.add(toolBar, BorderLayout.NORTH);

		gamePanel.setBackground(Color.BLACK);
		gamePanel.setLayout(new GridLayout(3,3,4,4));
		setUpGamePanel();

		this.add(gamePanel, BorderLayout.CENTER);

		solve.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				int[][] temp = getDisplayedValues();
				puzzle = new Puzzle(temp);
				System.out.println(puzzle);
				puzzle.setSolver(Solver.this);
				clear();
				new Thread(new Solve(puzzle, solve), "Solver").start();
			}
		});
		
		JPanel southPanel = new JPanel();
		
		selectSolution.setPreferredSize(new Dimension(150, 20));
		selectSolution.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				try{
					int wantsToSee = Integer.parseInt(selectSolution.getText());
					if(wantsToSee > 0 || wantsToSee <= solutions.size()){
						viewingSolution = wantsToSee;
						setValues(solutions.get(viewingSolution-1).getGrid());
					}
				}catch(NumberFormatException ne){
				}finally{
					selectSolution.setText(Integer.toString(viewingSolution));
				}
			}
		});
		
		southPanel.add(solve);
		southPanel.add(viewing);
		southPanel.add(selectSolution);
		southPanel.add(of);
		
		BasicArrowButton next = new BasicArrowButton(JButton.EAST);
		BasicArrowButton prev = new BasicArrowButton(JButton.WEST);
		
		next.setToolTipText("Click to view next solution.");
		prev.setToolTipText("Click to view previous solution.");
		
		next.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				if(viewingSolution+1 <= solutions.size()){
					viewingSolution++;
					setValues(solutions.get(viewingSolution-1).getGrid());
					selectSolution.setText(Integer.toString(viewingSolution));
				}
			}
		});
		
		prev.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				if(viewingSolution-1 > 0){
					viewingSolution--;
					setValues(solutions.get(viewingSolution-1).getGrid());
					selectSolution.setText(Integer.toString(viewingSolution));
				}
			}
		});
				
		southPanel.add(prev);
		southPanel.add(next);
		
		this.add(southPanel, BorderLayout.SOUTH);
		this.pack();
		this.setVisible(true);
	}

	private void setupChooseFrame() {
		chooseFrame.add(findPuzzle);
		chooseFrame.setPreferredSize(new Dimension(480, 320));
		chooseFrame.pack();
		chooseFrame.setLocation(this.getLocation());
		findPuzzle.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if(e.getActionCommand().equals("ApproveSelection")){
					try{
						Scanner fin = new Scanner(findPuzzle.getSelectedFile());
						int[][] temp = new int [9][9];
						for(int i=0; fin.hasNext(); i++){
							String row = fin.nextLine();
							for(int j=0; j<9; j++){
								temp[i][j] = row.charAt(j)%48;
							}
						}
						setValues(temp);
						clear();
					}catch(Exception ioe){
						JOptionPane.showMessageDialog(Solver.this, "Unable to open file.", "Error", JOptionPane.ERROR_MESSAGE);
						clear();
					}
					chooseFrame.setVisible(false);
				}else{
					chooseFrame.setVisible(false);
				}
			}
		});
	}

	private void setValues(int[][] puzzle) {
		reset();
		
		for(int i=0; i<9; i++)
			for(int j=0; j<9; j++)
				blocks[Puzzle.getBlockNumber(i, j)].setValue(puzzle[i][j]);
	}

	private void reset(){
		for(int i=0; i<9; i++)
			blocks[i].reset();
	}
	
	private void setUpGamePanel() {
		for(int i=0; i<9; i++){
			blocks[i] = new BlockPanel();
			if(i == 1 || i == 3 || i == 5 || i == 7)
				blocks[i].setTextBoxColor(new Color(220,220,220));

			gamePanel.add(blocks[i]);
		}
	}

	private int[][] getDisplayedValues(){
		int[][] vals = new int[9][9];
		
		for(int i=0; i<9; i++){
			if(i<3)
				for(int j=0; j<3; j++)
					for(int k=0; k<3; k++)
						vals[k][(i%3)*3 + j] = blocks[i].getBlockValues()[k][j];
			else if(i<6)
				for(int j=0; j<3; j++)
					for(int k=0; k<3; k++)
						vals[k+3][(i%3)*3 + j] = blocks[i].getBlockValues()[k][j];
			else
				for(int j=0; j<3; j++)
					for(int k=0; k<3; k++)
						vals[k+6][(i%3)*3 + j] = blocks[i].getBlockValues()[k][j];
		}
		return vals;
	}
	
	public void addSolution(Solution s){
		if(s != null && !solutions.contains(s)){
			solutions.add(s);
			of.setText(" of " + solutions.size());
			if(solutions.size() == 1){
				setValues(s.getGrid());
				viewingSolution++;
				selectSolution.setText(Integer.toString(viewingSolution));
			}
		}else if(s == null){
			JOptionPane.showMessageDialog(new JFrame(), "Puzzle has no Solutions. Invalid puzzle.", "Error", JOptionPane.ERROR_MESSAGE);
			reset();
		}
	}
		
	public static void main(String[] args) {
		new Solver().start();
	}
	
	private void clear(){
		viewingSolution = 0;
		solutions.clear();
		selectSolution.setText("0");
		of.setText(" of " + solutions.size());
	}
}
