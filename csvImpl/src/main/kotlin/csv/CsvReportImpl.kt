package csv

import formatting.FormattingConfig
import spec.ReportInterface
import java.io.File
import java.io.PrintWriter

class CsvReportImpl : ReportInterface {

    override val implName: String = "CSV"
    override val supportsFormatting: Boolean = false

    override fun applyFormatting(config: FormattingConfig) {
        //nema potrebe za implementacijom jer ga necemo koristiti
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

            populateCalculationRow(writer, data,  "Average")
            populateCalculationRow(writer, data,  "Sum")
            populateCalculationRow(writer, data, "Count")
        }
    }

    override fun generateReportWithFormatting(
        data: Map<String, List<String>>,
        destination: String,
        header: Boolean,
        title: String?,
        summary: String?,
        config: FormattingConfig
    ) {
        TODO("Not yet implemented")
    }

    private fun populateCalculationRow(
        writer: PrintWriter,
        data: Map<String, List<String>>,
        columnSuffix: String
    ) {
        val summaryColumnsNames = data.keys.filter { x -> x.contains("_$columnSuffix") }
            .map { x -> x.removeSuffix("_$columnSuffix")}
        if (summaryColumnsNames.isEmpty()) return

        writer.println()
        writer.println()

        writer.println("Column Name,$columnSuffix")

        summaryColumnsNames.forEach { colName ->
            writer.println("$colName," + (data[colName]?.get(0) ?: "N/A"))
        }
    }
}
