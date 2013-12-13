package cardgame;

import java.util.EventListener;

public interface CardGameListener extends EventListener {
    public void playerWonEventHandler(PlayerWonEvent event);
    public void confirmPlayerState( PlayerStateEvent event, String state );
}
