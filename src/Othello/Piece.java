package Othello;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

class Piece {
    int player = -1;
    int x, y;
    boolean placed;

    Piece (int x, int y) {
        this.x = y;
        this.y = x; // I have no idea why this is necessary.

        placed = false;
    }

    void setPlayer(int player) {
        this.player = player;
    }

    void flip() {
        player *= -1;
    }

    void show(GraphicsContext c, int scale) {
        if (player == -1)
            c.setFill(Color.rgb(20, 20, 30));
        else if (player == 1)
            c.setFill(Color.rgb(235, 235, 245));

        if (placed)
            c.fillOval(x * scale + (scale / 2f),
                    y * scale + (scale / 2f),
                    scale / 2f,
                    scale / 2f);
    }
}
