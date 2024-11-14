package exc


import formatting.FormattingConfig
import org.apache.poi.ss.usermodel.*
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import spec.ReportInterface
import java.io.FileOutputStream

class ExcelReportImpl : ReportInterface {
    override val implName: String = "XLS"
    override val supportsFormatting: Boolean = true
    override fun applyFormatting(config: FormattingConfig) {
        TODO("isto kao za PDF nekako cemo ga gurnuti i menjati celije")
    }


    override fun generateReport(
        data: Map<String, List<String>>,
        destination: String,
        header: Boolean,
        title: String?,
        summary: String?,
    ) {
        val workbook: Workbook = XSSFWorkbook()
        val sheet: Sheet = workbook.createSheet("Report")
        sheet.defaultColumnWidth = 16

        title?.let {
            val titleRow: Row = sheet.createRow(0)
            val titleCell: Cell = titleRow.createCell(0)
            titleCell.setCellValue(it)
            sheet.addMergedRegion(CellRangeAddress(0, 0, 0, data.size - 1))
            val titleStyle = workbook.createCellStyle().apply {
                alignment = HorizontalAlignment.CENTER
                val titleFont: Font = workbook.createFont().apply {
                    bold = true
                    fontHeightInPoints = 18
                }
                setFont(titleFont)
            }
            titleCell.cellStyle = titleStyle
        }

        val keysWithoutCalcs = data.keys.filter { x ->
            !x.contains("_Average") && !x.contains("_Sum") && !x.contains("Count")
        }

        if (header) {
            val headerRow: Row = sheet.createRow(1)
            keysWithoutCalcs.forEachIndexed { index, columnName ->
                val headerCell = headerRow.createCell(index)
                headerCell.setCellValue(columnName)
            }
        }

        val numRows = data.values.first().size
        for (i in 0 until numRows) {
            val dataRow: Row = sheet.createRow(if (header) i + 2 else i + 1)
            keysWithoutCalcs.forEachIndexed { index, columnName ->
                val cell = dataRow.createCell(index)
                cell.setCellValue(data[columnName]?.get(i) ?: "")
            }
        }

        summary?.let {
            val summaryRow: Row = sheet.createRow(numRows + 2)
            val summaryCell: Cell = summaryRow.createCell(0)
            summaryCell.setCellValue("Summary: $it")
        }

        val avgRows = populateCalculationRow(sheet, data, numRows + 4, "Average")
        val sumRows = populateCalculationRow(sheet, data, numRows + 4 + avgRows + 1, "Sum")
        val countRows = populateCalculationRow(sheet, data, numRows + 4 + avgRows + 1 + sumRows + 1, "Count")

        FileOutputStream(destination).use { outputStream -> workbook.write(outputStream) }
        workbook.close()
    }

    private fun populateCalculationRow(
        sheet: Sheet,
        data: Map<String, List<String>>,
        startRow: Int,
        columnSuffix: String
    ): Int {
        val summaryColumnsNames = data.keys.filter { x -> x.contains("_$columnSuffix") }
            .map { x -> x.removeSuffix("_$columnSuffix")}
        if(summaryColumnsNames.isEmpty()) return 0

        val rowOffset = startRow + 2

        val headerRow = sheet.createRow(rowOffset)
        headerRow.createCell(0).setCellValue("Column Name")
        headerRow.createCell(1).setCellValue(columnSuffix)

        summaryColumnsNames.forEachIndexed { ind, colName ->
            val row = sheet.createRow(rowOffset + 1 + ind)

            row.createCell(0).setCellValue(colName)
            row.createCell(1).setCellValue(data[colName]?.get(0) ?: " N/A ")
        }

        return summaryColumnsNames.size + 1
    }
}
