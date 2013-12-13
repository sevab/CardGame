package cardgame;

import java.util.EventListener;

public interface PlayerListener extends EventListener {
    public void gameOverEventHandler(GameOverEvent event);
    public void pausePlayerEventHandler(GameStateEvent event);
    public void resumePlayerEventHandler(GameStateEvent event);
}
