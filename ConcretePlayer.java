package matala1;

public class ConcretePlayer implements Player {
    private int wins = 0;
    private boolean playerTurn;
    private int playerNum;

    public ConcretePlayer(int num,boolean playerTurn){
        this.playerNum = num;
        this.playerTurn = playerTurn;
    }

    @Override
    public boolean isPlayerOne() {
        return this.playerNum==1;
    }

    @Override
    public int getWins() {
        return wins;
    }

    public void won(){
        this.wins++;
    }

    public void turnStart(){
        this.playerTurn = true;
    }

    public void turnEnd(){
        this.playerTurn = false;
    }

    public boolean turn(){
        return this.playerTurn;
    }
}
