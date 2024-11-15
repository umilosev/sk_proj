package formatting

data class FormatOptions(
    var textStyle: Set<TextStyle> = emptySet(),
    var color: String = "white",
    var isBold: Boolean = false,
    var isItalic: Boolean = false,
    var isUnderline: Boolean = false,
)

