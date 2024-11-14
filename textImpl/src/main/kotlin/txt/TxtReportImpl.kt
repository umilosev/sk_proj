package txt

import formatting.FormattingConfig
import spec.ReportInterface
import java.io.File
import java.io.PrintWriter

class TxtReportImpl : ReportInterface {

    override val implName: String = "TXT"
    override val supportsFormatting: Boolean = false
    override fun applyFormatting(config: FormattingConfig) {
        //Tekst ne koristi formatiranje tako da se ovo ne implementira
    }


    override fun generateReport(
        data: Map<String, List<String>>,
        destination: String,
        header: Boolean,
        title: String?,
        summary: String?
    ) {
        val keysWithoutCalcs = data.keys.filter { x ->
            !x.contains("_Average") && !x.contains("_Sum")
                    && !x.contains("Count")
        }

        val columnWidths = keysWithoutCalcs.map { column ->
            val maxDataWidth = data[column]?.maxOfOrNull { it.length } ?: 0
            maxOf(column.length, maxDataWidth)
        }

        val columns = keysWithoutCalcs.toList()
        val numRows = data.values.first().size

        // Write to TXT file
        val pw = File(destination).printWriter()

        pw.use { writer ->
            // Write title if provided
            title?.let {
                writer.println(it)
                writer.println()
            }

            // Write the header row
            keysWithoutCalcs.forEachIndexed { index, column ->
                writer.print(column.padEnd(columnWidths[index] + 2))  // +2 for spacing
            }
            writer.println()

            // Write dashes under the header
            columnWidths.forEach { width ->
                writer.print("-".repeat(width + 2))  // +2 for spacing
            }
            writer.println()

            // Write each row of data, properly spaced
            for (i in 0 until numRows) {
                keysWithoutCalcs.forEachIndexed { index, column ->
                    val cell = data[column]?.get(i) ?: ""
                    writer.print(cell.padEnd(columnWidths[index] + 2))  // +2 for spacing
                }
                writer.println()
            }

            // Write summary if provided
            summary?.let {
                writer.println()
                writer.println(it)
            }

            populateCalculationRow(writer, data,  "Average")
            populateCalculationRow(writer, data,  "Sum")
            populateCalculationRow(writer, data, "Count")
        }

        // calculation exporting
        pw.use { writer ->

        }
    }

    private fun populateCalculationRow(
        writer: PrintWriter,
        data: Map<String, List<String>>,
        columnSuffix: String,
    ) {
        val summaryColumnsNames = data.keys.filter { x -> x.contains("_$columnSuffix") }
            .map { x -> x.removeSuffix("_$columnSuffix")}
        if (summaryColumnsNames.isEmpty()) return

        val summaryColumnValues = summaryColumnsNames.map { name ->
            data[name]?.get(0) ?: " N/A "
        }

        writer.println()
        writer.println()

        val c1 = "Column Name"
        val c2 = columnSuffix

        val columnWidths : Array<Int> = Array<Int>(2) { 0 }
        columnWidths[0] = c1.length
        columnWidths[1] = c2.length

        summaryColumnsNames.forEach {name ->
            columnWidths[0] = maxOf(name.length, columnWidths[0])
        }

        summaryColumnValues.forEach {value ->
            columnWidths[0] = maxOf(value.length, columnWidths[0])
        }

        writer.print(c1.padEnd(columnWidths[0] + 2))  // +2 for spacing
        writer.print(c2.padEnd(columnWidths[1] + 2))  // +2 for spacing
        writer.println()
        writer.print("-".repeat(columnWidths[0] + columnWidths[1] + 4))  // +2 for spacing
        writer.println()

        summaryColumnsNames.forEachIndexed { ind, col ->
            writer.print(col.padEnd(columnWidths[0] + 2))  // +2 for spacing
            writer.print(summaryColumnValues[ind].padEnd(columnWidths[1] + 2))  // +2 for spacing
            writer.println()
        }
    }
}