package excel.model;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Construit des expressions à partir de chaînes de caractères
 */
public class ExpressionBuilder {
    private static final Pattern CELL_REFERENCE_PATTERN = Pattern.compile("([A-Z]+)([0-9]+)");

    private final SpreadsheetModel spreadsheet;

    // Constructeur avec référence au modèle
    public ExpressionBuilder(SpreadsheetModel spreadsheet) {
        this.spreadsheet = spreadsheet;
    }

    // Tokens pour l'analyse lexicale
    private enum TokenType {
        NUMBER, BOOLEAN, TEXT, CELL_REFERENCE, OPERATOR, LOGICAL_OPERATOR, COMPARISON_OPERATOR, WHITESPACE
    }

    private static class Token {
        TokenType type;
        String value;

        Token(TokenType type, String value) {
            this.type = type;
            this.value = value;
        }
    }

    /**
     * Construit une expression à partir d'une chaîne et de la cellule source
     */
    public Expression build(String content, Cell sourceCell) {
        if (content == null || content.isEmpty()) {
            return new LiteralExpression(CellValue.ofText(""));
        }

        try {
            // Analyse lexicale
            List<Token> tokens = tokenize(content);

            // Analyse syntaxique et construction de l'expression
            return parseExpression(tokens, sourceCell);
        } catch (Exception e) {
            return new LiteralExpression(CellValue.ofError(CellError.SYNTAX_ERROR));
        }
    }

    /**
     * Version surchargée pour compatibilité
     */
    public Expression build(String content) {
        return build(content, null);
    }

    private List<Token> tokenize(String expression) {
        List<Token> tokens = new ArrayList<>();
        StringBuilder currentToken = new StringBuilder();
        TokenType currentType = null;

        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);

            // Espace
            if (Character.isWhitespace(c)) {
                if (currentType != null) {
                    tokens.add(new Token(currentType, currentToken.toString()));
                    currentToken.setLength(0);
                    currentType = null;
                }
                continue;
            }

            // Opérateur
            if (c == '+' || c == '-' || c == '*' || c == '/') {
                if (currentType != null) {
                    tokens.add(new Token(currentType, currentToken.toString()));
                    currentToken.setLength(0);
                }
                tokens.add(new Token(TokenType.OPERATOR, String.valueOf(c)));
                currentType = null;
                continue;
            }

            // Opérateur de comparaison
            if (c == '>' || c == '<' || c == '=' || c == '!') {
                if (currentType != null) {
                    tokens.add(new Token(currentType, currentToken.toString()));
                    currentToken.setLength(0);
                }

                if (i + 1 < expression.length() && expression.charAt(i + 1) == '=') {
                    tokens.add(new Token(TokenType.COMPARISON_OPERATOR, c + "="));
                    i++;
                } else {
                    tokens.add(new Token(TokenType.COMPARISON_OPERATOR, String.valueOf(c)));
                }

                currentType = null;
                continue;
            }

            // Lettre (peut être une référence de cellule ou un opérateur logique)
            if (Character.isLetter(c)) {
                if (currentType == null) {
                    currentType = TokenType.TEXT;
                    currentToken.append(c);
                } else if (currentType == TokenType.TEXT) {
                    currentToken.append(c);
                } else {
                    tokens.add(new Token(currentType, currentToken.toString()));
                    currentToken.setLength(0);
                    currentType = TokenType.TEXT;
                    currentToken.append(c);
                }
                continue;
            }

            // Chiffre
            if (Character.isDigit(c) || c == ',' || c == '.') {
                if (currentType == null) {
                    currentType = TokenType.NUMBER;
                    currentToken.append(c);
                } else if (currentType == TokenType.NUMBER || currentType == TokenType.TEXT) {
                    currentToken.append(c);
                } else {
                    tokens.add(new Token(currentType, currentToken.toString()));
                    currentToken.setLength(0);
                    currentType = TokenType.NUMBER;
                    currentToken.append(c);
                }
                continue;
            }

            // Caractère non reconnu
            throw new IllegalArgumentException("Caractère non reconnu: " + c);
        }

        // Ajouter le dernier token
        if (currentType != null) {
            tokens.add(new Token(currentType, currentToken.toString()));
        }

        // Post-traitement des tokens
        List<Token> processedTokens = new ArrayList<>();

        for (Token token : tokens) {
            // Identifier les références de cellule et les opérateurs logiques
            if (token.type == TokenType.TEXT) {
                Matcher cellMatcher = CELL_REFERENCE_PATTERN.matcher(token.value);
                if (cellMatcher.matches()) {
                    processedTokens.add(new Token(TokenType.CELL_REFERENCE, token.value));
                } else if (token.value.equalsIgnoreCase("and") ||
                        token.value.equalsIgnoreCase("or") ||
                        token.value.equalsIgnoreCase("not")) {
                    processedTokens.add(new Token(TokenType.LOGICAL_OPERATOR, token.value.toLowerCase()));
                } else if (token.value.equalsIgnoreCase("true") ||
                        token.value.equalsIgnoreCase("false")) {
                    processedTokens.add(new Token(TokenType.BOOLEAN, token.value.toLowerCase()));
                } else {
                    processedTokens.add(new Token(TokenType.TEXT, token.value));
                }
            } else {
                processedTokens.add(token);
            }
        }

        return processedTokens;
    }

    private Expression parseExpression(List<Token> tokens, Cell sourceCell) {
        if (tokens.isEmpty()) {
            throw new IllegalArgumentException("Expression vide");
        }

        return parseLogicalOr(tokens, 0, sourceCell).expression;
    }

    private static class ParseResult {
        Expression expression;
        int nextTokenIndex;

        ParseResult(Expression expression, int nextTokenIndex) {
            this.expression = expression;
            this.nextTokenIndex = nextTokenIndex;
        }
    }

    // Niveau 6: OR
    private ParseResult parseLogicalOr(List<Token> tokens, int startIndex, Cell sourceCell) {
        ParseResult left = parseLogicalAnd(tokens, startIndex, sourceCell);

        int i = left.nextTokenIndex;
        while (i < tokens.size() &&
                tokens.get(i).type == TokenType.LOGICAL_OPERATOR &&
                tokens.get(i).value.equals("or")) {

            ParseResult right = parseLogicalAnd(tokens, i + 1, sourceCell);
            left = new ParseResult(
                    new LogicalExpression(left.expression, right.expression, LogicalExpression.Operator.OR),
                    right.nextTokenIndex
            );
            i = left.nextTokenIndex;
        }

        return left;
    }

    // Niveau 5: AND
    private ParseResult parseLogicalAnd(List<Token> tokens, int startIndex, Cell sourceCell) {
        ParseResult left = parseLogicalNot(tokens, startIndex, sourceCell);

        int i = left.nextTokenIndex;
        while (i < tokens.size() &&
                tokens.get(i).type == TokenType.LOGICAL_OPERATOR &&
                tokens.get(i).value.equals("and")) {

            ParseResult right = parseLogicalNot(tokens, i + 1, sourceCell);
            left = new ParseResult(
                    new LogicalExpression(left.expression, right.expression, LogicalExpression.Operator.AND),
                    right.nextTokenIndex
            );
            i = left.nextTokenIndex;
        }

        return left;
    }

    // Niveau 4: NOT
    private ParseResult parseLogicalNot(List<Token> tokens, int startIndex, Cell sourceCell) {
        if (startIndex < tokens.size() &&
                tokens.get(startIndex).type == TokenType.LOGICAL_OPERATOR &&
                tokens.get(startIndex).value.equals("not")) {

            ParseResult operand = parseComparison(tokens, startIndex + 1, sourceCell);
            return new ParseResult(
                    new LogicalExpression(operand.expression, LogicalExpression.Operator.NOT),
                    operand.nextTokenIndex
            );
        }

        return parseComparison(tokens, startIndex, sourceCell);
    }

    // Niveau 3: Comparaisons
    private ParseResult parseComparison(List<Token> tokens, int startIndex, Cell sourceCell) {
        ParseResult left = parseAdditive(tokens, startIndex, sourceCell);

        int i = left.nextTokenIndex;
        if (i < tokens.size() && tokens.get(i).type == TokenType.COMPARISON_OPERATOR) {
            ComparisonExpression.Operator operator;
            switch (tokens.get(i).value) {
                case ">": operator = ComparisonExpression.Operator.GREATER_THAN; break;
                case ">=": operator = ComparisonExpression.Operator.GREATER_THAN_OR_EQUAL; break;
                case "<": operator = ComparisonExpression.Operator.LESS_THAN; break;
                case "<=": operator = ComparisonExpression.Operator.LESS_THAN_OR_EQUAL; break;
                case "=": operator = ComparisonExpression.Operator.EQUAL; break;
                case "!=": operator = ComparisonExpression.Operator.NOT_EQUAL; break;
                default: throw new IllegalArgumentException("Opérateur de comparaison non reconnu: " + tokens.get(i).value);
            }

            ParseResult right = parseAdditive(tokens, i + 1, sourceCell);
            return new ParseResult(
                    new ComparisonExpression(left.expression, right.expression, operator),
                    right.nextTokenIndex
            );
        }

        return left;
    }

    // Niveau 2: Addition et soustraction
    private ParseResult parseAdditive(List<Token> tokens, int startIndex, Cell sourceCell) {
        ParseResult left = parseMultiplicative(tokens, startIndex, sourceCell);

        int i = left.nextTokenIndex;
        while (i < tokens.size() &&
                tokens.get(i).type == TokenType.OPERATOR &&
                (tokens.get(i).value.equals("+") || tokens.get(i).value.equals("-"))) {

            BinaryArithmeticExpression.Operator operator = tokens.get(i).value.equals("+") ?
                    BinaryArithmeticExpression.Operator.ADD :
                    BinaryArithmeticExpression.Operator.SUBTRACT;

            ParseResult right = parseMultiplicative(tokens, i + 1, sourceCell);
            left = new ParseResult(
                    new BinaryArithmeticExpression(left.expression, right.expression, operator),
                    right.nextTokenIndex
            );
            i = left.nextTokenIndex;
        }

        return left;
    }

    // Niveau 1: Multiplication et division
    private ParseResult parseMultiplicative(List<Token> tokens, int startIndex, Cell sourceCell) {
        ParseResult left = parsePrimary(tokens, startIndex, sourceCell);

        int i = left.nextTokenIndex;
        while (i < tokens.size() &&
                tokens.get(i).type == TokenType.OPERATOR &&
                (tokens.get(i).value.equals("*") || tokens.get(i).value.equals("/"))) {

            BinaryArithmeticExpression.Operator operator = tokens.get(i).value.equals("*") ?
                    BinaryArithmeticExpression.Operator.MULTIPLY :
                    BinaryArithmeticExpression.Operator.DIVIDE;

            ParseResult right = parsePrimary(tokens, i + 1, sourceCell);
            left = new ParseResult(
                    new BinaryArithmeticExpression(left.expression, right.expression, operator),
                    right.nextTokenIndex
            );
            i = left.nextTokenIndex;
        }

        return left;
    }

    // Niveau 0: Littéraux et références
    private ParseResult parsePrimary(List<Token> tokens, int startIndex, Cell sourceCell) {
        if (startIndex >= tokens.size()) {
            throw new IllegalArgumentException("Expression incomplète");
        }

        Token token = tokens.get(startIndex);

        switch (token.type) {
            case NUMBER:
                double number = Double.parseDouble(token.value.replace(',', '.'));
                return new ParseResult(
                        new LiteralExpression(CellValue.ofNumber(number)),
                        startIndex + 1
                );

            case BOOLEAN:
                boolean bool = Boolean.parseBoolean(token.value);
                return new ParseResult(
                        new LiteralExpression(CellValue.ofBoolean(bool)),
                        startIndex + 1
                );

            case TEXT:
                return new ParseResult(
                        new LiteralExpression(CellValue.ofText(token.value)),
                        startIndex + 1
                );

            case CELL_REFERENCE:
                // Créer une expression de référence avec le spreadsheet et la cellule source
                return new ParseResult(
                        new ReferenceExpression(token.value, spreadsheet, sourceCell),
                        startIndex + 1
                );

            default:
                throw new IllegalArgumentException("Token inattendu: " + token.value);
        }
    }
}