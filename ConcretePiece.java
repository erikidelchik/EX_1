package matala1;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public abstract class ConcretePiece implements Piece {
    private Position pos;
    private String name;
    private Queue<Position> posQueue = new LinkedList<>();
    private int steps = 0;
    private Player owner;

    public ConcretePiece(){

    }
    public ConcretePiece(ConcretePiece piece){
        this.pos = piece.pos;
        this.name = piece.name;
        this.posQueue = new LinkedList<>();
        this.steps = piece.steps;
        this.owner = piece.owner;

    }

    public void setPosQueue(Position pos){
        this.posQueue.add(pos);
    }

    public void setPos(Position pos) {
        this.pos = pos;
    }
    public void addSteps(int steps){
        this.steps+=steps;
    }
    public int getSteps(){
        return this.steps;
    }
    public Position getPos(){
        return this.pos;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return this.name;
    }
    public void setOwner(Player player){
        this.owner = player;
    }
    public Player returnOwner(){
        return this.owner;
    }
    public Queue<Position> getPosQueue(){
        return this.posQueue;
    }
    public void printQueue(){
        System.out.print(this.name+": [");
        while(this.posQueue.size()>1){
            System.out.print(posQueue.remove()+", ");
        }
        System.out.print(posQueue.remove()+"]");

    }



}
