import javax.swing.*;
import java.util.Random;


public class Main {
    public static void main(String[] args) {
        String algorithmString = JOptionPane.showInputDialog("Choose algorithm (1 - DFS, 2 - BFS, 3 - A*):");
        String sizeAsString = JOptionPane.showInputDialog("Choose maze size:");
        int size = Integer.parseInt(sizeAsString);
        int algorithm = Integer.parseInt(algorithmString);
        Maze maze;
        if (algorithm == Definitions.ALGORITHM_A_STAR){
            String startStringX = JOptionPane.showInputDialog("Choose start x (less than " + size + ") :");
            String startStringY = JOptionPane.showInputDialog("Choose start y (less than " + size + ") :");
            String goalStringX = JOptionPane.showInputDialog("Choose goal x (less than " + size + ") :");
            String goalStringY = JOptionPane.showInputDialog("Choose goal y (less than " + size + ") :");
            int startX = Integer.parseInt(startStringX);
            int startY = Integer.parseInt(startStringY);
            int goalX = Integer.parseInt(goalStringX);
            int goalY = Integer.parseInt(goalStringY);
            Square start = new Square(startX, startY);
            Square goal = new Square(goalX, goalY);
            maze = new Maze(algorithm, size, 0, 0, start, goal);
        } else {
            maze = new Maze(algorithm, size, 0, 0, new Square(0,0), new Square(size-1,size-1));
        }
        maze.checkWayOut();
    }

}
