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

fun printData(data: Map<String,List<String>>){
    val columns = data.keys.toList()
    val numRows = data.values.first().size

    // Calculate the max width for each column
    val columnWidths = columns.map { column ->
        val maxDataWidth = data[column]?.maxOfOrNull { it.length } ?: 0
        maxOf(column.length, maxDataWidth)
    }

    // Write the header row
    columns.forEachIndexed { index, column ->
        print(column.padEnd(columnWidths[index] + 2))  // +2 for spacing
    }
    println()

    // Write dashes under the header
    columnWidths.forEach { width ->
        print("-".repeat(width + 2))  // +2 for spacing
    }
    println()

    // Write each row of data, properly spaced
    for (i in 0 until numRows) {
        columns.forEachIndexed { index, column ->
            val cell = data[column]?.get(i) ?: ""
            print(cell.padEnd(columnWidths[index] + 2))  // +2 for spacing
        }
        println()
    }
    println()
}

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

    val options = exporterServices.keys.toList()

    val inputStream = object {}.javaClass.getResourceAsStream("/data.json")
    val reader = InputStreamReader(inputStream)
    var data = prepareData(reader)
    reader.close()
    val tutorial =             "\n1. Biranje eksportera\n" +
            "2. Formatiranje izvestaja(dostupno samo PDF i Excel exporter-u)\n" +
            "3. Odradjivanje kalkulacija\n" +
            "4. Ispisivanje dosadasnjeg izvestaja\n" +
            "5. Tutorijal\n" +
            "6. Ispis tabele\n" +
            "-1. Nazad/Prekid radu\n"

    val userReader = System.`in`.bufferedReader()
    println("\nDobro dosli, izvolite tutorijal za koriscenje programa\n" + tutorial)
    var eksporter =-1
    while(true) {
        println("\nIzaberite sta zelite da uradite, 5 za pomoc\n")
        var unos = System.`in`.bufferedReader().readLine().toString()

        when (unos) {
            "1" -> {
                var i = 1
                println("\nIzaberite eksporter:\n")
                for((eksporterServis) in exporterServices.entries){
                    println("$i.$eksporterServis")
                    i+=1
                }
                eksporter = userReader.readLine().toInt()-1
                if(eksporter == -1) {
                    println("Uspesno ste prekinuli akciju")
                    continue
                }
                println("\nEksporter "+options[eksporter]+" izabran!\n")
            }

            "2" -> {
                println("\nBiranje formata\n")
                //ovde bismo pozvali, tipa reportInterface, i rekli, okej pozovemo to i onda unutar reportInterface-a
                //mi pravimo taj konfig fajl, i zovemo ga
                //
            }

            "3" -> {
                println("Izaberite koju kalkulaciju zelite da uradite\n" +
                        "1.Count\n" +
                        "2.Sum\n" +
                        "3.Average\n" +
                        "-1. Nazad\n")
                when(userReader.readLine().toInt()){
                    1->{
                        println("Sta zelite da prebrojavate?\n" +
                                "1. Reci\n" +
                                "2. Brojeve\n")
                        val metoda = userReader.readLine().toInt()
                        if(metoda == -1) {
                            println("\nUspesno ste ponistili akciju\n")
                            continue
                        }
                        println("\nUnesite ime kolone\n")
                        val kolona = userReader.readLine().toString()
                        if(kolona.equals("-1")) {
                            println("\nUspesno ste ponistili akciju\n")
                            continue
                        }
                        when(metoda){
                            1 -> {
                                println("\nKoju rec trazite\n")
                                val kondicional = userReader.readLine().toString()
                                if(kondicional.equals("-1")){
                                    println("\nUspesno ste ponistili akciju\n")
                                    continue
                                }
                                data = calcEngine.calculateCount(data, kolona, 0, condition = {element -> element.contains(kondicional)})
                                printData(data)
                            }
                            2 -> {
                                println("Unesite broj po kojem poredimo")
                                val kondicional = userReader.readLine().toString()
                                if(kolona == "-1") {
                                    println("\nUspesno ste ponistili akciju\n")
                                    continue
                                }
                                println("\nDa li prebrojavamo vece/manje brojeve od unetog broja? "+kondicional)
                                val vm = userReader.readLine().toString()
                                if(vm == "-1") {
                                    println("\nUspesno ste ponistili akciju\n")
                                    continue
                                }
                                val veceManje = if(vm.contains("vece")) 1 else -1
                                if(veceManje==1){
                                    data = calcEngine.calculateCount(data, kolona, veceManje, condition = {element -> (element.toDouble() > kondicional.toDouble())})
                                    printData(data)
                                }
                                else {
                                    data = calcEngine.calculateCount(data, kolona, veceManje, condition = {element -> (element.toDouble() < kondicional.toDouble())})
                                    printData(data)
                                }
                            }
                        }
                    }
                    2->{
                        println("Unesite ime kolone")
                        val kolona = userReader.readLine().toString()
                        if(kolona == "-1") {
                            println("\nUspesno ste ponistili akciju\n")
                            continue
                        }
                        data = calcEngine.calculateSum(data, kolona)
                    }
                    3->{
                        println("Unesite ime kolone")
                        val kolona = userReader.readLine().toString()
                        if(kolona == "-1") {
                            println("\nUspesno ste ponistili akciju\n")
                            continue
                        }
                        data = calcEngine.calculateAverage(data, kolona)
                    }
                }
            }

            "4" -> {
                if(eksporter == -1) {
                    println("\nIzaberite eksporter prvo")
                    continue
                }
                println(
                    "\nDa biste ispisali izvestaj potrebno je da ga imenujete\n" +
                            "potrebno je da pratite ovu konvenciju\n" +
                            "Excel -> .xlsx\n" +
                            "PDF -> .pdf\n" +
                            "CSV -> .csv\n" +
                            "TXT -> .txt"
                )
                println("\nVi ste odabrali "+options[eksporter]+"\n")
                var destinacija = userReader.readLine().toString()
                if (destinacija.equals("-1")) {
                    println("\nUspesno ste ponistili akciju")
                    continue
                }
                exporterServices[options[eksporter]]?.generateReport(data, destinacija, true)
                println("\nUspesno ste ispisali izvestaj sa "+options[eksporter]+" eksporterom\n")
            }

            "5" -> {
                println(tutorial)
            }

            "6" ->{
                printData(data)
            }

            "-1" -> {
                println("Hvala sto ste koristili nas program!")
                break
            }

        }
    }
//    println("Unesite ime kolone")
//    val kolona = userReader.readLine().toString()
//    println("Vece ili manje?")
//    val vm = userReader.readLine().toString()
//    var veceManje = if(vm.contains("vece")) 1 else -1
//    println("Po kom broju zelite da prebrojite")
//    val kondicional=userReader.readLine().toString()
//    println("Pre kalkulacije")
//
//    println(data)
//
//    data = calcEngine.calculateAverage(data, "ESPB")
//    if(veceManje==1){
//        data = calcEngine.calculateCount(data, kolona, veceManje, condition = {element -> (element.toDouble() > kondicional.toDouble())})
//    }
//    else data = calcEngine.calculateCount(data, kolona, veceManje, condition = {element -> (element.toDouble() < kondicional.toDouble())})
//
//    data = calcEngine.calculateSum(data, "ESPB")
//
//    println("Nakon kalkulacije")
//
//    println(data)

//    exporterServices["XLS"]?.generateReport(data, "excelReport.xlsx", true)


}

private fun testAppInstant(
    data: Map<String, List<String>>,
    calcEngine: CalculationEngine,
    exporterServices: Map<String, ReportInterface>
)
{
    var myData = data

    myData = calcEngine.calculateAverage(myData, "ESPB")
    myData = calcEngine.calculateAverage(myData, "year")
    myData = calcEngine.calculateAverage(myData, "group")
    myData = calcEngine.calculateSum(myData, "ESPB")
    myData = calcEngine.calculateCount(myData, "ESPB")
    myData = calcEngine.calculateCount(myData, "group")

    exporterServices["TXT"]?.generateReport(myData, "reported.txt", true)
    exporterServices["CSV"]?.generateReport(myData, "reported.csv", true)
    exporterServices["XLS"]?.generateReport(myData, "reported.xlsx", true)

    Thread.sleep(99999999999)
}