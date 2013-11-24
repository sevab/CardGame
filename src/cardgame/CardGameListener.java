package cardgame;

import java.util.EventListener;

public interface CardGameListener extends EventListener {
    public void playerWonEventHandler(PlayerWonEvent event);
}
