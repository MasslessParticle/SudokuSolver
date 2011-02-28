package patterson.travis.sudoku.gui;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicArrowButton;

import patterson.travis.sudoku.Puzzle;
import patterson.travis.sudoku.Solver;

public class MainWindow extends JFrame{
	private static final long serialVersionUID = 1L;
		
	private JButton m_solveButton = new JButton("Solve");
	private JButton m_stopSolvingButton = new JButton("Stop Solving");
	private GamePanel m_gamePanel;
	private JLabel m_solutionsOutOfLabel = new JLabel(" of 0");
	private JTextField m_solutionSelection = new JTextField("0");
	
	private Puzzle m_puzzle = new Puzzle();
	private Solver m_solver;
			
	public MainWindow(){
		this.setTitle("Travis' Sudoku Solver");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setPreferredSize(new Dimension(480, 640));
		this.setLayout(new BorderLayout());
		this.setIconImage(Toolkit.getDefaultToolkit().createImage(MainWindow.class.getResource("untitled.GIF")));
		
		setupToolbar();
		setupGamePanel();
		setupSouthPanel();
		
		this.pack();
		this.setVisible(true);
	}
	
	private void setupGamePanel(){
		m_gamePanel = new GamePanel(m_puzzle);
		this.add(m_gamePanel, BorderLayout.CENTER);
	}	
	
	private void setupSouthPanel() {
		JPanel southPanel = new JPanel();
		JLabel viewing = new JLabel("Soluion ");
		BasicArrowButton next = new BasicArrowButton(JButton.EAST);
		BasicArrowButton prev = new BasicArrowButton(JButton.WEST);
				
		m_solveButton.addActionListener(new PuzzleSolver(true));
		m_stopSolvingButton.addActionListener(new PuzzleSolver(false));
		m_stopSolvingButton.setEnabled(false);
				
		m_solutionSelection.setHorizontalAlignment(JTextField.RIGHT);
		m_solutionSelection.setPreferredSize(new Dimension(100, 20));
		m_solutionSelection.addActionListener(new SelectSolutionListener());
				
		next.setToolTipText("Click to view next solution.");
		prev.setToolTipText("Click to view previous solution.");
		next.addActionListener(new ChangeViewedSolutionListener());
		prev.addActionListener(new ChangeViewedSolutionListener());
		
		southPanel.add(m_solveButton);
		southPanel.add(m_stopSolvingButton);
		southPanel.add(viewing);
		southPanel.add(m_solutionSelection);
		southPanel.add(m_solutionsOutOfLabel);
		southPanel.add(prev);
		southPanel.add(next);
		
		this.add(southPanel, BorderLayout.SOUTH);
	}

	private void setupToolbar() {
		JToolBar toolBar = new JToolBar();
		setupOpenFileButton(toolBar);
		JButton printPuzz = new JButton("is Solved");
		printPuzz.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				System.out.println(m_puzzle);
				System.out.println(m_puzzle.isSolved());
			}
		});
		
		toolBar.add(printPuzz);
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
	        	m_puzzle = new Puzzle(diag.getSelectedFile().getAbsolutePath());
	        	m_gamePanel.setPuzzle(m_puzzle);
	        }
		}
	}
	
	private class PuzzleSolver implements ActionListener {
		private boolean m_startSolving;
		
		public PuzzleSolver(boolean startSolving){
			m_startSolving = startSolving;
		}
		
		public void actionPerformed(ActionEvent e) {
			if (m_startSolving){
				m_solver = new Solver(m_puzzle, m_gamePanel);
				m_solveButton.setEnabled(false);
				m_stopSolvingButton.setEnabled(true);
				new Thread(m_solver).start();
				//SwingUtilities.invokeLater(m_solver);
			} else {
				m_stopSolvingButton.setEnabled(false);
				m_solveButton.setEnabled(true);
				m_solver.stopSolving();
			}
		}
	}
	
	private class SelectSolutionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
		}
	}
	
	private class ChangeViewedSolutionListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
		}
	}
	
	public static void main(String[] args) {
		new MainWindow();
	}	
}
