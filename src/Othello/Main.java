package Othello;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.jetbrains.annotations.Contract;

import java.awt.*;
import java.util.ArrayList;

public class Main extends Application {
    private GraphicsContext c;
    private final int width = 720, height = 360;

    ArrayList<Action> actions;
    int activeGameState;

    private Player white;
    private Player black;

    private Piece[][] pieces;
    private boolean[][] validMap;

    private int turn;

    private int mouseX, mouseY;
    private int gridX, gridY;

    private int scale;
    private int padding;

    private int[][] dirs = {{1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}, {1, -1}};

    private Button[] buttons;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Othello");
        primaryStage.setResizable(false);

        padding = 20;
        scale = (height - padding) / 8;

        turn = -1;

        mouseX = 0;
        mouseY = 0;

        gridX = 0;
        gridY = 0;

        pieces = new Piece[8][8];
        validMap = new boolean[8][8];

        white = new Player(1);
        black = new Player(-1);

        // the variable padding is used throughout the program as a "unit" of size

        buttons = new Button[4];
        buttons[0] = new UndoBtn(width / 2 + padding / 2, height - padding / 2 - (padding * 5), padding * 2, (int) ((double) padding * 1.5f));
        buttons[0].setParent(this);
        buttons[1] = new RestartBtn(width / 2 + padding / 2 + padding * 2, height - padding / 2 - (padding * 5), padding * 2, (int) ((double) padding * 1.5f));
        buttons[1].setParent(this);
        buttons[2] = new AIBtn(width - width / 4, 1 * padding, 4 * padding, padding, white); // shut up IDEA
        buttons[2].setParent(this);
        buttons[3] = new AIBtn(width - width / 4, 2 * padding, 4 * padding, padding, black);
        buttons[3].setParent(this);

        actions = new ArrayList<>();

        for (int i = 0; i < pieces.length; i++)
            for (int j = 0; j < pieces[i].length; j++) {
                pieces[i][j] = new Piece(i, j);

                if ((i == 3 || i == 4) && (j == 3 || j == 4)) {
                    pieces[i][j].placed = true;

                    if (i == j)
                        pieces[i][j].player = 1;
                    else
                        pieces[i][j].player = -1;
                }
            }

        Group root = new Group();

        Scene theScene = new Scene(root);
        primaryStage.setScene(theScene);

        Canvas canvas = new Canvas(width, height);

        root.getChildren().addAll(canvas);

        c = canvas.getGraphicsContext2D();

        new AnimationTimer() {
            public void handle(long currentTime) {
                final int windowX = (int) theScene.getWindow().getX() + (int) theScene.getX();
                final int windowY = (int) theScene.getWindow().getY() + (int) theScene.getY();

                mouseX = (int) MouseInfo.getPointerInfo().getLocation().getX() - windowX;
                mouseY = (int) MouseInfo.getPointerInfo().getLocation().getY() - windowY;

                draw(mouseX, mouseY);
            }
        }.start();

        theScene.setOnMousePressed(event -> handleClick());

        validMap = calculateValidMap();

        actions.add(new Action(Action.INIT, getGameState(), turn));
        activeGameState = actions.size() - 1;

        primaryStage.show();
    }

    private void handleClick() {
        int x = (mouseX - padding / 2) / scale;
        int y = (mouseY - padding / 2) / scale;

        // x and y refer to piece indices rather than coordinates on the plane in case that wasn't obvious.

        if (x < 8 && x >= 0 && y < 8 && y >= 0)
            if (validMove(y, x) && !pieces[y][x].placed) {
                placePiece(x, y);
            }

        for (Button b : buttons)
            b.handleClick(mouseX, mouseY);
    }

    @Contract(pure = true) // IDEA did that, not me
    private boolean validMove(int x, int y) {
        if (pieces[x][y].placed)
            return false;

        return validMap[x][y];
    }

    private void draw(int mouseX, int mouseY) {
        c.setFill(Color.rgb(50, 50, 50));
        c.fillRect(0, 0, 720, 360);

        white.calcScore(pieces);
        black.calcScore(pieces);

        c.setStroke(Color.rgb(130, 190, 160));
        c.strokeRect(padding / 2f - 2, padding / 2f - 2, height - padding, height - padding);
        c.setStroke(Color.rgb(100, 100, 100));

        boolean gameOver = gameOver();

        if (!gameOver) {
            for (int i = 0; i < 8; i++)
                for (int j = 0; j < 8; j++) {
                    c.strokeRect(i * scale + (padding / 2f), j * scale + 10, scale, scale);

                    final int x = mouseX - (padding / 2);
                    final int y = mouseY - (padding / 2);

                    if ((x > i * scale && x < (i + 1) * scale) && (y > j * scale && y < (j + 1) * scale)) {
                        // just a small rectangle collision detector
                        gridX = i;
                        gridY = j;
                        if (validMove(j, i))
                            c.setFill(Color.rgb(0, 205, 140));
                        else
                            c.setFill(Color.rgb(205, 0, 80));

                        c.fillRect(5 + padding / 2f + i * scale,
                                5 + padding / 2f + j * scale,
                                scale - 10,
                                scale - 10);
                    }
                }

            for (Piece[] pcs : pieces)
                for (Piece piece : pcs)
                    piece.show(c, scale);
        }

        c.setFill(Color.rgb(70, 70, 70));
        c.fillRect(width / 2f, padding / 2f, width / 2f - padding / 2f, (height - 2) - padding);
        c.setStroke(Color.rgb(130, 190, 160));
        c.strokeRect(height - 2, padding / 2f - 2, height - padding / 2f, height - padding);

        c.setFill(Color.rgb(200, 205, 205));
        c.fillText("White: " + white.score, padding + width / 2f, 2 * padding);
        c.fillText("Black: " + black.score, padding + width / 2f, 3 * padding);

        c.setFill(Color.rgb(180, 185, 185));
        c.fillText("Cell: (" + (gridX + 1) + ", " + (gridY + 1) + ")", padding + width / 2f, 5 * padding);

        c.fillText("Moves: " + activeGameState, padding + width / 2f, 6 * padding);

        if (turn == -1) // turn -1 is black
            c.setFill(Color.rgb(20, 20, 30));
        else
            c.setFill(Color.rgb(235, 235, 245));

        c.fillRect(height + padding / 2f,
                height - (3 * padding + padding / 2f),
                height - 2 * padding,
                2 * padding);

        for (Button btn : buttons) {
            btn.show(c, btn.isOver(mouseX, mouseY));
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    private boolean gameOver() {
        boolean movesAvail = false;

        for (boolean[] pcs : validMap) {
            for (boolean p : pcs) {
                if (p)
                    movesAvail = true;
            }
        }

        return !movesAvail;
    }

    private void placePiece(int x, int y) {
        Piece p = pieces[y][x];
        p.placed = true;
        p.setPlayer(turn);

        turn *= -1;

        Piece[][] surroundedPieces = getIntersectingForIndex(y, x, turn * -1);

        for (Piece[] pcs : surroundedPieces) {
            if (pcs != null)
                for (int i = 0; i < pcs.length - 1; i++) {
                    pcs[i].flip();
                }
        }

        validMap = calculateValidMap();

        actions.add(new Action(Action.MOVE, getGameState(), turn));
        activeGameState++;

        if (turn == white.player)
            white.move(getGameState());
        else
            black.move(getGameState());
    }

    private boolean[][] calculateValidMap() {
        boolean[][] validMap = new boolean[8][8];

        for (Piece[] pcs : pieces) {
            for (Piece p : pcs) {

                int dirsValid = 0;

                Piece[][] valids = getIntersectingForIndex(p.x, p.y, turn);

                for (Piece[] _pcs : valids) {
                    if (_pcs != null)
                        if (_pcs.length > 0)
                            dirsValid++;
                }

                validMap[p.x][p.y] = dirsValid >= 1;
            }
        }

        return validMap;
    }

    private Piece[][] getIntersectingForIndex(int x, int y, int turn) {
        Piece[][] valids = new Piece[8][];

        int dirIndex = 0;
        for (int[] dir : dirs) {
            int _x = x;
            int _y = y;

            ArrayList<Piece> potentiallyValid = new ArrayList<>();

            while (_x <= 7 && _y <= 7 && _x >= 0 && _y >= 0) {
                _y += dir[1];
                _x += dir[0];

                if (_x <= 7 && _y <= 7 && _x >= 0 && _y >= 0) {
                    Piece piece = pieces[_x][_y];

                    if (!piece.placed)
                        break;

                    potentiallyValid.add(piece);
                    if (piece.player == turn)
                        break;
                } else
                    break;
            }

            if (potentiallyValid.size() >= 1) {
                int opponentCount = 0;
                boolean hasEncounteredOwn = false;
                for (Piece piece : potentiallyValid) {
                    if (piece.player != turn)
                        opponentCount++;
                    else {
                        hasEncounteredOwn = true;
                        break;
                    }
                }

                if (hasEncounteredOwn && opponentCount > 0) {
                    Piece[] dirPieces = new Piece[potentiallyValid.size()];

                    for (int i = 0; i < potentiallyValid.size(); i++) dirPieces[i] = potentiallyValid.get(i);

                    valids[dirIndex] = dirPieces;
                } else {
                    valids[dirIndex] = new Piece[0];
                }
            }
            dirIndex++;
        }

        for (int i = 0; i < valids.length; i++)
            if (valids[i] == null)
                valids[i] = new Piece[0];

        return valids;
    }

    private int[][] getGameState() {
        int[][] gameState = new int[8][8];

        for (int i = 0; i < pieces.length; i++) {
            for (int j = 0; j < pieces[i].length; j++) {
                Piece p = pieces[i][j];

                if (p != null) {
                    if (!p.placed)
                        gameState[i][j] = 0;
                    else if (p.player == -1)
                        gameState[i][j] = 1;
                    else
                        gameState[i][j] = 2;
                } else {
                    gameState[i][j] = -1;
                }
            }
        }

        return gameState;
    }

    void applyGameState(Action action) {
        for (int i = 0; i < action.gameState.length; i++) {
            for (int j = 0; j < action.gameState[i].length; j++) {
                switch (action.gameState[j][i]) {
                    case 0:
                        pieces[j][i].placed = false; // for some weird reason that I don't have enough time to investigate,
                        // the board is flipped diagonally.
                        break;
                    case 1:
                        pieces[j][i].player = -1;
                        break;
                    case 2:
                        pieces[j][i].player = 1;
                        break;
                }
            }
        }

        turn = action.turn;
        validMap = calculateValidMap();
    }
}
