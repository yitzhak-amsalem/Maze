
import javax.swing.*;
import java.awt.*;
import java.util.*;

public class Maze extends JFrame {

    private int[][] values;
    private boolean[][] visited;
    private int startRow;
    private int startColumn;
    private ArrayList<JButton> buttonList;
    private int rows;
    private int columns;
    private boolean backtracking;
    private int algorithm;

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
        values[0][0] = Definitions.EMPTY;
        values[size - 1][size - 1] = Definitions.EMPTY;
        this.visited = new boolean[this.values.length][this.values.length];
        this.startRow = startRow;
        this.startColumn = startColumn;
        this.buttonList = new ArrayList<>();
        this.rows = values.length;
        this.columns = values.length;

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
                case Definitions.ALGORITHM_BRUTE_FORCE:
                    result = this.DFSRec(new Point(0,0));
                    break;
                case Definitions.ALGORITHM_DFS:
                    result = this.DFS();
                    break;
                case Definitions.ALGORITHM_BFS:
                    result = this.BFS();
                    break;
            }
            JOptionPane.showMessageDialog(null,  result ? "FOUND SOLUTION" : "NO SOLUTION FOR THIS MAZE");

        }).start();
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
    private boolean DFSRec(Point point){
        if (!this.visited[point.x][point.y]){
            setSquareAsVisited(point.x, point.y, true);
            if (point.x == this.values.length-1 && point.y == this.values.length-1){
                return true;

            }
            LinkedList<Point> neighbors = this.setNeighbors(point.x, point.y);
            for (Point neighbor: neighbors){
                DFSRec(neighbor);
            }
        }
        return false;
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
                if (this.values[column][row] == Definitions.EMPTY) {
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