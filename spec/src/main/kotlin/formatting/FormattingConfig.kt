package formatting


data class FormattingConfig(
    val headerFormats: List<HeaderFormatting> = emptyList(),
    val columnFormats: List<ColumnFormatting> = emptyList(),
    var titleFontSize: Int,
    var columnHeaderFontSize: Int,
    var dataFontSize: Int
) {
    fun getColumnFormat(columnName: String): FormatOptions? {
        return columnFormats.find { it.columnName == columnName }?.options
    }
}
