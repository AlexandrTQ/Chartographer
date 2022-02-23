package chartographer.exceptions;

public class ChartDoesNotExistException extends RuntimeException {
    public ChartDoesNotExistException(String message) {
        super(message);
    }
}
