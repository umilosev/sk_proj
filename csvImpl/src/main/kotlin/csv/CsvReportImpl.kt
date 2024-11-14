package csv

import spec.ReportInterface
import java.io.File
import java.io.PrintWriter

class CsvReportImpl : ReportInterface {

    override val implName: String = "CSV"
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

        val columns = keysWithoutCalcs.toList()
        val numRows = data.values.first().size

        val pw = File(destination).printWriter()

        pw.use { writer ->
            if (header)
                writer.println(columns.joinToString(","))  // Write the header
            for (i in 0 until numRows) {
                val row = columns.map { column -> data[column]?.get(i) ?: "" }
                writer.println(row.joinToString(","))   // Write each row
            }
        }

        // calculation exporting
        pw.use {
            writer ->
            populateSummaryRow(writer, data,  "Average")
            populateSummaryRow(writer, data,  "Sum")
            populateSummaryRow(writer, data, "Count")
        }
    }

    private fun populateSummaryRow(
        writer: PrintWriter,
        data: Map<String, List<String>>,
        columnSuffix: String
    ) {
        val summaryCols = data.keys.filter { x -> x.contains("_$columnSuffix") }
        if (summaryCols.isEmpty()) return

        println()
        println()

        writer.println("Column Name, $columnSuffix")

        summaryCols.forEach { colName ->
            writer.println("$colName, " + (data[colName]?.get(0) ?: " N/A"))
        }
    }
}
