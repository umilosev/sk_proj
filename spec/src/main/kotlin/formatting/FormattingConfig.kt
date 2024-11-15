package formatting


data class FormattingConfig(
    var headerFormats: List<HeaderFormatting> = emptyList(),
    var columnFormats: List<ColumnFormatting> = emptyList(),
    var titleFontSize: Float,
    var columnHeaderFontSize: Float,
    var dataFontSize: Float
) {
    fun getColumnFormat(columnName: String): FormatOptions {
//        println("Hello? " + columnName + " - " + columnFormats.find{x -> x.columnName == columnName})

        return columnFormats.find { it.columnName == columnName }?.options ?: FormatOptions()
    }
    fun getHeaderFormat(columnName: String): FormatOptions {
//        println("Hello? " + columnName + " - " + headerFormats.find{x -> x.headerName == columnName})

        return headerFormats.find { it.headerName == columnName }?.options ?: FormatOptions()
    }
}

