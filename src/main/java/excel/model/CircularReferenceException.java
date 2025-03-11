package excel.model;

/**
 * Exception déclenchée lors de la détection d'une référence circulaire
 */
public class CircularReferenceException extends RuntimeException {
    public CircularReferenceException(String message) {
        super(message);
    }
}