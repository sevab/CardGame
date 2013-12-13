package cardgame;

public class StackUnderflowException extends RuntimeException {

    public StackUnderflowException() {
    }

    public StackUnderflowException(String msg) {
        super(msg);
    }
}
