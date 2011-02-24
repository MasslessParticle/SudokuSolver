package puzzle;

public class Puzzle {
	private int[][] m_puzzle = null;	
	private int m_zeroCount = 0;	
	private int m_blockLayout[][] = null;
	private int m_size = 0;
	private int m_numBlocks = 0;
	private int m_defaultSize = 9;
	
	public Puzzle(){
		int[][] vals = new int[m_defaultSize][m_defaultSize];
		for (int i = 0; i < m_defaultSize; i++){
			for (int j = 0; j < m_defaultSize; j++){
				vals[i][j] = 0;
			}
		}
		
		makePuzzle(vals, m_defaultSize);
	}
	
	public Puzzle(String filePath){
		//TODO: open a file and parse it into a puzzle
	}
	
	public Puzzle(int[][] values, int puzzleSize){
		makePuzzle(values, puzzleSize);
	}
	
	public int getBlockNumber(int x, int y){
		int blockX = x / m_size;
		int blockY = x / m_size;
		
		return m_blockLayout[blockX][blockY];	
	}
	
	public int[] rowCandidates(int x){
		int[] candidates = getNewCandidateList();
		int numZeros = 0;
		
		for (int i = 0; i < m_size; i ++){
			int cellValue = m_puzzle[x][i];
			if (cellValue != 0){
				candidates[cellValue] = 0;
				numZeros++;
			}
		}
		return stripZeros(candidates, numZeros);
	}
	
	public int[] columnCandidates(int y){
		int[] candidates = getNewCandidateList();
		int numZeros = 0;
		
		for (int i = 0; i < m_size; i ++){
			int cellValue = m_puzzle[i][y];
			if (cellValue != 0){
				candidates[cellValue] = 0;
				numZeros++;
			}
		}
		return stripZeros(candidates, numZeros);
	}
	
	public int[] blockCandidates(int block){
		int[] candidates = getNewCandidateList();
		int blockX = 0;
		int blockY = 0;
				
		//Find the block indices
		for (int i = 0; i < m_numBlocks; i ++){
			for (int j = 0; j < m_numBlocks; j++){
				if(block == m_blockLayout[i][j]){
					blockX = i;
					blockY = j;
					break;
				}
			}
		}
		
		//Convert the block indices to puzzle indices
		blockX = blockX * m_numBlocks;
		blockY = blockY * m_numBlocks;
		int numZeros = 0;
		
		for (int i = blockX; i < blockX + m_numBlocks; i++){
			for (int j = blockX; j < blockX + m_numBlocks; j++){
				int cellValue = m_puzzle[i][j];
				if (cellValue != 0){
					candidates[cellValue] = 0;
					numZeros++;
				}
			}
		}
		
		return stripZeros(candidates, numZeros);
	}
	
	public int[] cellCandidates(int x, int y){
		int[] candidates = new int[m_size];
		int[] rowCandidates = rowCandidates(x);
		int[] colCandidates = columnCandidates(y);
		int[] blockCandidates = blockCandidates(getBlockNumber(x, y));
		int numZeros = 0;
		
		for (int i = 0; i < rowCandidates.length; i++){
			int cellValue = rowCandidates[i];
			candidates[cellValue] = 0;
			numZeros++;
		}
		
		for (int i = 0; i < colCandidates.length; i++){
			int cellValue = colCandidates[i];
			if (candidates[cellValue] != 0){
				candidates[cellValue] = 0;
				numZeros++;
			}
		}
		
		for (int i = 0; i < blockCandidates.length; i++){
			int cellValue = blockCandidates[i];
			if (candidates[cellValue] != 0){
				candidates[cellValue] = 0;
				numZeros++;
			}
		}
				
		return stripZeros(candidates, numZeros);
	}
		
	public String toString(){
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < m_size; i ++){
			for (int j = 0; j < m_size; j++){
				sb.append(m_puzzle[i][j]);
				sb.append(" ");
			}
			sb.append("\n");
		}
		return sb.toString();
	}
	
	private void makePuzzle(int[][] values, int size){
		m_size = size;
		m_puzzle = new int[m_size][m_size];
		for(int i=0; i<m_size; i++){
			for(int j=0; j<m_size; j++){
				m_puzzle[i][j] = values[i][j];

				if(values[i][j] == 0)
					m_zeroCount++;
			}
		}
		
		setupBlockLayout();
	}
	
	private int[] getNewCandidateList(){
		int[] candidates = new int[m_size];
		
		for (int i = 0; i < m_size; i++){
			candidates[i] = i;
		}
		
		return candidates; 
	}
	
	private int[] stripZeros(int[] candidateArray, int numZeros){
		int[] strippedCandidates = new int[candidateArray.length - numZeros];
		
		int strippedIndex = 0;
		for (int i = 0; i < candidateArray.length; i++){
			if (candidateArray[i] != 0){
				strippedCandidates[strippedIndex] = candidateArray[i];
				strippedIndex++;
			}
		}
		
		return strippedCandidates;
	}
	
	private void setupBlockLayout() {
		int m_numBlocks = (int)Math.sqrt(m_size);
		m_blockLayout = new int[m_numBlocks][m_numBlocks];
		
		int blockNumber = 0;
		for (int i = 0; i < m_numBlocks; i++){
			for (int j = 0; j < m_numBlocks; j++){
				m_blockLayout[i][j] = blockNumber;
				blockNumber++;
			}
		}
		
	}
}