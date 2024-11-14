package formatting

data class FormatOptions(
    val textStyle: Set<TextStyle> = emptySet(),
    val color: String? = null,
    val isBold: Boolean? = null,
    val isItalic: Boolean? = null,
    val isUnderline: Boolean? = null,
)

