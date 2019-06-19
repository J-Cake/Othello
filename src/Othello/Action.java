package Othello;

class Action {
    private int action;
    int turn;
    int[][] gameState;
    Action (int action, int [][] gameState, int turn) {
        this.action = action;
        this.gameState = gameState;
        this.turn = turn;
    }

    static int INIT = 0;
    static int MOVE = 1;
}
