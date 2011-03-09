package patterson.travis.sudoku.gui;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.plaf.basic.BasicArrowButton;

import patterson.travis.sudoku.Puzzle;
import patterson.travis.sudoku.Solver;

/**
 * @author Travis Patterson
 * Entry class for the sudoku solver.
 */
public class MainWindow extends JFrame{
	private static final long serialVersionUID = 1L;
		
	private JButton m_solveButton = new JButton("Solve");
	private JButton m_stopSolvingButton = new JButton("Stop Solving");
	private JLabel m_numberSolutions = new JLabel("0");
	private JLabel m_solutionSelection = new JLabel("0");
	private GamePanel m_gamePanel;
	private Puzzle m_puzzle = new Puzzle();
	private Solver m_solver;
			
	/**
	 * Kicks off the main window of the solver application
	 */
	public MainWindow(){
		this.setTitle("Travis' Sudoku Solver");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setPreferredSize(new Dimension(480, 640));
		this.setLayout(new BorderLayout());
		this.setIconImage(Toolkit.getDefaultToolkit().createImage(MainWindow.class.getResource("untitled.GIF")));
		
		setupToolbar();
		setupSouthPanel();
		setupGamePanel();
				
		this.pack();
		this.setVisible(true);
	}
	
	/**
	 * Initializes the game panel
	 */
	private void setupGamePanel(){
		m_gamePanel = new GamePanel(m_puzzle, m_numberSolutions, m_solutionSelection, m_solveButton, m_stopSolvingButton);
		this.add(m_gamePanel, BorderLayout.CENTER);
	}	
	
	/**
	 * Initializes the panel containing the solution labels, solve buttons,
	 * and solution navigation buttons
	 */
	private void setupSouthPanel() {
		JPanel southPanel = new JPanel();
		JLabel viewing = new JLabel("Soluion ");
		BasicArrowButton next = new BasicArrowButton(JButton.EAST);
		BasicArrowButton prev = new BasicArrowButton(JButton.WEST);
				
		m_solveButton.addActionListener(new PuzzleSolver(true));
		m_stopSolvingButton.addActionListener(new PuzzleSolver(false));
		m_stopSolvingButton.setEnabled(false);
						
		next.setToolTipText("Click to view next solution.");
		prev.setToolTipText("Click to view previous solution.");
		next.addActionListener(new ChangeViewedSolutionListener(true));
		prev.addActionListener(new ChangeViewedSolutionListener(false));
		
		southPanel.add(m_solveButton);
		southPanel.add(m_stopSolvingButton);
		southPanel.add(viewing);
		southPanel.add(m_solutionSelection);
		southPanel.add(new JLabel(" of "));
		southPanel.add(m_numberSolutions);
		southPanel.add(prev);
		southPanel.add(next);
		
		this.add(southPanel, BorderLayout.SOUTH);
	}

	/**
	 * Initializes the toolbar
	 */
	private void setupToolbar() {
		JToolBar toolBar = new JToolBar();
		setupOpenFileButton(toolBar);
		this.add(toolBar, BorderLayout.NORTH);
	}
	
	/**
	 * Initializes the open file button
	 * @param toolbar Toolbar that the open file button belongs to
	 */
	private void setupOpenFileButton(JToolBar toolbar){
		JButton openFile = new JButton("Open File");
		openFile.addActionListener(new OpenFileListener(this));
		toolbar.add(openFile);
	}

	/**
	 * Listener opens the open file dialog and creates a new puzzle
	 * if a file is successfully chosen
	 */
	private class OpenFileListener implements ActionListener {
		private JFrame m_container;
		
		/**
		 * @param container The JFrame that the open file dialog belongs to
		 */
		public OpenFileListener(JFrame container){
			m_container = container;
		}
		
		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			JFileChooser diag = new JFileChooser();
			
			diag.setDialogType(JFileChooser.OPEN_DIALOG);
			diag.setDialogTitle("Open File");
			int option = diag.showOpenDialog(m_container);
	        if(option == JFileChooser.APPROVE_OPTION){
	        	m_puzzle = new Puzzle(diag.getSelectedFile().getAbsolutePath());
	        	m_gamePanel.setPuzzle(m_puzzle);
	        	m_numberSolutions.setText("0");
	        	m_solutionSelection.setText("0");
	        }
		}
	}
	
	/**
	 * Listener to kick off or stop the Solve object
	 */
	private class PuzzleSolver implements ActionListener {
		private boolean m_startSolving;
		
		/**
		 * @param startSolving True if beginning solving, else false
		 */
		public PuzzleSolver(boolean startSolving){
			m_startSolving = startSolving;
		}
		
		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			if (m_startSolving){
				int[][] values = m_gamePanel.getValues();
				int size = m_puzzle.getSize();
				m_puzzle = new Puzzle(values, size);
				m_gamePanel.setPuzzle(m_puzzle);
				m_solver = new Solver(m_puzzle, m_gamePanel);
				m_numberSolutions.setText("0");
				m_solutionSelection.setText("0");
				m_solveButton.setEnabled(false);
				m_stopSolvingButton.setEnabled(true);
				new Thread(m_solver).start();
			} else {
				m_stopSolvingButton.setEnabled(false);
				m_solveButton.setEnabled(true);
				m_solver.stopSolving();
			}
		}
	}
			
	/**
	 * Listener to control the solution navigation buttons.
	 */
	private class ChangeViewedSolutionListener implements ActionListener{
		boolean m_next = false;
		
		/**
		 * @param next True if button displays next solution else false
		 */
		public ChangeViewedSolutionListener(boolean next){
			m_next = next;
		}
		
		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			int solutionNumber = Integer.parseInt(m_solutionSelection.getText());
			int maxSolutions = Integer.parseInt(m_numberSolutions.getText());
			
			if(m_next){
				solutionNumber = Math.min(solutionNumber + 1, maxSolutions);
			} else {
				solutionNumber = Math.max(solutionNumber - 1, 0);
			}
			
			String newSolutionNumber = Integer.toString(solutionNumber);
			m_solutionSelection.setText(newSolutionNumber);
			m_gamePanel.update();
		}
	}
	
	public static void main(String[] args) {
		new MainWindow();
	}	
}
