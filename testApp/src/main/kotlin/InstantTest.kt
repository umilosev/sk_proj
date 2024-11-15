package testApp

import calc.CalculationEngine
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import formatting.ColumnFormatting
import formatting.FormattingConfig
import formatting.HeaderFormatting
import spec.ReportInterface
import java.io.InputStreamReader
import java.util.*

private fun prepareData(jsonData: InputStreamReader): Map<String, List<String>> {
    val gson = Gson()
    val scheduleType = object : TypeToken<List<Schedule>>() {}.type
    val schedules: List<Schedule> = gson.fromJson(jsonData, scheduleType)

    // Convert the list into a Map<String, List<String>> where key is column name and value is a list of corresponding column data
    val reportData: Map<String, List<String>> = mapOf(
        "subject" to schedules.map { it.subject },
        "classroom" to schedules.map { it.classroom },
        "year" to schedules.map { it.year.toString() },
        "group" to schedules.map { it.group },
        "day" to schedules.map { it.day },
        "time_from" to schedules.map { it.time_from },
        "time_to" to schedules.map { it.time_to },
        "ESPB" to schedules.map { it.ESPB }
    )

    return reportData
}

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

    exporterServices["TXT"]?.generateReport(myData, "report.txt", true, "TXT File")
    exporterServices["CSV"]?.generateReport(myData, "report.csv", true, "CSV File")
    exporterServices["XLS"]?.generateReport(myData, "report.xlsx", true, "XLSX File")
    exporterServices["PDF"]?.generateReport(myData, "report.pdf", true, "PDF File")

    println("passed exporting non-formatted stuff")

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
        myData, "formatted_report.pdf", true, "Formatted PDF",
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
        myData, "formatted_report.xlsx", true, "Formatted XLSX",
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