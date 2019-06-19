package Othello;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

abstract class Button {
    private int x, y;
    private int w, h;
    private String text;

    Main main;
    Button (int x, int y, int w, int h, String text) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.text = text;
    }

    boolean isOver(int x, int y) {
        return x > this.x && y > this.y && x < this.x + w && y < this.y + h;
    }

    void handleClick(int x, int y) {
        if (isOver(x, y))
            onClick();
    }

    void show(GraphicsContext c, boolean hover) {
        c.setFill(Color.rgb(130, 130, 130));
        c.fillRect(x, y, w, h);

        c.setFill(Color.rgb(30, 30, 30));
        c.fillText(text, x + (w + text.length()) / 2.5 - 6, y + h / 2f + 5);

        if (hover) {
            c.setStroke(Color.rgb(130, 190, 160));
            c.strokeRect(x, y, w, h);
        }
    }

    abstract void onClick();

    void setParent(Main m) {
        main = m;
    }
}
