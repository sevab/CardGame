package cardgame;

import java.util.EventListener;

public interface PlayerListener extends EventListener {
    public void gameOverEventHandler(GameOverEvent event);
}
