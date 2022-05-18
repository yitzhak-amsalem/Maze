
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class Maze extends JFrame {

    private int[][] values;

    private String[][] color;
    private boolean[][] visited;
    private int startRow;
    private int startColumn;
    private ArrayList<JButton> buttonList;
    private int rows;
    private int columns;
    private boolean backtracking;
    private int algorithm;
    private ArrayList<ArrayList<Square>> squares;
    private ArrayList<Square> squaresColumns;
    private HashMap<Square, Square> path;

    public Maze(int algorithm, int size, int startRow, int startColumn) {
        this.algorithm = algorithm;
        Random random = new Random();
        this.values = new int[size][];
        for (int i = 0; i < values.length; i++) {
            int[] row = new int[size];
            for (int j = 0; j < row.length; j++) {
                if (i > 1 || j > 1) {
                    row[j] = random.nextInt(8) % 7 == 0 ? Definitions.OBSTACLE : Definitions.EMPTY;
                } else {
                    row[j] = Definitions.EMPTY;
                }
            }
            values[i] = row;
        }
        this.color = new String[size][size];
        for (int i = 0; i < values.length; i++) {
            for (int j = 0; j < values.length; j++) {
                this.color[i][j] = "white";
            }
        }
        values[0][0] = Definitions.EMPTY;
        values[size - 1][size - 1] = Definitions.EMPTY;
        this.visited = new boolean[this.values.length][this.values.length];
        this.startRow = startRow;
        this.startColumn = startColumn;
        this.buttonList = new ArrayList<>();
        this.rows = values.length;
        this.columns = values.length;

        this.path = new HashMap<>();
        this.squares = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            this.squaresColumns = new ArrayList<>();
            squares.add(squaresColumns);
            for (int j = 0; j < columns; j++) {
                Square s = new Square(i, j);
                squares.get(i).add(s);
                path.put(s, null);
                squares.get(i).get(j).sethCost(squares.get(i).get(j).hCost(new Square(rows - 1, columns - 1)));
            }
        }

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        this.setLocationRelativeTo(null);
        GridLayout gridLayout = new GridLayout(rows, columns);
        this.setLayout(gridLayout);
        for (int i = 0; i < rows * columns; i++) {
            int value = values[i / rows][i % columns];
            JButton jButton = new JButton(String.valueOf(i));
            if (value == Definitions.OBSTACLE) {
                jButton.setBackground(Color.BLACK);
            } else {
                jButton.setBackground(Color.WHITE);
            }
            this.buttonList.add(jButton);
            this.add(jButton);
        }
        this.setVisible(true);
        this.setSize(Definitions.WINDOW_WIDTH, Definitions.WINDOW_HEIGHT);
        this.setResizable(false);
    }

    public void checkWayOut() {
        new Thread(() -> {
            boolean result = false;

            switch (this.algorithm) {
                case Definitions.ALGORITHM_DFS:
                    result = this.DFS();
                    break;
                case Definitions.ALGORITHM_BFS:
                    result = this.BFS();
                    break;
                case Definitions.ALGORITHM_A_STAR:
                    result = this.AStar();
                    System.out.println(this.printPath(squares.get(rows-1).get(columns-1)));
                    break;
            }
            JOptionPane.showMessageDialog(null,  result ? "FOUND SOLUTION" : "NO SOLUTION FOR THIS MAZE");

        }).start();
    }
    private boolean AStar(){
        boolean result = false;
        Square start = squares.get(0).get(0);
        Square goal = squares.get(rows-1).get(columns-1);
        PriorityQueue<Square> priorityQueue = new PriorityQueue<>(rows * columns, new Square());
        start.setgCost(0);
        start.setfCost(start.gethCost());
        priorityQueue.add(start);
        while (!priorityQueue.isEmpty()){
            Square currentSquare = priorityQueue.poll();
            setSquareAsVisited(currentSquare.getX(), currentSquare.getY(), true);
/*            System.out.println("currentSquare: " + currentSquare +""+ currentSquare.getfCost());*/
            if (currentSquare.equals(goal)){
                result = true;
                break;
            }
            HashMap<Square, Boolean> neighbors;
            neighbors = this.setSquareNeighbors(currentSquare.getX(), currentSquare.getY(), currentSquare.getgCost());
            for (Square square: neighbors.keySet()){
/*                System.out.println(square + "" + square.getfCost());
                System.out.println((currentSquare.getfCost()) + "::" + square.getfCost());*/
                if (!this.visited[square.getX()][square.getY()] ) {
                    path.put(square, currentSquare);  //TODO fix path
                    priorityQueue.add(square);
                }
            }
        }
        return result;
    }

    /*
    void tracePath(cell cellDetails[][COL], Pair dest)
{
    printf("\nThe Path is ");
    int row = dest.first;
    int col = dest.second;

    stack<S> Path;

    while (!(cellDetails[row][col].parent_i == row
        && cellDetails[row][col].parent_j == col)) {
        Path.push(make_pair(row, col));
        int temp_row = cellDetails[row][col].parent_i;
        int temp_col = cellDetails[row][col].parent_j;
        row = temp_row;
        col = temp_col;
    }

    Path.push(make_pair(row, col));
    while (!Path.empty()) {
        pair<int, int> p = Path.top();
        Path.pop();
        printf("-> (%d,%d) ", p.first, p.second);
    }

    return;
}
    */
    private List<Square> printPath(Square goal){
        List<Square> pathToGoal = new ArrayList<>();
        for(Square square = goal; square!=null; square = this.path.get(square)){
            pathToGoal.add(square);
        }
        Collections.reverse(pathToGoal);

        return pathToGoal;
    }

    private HashMap<Square, Boolean> setSquareNeighbors(int x, int y, int gCost){
        HashMap<Square, Boolean> neighbors = new HashMap<>();
        int row;
        int column;
        for (int i = 0; i < 4; i++){
            if (i % 2 == 0){
                column = x;
                row = y + (i - 1);
            } else {
                column = x + (i - 2);
                row = y;
            }
            if ((column < this.values.length && row < this.values.length) && (column >= 0 && row >= 0)) {
                if (this.values[column][row] == Definitions.EMPTY) {

                    if (gCost + 10 < this.squares.get(column).get(row).getgCost()){
                        this.squares.get(column).get(row).setgCost(gCost + 10);
                    }
                    this.squares.get(column).get(row).setfCost(this.squares.get(column).get(row).fCost());
                    neighbors.put(this.squares.get(column).get(row), false);
                }
            }
        }
        for (int i = 0; i < 4; i++){
            if (i % 2 == 0){
                if (i == 0) {
                    column = x - 1;
                    row = y + 1;
                } else {
                    column = x + 1;
                    row = y - 1;
                }
            } else {
                column = x + (i - 2);
                row = y + (i - 2);
            }
            if ((column < this.values.length && row < this.values.length) && (column >= 0 && row >= 0)) {
                if (this.values[column][row] == Definitions.EMPTY) {

                    if (gCost + 14 < this.squares.get(column).get(row).getgCost()){
                        this.squares.get(column).get(row).setgCost(gCost + 14);
                    }
                    this.squares.get(column).get(row).setfCost(this.squares.get(column).get(row).fCost());
                    neighbors.put(this.squares.get(column).get(row), false);
                }
            }
        }
        return neighbors;
    }

    private boolean BFS (){
        boolean result = false;
        int x = 0;
        int y = 0;
        Queue<Point> queue = new LinkedList<>();
        queue.add(new Point(x,y));
        while (!queue.isEmpty()){
            Point currentButton = queue.remove();
            setSquareAsVisited(currentButton.x, currentButton.y, true);
            if (currentButton.x == this.values.length-1 && currentButton.y == this.values.length-1){
                result = true;
                break;
            }
            LinkedList<Point> neighbors = this.setNeighbors(currentButton.x, currentButton.y);
            for (Point neighbor: neighbors){
                if (!this.visited[neighbor.x][neighbor.y]){
                    queue.add(neighbor);
                }
            }
        }
        return result;
    }

    private boolean DFS(){
        boolean result = false;
        Stack<Point> myStack = new Stack<>();
        myStack.add(new Point(0,0));
        while (!myStack.isEmpty()){
            Point currentButton = myStack.pop();
            if (!this.visited[currentButton.x][currentButton.y]){
                setSquareAsVisited(currentButton.x, currentButton.y, true);
                if (currentButton.x == this.values.length-1 && currentButton.y == this.values.length-1){
                    result = true;
                    break;
                }
                LinkedList<Point> neighbors = this.setNeighbors(currentButton.x, currentButton.y);
                for (Point neighbor: neighbors){
                    if (!this.visited[neighbor.x][neighbor.y]){
                        myStack.add(neighbor);
                    }
                }
            }
        }
        return result;
    }

    private LinkedList<Point> setNeighbors(int x, int y){
        LinkedList<Point> neighbors = new LinkedList<>();
        int row;
        int column;
        for (int i = 0; i < 4; i++){
            if (i % 2 == 0){
                column = x;
                row = y + (i - 1);
            } else {
                column = x + (i - 2);
                row = y;
            }
            if ((column < this.values.length && row < this.values.length) && (column >= 0 && row >= 0)) {
                if (this.values[column][row] == Definitions.EMPTY && this.color[column][row].equals("white")) {
                    neighbors.add(new Point(column, row));
                }
            }
        }
        return neighbors;
    }

    public void setSquareAsVisited(int x, int y, boolean visited) {
        try {
            if (visited) {
                if (this.backtracking) {
                    Thread.sleep(Definitions.PAUSE_BEFORE_NEXT_SQUARE * 5);
                    this.backtracking = false;
                }
                this.visited[x][y] = true;
                for (int i = 0; i < this.visited.length; i++) {
                    for (int j = 0; j < this.visited[i].length; j++) {
                        if (this.visited[i][j]) {
                            if (i == x && y == j) {
                                this.buttonList.get(i * this.rows + j).setBackground(Color.RED);
                            } else {
                                this.buttonList.get(i * this.rows + j).setBackground(Color.BLUE);
                            }
                        }
                    }
                }
            } else {
                this.visited[x][y] = false;
                this.buttonList.get(x * this.columns + y).setBackground(Color.WHITE);
                Thread.sleep(Definitions.PAUSE_BEFORE_BACKTRACK);
                this.backtracking = true;
            }
            if (!visited) {
                Thread.sleep(Definitions.PAUSE_BEFORE_NEXT_SQUARE / 4);
            } else {
                Thread.sleep(Definitions.PAUSE_BEFORE_NEXT_SQUARE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
