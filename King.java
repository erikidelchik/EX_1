package matala1;

public class King extends ConcretePiece {


    @Override
    public Player getOwner() {
        return returnOwner();
    }

    @Override
    public String getType() {
        return "♔";
    }

}

