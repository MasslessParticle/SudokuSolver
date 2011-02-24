package gui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComponent;
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
	private JPanel gamePanel = new JPanel();
	private JPanel southPanel = new JPanel();
	private JLabel of = new JLabel(" of 0");
	private JTextField selectSolution = new JTextField("0");
	
	private ArrayList<Solution> solutions = new ArrayList<Solution>();
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
		
		this.pack();
		this.setVisible(true);
	}
	
	private void setupGamePanel() {
		gamePanel.setBackground(Color.BLACK);
		gamePanel.setLayout(new GridLayout(9,9));
		
		this.add(gamePanel, BorderLayout.CENTER);		
	}

	private void setupSouthPanel() {
		JLabel viewing = new JLabel("Viewing soluion ");
		BasicArrowButton next = new BasicArrowButton(JButton.EAST);
		BasicArrowButton prev = new BasicArrowButton(JButton.WEST);
				
		solve.addActionListener(new PuzzleSolver());
				
		selectSolution.setPreferredSize(new Dimension(150, 20));
		selectSolution.addActionListener(new SelectSolutionListener());
				
		next.setToolTipText("Click to view next solution.");
		prev.setToolTipText("Click to view previous solution.");
		next.addActionListener(new ChangeViewedSolutionListener());
		prev.addActionListener(new ChangeViewedSolutionListener());
		
		southPanel.add(solve);
		southPanel.add(viewing);
		southPanel.add(selectSolution);
		southPanel.add(of);
		southPanel.add(prev);
		southPanel.add(next);
		
		this.add(southPanel, BorderLayout.SOUTH);
	}

	private void setupToolbar() {
		JToolBar toolBar = new JToolBar();
		setupOpenFileButton(toolBar);
		this.add(toolBar, BorderLayout.NORTH);
	}
	
	private void setupOpenFileButton(JToolBar toolbar){
		JButton openFile = new JButton("Open File");
		openFile.addActionListener(new OpenFileListener());
		toolbar.add(openFile);
	}

	private class OpenFileListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JFileChooser diag = new JFileChooser();
							
			diag.setDialogType(JFileChooser.OPEN_DIALOG);
			diag.setDialogTitle("Open File");
			int option = diag.showOpenDialog(null);
	        if(option == JFileChooser.APPROVE_OPTION){
	        	puzzle = new Puzzle(diag.getSelectedFile().getAbsolutePath());
	        }
		}
	}
	
	private class PuzzleSolver implements ActionListener {
				
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			System.out.println(puzzle);
		}
		
	}
	
	private class SelectSolutionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	private class ChangeViewedSolutionListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public static void main(String[] args) {
		new Solver();
	}	
}
