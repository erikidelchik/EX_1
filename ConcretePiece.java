package matala1;

import java.util.Stack;

public abstract class ConcretePiece implements Piece {
    private Position pos;
    private String name;
    private Stack<Position> posStack = new Stack<>();
    private int steps = 0;
    private Player owner;

    public void setPosStack(Position pos){
        this.posStack.push(pos);
    }

    public void setPos(Position pos) {
        this.pos = pos;
    }


    public void addSteps(int steps){
        this.steps+=steps;
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
    public Stack<Position> getPosStack(){
        return this.posStack;
    }


}
