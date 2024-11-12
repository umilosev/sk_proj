package testApp

import calc.CalculationEngine
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import spec.ReportInterface
import java.io.InputStream
import java.io.InputStreamReader
import java.util.*

data class Schedule(
    val subject: String,
    val classroom: String,
    val year: Int,
    val group: String,
    val day: String,
    val time_from: String,
    val time_to: String,
    val ESPB: String,
)

fun prepareData(jsonData: InputStreamReader): Map<String, List<String>> {
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

fun main() {
    val serviceLoader = ServiceLoader.load(ReportInterface::class.java)

    val calcEngine = CalculationEngine()

    val exporterServices = mutableMapOf<String, ReportInterface>()

    serviceLoader.forEach { service ->
        exporterServices[service.implName] = service
    }

    //TODO : aplikacija ce vrteti forever while i cekace unos, kada doceka unos recimo 0 onda ce da izadje,
    // a do tada cemo da ispisujemo tutorijal za koriscenje i pustiti korisnika da koristi program

    //while(true){
    println("Exporters: " + exporterServices.keys + " - " + serviceLoader.count())

    val inputStream = object {}.javaClass.getResourceAsStream("/data.json")
    val reader = InputStreamReader(inputStream)
    var data = prepareData(reader)
    reader.close()

    val userReader = System.`in`.bufferedReader()
    println("Unesite ime kolone")
    val kolona = userReader.readLine().toString()
    println("Vece ili manje?")
    val vm = userReader.readLine().toString()
    var veceManje : Int = 0
    veceManje = if(vm.contains("vece")) 1 else -1
    println("Po kom broju zelite da prebrojite")
    val kondicional=userReader.readLine().toString()
    println("Pre kalkulacije")

    println(data)

    data = calcEngine.calculateAverage(data, "ESPB")
    data = calcEngine.calculateAverage(data, "year")
    data = calcEngine.calculateAverage(data, "group")
    if(veceManje==1){
        data = calcEngine.calculateCount(data, kolona, veceManje, condition = {element -> (element.toDouble() > kondicional.toDouble())})
    }
    else data = calcEngine.calculateCount(data, kolona, veceManje, condition = {element -> (element.toDouble() < kondicional.toDouble())})

    data = calcEngine.calculateAverage(data, "ESPB")
    data = calcEngine.calculateSum(data, "ESPB")

    println("Nakon kalkulacije")

    println(data)

    exporterServices["XLS"]?.generateReport(data, "excelReport.xlsx", true)
    //}

}