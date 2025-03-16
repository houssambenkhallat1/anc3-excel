package excel.model;

/**
 * Énumération des types d'erreurs possibles dans une cellule
 */
public enum CellError {
    SYNTAX_ERROR("SYNTAX_ERROR"),
    VALUE_ERROR("VALUE"),
    CIRCULAR_REF("CIRCULAR REF");
    private final String message;

    CellError(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}
