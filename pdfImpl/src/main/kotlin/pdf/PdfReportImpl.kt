package pdf

import com.lowagie.text.*
import com.lowagie.text.FontFactory.*
import com.lowagie.text.pdf.PdfPCell
import com.lowagie.text.pdf.PdfPTable
import com.lowagie.text.pdf.PdfWriter
import formatting.FormattingConfig
import spec.ReportInterface
import java.io.FileOutputStream

class PdfReportImpl : ReportInterface {
    override val implName: String = "PDF"
    override val supportsFormatting: Boolean = true

    override fun applyFormatting(config: FormattingConfig) {
        //TODO ovo cemo implementirati i videcemo kako cemo ga nagurati ovde
    }

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

            val keysWithoutCalcs = data.keys.filter { x ->
                !x.contains("_Average") && !x.contains("_Sum") && !x.contains("Count")
            }

            // Add title if provided2
            title?.let {
                val titleParagraph = Paragraph(it, getFont(HELVETICA_BOLD, 18f))
                titleParagraph.alignment = Element.ALIGN_CENTER
                document.add(titleParagraph)
                document.add(Chunk.NEWLINE)  // Add a new line after the title
            }

            // Create a table based on the number of columns in the data
            val columns = keysWithoutCalcs.toList()
            val numColumns = columns.size
            val table = PdfPTable(numColumns)

            // Add header row if necessary
            if (header) {
                columns.forEach { column ->
                    val cell = PdfPCell(Paragraph(column, getFont(HELVETICA_BOLD)))
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
                val summaryParagraph = Paragraph("Summary: $summary", getFont(HELVETICA_OBLIQUE))
                document.add(summaryParagraph)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            // Close the document
            document.close()
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
        // Create a new document
        val document = Document()

        try {
            // Initialize PdfWriter
            PdfWriter.getInstance(document, FileOutputStream(destination))

            // Open the document for writing
            document.open()

            val titleFont = getFont(HELVETICA_BOLD, config.titleFontSize)

            val dataFont =  getFont(HELVETICA, config.dataFontSize)

            val keysWithoutCalcs = data.keys.filter { x ->
                !x.contains("_Average") && !x.contains("_Sum") && !x.contains("Count")
            }

            // Add title if provided
            title?.let {
                val titleParagraph = Paragraph(it, titleFont)
                titleParagraph.alignment = Element.ALIGN_CENTER
                document.add(titleParagraph)
                document.add(Chunk.NEWLINE)  // Add a new line after the title
            }

            // Create a table based on the number of columns in the data
            val columns = keysWithoutCalcs.toList()
            val numColumns = columns.size
            val table = PdfPTable(numColumns)

            // Add header row if necessary
            if (header) {
                columns.forEach { column ->
                    //pitamo da li nije ni jedan ni drugi, vratimo TIMES
                    val font = if(!config.getHeaderFormat(column).isBold && !config.getHeaderFormat(column).isItalic)
                        TIMES
                    //u ovaj else skliznemo ako znamo da je barem jedan ili oba
                    //pa cemo pitati da li je jedan i drugi
                    else if(config.getHeaderFormat(column).isBold && config.getHeaderFormat(column).isItalic) {
                        TIMES_BOLDITALIC
                    }
                    //i onda za kraj ako znamo da nisu oba onda je ili jedan ili drugi
                    //pa cemo pitati da li je jedan ako nije znamo da je drugi
                    else if(config.getHeaderFormat(column).isItalic) TIMES_ITALIC else TIMES_BOLD

                    val headerFont = getFont(font, config.columnHeaderFontSize)
                    val cell = PdfPCell(Paragraph(column, headerFont))
                    cell.horizontalAlignment = Element.ALIGN_CENTER
                    table.addCell(cell)
                }
            }

            // Add data rows
            val numRows = data.values.first().size
            for (i in 0 until numRows) {
                columns.forEach { column ->
                    val cellData = data[column]?.get(i) ?: ""

                    //pitamo da li nije ni jedan ni drugi, vratimo TIMES
                    val font = if(!config.getColumnFormat(column).isBold && !config.getColumnFormat(column).isItalic) TIMES
                    //u ovaj else skliznemo ako znamo da je barem jedan ili oba
                    //pa cemo pitati da li je jedan i drugi
                    else if(config.getColumnFormat(column).isBold && config.getColumnFormat(column).isItalic) {
                        TIMES_BOLDITALIC
                    }
                    //i onda za kraj ako znamo da nisu oba onda je ili jedan ili drugi
                    //pa cemo pitati da li je jedan ako nije znamo da je drugi
                    else if(config.getColumnFormat(column).isItalic) TIMES_ITALIC else TIMES_BOLD

                    val columnFont = getFont(font, config.dataFontSize)

                    val cell = PdfPCell(Paragraph(cellData, columnFont))
                    cell.horizontalAlignment = Element.ALIGN_CENTER
                    table.addCell(cell)
                }
            }

            // Add the table to the document
            document.add(table)

            // Add summary if provided
            summary?.let {
                document.add(Chunk.NEWLINE)
                val summaryParagraph = Paragraph("Summary: $summary", getFont(HELVETICA_OBLIQUE))
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

