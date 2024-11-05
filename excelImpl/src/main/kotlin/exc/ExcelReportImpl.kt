package exc

import org.apache.poi.ss.usermodel.*
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import spec.ReportInterface
import java.io.FileOutputStream

class ExcelReportImpl : ReportInterface {
    override val implName: String = "XLS"
    override val supportsFormatting: Boolean = true

    override fun generateReport(
        data: Map<String, List<String>>,
        destination: String,
        header: Boolean,
        title: String?,
        summary: String?
    ) {
        val workbook: Workbook = XSSFWorkbook()
        val sheet: Sheet = workbook.createSheet("Report")

        // Add title if provided
        title?.let {
            val titleRow: Row = sheet.createRow(0)
            val titleCell: Cell = titleRow.createCell(0)
            titleCell.setCellValue(it)

            // Merge title cells
            sheet.addMergedRegion(CellRangeAddress(0, 0, 0, data.size - 1))

            // Create and set title style
            val titleStyle = workbook.createCellStyle().apply {
                alignment = HorizontalAlignment.CENTER
                // Import Font class
                val titleFont: Font = workbook.createFont().apply {
                    bold = true
                    fontHeightInPoints = 18
                }
                this.setFont(titleFont)
            }
            titleCell.cellStyle = titleStyle
        }

        // Create header row if necessary
        if (header) {
            val headerRow: Row = sheet.createRow(1)
            data.keys.forEachIndexed { index, columnName ->
                headerRow.createCell(index).setCellValue(columnName)
            }
        }

        // Add data rows
        val numRows = data.values.first().size
        for (i in 0 until numRows) {
            val dataRow: Row = sheet.createRow(if (header) i + 2 else i + 1) // Adjust for header
            data.keys.forEachIndexed { index, columnName ->
                dataRow.createCell(index).setCellValue(data[columnName]?.get(i) ?: "")
            }
        }

        // Add summary if provided
        summary?.let {
            val summaryRow: Row = sheet.createRow(numRows + 2) // Place summary after data
            val summaryCell: Cell = summaryRow.createCell(0)
            summaryCell.setCellValue("Summary: $it")
        }

        // Write to the destination file
        FileOutputStream(destination).use { outputStream ->
            workbook.write(outputStream)
        }

        // Closing the workbook
        workbook.close()
    }
}
