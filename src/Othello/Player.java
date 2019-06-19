package Othello;

class Player {
    int score;
    int player;
    boolean autoMove;

    Player(int player) {
        score = 0;
        this.player = player;
        autoMove = false;
    }

    void calcScore(Piece[][] pieces) {
        score = 0;
        for (Piece[] pcs : pieces)
            for (Piece p : pcs)
                if (p.placed && p.player == player)
                    score++;
    }

    void move(int[][] gameState) {
        System.out.println("Minimax is not implemented yet");
    }
}
