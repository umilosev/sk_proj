package txt

import spec.ReportInterface
import java.io.File
import java.io.PrintWriter

class TxtReportImpl : ReportInterface {

    override val implName: String = "TXT"
    override val supportsFormatting: Boolean = false


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

        // Calculate the max width for each column
        val columnWidths = data.keys.map { column ->
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
            columns.forEachIndexed { index, column ->
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
                columns.forEachIndexed { index, column ->
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
        }

//        // calculation exporting
//        pw.use { writer ->
//            populateSummaryRow(writer, data,  "Average")
//            populateSummaryRow(writer, data,  "Sum")
//            populateSummaryRow(writer, data, "Count")
//        }
    }

    private fun populateSummaryRow(
        writer: PrintWriter,
        data: Map<String, List<String>>,
        columnSuffix: String,
        columnWidth: Int
    ) {
        val summaryCols = data.keys.filter { x -> x.contains("_$columnSuffix") }
        if (summaryCols.isEmpty()) return

        println()
        println()

        // Write the header row
        val c1 = "Column Name"
        val c2 = columnSuffix

        val columnWidths = summaryCols.map { column ->
            val maxDataWidth = data[column]?.maxOfOrNull { it.length } ?: 0
            maxOf(column.length, maxDataWidth)
        }
        writer.println()

        columnWidths.forEach { width ->
            writer.print("-".repeat(width + 2))  // +2 for spacing
        }
        writer.println()

        writer.print(c1.padEnd(columnWidths[0] + 2))  // +2 for spacing
        writer.print(c2.padEnd(columnWidths[1] + 2))  // +2 for spacing
        writer.print("-".repeat(columnWidths[0] + columnWidths[1] + 4))  // +2 for spacing

        columnWidths.forEach { width ->
        }
        writer.println()


        for (i in summaryCols.indices) {
            summaryCols.forEachIndexed { index, column ->
                val cell = data[column]?.get(i) ?: ""
                writer.print(cell.padEnd(columnWidths[index] + 2))  // +2 for spacing
            }
            writer.println()
        }
    }
}