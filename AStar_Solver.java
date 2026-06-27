package astar_solver;
import java.util.*;

public class AStar_Solver {

    static int heuristicType = 1; // 1 = misplaced tiles, 2 = Manhattan distance
    static int[] goalBoard;
        
    public static class PuzzleState implements Comparable<PuzzleState> {
        int[] board;
        PuzzleState parent;
        String move;
        int g, h, f; // g = path cost, h = heuristic value, f = g + h

        // constructor for start state
        public PuzzleState(int[] board) {
            this.board = board;
            this.parent = null;
            this.move = "";
            this.g = 0;
            calculateCost();
        }

        // constructor for successor states
        public PuzzleState(int[] board, PuzzleState parent, String move, int g) {
            this.board = board;
            this.parent = parent;
            this.move = move;
            this.g = g;

            // heuristic calculated here 
            calculateCost();
        }
        
        private void calculateCost() {
            this.h = calculateHeuristic(board, AStar_Solver.goalBoard, AStar_Solver.heuristicType);
            this.f = g + h;
        }

        public void printBoard() {
            for (int i = 0; i < board.length; i++) {
                System.out.print(board[i] + " ");
                if ((i + 1) % 3 == 0) {
                    System.out.println();
                }
            }
        }

        public boolean isGoal(int[] goalBoard) {
            return Arrays.equals(board, goalBoard);
        }
        
        // find the position of the blank tile
        public int findBlank() {
            for (int i = 0; i < board.length; i++) {
                if (board[i] == 0) {
                    return i;
                }
            }
            return -1;
        }

        public int[] copyBoard() {
            int[] newBoard = new int[board.length];
            for (int i = 0; i < board.length; i++) {
                newBoard[i] = board[i];
            }
            return newBoard;
        }

        public void swap(int[] arr, int i, int j) {
            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }

        public PuzzleState moveUp(){
            int blank = findBlank();

            if (blank - 3 < 0){
                return null;
            }

            int[] newBoard = copyBoard();
            swap(newBoard, blank, blank - 3);

            return new PuzzleState(newBoard, this, "Up", g + 1);
        }

        public PuzzleState moveDown() {
            int blank = findBlank();
            
            if (blank + 3 >= board.length) {
                return null;
            }
            
            int[] newBoard = copyBoard();
            swap(newBoard, blank, blank + 3);
            
            return new PuzzleState(newBoard, this, "Down", g + 1);
        }
        
        public PuzzleState moveLeft() {
            int blank = findBlank();
            
            if (blank % 3 == 0) {
                return null;
            }
            
            int[] newBoard = copyBoard();
            swap(newBoard, blank, blank - 1);
            
            return new PuzzleState(newBoard, this, "Left", g + 1);
        }
        
        public PuzzleState moveRight() {
            int blank = findBlank();
            if (blank % 3 == 2) {
                return null;
            }
            
            int[] newBoard = copyBoard();
            swap(newBoard, blank, blank + 1);
            return new PuzzleState(newBoard, this, "Right", g + 1);
        }

        // generate all valid successor states
        public ArrayList<PuzzleState> getSuccessors() {
            ArrayList<PuzzleState> successors = new ArrayList<>();

            PuzzleState up = moveUp();
            PuzzleState down = moveDown();
            PuzzleState left = moveLeft();
            PuzzleState right = moveRight();
            
            if (up != null) successors.add(up);
            if (down != null) successors.add(down);
            if (left != null) successors.add(left);
            if (right != null) successors.add(right);

            return successors;
        }

        // convert board to string for visited checking
        public String boardToString() {
            String result = "";
            for (int i = 0; i < board.length; i++) {
                result += board[i];
            }
            return result;
        }

        // compare states by f value for priority queue
        @Override
        public int compareTo(PuzzleState other) {
            return this.f - other.f;
        }

    } // PuzzleState end 
    
    public static List<String> getPath(PuzzleState goalState) {
        List<String> path = new ArrayList<>();
        PuzzleState current = goalState;
        
        while (current.parent != null) {
            path.add(current.move);
            current = current.parent;
        }

        Collections.reverse(path);
        return path;
    } // getPath end 

    public static void printSolutionBoards(PuzzleState goalState) {
        ArrayList<PuzzleState> states = new ArrayList<>();
        PuzzleState current = goalState;

        while (current != null) {
            states.add(current);
            current = current.parent;
        }

        Collections.reverse(states);

        for (int i = 0; i < states.size(); i++) {
            System.out.println("Step " + i + ":");
            states.get(i).printBoard();
            System.out.println();
        }
    } //printSolutionBoards end
   
    // solve the puzzle using A* search structure
    public static void solve(int[] startBoard, int[] goalBoard){
        long startTime = System.currentTimeMillis();
        
        PuzzleState start = new PuzzleState(startBoard);
        System.out.println("Start State:");
        start.printBoard();
        System.out.println();

        PriorityQueue<PuzzleState> openList = new PriorityQueue<>();
        HashSet<String> closedList = new HashSet<>();

        openList.add(start);

        int expandedNodes = 0;
        boolean found = false;

        // search loop
        while (!openList.isEmpty()) {
            PuzzleState current = openList.poll();

            if (closedList.contains(current.boardToString())) {
                continue;
            }

            expandedNodes++;

            if (current.isGoal(goalBoard)) {
                System.out.println("Goal Found!");
                System.out.println("Expanded Nodes: " + expandedNodes);

                List<String> path = getPath(current);
                System.out.println("Moves: " + path);
                System.out.println("Number of Steps: " + path.size());
                long endTime = System.currentTimeMillis();
                System.out.println("Execution Time : " + (endTime - startTime) + " ms");
                System.out.println();

                System.out.println("Goal State:");
                for (int i = 0; i < goalBoard.length; i++) {
                    System.out.print(goalBoard[i] + " ");
                    if ((i + 1) % 3 == 0) {
                        System.out.println();
                    }
                }
                System.out.println();
                
                printSolutionBoards(current);
                found = true;
                break;
            }

            // mark current state as visited
            closedList.add(current.boardToString());

            // generate next states
            ArrayList<PuzzleState> nextStates = current.getSuccessors();

            // add unvisited successors to the queue
            for (PuzzleState next : nextStates) {
                if (!closedList.contains(next.boardToString())) {
                    openList.add(next);
                }
            }
        }

        if (!found) {
            System.out.println("No Solution Found.");
        }
    } // solve end
    
    // Determine heuristic type
    public static int calculateHeuristic(int[] board, int[] goal, int type) {
        if (type == 1) {
            return misplacedTiles(board, goal);
        } else {
            return manhattanDistance(board, goal);
        }
    } // calculateHeuristic end
    
    // h1 = number of misplaced tiles
    public static int misplacedTiles(int[] board, int[] goal) {
        int count = 0;

        for (int i = 0; i < board.length; i++) {
            if (board[i] != 0 && board[i] != goal[i]) {
                count++;
            }
        }

        return count;
    } // misplacedTiles end
    
    // h2 = total Manhattan distance for each tile from its goal position
    public static int manhattanDistance(int[] board, int[] goal) {
        int distance = 0;

        for (int i = 0; i < board.length; i++) {
            int value = board[i];

            if (value != 0) {
                int goalIndex = findIndex(goal, value);

                int x1 = i / 3;
                int y1 = i % 3;

                int x2 = goalIndex / 3;
                int y2 = goalIndex % 3;

                distance += Math.abs(x1 - x2) + Math.abs(y1 - y2);
            }
        }

        return distance;
    } // manhattanDistance end
    
    // find the index of a tile in the goal state
    public static int findIndex(int[] arr, int value) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == value) {
                return i;
            }
        }
        return -1;
    } // findIndex end
    
    public static void main(String[] args) {
        // int[] startBoard = {1, 2, 3, 8, 0, 4, 7, 6, 5};   // test 1 
        // int[] startBoard = {0, 1, 3, 8, 2, 4, 7, 6, 5};   // test 2
        // int[] startBoard = {8, 1, 3, 0, 2, 4, 7, 6, 5};   // test 3
        // int[] startBoard = {2, 8, 3, 1, 6, 4, 7, 5, 0};   // test 4
         int[] startBoard = {0, 1, 2, 3, 8, 4, 7, 6, 5};   // test 5
         
        goalBoard = new int[]{1, 2, 3, 8, 0, 4, 7, 6, 5};
        
        // h1 (misplaced tiles)
        System.out.println("\n------ Heuristic 1: Misplaced Tiles ------");
        heuristicType = 1;
        solve(startBoard, goalBoard);
    
        // h2 (Manhattan distance)
        System.out.println("\n------ Heuristic 2: Manhattan Distance ------");
        heuristicType = 2;
        solve(startBoard, goalBoard);

    }

}