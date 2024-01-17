package matala1;

import java.util.*;

public class GameLogic implements PlayableLogic {

    private Piece[][] board;
    private Stack<Piece[][]> boardStack;
    private Player player1;
    private Player player2;
    private Player currentWinner;
    private boolean gameFinished = false;
    private LinkedList<ConcretePiece> piecesThatMoved;
    private LinkedList<Pawn> piecesThatKilled;
    private HashMap<Position, LinkedList<String>> differentPiecesOnBlock;
    private Position kingPos;


    public GameLogic() {

        this.boardStack = new Stack<>();
        this.player1 = new ConcretePlayer(1, false);
        this.player2 = new ConcretePlayer(2, true);
        this.currentWinner = new ConcretePlayer();
        this.board = setNewBoard();
        this.piecesThatMoved = new LinkedList<>();
        this.piecesThatKilled = new LinkedList<>();
        this.differentPiecesOnBlock = new HashMap<>();

    }

    @Override
    public boolean move(Position a, Position b) {
        //moveIsValid also adding steps
        if (moveIsValid(a, b)) {

            this.board[b.getX()][b.getY()] = this.board[a.getX()][a.getY()];
            this.board[a.getX()][a.getY()] = null;
            ConcretePiece piece = (ConcretePiece) this.board[b.getX()][b.getY()];

            piece.setPos(b);
            piece.setPosQueue(new Position(b.getX(), b.getY()));

            if (this.board[b.getX()][b.getY()] instanceof Pawn) {
                checkIfAteNearWall((Pawn) this.board[b.getX()][b.getY()]);
                checkIfAte((Pawn) this.board[b.getX()][b.getY()]);
            } else {
                this.kingPos = b;
                if (posAtCorner(b)) {
//
                    ((ConcretePlayer) this.player1).won();
                    this.currentWinner = player1;
                    gameFinished = true;
                    printStats();
                    return true;
                }

            }
            this.boardStack.push(copyBoard(this.board));
            if (p1Lost()) {
                this.currentWinner = this.player2;
                ((ConcretePlayer) this.player2).won();
                gameFinished = true;
                printStats();
                return true;
            }
            addPieceToMapIfNeeded(b);
            if (!this.piecesThatMoved.contains(piece))
                this.piecesThatMoved.add(piece);
            switchTurns();


            return true;
        }
        return false;
    }

    private boolean moveIsValid(Position a, Position b) {
        boolean available = false;
        if (!((ConcretePlayer) getPieceAtPosition(a).getOwner()).turn()) return false;
        if (a.getX() == b.getX() && a.getY() == b.getY()) return false;


        if (this.board[a.getX()][a.getY()] != null) {
            //check if pawn trying to go to the corner
            if (this.board[a.getX()][a.getY()] instanceof Pawn && posAtCorner(b))
                return false;

            else {
                //in case of the same x value
                if (a.getX() == b.getX()) {
                    if (b.getY() > a.getY()) {
                        for (int i = 1; i <= Math.abs(b.getY() - a.getY()); i++) {
                            //check if any piece is in the way
                            if (this.board[a.getX()][a.getY() + i] != null) return false;
                        }
                        //in case move is available
                        ((ConcretePiece) this.board[a.getX()][a.getY()]).addSteps(Math.abs(b.getY() - a.getY()));
                        available = true;
                    } else {
                        for (int i = 1; i <= Math.abs(b.getY() - a.getY()); i++) {
                            //check if any piece is in the way
                            if (this.board[a.getX()][a.getY() - i] != null) return false;
                        }
                        //in case move is available
                        ((ConcretePiece) this.board[a.getX()][a.getY()]).addSteps(Math.abs(b.getY() - a.getY()));
                        available = true;
                    }
                }
                //in case of the same y value
                else if (a.getY() == b.getY()) {
                    //dest point is on the right
                    if (b.getX() > a.getX()) {
                        for (int i = 1; i <= Math.abs(b.getX() - a.getX()); i++) {
                            //check if any piece is in the way
                            if (this.board[a.getX() + i][a.getY()] != null) return false;
                        }
                        //in case move is available
                        ((ConcretePiece) this.board[a.getX()][a.getY()]).addSteps(Math.abs(b.getX() - a.getX()));
                        available = true;
                    }
                    //dest point is on the left
                    else {
                        for (int i = 1; i <= Math.abs(b.getX() - a.getX()); i++) {
                            //check if any piece is in the way
                            if (this.board[a.getX() - i][a.getY()] != null) return false;
                        }
                        //in case move is available
                        ((ConcretePiece) this.board[a.getX()][a.getY()]).addSteps(Math.abs(b.getX() - a.getX()));
                        available = true;
                    }
                }
            }

        }
        return available;
    }

    @Override
    public Piece getPieceAtPosition(Position position) {
        return this.board[position.getX()][position.getY()];
    }

    @Override
    public Player getFirstPlayer() {
        return this.player1;
    }

    @Override
    public Player getSecondPlayer() {
        return this.player2;
    }

    @Override
    public boolean isGameFinished() {
        return this.gameFinished;
    }

    @Override
    public boolean isSecondPlayerTurn() {
        return ((ConcretePlayer) this.player2).turn();
    }

    @Override
    public void reset() {

        if (((ConcretePlayer) this.player1).turn()) {
            switchTurns();
        }
        while (!this.boardStack.isEmpty()) {
            this.boardStack.pop();
        }
        this.differentPiecesOnBlock = new HashMap<>();
        this.piecesThatMoved = new LinkedList<>();
        this.piecesThatKilled = new LinkedList<>();

        this.board = setNewBoard();
        this.gameFinished = false;


    }

    @Override
    public void undoLastMove() {
        if (this.boardStack.size() > 1) {

            this.boardStack.pop();

            this.board = copyBoard(boardStack.peek());


            switchTurns();
        }
    }

//    public void displayBoard(Piece [][] b){
//        for(int i=0;i<11;i++){
//            System.out.println("[");
//            for(int j=0;j<11;j++){
//                if(b[i][j]==null) System.out.print("null,");
//                else System.out.print(((ConcretePiece)b[i][j]).getName()+",");
//            }
//            System.out.println("]\n");
//        }
//        System.out.println("*************************************************************");
//    }

    @Override
    public int getBoardSize() {
        return 11;
    }

    private boolean p1Lost() {
        int kingX = this.kingPos.getX();
        int kingY = this.kingPos.getY();
        boolean p1lost = false;
        if (kingX <= 9 && kingX >= 1 && kingY <= 9 && kingY >= 1) {
            if (this.board[kingX + 1][kingY] != null && this.board[kingX - 1][kingY] != null && this.board[kingX][kingY + 1] != null && this.board[kingX][kingY - 1] != null) {
                if (this.board[kingX + 1][kingY].getOwner().equals(player2) && this.board[kingX - 1][kingY].getOwner().equals(player2) && this.board[kingX][kingY + 1].getOwner().equals(player2) && this.board[kingX][kingY - 1].getOwner().equals(player2))
                    p1lost = true;
            }
        } else {
            if (kingX == 0) {
                if (this.board[kingX + 1][kingY] != null && this.board[kingX][kingY + 1] != null && this.board[kingX][kingY - 1] != null) {
                    if (this.board[kingX + 1][kingY].getOwner().equals(player2) && this.board[kingX][kingY + 1].getOwner().equals(player2) && this.board[kingX][kingY - 1].getOwner().equals(player2))
                        p1lost = true;
                }
            }
            if (kingX == 10) {
                if (this.board[kingX - 1][kingY] != null && this.board[kingX][kingY + 1] != null && this.board[kingX][kingY - 1] != null) {
                    if (this.board[kingX - 1][kingY].getOwner().equals(player2) && this.board[kingX][kingY + 1].getOwner().equals(player2) && this.board[kingX][kingY - 1].getOwner().equals(player2))
                        p1lost = true;
                }
            }
            if (kingY == 0) {
                if (this.board[kingX + 1][kingY] != null && this.board[kingX - 1][kingY] != null && this.board[kingX][kingY + 1] != null) {
                    if (this.board[kingX + 1][kingY].getOwner().equals(player2) && this.board[kingX - 1][kingY].getOwner().equals(player2) && this.board[kingX][kingY + 1].getOwner().equals(player2))
                        p1lost = true;
                }
            }
            if (kingY == 10) {
                if (this.board[kingX + 1][kingY] != null && this.board[kingX - 1][kingY] != null && this.board[kingX][kingY - 1] != null) {
                    if (this.board[kingX + 1][kingY].getOwner().equals(player2) && this.board[kingX - 1][kingY].getOwner().equals(player2) && this.board[kingX][kingY - 1].getOwner().equals(player2))
                        p1lost = true;
                }
            }
        }
        if (p1lost) {
            return true;
        }
        return false;

    }

    private void printStats() {
        PieceMovesComparator moveCom = new PieceMovesComparator();
        Collections.sort(piecesThatMoved,moveCom);
        while (!this.piecesThatMoved.isEmpty()) {
            ConcretePiece piece = piecesThatMoved.getFirst();
            piece.printQueue();

            this.piecesThatMoved.removeFirst();
            System.out.println();
        }
        print75();
        PieceKillsComparator com = new PieceKillsComparator();
        Collections.sort(this.piecesThatKilled,com);
        while (!this.piecesThatKilled.isEmpty()){
            System.out.println(piecesThatKilled.getFirst().getName()+": "+piecesThatKilled.getFirst().getKills()+" kills");
            piecesThatKilled.removeFirst();
        }

    }

    private void print75(){
        for(int i=1;i<=75;i++)
            System.out.print("*");
        System.out.println();
    }

    private void addPieceToMapIfNeeded(Position b) {
        if (!this.differentPiecesOnBlock.containsKey(b)) {
            //first step on this block
            LinkedList<String> list = new LinkedList<>();
            list.add(((ConcretePiece) this.board[b.getX()][b.getY()]).getName());
            this.differentPiecesOnBlock.put(b, list);
        }
        //more than one piece stepped on this block
        else {
            //if the piece not in the list, add it to the list
            if (!differentPiecesOnBlock.get(b).contains(((ConcretePiece) this.board[b.getX()][b.getY()]).getName())) {
                differentPiecesOnBlock.get(b).add(((ConcretePiece) this.board[b.getX()][b.getY()]).getName());
            }
        }
    }

    private boolean posAtCorner(Position position) {
        Position[] posArr = {new Position(0, 0), new Position(0, 10), new Position(10, 0), new Position(10, 10)};
        for (int i = 0; i < 4; i++) {
            if (position.getX() == posArr[i].getX() && position.getY() == posArr[i].getY()) return true;
        }
        return false;
    }

    //copies the given board and returns it
    private Piece[][] copyBoard(Piece[][] board) {
        Piece[][] copy = new Piece[11][11];
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 11; j++) {
                copy[i][j] = board[i][j];
            }
        }
        return copy;
    }

    private boolean pointAtCorner(int x, int y) {
        Position[] posArr = {new Position(0, 0), new Position(0, 10), new Position(10, 0), new Position(10, 10)};
        for (int i = 0; i < 4; i++) {
            if (x == posArr[i].getX() && y == posArr[i].getY()) return true;
        }
        return false;
    }

    private void checkIfAteNearWall(Pawn pawn) {
        ConcretePiece right, left, up, down;
        boolean killed = false;
        int pawnX = pawn.getPos().getX();
        int pawnY = pawn.getPos().getY();

        if (pawnX == 9) {
            if (this.board[pawn.getPos().getX() + 1][pawn.getPos().getY()] instanceof Pawn) {
                right = (Pawn) this.board[pawn.getPos().getX() + 1][pawn.getPos().getY()];
                if (!right.getOwner().equals(pawn.getOwner()))
                    killed = isKilled(pawn, right);
            }
        }

        if (pawnX == 1) {
            if (this.board[pawn.getPos().getX() - 1][pawn.getPos().getY()] instanceof Pawn) {
                left = (Pawn) this.board[pawn.getPos().getX() - 1][pawn.getPos().getY()];
                if (!left.getOwner().equals(pawn.getOwner()))
                    killed = isKilled(pawn, left);
            }
        }

        if (pawnY == 1) {
            if (this.board[pawn.getPos().getX()][pawn.getPos().getY() - 1] instanceof Pawn) {
                up = (Pawn) this.board[pawn.getPos().getX()][pawn.getPos().getY() - 1];
                if (!up.getOwner().equals(pawn.getOwner()))
                    killed = isKilled(pawn, up);
            }
        }

        if (pawnY == 9) {
            if (this.board[pawn.getPos().getX()][pawn.getPos().getY() + 1] instanceof Pawn) {
                down = (Pawn) this.board[pawn.getPos().getX()][pawn.getPos().getY() + 1];
                if (!down.getOwner().equals(pawn.getOwner()))
                    killed = isKilled(pawn, down);
            }
        }
        if (killed && !piecesThatKilled.contains(pawn)) piecesThatKilled.add(pawn);

    }

    //check if ate by sandwiching a piece
    private void checkIfAte(Pawn pawn) {

        ConcretePiece right, rightright, left, leftleft, up, upup, down, downdown;
        boolean killed = false;

        //check if can take a pawn on right side


        if (pawn.getPos().getX() <= 8) {
            if ((!(this.board[pawn.getPos().getX() + 1][pawn.getPos().getY()] instanceof King) && !(this.board[pawn.getPos().getX() + 2][pawn.getPos().getY()] instanceof King))) {
                right = (Pawn) this.board[pawn.getPos().getX() + 1][pawn.getPos().getY()];
                rightright = (Pawn) this.board[pawn.getPos().getX() + 2][pawn.getPos().getY()];
                if (right != null) {
                    //check if right block is opponent block
                    if (!right.getOwner().equals(pawn.getOwner())) {
                        //check if the right right block is a corner block or a friendly piece block
                        if ((rightright != null && rightright.getOwner().equals(pawn.getOwner())) || pointAtCorner(pawn.getPos().getX() + 2, pawn.getPos().getY())) {
                            killed = isKilled(pawn, right);


                        }
                    }
                }
            }
        }
        //check if can take a pawn on left side
        if (pawn.getPos().getX() >= 2) {
            if ((!(this.board[pawn.getPos().getX() - 1][pawn.getPos().getY()] instanceof King) && !(this.board[pawn.getPos().getX() - 2][pawn.getPos().getY()] instanceof King))) {
                left = (Pawn) this.board[pawn.getPos().getX() - 1][pawn.getPos().getY()];
                leftleft = (Pawn) this.board[pawn.getPos().getX() - 2][pawn.getPos().getY()];
                if (left != null) {
                    //check if left block is opponent block
                    if (!left.getOwner().equals(pawn.getOwner())) {
                        //check if the left left block is a corner block or a friendly piece block
                        if ((leftleft != null && leftleft.getOwner().equals(pawn.getOwner())) || pointAtCorner(pawn.getPos().getX() - 2, pawn.getPos().getY())) {
                            killed = isKilled(pawn, left);

                        }
                    }
                }
            }
        }
        //check if can take a piece from up
        if (pawn.getPos().getY() >= 2) {
            if ((!(this.board[pawn.getPos().getX()][pawn.getPos().getY() - 1] instanceof King) && !(this.board[pawn.getPos().getX()][pawn.getPos().getY() - 2] instanceof King))) {
                up = (Pawn) this.board[pawn.getPos().getX()][pawn.getPos().getY() - 1];
                upup = (Pawn) this.board[pawn.getPos().getX()][pawn.getPos().getY() - 2];
                if (up != null) {
                    //check if up block is opponent block
                    if (!up.getOwner().equals(pawn.getOwner())) {
                        //check if the up up block is a corner block or a friendly piece block
                        if ((upup != null && upup.getOwner().equals(pawn.getOwner())) || pointAtCorner(pawn.getPos().getX(), pawn.getPos().getY() - 2)) {
                            killed = isKilled(pawn, up);

                        }
                    }
                }
            }
        }
        //check if can take a piece from down
        if (pawn.getPos().getY() <= 8) {
            if ((!(this.board[pawn.getPos().getX()][pawn.getPos().getY() + 1] instanceof King) && !(this.board[pawn.getPos().getX()][pawn.getPos().getY() + 2] instanceof King))) {
                down = (Pawn) this.board[pawn.getPos().getX()][pawn.getPos().getY() + 1];
                downdown = (Pawn) this.board[pawn.getPos().getX()][pawn.getPos().getY() + 2];
                if (down != null) {
                    //check if up block is opponent block
                    if (!down.getOwner().equals(pawn.getOwner())) {
                        //check if the up up block is a corner block or a friendly piece block
                        if ((downdown != null && downdown.getOwner().equals(pawn.getOwner())) || pointAtCorner(pawn.getPos().getX(), pawn.getPos().getY() + 2)) {
                            killed = isKilled(pawn, down);

                        }
                    }
                }
            }
        }

        if (killed && !piecesThatKilled.contains(pawn)) piecesThatKilled.add(pawn);


    }

    //adding the side piece to piecesThatMoved and piecesThatKilled if not there, and removing it from the board
    private boolean isKilled(Pawn pawn, ConcretePiece side) {
//        if (piecesThatMoved.contains(side)) {
//            piecesThatMoved.remove(side);
//            piecesThatMoved.add(new Pawn((Pawn) side));
//        }
//        if (piecesThatKilled.contains(side)) {
//            piecesThatKilled.remove(side);
//            piecesThatKilled.add(new Pawn((Pawn) side));
//        }

        this.board[side.getPos().getX()][side.getPos().getY()] = null;
        ((Pawn) this.board[pawn.getPos().getX()][pawn.getPos().getY()]).getKill();

        return true;
    }

    private void switchTurns() {
        if (((ConcretePlayer) this.player1).turn()) {
            ((ConcretePlayer) this.player1).turnEnd();
            ((ConcretePlayer) this.player2).turnStart();
        } else {
            ((ConcretePlayer) this.player1).turnStart();
            ((ConcretePlayer) this.player2).turnEnd();
        }
    }

    private Piece[][] setNewBoard() {

        String[][] arr = {{"0", "0", "0", "A1", "A2", "A3", "A4", "A5", "0", "0", "0"},
                {"0", "0", "0", "0", "0", "A6", "0", "0", "0", "0", "0"},
                {"0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0"},
                {"A7", "0", "0", "0", "0", "D1", "0", "0", "0", "0", "A8"},
                {"A9", "0", "0", "0", "D2", "D3", "D4", "0", "0", "0", "A10"},
                {"A11", "A12", "0", "D5", "D6", "K7", "D8", "D9", "0", "A13", "A14"},
                {"A15", "0", "0", "0", "D10", "D11", "D12", "0", "0", "0", "A16"},
                {"A17", "0", "0", "0", "0", "D13", "0", "0", "0", "0", "A18"},
                {"0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0"},
                {"0", "0", "0", "0", "0", "A19", "0", "0", "0", "0", "0"},
                {"0", "0", "0", "A20", "A21", "A22", "A23", "A24", "0", "0", "0"}};

        Piece[][] newBoard = new ConcretePiece[11][11];
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 11; j++) {
                if (arr[i][j].equals("0")) {
                    arr[i][j] = null;
                } else if (arr[i][j].charAt(0) == 'K') {
                    Position p = new Position(i, j);
                    this.kingPos = p;
                    newBoard[i][j] = new King();
                    ((King) newBoard[i][j]).setPos(p);
                    ((King) newBoard[i][j]).setPosQueue(p);
                    ((King) newBoard[i][j]).setName(arr[i][j]);
                    ((King) newBoard[i][j]).setOwner(player1);

                } else {
                    Position p = new Position(i, j);
                    newBoard[i][j] = new Pawn();
                    ((Pawn) newBoard[i][j]).setPos(p);
                    ((Pawn) newBoard[i][j]).setPosQueue(p);
                    ((Pawn) newBoard[i][j]).setName(arr[i][j]);
                    if (arr[i][j].charAt(0) == 'A') {
                        ((Pawn) newBoard[i][j]).setOwner(player2);
                    } else ((Pawn) newBoard[i][j]).setOwner(player1);
                }
            }
        }


        this.boardStack.push(copyBoard(newBoard));
        return newBoard;


    }

    class PieceMovesComparator implements Comparator<ConcretePiece>{

        @Override
        public int compare(ConcretePiece p1, ConcretePiece p2) {
            if(!p1.getOwner().equals(p2.getOwner())){
                if(p1.getOwner().equals(player1)){
                    if(currentWinner.equals(player2)) return 1;
                    else return -1;

                }
                else if(p1.getOwner().equals(player2)){
                    if(currentWinner.equals(player2)) return -1;
                    else return 1;
                }
            }
            else{
                //first sort by stack sizes
                if(p1.getPosQueue().size()<p2.getPosQueue().size()) return -1;
                else if (p1.getPosQueue().size()>p2.getPosQueue().size()) return 1;
                //then sort by name values
                else if(Integer.parseInt(p1.getName().substring(1))>Integer.parseInt(p2.getName().substring(1))) return 1;
                else if(Integer.parseInt(p1.getName().substring(1))<Integer.parseInt(p2.getName().substring(1))) return -1;
            }
            return 0;
        }
    }


    class PieceKillsComparator implements Comparator<Pawn> {

        @Override
        public int compare(Pawn p1, Pawn p2) {

            if (p1.getKills() < p2.getKills()) return 1;
            else if (p1.getKills() > p2.getKills()) {
                return -1;
            }
            //swap if p1 lost and p2 won

            else {
                if (!p1.getOwner().equals(p2.getOwner())) {
                    if(p1.getOwner().equals(player1)) {
                        if(currentWinner.equals(player2)) return 1;
                        else return -1;
                    }
                    else if(p1.getOwner().equals(player2)){
                        if(currentWinner.equals(player2)) return -1;
                        else return 1;
                    }
                }
            }
            return 0;
        }
    }


}
