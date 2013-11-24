package cardgame;

import java.util.EventObject;

public class PlayerWonEvent extends EventObject 
{
    public PlayerWonEvent(Object source) {
        super(source);
    }
}

