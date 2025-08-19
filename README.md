# 📊 Mini Spreadsheet

A modern spreadsheet application implemented in Java with JavaFX, offering features similar to Excel, Google Sheets, or OpenCalc.


## 📝 Project Description

This project implements a mini spreadsheet offering features similar to Excel, Google Sheets, or OpenCalc. The application allows manipulation of cells with literal values (numbers, booleans, text) or calculated expressions.

## ✨ Key Features

* **Cell editing** with double-click or editing bar
* **Complex expressions** with arithmetic, logical, and comparison operators
* **Cell references** (e.g., `=A1+B2`)
* **Error handling**: SYNTAX ERROR, #VALUE, #CIRCULAR REF
* **Automatic number formatting** (e.g., 5.0 → 5, 5.35 → 5.35)
* **SUM function** (e.g., `=SUM(A1:A5)`)
* **Undo/Redo** operations (Ctrl+Z / Ctrl+Y)
* **Save/Load** spreadsheet files

## 🧠 Architecture and Design Patterns

### Implemented Design Patterns

1. **Interpreter**:
   * Expression representation as syntax trees
   * Recursive expression evaluation

2. **Builder**:
   * Construction of `Expression` objects from strings
   * Syntax parsing and expression validation

3. **Command**:
   * Undo/Redo command management
   * Modification history tracking

4. **MVVM (Model-View-ViewModel)**:
   * Clear separation of concerns
   * Reactive user interface

## 🚀 How to Run the Project

### Prerequisites

* Java 17 or higher
* Maven 3.6+

### Installation

```bash
git clone https://github.com/houssambenkhallat1/anc3-excel.git
cd anc3-excel
mvn clean javafx:run
```

### Keyboard Shortcuts

* **Ctrl+Z**: Undo last action
* **Ctrl+Y**: Redo undone action
* **Ctrl+S**: Save spreadsheet
* **Ctrl+O**: Open spreadsheet

## 🧪 Technical Features

### Supported Data Types

| Type | Examples |
|------|----------|
| Number | `42`, `3.14`, `-5.5` |
| Boolean | `true`, `FALSE`, `True` |
| Text | `"Hello"`, `'World'` |
| Expression | `=A1+B2`, `=5>3` |

### Supported Operators

| Category | Operators | Priority |
|----------|-----------|----------|
| Multiplicative | `*`, `/` | 1 (highest) |
| Additive | `+`, `-` | 2 |
| Comparison | `>`, `>=`, `<`, `<=`, `=`, `!=` | 3 |
| Logical | `not` | 4 |
| | `and` | 5 |
| | `or` | 6 (lowest) |

### Expression Examples

1. **Arithmetic**: `=5+3*2` → 11
2. **Logic**: `=5>3 and not 2>4 or true` → true
3. **Reference**: `=B2+3*5+C4`
4. **Function**: `=SUM(A1:A5)`

## 📁 File Structure

```
mini-spreadsheet/
├── src/
│   ├── main/
│   │   ├── java/excel/
│   │   │   ├── model/        # Data model
│   │   │   ├── view/         # User interface
│   │   │   ├── viewmodel/    # ViewModel (MVVM)
│   │   │   └── App.java      # Entry point
│   │   └── resources/        # CSS files and resources
│   └── test/                 # Unit tests
├── pom.xml                   # Maven configuration
└── README.md                 # This file
```

## 🛠 Development

### Implemented Features

- [x] Expression syntax parsing
- [x] Expression evaluation
- [x] Cell reference management
- [x] Circular reference detection
- [x] Automatic number formatting
- [x] SUM function
- [x] Undo/Redo system
- [x] File save/load functionality

### Possible Improvements

- [ ] Additional functions (AVERAGE, MAX, MIN, etc.)
- [ ] Array formula support
- [ ] Syntax highlighting in editing bar
- [ ] Multiple sheet management
- [ ] CSV and Excel import/export
- [ ] Charts and visualizations
- [ ] Conditional cell formatting



## 📜 License

This project is developed as part of the Development Project course at EPFC.


