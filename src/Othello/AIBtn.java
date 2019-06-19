package Othello;

class AIBtn extends Button {
    private Player player;
    AIBtn (int x, int y, int w, int h, Player p) {
        super(x, y, w, h, "Auto");
        player = p;
    }

    @Override
    void onClick() {
        player.autoMove = true;
    }
}
