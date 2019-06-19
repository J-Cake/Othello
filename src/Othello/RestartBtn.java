package Othello;

class RestartBtn extends Button {
    RestartBtn(int x, int y, int w, int h) {
        super(x, y, w, h, "");
    }

    @Override
    void onClick() {
        main.applyGameState(main.actions.get(0));
    }
}
