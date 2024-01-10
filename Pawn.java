package matala1;

public class Pawn extends ConcretePiece {
    private int kills = 0;


    @Override
    public Player getOwner() {
        return returnOwner();

    }

    @Override
    public String getType() {
        return "♟";
    }

    public void getKill(){
        this.kills++;
    }




}
