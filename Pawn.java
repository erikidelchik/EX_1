package matala1;

public class Pawn extends ConcretePiece {
    private int kills = 0;

    public Pawn(){

    }
    public Pawn(Pawn p){
        super(p);
        this.kills = p.kills;
    }

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
    public int getKills(){
        return this.kills;
    }



}
