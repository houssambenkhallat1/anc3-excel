package excel.model;

/**
 * Représente une valeur typée d'une cellule (texte, nombre, booléen, erreur)
 */
public class CellValue {
    public enum Type {
        TEXT, NUMBER, BOOLEAN, ERROR
    }

    private final Type type;
    private final String textValue;
    private final double numberValue;
    private final boolean booleanValue;
    private final CellError errorValue;

    private CellValue(Type type, String textValue, double numberValue, boolean booleanValue, CellError errorValue) {
        this.type = type;
        this.textValue = textValue;
        this.numberValue = numberValue;
        this.booleanValue = booleanValue;
        this.errorValue = errorValue;
    }

    public static CellValue ofText(String text) {
        return new CellValue(Type.TEXT, text, 0, false, null);
    }

    public static CellValue ofNumber(double number) {
        return new CellValue(Type.NUMBER, String.valueOf(number), number, false, null);
    }

    public static CellValue ofBoolean(boolean value) {
        return new CellValue(Type.BOOLEAN, String.valueOf(value), value ? 1 : 0, value, null);
    }

    public static CellValue ofError(CellError error) {
        return new CellValue(Type.ERROR, error.toString(), 0, false, error);
    }

    public Type getType() {
        return type;
    }

    public String getTextValue() {
        return textValue;
    }

    public double getNumberValue() {
        return numberValue;
    }

    public boolean getBooleanValue() {
        return booleanValue;
    }

    public CellError getErrorValue() {
        return errorValue;
    }

    public boolean isText() {
        return type == Type.TEXT;
    }

    public boolean isNumber() {
        return type == Type.NUMBER;
    }

    public boolean isBoolean() {
        return type == Type.BOOLEAN;
    }

    public boolean isError() {
        return type == Type.ERROR;
    }

    /**
     * Formate la valeur pour l'affichage
     */
    public String format() {
        switch (type) {
            case TEXT:
                return textValue;
            case NUMBER:
                // Format simple pour les nombres sans décimales
                if (numberValue == Math.floor(numberValue)) {
                    return String.valueOf((int) numberValue);
                }
                return String.valueOf(numberValue);
            case BOOLEAN:
                return booleanValue ? "TRUE" : "FALSE";
            case ERROR:
                return "#" + errorValue.toString();
            default:
                return "";
        }
    }

    @Override
    public String toString() {
        return "CellValue{" +
                "type=" + type +
                ", value=" + format() +
                '}';
    }
}
