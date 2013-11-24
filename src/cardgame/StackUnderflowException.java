package cardgame;

/**
 *
 * @author xxx
 */
public class StackUnderflowException extends RuntimeException {

    public StackUnderflowException() {
    }

    public StackUnderflowException(String msg) {
        super(msg);
    }
}
