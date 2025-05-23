package excel.model;

import excel.tools.ExcelConverter;
import javafx.beans.property.*;


public class Cell {
    private final int row;
    private final int column;
    private final StringProperty content = new SimpleStringProperty("");
    private final ObjectProperty<CellValue> value = new SimpleObjectProperty<>(CellValue.ofText(""));
    private final StringProperty displayValue = new SimpleStringProperty("");
    private Expression expression;
    private boolean evaluating = false; // Pour détecter les références circulaires
    private final SpreadsheetModel spreadsheet;

    private IntegerProperty sumCountAndPowInCellModel = new SimpleIntegerProperty(0);

    public int getSumCountAndPowInCellModel() {
        return sumCountAndPowInCellModel.get();
    }

    public IntegerProperty sumCountAndPowInCellModelProperty() {
        return sumCountAndPowInCellModel;
    }

    public void setSumCountAndPowInCellModel(int sumCountAndPowInCellModel) {
        this.sumCountAndPowInCellModel.set(sumCountAndPowInCellModel);
    }

    public Cell(int row, int column, SpreadsheetModel spreadsheet) {
        this.row = row;
        this.column = column;
        this.spreadsheet = spreadsheet;

        // Mettre à jour la valeur quand le contenu change
        content.addListener((obs, oldVal, newVal) -> {
            String contentText = content.get();
            updateValue();
            this.setContent(contentText);
        });

        // Mettre à jour la valeur d'affichage quand la valeur change
        value.addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                displayValue.set(newVal.format());
            } else {
                displayValue.set("");
            }
        });

        sumCountAndPowInCellModel.addListener((obs, oldVal, newVal) -> {

            spreadsheet.setSumCountAndPowInSpreadheet(newVal.intValue() - oldVal.intValue());

        });
    }

    /**
     * Retourne l'adresse de la cellule au format Excel (A1, B2, etc.)
     */
    public String getAddress() {
        return ExcelConverter.columnIndexToLetter(column) + (row + 1);
    }

    public StringProperty contentProperty() {
        return content;
    }

    public String getContent() {
        return content.get();
    }

    public void setContent(String content) {
        this.content.set(content);
    }

    public ObjectProperty<CellValue> valueProperty() {
        return value;
    }

    /**
     * Propriété pour la valeur formatée à afficher
     */
    public StringProperty displayValueProperty() {
        return displayValue;
    }


    public CellValue getValue() {
        return value.get();
    }

    private void setValue(CellValue value) {
        this.value.set(value);
        notifyDisplayValue();
    }
    private void notifyDisplayValue(){
        if (getValue() != null) {
            displayValue.set(getValue().format());
        }
    }


    private void setExpression(Expression expression) {
        this.expression = expression;
    }

    public boolean isEvaluating() {
        return evaluating;
    }

    public void setEvaluating(boolean evaluating) {
        this.evaluating = evaluating;
    }


    /**
     * Met à jour la valeur calculée de la cellule en fonction de son contenu
     */
    public void updateValue() {
        String contentText = content.get();

        if (contentText == null || contentText.isEmpty()) {
            setValue(CellValue.ofText(""));
            setExpression(null);
            return;
        }

        try {
            // Si c'est une expression (commence par =)
            if (contentText.startsWith("=")) {
                // Utiliser le Builder pour construire et évaluer l'expression
                Expression expr = spreadsheet.getExpressionBuilder().build(contentText.substring(1).trim(), this);
                setExpression(expr);

                // Marquer la cellule comme étant en cours d'évaluation
                setEvaluating(true);

                try {
                    // Évaluer l'expression
                    CellValue result = expr.evaluate(spreadsheet);
                    setValue(result);
                } catch (CircularReferenceException e) {
                    setValue(CellValue.ofError(CellError.CIRCULAR_REF));
                } catch (Exception e) {
                    setValue(CellValue.ofError(CellError.VALUE_ERROR));
                } finally {
                    // Fin de l'évaluation
                    setEvaluating(false);
                }
            } else {
                // Essayer d'interpréter comme une valeur littérale
                setValue(parseContent(contentText));
                setExpression(null);
            }
        } catch (Exception e) {
            setValue(CellValue.ofError(CellError.SYNTAX_ERROR));
            setExpression(null);
        }

        // Notifier les cellules dépendantes (si nécessaire)
        spreadsheet.notifyCellChanged(this);
    }

    /**
     * Analyse le contenu textuel pour en extraire une valeur typée
     */
    private CellValue parseContent(String content) {
        // Essayer d'interpréter comme un nombre
        try {
            double numValue = Double.parseDouble(content.replace(',', '.'));
            return CellValue.ofNumber(numValue);
        } catch (NumberFormatException e) {
            // Pas un nombre
        }

        // Essayer d'interpréter comme un booléen
        if (content.equalsIgnoreCase("true")) {
            return CellValue.ofBoolean(true);
        } else if (content.equalsIgnoreCase("false")) {
            return CellValue.ofBoolean(false);
        }

        // Sinon, c'est du texte
        return CellValue.ofText(content);
    }

    /**
     * Réévalue la valeur de la cellule
     */
    public void recalculate() {
        if (expression != null) {
            updateValue();
        }
    }

    @Override
    public String toString() {
        return "Cell[" + getAddress() + ", content=" + getContent() + ", value=" + getValue() + "]";
    }

    public void setCounterSumAndPowInCell(int sumCountAndPowInCell) {
        setSumCountAndPowInCellModel(sumCountAndPowInCell);
    }

}