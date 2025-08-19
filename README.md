📝 Project Description
This project implements a mini spreadsheet offering features similar to Excel, Google Sheets, or OpenCalc. The application allows manipulation of cells with literal values (numbers, booleans, text) or calculated expressions.
✨ Key Features

Cell editing with double-click or editing bar
Complex expressions with arithmetic, logical, and comparison operators
Cell references (e.g., =A1+B2)
Error handling: SYNTAX ERROR, #VALUE, #CIRCULAR REF
Automatic number formatting (e.g., 5.0 → 5, 5.35 → 5.35)
SUM function (e.g., =SUM(A1:A5))
Undo/Redo operations (Ctrl+Z / Ctrl+Y)
Save/Load spreadsheet files

🧠 Architecture and Design Patterns
Implemented Design Patterns

Interpreter:

Expression representation as syntax trees
Recursive expression evaluation


Builder:

Construction of Expression objects from strings
Syntax parsing and expression validation


Command:

Undo/Redo command management
Modification history tracking


MVVM (Model-View-ViewModel):

Clear separation of concerns
Reactive user interface



🚀 How to Run the Project
Prerequisites

Java 17 or higher
Maven 3.6+

Installation
bashgit clone https://github.com/your-username/mini-spreadsheet.git
cd mini-spreadsheet
mvn clean javafx:run
Keyboard Shortcuts

Ctrl+Z: Undo last action
Ctrl+Y: Redo undone action
Ctrl+S: Save spreadsheet
Ctrl+O: Open spreadsheet

🧪 Technical Features
Supported Data Types
TypeExamplesNumber42, 3.14, -5.5Booleantrue, FALSE, TrueText"Hello", 'World'Expression=A1+B2, =5>3
Supported Operators
CategoryOperatorsPriorityMultiplicative*, /1 (highest)Additive+, -2Comparison>, >=, <, <=, =, !=3Logicalnot4and5or6 (lowest)
Expression Examples

Arithmetic: =5+3*2 → 11
Logic: =5>3 and not 2>4 or true → true
Reference: =B2+3*5+C4
Function: =SUM(A1:A5)

📁 File Structure
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
🛠 Development
Implemented Features

 Expression syntax parsing
 Expression evaluation
 Cell reference management
 Circular reference detection
 Automatic number formatting
 SUM function
 Undo/Redo system
 File save/load functionality

Possible Improvements

 Additional functions (AVERAGE, MAX, MIN, etc.)
 Array formula support
 Syntax highlighting in editing bar
 Multiple sheet management
 CSV and Excel import/export
 Charts and visualizations
 Conditional cell formatting



📜 License
This project is developed as part of the Development Project course at EPFC.
