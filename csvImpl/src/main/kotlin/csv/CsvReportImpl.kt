package csv

import spec.ReportInterface
import java.io.File

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
        val columns = data.keys.toList()
        val numRows = data.values.first().size

        // Write to CSV file
        File(destination).printWriter().use { writer ->
            if(header)
                writer.println(columns.joinToString(","))  // Write the header
            for (i in 0 until numRows) {
                val row = columns.map { column -> data[column]?.get(i) ?: "" }
                writer.println(row.joinToString(","))   // Write each row
            }
        }
    }
}
