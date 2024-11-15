package formatting


data class FormattingConfig(
    var headerFormats: List<HeaderFormatting> = emptyList(),
    var columnFormats: List<ColumnFormatting> = emptyList(),
    var titleFontSize: Float,
    var columnHeaderFontSize: Float,
    var dataFontSize: Float
) {
    fun getColumnFormat(columnName: String): FormatOptions {
        return columnFormats.find { it.columnName == columnName }!!.options
    }
    fun getHeaderFormat(columnName: String): FormatOptions {
        return headerFormats.find { it.headerName == columnName }!!.options
    }
}

