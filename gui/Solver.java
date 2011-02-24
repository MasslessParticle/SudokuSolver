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
		
	private JButton solve = new JButton("Solve");
	private JButton openFile = new JButton("Open File");
	
	private JToolBar toolBar = new JToolBar();
	
	private JPanel gamePanel = new JPanel();
	private JPanel southPanel = new JPanel();
	
	private ArrayList<Solution> solutions = new ArrayList<Solution>();
	
	private JLabel viewing = new JLabel("Viewing soluion ");
	private JLabel of = new JLabel(" of " + solutions.size());
	
	private JTextField selectSolution = new JTextField("0");
	
	private int viewingSolution = 0;
	private Puzzle puzzle;
	
	
	public Solver(){
		this.setTitle("Travis' Sudoku Solver");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setPreferredSize(new Dimension(480, 640));
		this.setLayout(new BorderLayout());
		this.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width/2) - (480/2), (Toolkit.getDefaultToolkit().getScreenSize().height/2) - (640/2));
		this.setIconImage(Toolkit.getDefaultToolkit().createImage(Solver.class.getResource("untitled.GIF")));
		setupToolbar();
		
		setupGamePanel();
		setupSouthPanel();
		
	}
	
	private void setupToolbar() {
		setupOpenFileButton();
		toolBar.add(openFile);
		this.add(toolBar, BorderLayout.NORTH);
	}
	
	private void setupOpenFileButton(){
		
	}

	public void start(){
		
				
		
		
		

		gamePanel.setBackground(Color.BLACK);
		gamePanel.setLayout(new GridLayout(3,3,4,4));
		setUpGamePanel();

		this.add(gamePanel, BorderLayout.CENTER);

		solve.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				int[][] temp = getDisplayedValues();
				puzzle = new Puzzle(temp, 3);
				System.out.println(puzzle);
				clear();
				new Thread(new Solve(puzzle, solve), "Solver").start();
			}
		});
		
		
		
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

	public void addSolution(Solution s){
		if(s != null && !solutions.contains(s)){
			solutions.add(s);
			of.setText(" of " + solutions.size());
			if(solutions.size() == 1){
				//setValues(s.getGrid());
				viewingSolution++;
				selectSolution.setText(Integer.toString(viewingSolution));
			}
		}else if(s == null){
			JOptionPane.showMessageDialog(new JFrame(), "Puzzle has no Solutions. Invalid puzzle.", "Error", JOptionPane.ERROR_MESSAGE);
			//reset();
		}
	}
	
	private void clear(){
		viewingSolution = 0;
		solutions.clear();
		selectSolution.setText("0");
		of.setText(" of " + solutions.size());
	}
	
	private class OpenFileListener implements ActionListener {

		private JTextField m_popField = null;
		private String m_title;
		
		public String m_path = "";
				
		public void actionPerformed(ActionEvent e) {
			JFileChooser diag = new JFileChooser();
							
			if(m_title.equalsIgnoreCase("load")){
				diag.setDialogType(JFileChooser.OPEN_DIALOG);
			}else{
				diag.setDialogType(JFileChooser.SAVE_DIALOG);
			}
						
			diag.setDialogTitle(m_title);
			int option = diag.showOpenDialog(null);
	        if(option == JFileChooser.APPROVE_OPTION){
	        	m_path = diag.getSelectedFile().getAbsolutePath();
	        }
		}
		
		public String getPath(){
			return m_path;
		}
	}
	
	public static void main(String[] args) {
		new Solver();
	}	
}
