/**
 * Copyright 2011 Travis Patterson
 * 
 * This file is part of SudokuSolver.
 * 
 * SudokuSolver is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * SudokuSolver is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SudokuSolver.  If not, see <http://www.gnu.org/licenses/>.
 */

package patterson.travis.sudoku;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JOptionPane;

import patterson.travis.sudoku.gui.GamePanel;


/**
 * @author Travis Patterson
 * @copyright 2011
 * Class that solves a Puzzle object
 */
public class Solver implements Runnable{
	private Puzzle m_puzzle;
	private GamePanel m_gamePanel;
	private boolean m_solved = false;
	private boolean m_isSolving = false;
	
	/**
	 * @param puzzle The puzzle to solve
	 * @param gamePanel The visual representation of the puzzle
	 */
	public Solver(Puzzle puzzle, GamePanel gamePanel){
		m_puzzle = puzzle;
		m_gamePanel = gamePanel;
		initializePuzzle();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		m_solved = false;
		m_isSolving = true;
		solvePuzzle();
	}
	
	/**
	 * @return True if the puzzle has been solved.
	 */
	public boolean isSolved(){
		return m_solved;
	}
	
	/**
	 * Stops the solver
	 */
	public void stopSolving(){
		m_isSolving = false;
	}
	
	/**
	 * Initialized the puzzle object to agree with the visual
	 * representation in the game panel
	 */
	private void initializePuzzle() {
		int size = m_puzzle.getSize();
		int[][] values = m_gamePanel.getValues();
		
		for (int i = 0; i < size; i++){
			for (int j = 0; j < size; j++){
				m_puzzle.setValue(i, j, values[i][j]);
			}
		}
	}
	
	/**
	 * Solves the puzzle using three techniques:
	 * naked single
	 * hidden single
	 * brute force via recursive backtracking.
	 */
	private void solvePuzzle(){
		m_isSolving = true;
		
		while (m_isSolving && !m_solved){
			m_solved = !tryNakedSingle();
			m_solved = m_solved && !tryHiddenSingle();			
		}
		
		if(m_isSolving){
			m_solved = false;
			recursiveBacktrack(0,0, m_puzzle.getSize());
		}
		
		if(m_solved){
			m_gamePanel.update();
		} else {
			JOptionPane.showMessageDialog(m_gamePanel,
			    "No Solutions for this puzzle",
			    "InvalidPuzzle",
			    JOptionPane.ERROR_MESSAGE);
		}
		
		m_gamePanel.startSolvingButton().setEnabled(true);
		m_gamePanel.stopSolvingButton().setEnabled(false);
	}
	
	/**
	 * @return True if a naked single was found
	 */
	private boolean tryNakedSingle() {
		int puzzleSize = m_puzzle.getSize();
		boolean foundValue = false;
		
		for (int i = 0; i < puzzleSize; i++){
			for (int j = 0; j < puzzleSize; j++){
				int[] cellCandidates = m_puzzle.cellCandidates(i, j);
				Integer[] strippedCandidates = stripZeros(cellCandidates);
				if(strippedCandidates.length == 1){
					int value = strippedCandidates[0];
					m_puzzle.setValue(i, j, value);
					m_gamePanel.update();
					foundValue = true;
				}
			}
		}
		return foundValue;
	}
	
	/**
	 * @return True if a hidden single is found
	 */
	private boolean tryHiddenSingle() {
		int size = m_puzzle.getSize();
		boolean foundValue = false;
		for (int i = 0; i < size; i ++){
			foundValue = findHiddinSingleRow(size, i);
			foundValue = foundValue || findHiddinSingleColumn(size, i);
			foundValue = foundValue || findHiddinSingleBlock(size, i);
		}
		return foundValue;
	}
	
	/**
	 * @return True if there is a hidden single in the given column of size "size"
	 */
	private boolean findHiddinSingleColumn(int size, int y) {
		ArrayList<int[]> allCandidates = new ArrayList<int[]>();
		boolean valueSet = false;
		
		for(int i = 0; i < size; i++){
			allCandidates.add(m_puzzle.cellCandidates(i, y));
		}
		
		for (int i = 0; i < size; i ++){
			int count = 0;
			int lastCandidateLocation = -1;
			for (int j = 0; j < size; j++){
				if (allCandidates.get(j)[i] > 0){
					lastCandidateLocation = j;
					count++;
				}
			}
			if (count == 1){
				m_puzzle.setValue(lastCandidateLocation, y, i+1);
				m_gamePanel.update();
				valueSet = true;
			}
		}
		return valueSet;
	}

	/**
	 * @return True if there is a hidden single in the given row of size "size"
	 */
	private boolean findHiddinSingleRow(int size, int x) {
		ArrayList<int[]> allCandidates = new ArrayList<int[]>();
		boolean valueSet = false;
		
		for(int i = 0; i < size; i++){
			allCandidates.add(m_puzzle.cellCandidates(x, i));
		}
		
		for (int i = 0; i < size; i ++){
			int count = 0;
			int lastCandidateLocation = -1;
			for (int j = 0; j < size; j++){
				if (allCandidates.get(j)[i] > 0){
					lastCandidateLocation = j;
					count++;
				}
			}
			if (count == 1){
				m_puzzle.setValue(x, lastCandidateLocation, i+1);
				m_gamePanel.update();
				valueSet = true;
			}
		}
		return valueSet;
	}

	/**
	 * @return True if there is a hidden single in the given block of size "size"
	 */
	private boolean findHiddinSingleBlock(int size, int blockNumber) {
		HashMap<Point,int[]> allCandidates = new HashMap<Point,int[]>();
		boolean valueSet = false;
		int blockSize = (int)Math.sqrt(size);
		Point blockIndices = m_puzzle.getBlockIndices(blockNumber);
		int blockX = blockIndices.x;
		int blockY = blockIndices.y;
		
		for(int i = blockX; i < blockX + blockSize; i++){
			for (int j = blockY; j < blockY + blockSize; j++){
				allCandidates.put(new Point(i,j), m_puzzle.cellCandidates(i, j));
			}
		}
		
		Point[] blockCells = allCandidates.keySet().toArray(new Point[0]);
		for (int i = 0; i < size; i ++){
			int count = 0;
			int lastCandidateLocation = -1;
			for (int j = 0; j < size; j++){
				Point blockCell = blockCells[j];
				if (allCandidates.get(blockCell)[i] > 0){
					lastCandidateLocation = j;
					count++;
				}
			}
			if (count == 1){
				Point blockCell = blockCells[lastCandidateLocation];
				m_puzzle.setValue(blockCell.x, blockCell.y, i+1);
				m_gamePanel.update();
				valueSet = true;
			}
		}
		return valueSet;
	}

	/**
	 * Performs a recursive backtrack traversal of the puzzle solving as much as it can
	 */
	private void recursiveBacktrack(int x, int y, int size) {
		Integer[] candidates = stripZeros(m_puzzle.cellCandidates(x, y));
				
		if(m_isSolving){
			Point nextCell = getNextCell(x, y, size);
			
			if (m_puzzle.getValue(x, y) == 0){		
				for (Integer candidate : candidates) {
					m_puzzle.setValue(x, y, candidate);
															
					if(nextCell != null){
						recursiveBacktrack(nextCell.x, nextCell.y, size);
					}
					
					if(!m_isSolving){
						return;
					}
					
					if(m_puzzle.isSolved()){
						m_solved = true;
						m_puzzle.saveSolution();
						m_gamePanel.update();
					}
				}
				
				if(m_isSolving){
					m_puzzle.setValue(x, y, 0);
				}
			} else {
				if(nextCell != null){
					recursiveBacktrack(nextCell.x, nextCell.y, size);
				}
			}
		}
	}
		
	/**
	 * @return The next cell for the recursive traveral given the 
	 * current cell.
	 */
	private Point getNextCell(int x, int y, int size) {
		if (y + 1 < size){
			return new Point(x, y + 1);
		} else if (x + 1 < size){
			return new Point(x + 1, 0);
		} else {
			return null;
		}		
	}

	/**
	 * @return The given array without zeros
	 */
	private Integer[] stripZeros(int[] values){
		ArrayList<Integer> strippedValueList = new ArrayList<Integer>();
				
		for (int i = 0; i < values.length; i++){
			if (values[i] != 0){
				strippedValueList.add(values[i]);
			}
		}
		
		return strippedValueList.toArray(new Integer[0]);
	}
}