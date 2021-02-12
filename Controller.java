import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

public class Controller {
	
	static int[][] puzzle = new int[9][9];
	static boolean foundSolution = false;
	static int timesLooking = 0;
	
	public static void main(String[] args) throws IOException {
		File puzzleFile = new File("puzzle");
		Scanner puzzleScanner = new Scanner(puzzleFile);
		
		int i = 0, j = 0;
		
		// Reads puzzle.txt file into puzzle array
		while(puzzleScanner.hasNext()) {
			if(j == 9) {
				j = 0;
				i++;
			}
			if(puzzleScanner.hasNextInt()) {
				puzzle[i][j] = puzzleScanner.nextInt();
				j++;
			}
			else {
				puzzleScanner.next().charAt(0);
			}
		}
		
		System.out.println("Original puzzle\n");
		printPuzzle(puzzle);
		System.out.println();
		
		while(!solved(puzzle) && !foundSolution) {
			// Loop through each cell to check for solutions
			for(i = 0; i < puzzle.length; i++) {
				for(j = 0; j < puzzle[0].length; j++) {
					if(puzzle[i][j] == 0) {
						// Tests each possible value at the current empty
						for(int val = 1; val < 10; val++) {
							System.out.println("Checking " + val + " at row: " + i + " column: " + j + "\n");
							puzzle[i][j] = solutionAtIndex(i, j, val);
							if(puzzle[i][j] != 0) {
								System.out.println("Solution of " + puzzle[i][j] + " found at row: " + i + " column: " + j + "\n");
								val = 10;
							}
						}
					}
				}
			}
		}
		
		System.out.println("Solved puzzle\n");
		printPuzzle(puzzle);
		System.out.println();
		puzzleScanner.close();
	}
	
	// Returns 0 if no solution can be obtained at the given index, otherwise returns the solution.
	public static int solutionAtIndex(int row, int col, int val) {
		int solution = 0;

		// First, checks if the tested value is possible at the index
		if(possibleAtIndex(row, col, val)) {
			printTablesAtIndex(row, col);
			
			Hashtable<int[], Integer> boxTable = boxTableAtIndex(row, col);
			Hashtable<int[], Integer> rowTable = rowTableAtIndex(row, col);
			Hashtable<int[], Integer> columnTable = columnTableAtIndex(row, col);

			// Checks if the current empty is the only one in its box to work with the solution
			int numPossible = numOfEmpty(boxTable);
			Set<int[]> indexSet = boxTable.keySet();
			Iterator<int[]> indexInterator = indexSet.iterator();

			while(indexInterator.hasNext()) {
				int[] index = indexInterator.next();
				if(boxTable.get(index) == 0 && !possibleAtIndex(index[0], index[1], val)) {
					numPossible--;
				}
			}
			if(numPossible == 1) {
				solution = val;
				foundSolution = true;
			}

			// Checks if the current empty is the only one in its row to work with the solution
			numPossible = numOfEmpty(rowTable);
			indexSet = rowTable.keySet();
			indexInterator = indexSet.iterator();
			while(indexInterator.hasNext()) {
				int[] index = indexInterator.next();
				if(rowTable.get(index) == 0 && !possibleAtIndex(index[0], index[1], val)) {
					numPossible--;
				}
			}
			if(numPossible == 1 && solution == 0) {
				solution = val;
				foundSolution = true;
			}

			// Checks if the current empty is the only one in its column to work with the solution
			numPossible = numOfEmpty(columnTable);
			indexSet = columnTable.keySet();
			indexInterator = indexSet.iterator();
			while(indexInterator.hasNext()) {
				int[] index = indexInterator.next();
				if(columnTable.get(index) == 0 && !possibleAtIndex(index[0], index[1], val)) {
					numPossible--;
				}
			}
			if(numPossible == 1 && solution == 0) {
				solution = val;
				foundSolution = true;
			}
		}
		return solution;
	}
	
	// Returns true if the number is a possible solution at the given index, otherwise returns false.
	public static boolean possibleAtIndex(int row, int col, int num) {
		boolean possible = true;
		if(rowTableAtIndex(row, col).contains(num) || 
		   columnTableAtIndex(row, col).contains(num) || 
		   boxTableAtIndex(row, col).contains(num)) {
			possible = false;
		}
		return possible;
	}
	
	public static int numOfEmpty(Hashtable<int[], Integer> table) {
		int amount = 0;
		Set<int[]> indexSet = table.keySet();
		Iterator<int[]> indexInterator = indexSet.iterator();
		while(indexInterator.hasNext()) {
			int[] index = indexInterator.next();
			if(table.get(index) == 0) {
				amount++;
			}
		}
		return amount;
	}
	
	// Returns true if the puzzle has no more indices without solutions, and returns true if the puzzle has been solved.
	public static boolean solved(int[][] puzzle) {
		boolean s = true;
		for(int i = 0; i < puzzle.length; i++) {
			for(int j = 0; j < puzzle[0].length; j++) {
				if(puzzle[i][j] == 0) {
					s = false;
				}
			}
		}
		return s;
	}
	
	public static void printValuesInTable(Hashtable<int[], Integer> table) {
		Set<int[]> indexSet = table.keySet();
		Iterator<int[]> indexInterator = indexSet.iterator();
		System.out.print("[");

		while(indexInterator.hasNext()) {
			int[] index = indexInterator.next();
			System.out.print(table.get(index) + " ");
		}
		System.out.print("]\n");
	}
	
	public static void printTablesAtIndex(int row, int col) {
		Hashtable<int[], Integer> boxTable = boxTableAtIndex(row, col);
		Hashtable<int[], Integer> rowTable = rowTableAtIndex(row, col);
		Hashtable<int[], Integer> columnTable = columnTableAtIndex(row, col);
		System.out.println("Box table at row: " + row + " column: " + col);
		printValuesInTable(boxTable);
		System.out.println("Row table at row: " + row + " column: " + col);
		printValuesInTable(rowTable);
		System.out.println("Column table at row: " + row + " column: " + col);
		printValuesInTable(columnTable);
	}
	
	// Returns a Hash-table with the indices and their values of each value in the same row of the index.
	public static Hashtable<int[], Integer> rowTableAtIndex(int row, int col) {
		Hashtable<int[], Integer> table = new Hashtable<int[], Integer>();
		for(int j = 0; j < puzzle[0].length; j++) {
			if(j != col) {
				table.put(new int[] {row, j}, puzzle[row][j]);
			}
		}
		return table;
	}
	
	// Returns a Hash-table with the indices and their values of each value in the same column of the index.
	public static Hashtable<int[], Integer> columnTableAtIndex(int row, int col) {
		Hashtable<int[], Integer> table = new Hashtable<int[], Integer>();
		for(int i = 0; i < puzzle.length; i++) {
			if(i != row) {
				table.put(new int[] {i, col}, puzzle[i][col]);
			}
		}
		return table;
	}
	
	// Returns a Hash-table with the indices and their values of each value in the same box of the index.
	public static Hashtable<int[], Integer> boxTableAtIndex(int row, int col) {
		Hashtable<int[], Integer> table = new Hashtable<int[], Integer>();
		for(int i = (row / 3) * 3; i < ((row / 3) * 3) + 3; i++) { 
			for(int j = (col / 3) * 3; j < ((col / 3) * 3) + 3; j++) {
				if(i != row || j != col) {
					table.put(new int[]{i, j}, puzzle[i][j]);
				}
			}
		}
		return table;
	}
	
	
	// Prints the solved puzzle to the console.
	public static void printPuzzle(int[][] puzzle) {
		printLine(puzzle.length);
		System.out.println();
		for(int i = 0; i < puzzle.length; i++) {
			System.out.print("| ");
			for(int j = 0; j < puzzle[i].length; j++) {
				System.out.print(puzzle[i][j] + " ");
				if((j + 1) % 3 == 0) {
					System.out.print("| ");
				}
			}
			System.out.println();
			if((i + 1) % 3 == 0) {
				printLine(puzzle.length);
				System.out.println();
			}
		}
	}
	
	// Prints a line of dashes meant to separate each row of boxes.
	public static void printLine(int length) {
		System.out.print(" ");
		for(int i = 0; i < length; i++) {
			System.out.print("- ");
			if((i + 1) % 3 == 0) {
				System.out.print("- ");
			}
		}
	}

}
