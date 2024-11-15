package testApp

import calc.CalculationEngine
import formatting.ColumnFormatting
import formatting.FormattingConfig
import formatting.HeaderFormatting
import spec.ReportInterface
import java.io.InputStreamReader
import java.util.*

fun main()
{
    val serviceLoader = ServiceLoader.load(ReportInterface::class.java)

    val calcEngine = CalculationEngine()

    val exporterServices = mutableMapOf<String, ReportInterface>()

    serviceLoader.forEach { service ->
        exporterServices[service.implName] = service
    }

    val options = exporterServices.keys.toList()

    // Postavi podrazumevane vrednosti za FormattingConfig
    val formattingConfig = FormattingConfig(
        titleFontSize = 16f, // podrazumevana veličina fonta za naslov
        columnHeaderFontSize = 12f, // podrazumevana veličina fonta za zaglavlja kolona
        dataFontSize = 10f, // podrazumevana veličina fonta za podatke
    )

    var skipFlag = false

    val inputStream = object {}.javaClass.getResourceAsStream("/data.json")
    val reader = InputStreamReader(inputStream)
    var data = prepareData(reader)
    reader.close()

    testAppInstant(data, calcEngine, exporterServices)
}

private fun testAppInstant(
    data: Map<String, List<String>>,
    calcEngine: CalculationEngine,
    exporterServices: Map<String, ReportInterface>
) {
    var myData = data

    myData = calcEngine.calculateAverage(myData, "ESPB")
    myData = calcEngine.calculateAverage(myData, "year")
    myData = calcEngine.calculateAverage(myData, "group")
    myData = calcEngine.calculateSum(myData, "ESPB")
    myData = calcEngine.calculateCount(myData, "ESPB")
    myData = calcEngine.calculateCount(myData, "group")

    exporterServices["TXT"]?.generateReport(myData, "reported.txt", true, "Yokoso")
    exporterServices["CSV"]?.generateReport(myData, "reported.csv", true, "Watashi")
    exporterServices["XLS"]?.generateReport(myData, "reported.xlsx", true, "No")
    exporterServices["PDF"]?.generateReport(myData, "reported.pdf", true, "Soul Society")

    println("yokoso watashi no soul society")

    // explodes if columns non-existent
    val hdrEspbFormatting = HeaderFormatting("ESPB")
    hdrEspbFormatting.options.color = "#FFAB22"
    hdrEspbFormatting.options.isBold = true
    val hdrGroupFormatting = HeaderFormatting("group")
    hdrGroupFormatting.options.isItalic = true
    hdrGroupFormatting.options.isUnderline = true

    val colEspbFormatting = ColumnFormatting("ESPB")
    colEspbFormatting.options.isItalic = true
    val colGroupFormatting = ColumnFormatting("group")
    colGroupFormatting.options.isItalic = true
    colGroupFormatting.options.isUnderline = true
    colGroupFormatting.options.isBold = true

    exporterServices["PDF"]?.generateReportWithFormatting(
        myData, "reported_la_formatted.pdf", true, "Soul",
        config = FormattingConfig(
            headerFormats = listOf(
                hdrEspbFormatting, hdrGroupFormatting
            ),
            columnFormats = listOf(
                colEspbFormatting, colGroupFormatting
            ),
            titleFontSize = 24f,
            columnHeaderFontSize = 14f,
            dataFontSize = 12f
        )
    )

    exporterServices["XLS"]?.generateReportWithFormatting(
        myData, "reported_la_formatted.xlsx", true, "Society",
        config = FormattingConfig(
            headerFormats = listOf(
                hdrEspbFormatting, hdrGroupFormatting
            ),
            columnFormats = listOf(
                colEspbFormatting, colGroupFormatting
            ),
            titleFontSize = 24f,
            columnHeaderFontSize = 14f,
            dataFontSize = 12f
        )
    )

    Thread.sleep(99999999999)
}