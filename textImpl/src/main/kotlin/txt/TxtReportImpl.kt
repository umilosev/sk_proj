package txt

import spec.ReportInterface
import java.io.File

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
        val columns = data.keys.toList()
        val numRows = data.values.first().size

        // Calculate the max width for each column
        val columnWidths = columns.map { column ->
            val maxDataWidth = data[column]?.maxOfOrNull { it.length } ?: 0
            maxOf(column.length, maxDataWidth)
        }

        // Write to TXT file
        File(destination).printWriter().use { writer ->
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
    }
}