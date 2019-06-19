package Othello;

class UndoBtn extends Button {
    UndoBtn(int x, int y, int w, int h) {
        super(x, y, w, h, "<");
    }

    @Override
    void onClick() {
        if (main.activeGameState > 0) {
            main.actions.remove(main.actions.size() - 1);
            main.applyGameState(main.actions.get(main.actions.size() - 1));
        }
    }
}
