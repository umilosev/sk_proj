package pdf

import com.lowagie.text.*
import com.lowagie.text.pdf.PdfPCell
import com.lowagie.text.pdf.PdfPTable
import com.lowagie.text.pdf.PdfWriter
import spec.ReportInterface
import java.io.FileOutputStream

class PdfReportImpl : ReportInterface {
    override val implName: String = "PDF"
    override val supportsFormatting: Boolean = true

    override fun generateReport(
        data: Map<String, List<String>>,
        destination: String,
        header: Boolean,
        title: String?,
        summary: String?
    ) {
        // Create a new document
        val document = Document()

        try {
            // Initialize PdfWriter
            PdfWriter.getInstance(document, FileOutputStream(destination))

            // Open the document for writing
            document.open()

            // Add title if provided
            title?.let {
                val titleParagraph = Paragraph(it, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18f))
                titleParagraph.alignment = Element.ALIGN_CENTER
                document.add(titleParagraph)
                document.add(Chunk.NEWLINE)  // Add a new line after the title
            }

            // Create a table based on the number of columns in the data
            val columns = data.keys.toList()
            val numColumns = columns.size
            val table = PdfPTable(numColumns)

            // Add header row if necessary
            if (header) {
                columns.forEach { column ->
                    val cell = PdfPCell(Paragraph(column, FontFactory.getFont(FontFactory.HELVETICA_BOLD)))
                    cell.horizontalAlignment = Element.ALIGN_CENTER
                    table.addCell(cell)
                }
            }

            // Add data rows
            val numRows = data.values.first().size
            for (i in 0 until numRows) {
                columns.forEach { column ->
                    val cellData = data[column]?.get(i) ?: ""
                    table.addCell(cellData)
                }
            }

            // Add the table to the document
            document.add(table)

            // Add summary if provided
            summary?.let {
                document.add(Chunk.NEWLINE)
                val summaryParagraph = Paragraph("Summary: $summary", FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE))
                document.add(summaryParagraph)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            // Close the document
            document.close()
        }
    }
}