import java.awt.*;
import java.util.Comparator;

import static java.lang.Math.abs;
import static java.lang.Math.min;


public class Square implements Comparator<Square> {
    private int x;
    private int y;
    private int fCost;
    private int gCost;
    private int hCost;
    public static final int MAX_DIST = 99999;

    public Square() {

    }

    public Square(int x, int y) {
        this.x = x;
        this.y = y;
        this.fCost = MAX_DIST;
        this.gCost = MAX_DIST;
        this.hCost = MAX_DIST;
    }

    public int hCost(Square goal){
        int dx = abs(this.x - goal.x);
        int dy = abs(this.y - goal.y);

        return 10 * (dx + dy) + (14 - 2 * 10) * min(dx, dy);
    }
    public String toString(){
        return "[" + this.x + ", " + this.y + "] " + this.fCost;
    }

    public int fCost(){
        return this.gCost + this.hCost;
    }

    @Override
    public int compare(Square s1, Square s2) {
        return Integer.compare(s1.fCost, s2.fCost);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getfCost() {
        return fCost;
    }

    public void setfCost(int fCost) {
        this.fCost = fCost;
    }

    public int getgCost() {
        return gCost;
    }

    public void setgCost(int gCost) {
        this.gCost = gCost;
    }

    public int gethCost() {
        return hCost;
    }

    public void sethCost(int hCost) {
        this.hCost = hCost;
    }


}
