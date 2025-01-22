sealed class CellReference {
    data class A1(val column: Char, val row: Int) : CellReference() {
        init {
            require(column in 'A'..'Z') { "Column must be between 'A' and 'Z'" }
            require(row > 0) { "Row must be greater than 0" }
        }

        override fun toString(): String {
            return "$column$row"
        }
    }

    data class R1C1(val row: Int, val column: Int) : CellReference() {
        init {
            require(row > 0) { "Row must be greater than 0" }
            require(column > 0) { "Column must be greater than 0" }
        }

        override fun toString(): String {
            return "R$row$column"
        }
    }
}

class Cell(val reference: CellReference) {
    var formula: String = ""

    fun sum(vararg references: CellReference) {
        formula = "SUM(${references.joinToString(",") { it.toString() }})"
    }

    fun average(vararg references: CellReference) {
        formula = "AVERAGE(${references.joinToString(",") { it.toString() }})"
    }

    fun ifCondition(condition: String, trueValue: String, falseValue: String) {
        formula = "IF($condition, $trueValue, $falseValue)"
    }
}

class Sheet(val name: String) {
    val cells = mutableListOf<Cell>()

    fun cell(reference: CellReference, init: Cell.() -> Unit) {
        val cell = Cell(reference)
        cell.init()
        cells.add(cell)
    }
}

class Spreadsheet {
    val sheets = mutableListOf<Sheet>()

    fun sheet(name: String, init: Sheet.() -> Unit) {
        val sheet = Sheet(name)
        sheet.init()
        sheets.add(sheet)
    }
}

fun spreadsheet(init: Spreadsheet.() -> Unit): Spreadsheet {
    val spreadsheet = Spreadsheet()
    spreadsheet.init()
    return spreadsheet
}

fun sheet(name: String, init: Sheet.() -> Unit): Sheet {
    val sheet = Sheet(name)
    sheet.init()
    return sheet
}

fun cell(reference: CellReference, init: Cell.() -> Unit): Cell {
    val cell = Cell(reference)
    cell.init()
    return cell
}

fun main() {
    val mySpreadsheet = spreadsheet {
        sheet("Sheet1") {
            cell(CellReference.A1('A', 1)) {
                sum(CellReference.A1('B', 1), CellReference.R1C1(2, 2), CellReference.A1('B', 3))
            }
            cell(CellReference.R1C1(2, 1)) {
                average(CellReference.A1('B', 1), CellReference.R1C1(2, 2), CellReference.A1('B', 3))
            }
            cell(CellReference.A1('A', 3)) {
                ifCondition("R1C2 > 10", "R1C2", "0")
            }
        }
    }

    // Print the spreadsheet structure for demonstration purposes
    mySpreadsheet.sheets.forEach { sheet ->
        println("Sheet: ${sheet.name}")
        sheet.cells.forEach { cell ->
            println("Cell ${cell.reference}: ${cell.formula}")
        }
    }
}