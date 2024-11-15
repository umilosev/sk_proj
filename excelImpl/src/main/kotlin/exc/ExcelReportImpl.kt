package exc


import formatting.FormatOptions
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

    override fun generateReportWithFormatting(
        data: Map<String, List<String>>,
        destination: String,
        header: Boolean,
        title: String?,
        summary: String?,
        config: FormattingConfig
    ) {
        val workbook: Workbook = XSSFWorkbook()
        val sheet: Sheet = workbook.createSheet("Report")
        sheet.defaultColumnWidth = 16

        // Apply title formatting
        title?.let {
            val titleRow: Row = sheet.createRow(0)
            val titleCell: Cell = titleRow.createCell(0)
            titleCell.setCellValue(it)
            sheet.addMergedRegion(CellRangeAddress(0, 0, 0, data.size - 1))
            val titleStyle = workbook.createCellStyle().apply {
                alignment = HorizontalAlignment.CENTER
                val titleFont: Font = workbook.createFont().apply {
                    bold = true
                    fontHeightInPoints = config.titleFontSize.toInt().toShort()
                }
                setFont(titleFont)
            }
            titleCell.cellStyle = titleStyle
        }

        val keysWithoutCalcs = data.keys.filter { x ->
            !x.contains("_Average") && !x.contains("_Sum") && !x.contains("Count")
        }

        // Apply header formatting
        if (header) {
            val headerRow: Row = sheet.createRow(1)
            keysWithoutCalcs.forEachIndexed { index, columnName ->
                val headerCell = headerRow.createCell(index)
                headerCell.setCellValue(columnName)

                val headerStyle = createCellStyle(workbook, config.getHeaderFormat(columnName))
                headerCell.cellStyle = headerStyle
            }
        }

        // Apply data formatting
        val numRows = data.values.first().size
        for (i in 0 until numRows) {
            val dataRow: Row = sheet.createRow(if (header) i + 2 else i + 1)
            keysWithoutCalcs.forEachIndexed { index, columnName ->
                val cell = dataRow.createCell(index)
                cell.setCellValue(data[columnName]?.get(i) ?: "")

                val columnStyle = createCellStyle(workbook, config.getColumnFormat(columnName))
                cell.cellStyle = columnStyle
            }
        }

        summary?.let {
            val summaryRow: Row = sheet.createRow(numRows + 2)
            val summaryCell: Cell = summaryRow.createCell(0)
            summaryCell.setCellValue("Summary: $it")
        }

        val avgRows = populateCalculationRowFormatted(workbook, sheet, data, numRows + 4
            , "Average", config)
        val sumRows = populateCalculationRowFormatted(workbook, sheet, data, numRows + 4 + avgRows + 1
            , "Sum", config)
        val countRows = populateCalculationRowFormatted(workbook, sheet, data, numRows + 4 + avgRows + 1 + sumRows + 1
            , "Count", config)

        FileOutputStream(destination).use { outputStream -> workbook.write(outputStream) }
        workbook.close()
    }

    private fun createCellStyle(workbook: Workbook, options: FormatOptions): CellStyle {
        val style = workbook.createCellStyle()
        val font = workbook.createFont()

        // Apply formatting options
        if (options.isBold) font.bold = true
        if (options.isItalic) font.italic = true
        if (options.isUnderline) font.underline = Font.U_SINGLE
// we can supply color this way too huh
//        if (options.textStyle.contains(TextStyle.COLOR)) {
//            font.color = IndexedColors.valueOf(options.color.uppercase()).index
//        }

//        if (options.color.isNotEmpty()) {
//            val colorString = if (options.color.startsWith("#")) options.color else "#${options.color}"
//            val rgbColor = java.awt.Color.decode(colorString)
//
//            val fillColor = XSSFColor(rgbColor, null)  // For Excel file format .xlsx
//
//            println("$colorString - $rgbColor - $fillColor")
//
//            style.fillForegroundColor = fillColor.index // Set the color using XSSFColor
//            style.fillPattern = FillPatternType.SOLID_FOREGROUND // Solid fill pattern
//        }

        style.setFont(font)
        style.alignment = HorizontalAlignment.CENTER // Adjust as needed

        return style
    }

    private fun populateCalculationRow(
        sheet: Sheet,
        data: Map<String, List<String>>,
        startRow: Int,
        columnSuffix: String
    ): Int {
        val summaryColumnsNames = data.keys.filter { x -> x.contains("_$columnSuffix") }
            .map { x -> x.removeSuffix("_$columnSuffix") }
        if (summaryColumnsNames.isEmpty()) return 0

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

    // data style from original column, header style for specific calculation
    private fun populateCalculationRowFormatted(
        workbook: Workbook,
        sheet: Sheet,
        data: Map<String, List<String>>,
        startRow: Int,
        columnSuffix: String,
        config: FormattingConfig
    ): Int {
        val summaryColumnsNames = data.keys.filter { it.contains("_$columnSuffix") }
            .map { it.removeSuffix("_$columnSuffix") }
        if (summaryColumnsNames.isEmpty()) return 0

        val rowOffset = startRow + 2
        val headerRow = sheet.createRow(rowOffset)

        val headerStyle = createCellStyle(workbook, config.getHeaderFormat(columnSuffix))
        headerRow.createCell(0).apply {
            setCellValue("Column Name")
            cellStyle = headerStyle
        }
        headerRow.createCell(1).apply {
            setCellValue(columnSuffix)
            cellStyle = headerStyle
        }

        summaryColumnsNames.forEachIndexed { ind, colName ->
            val row = sheet.createRow(rowOffset + 1 + ind)
            row.createCell(0).apply {
                setCellValue(colName)
                cellStyle = createCellStyle(workbook, config.getColumnFormat(colName))
            }
            row.createCell(1).apply {
                setCellValue(data[colName]?.get(0) ?: " N/A ")
                cellStyle = createCellStyle(workbook, config.getColumnFormat(colName))
            }
        }

        return summaryColumnsNames.size + 1
    }
}
