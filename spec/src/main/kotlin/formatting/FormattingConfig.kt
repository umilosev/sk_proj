package formatting


data class FormattingConfig(
    val headerFormat: FormatOptions = FormatOptions(),
    val columnFormats: List<ColumnFormatting> = emptyList()
) {
    fun getColumnFormat(columnName: String): FormatOptions? {
        return columnFormats.find { it.columnName == columnName }?.options
    }
}
