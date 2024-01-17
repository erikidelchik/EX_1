package matala1;

import java.util.LinkedList;

public class Position {
    private LinkedList<String> differentPieces;
    private int x;
    private int y;
    public Position(int x,int y){
        this.x = x;
        this.y = y;
        this.differentPieces = new LinkedList<>();
    }
    public int getX(){
        return this.x;
    }
    public int getY(){
        return this.y;
    }
    public void setX(int x){
        this.x = x;
    }
    public void setY(int y){
        this.y = y;
    }

    public String toString(){
        return "("+this.x+", "+this.y+")";
    }

    public LinkedList<String> getList(){
        return this.differentPieces;
    }
    public void addItemToList(String str){
        this.differentPieces.add(str);
    }
}

