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

    val options = exporterServices.keys.toList()
    //TODO : aplikacija ce vrteti forever while i cekace unos, kada doceka unos recimo 0 onda ce da izadje,
    // a do tada cemo da ispisujemo tutorijal za koriscenje i pustiti korisnika da koristi program


    println("Exporters: " + exporterServices.keys + " - " + serviceLoader.count())

    val inputStream = object {}.javaClass.getResourceAsStream("/data.json")
    val reader = InputStreamReader(inputStream)
    var data = prepareData(reader)
    reader.close()

    val tutorial =             "1. Biranje eksportera\n" +
            "2. Formatiranje izvestaja(dostupno samo PDF i Excel exporter-u)\n" +
            "3. Odradjivanje kalkulacija\n" +
            "4. Ispisivanje dosadasnjeg izvestaja\n" +
            "5. Tutorijal\n" +
            "6. Ispis tabele" +
            "-1. Nazad\n"+
            "0. Prekid radu\n"
    val userReader = System.`in`.bufferedReader()
    println("Dobro dosli, izvolite tutorijal za koriscenje programa\n" + tutorial)
    var eksporter =-1
    while(true) {
        println("Izaberite sta zelite da uradite")
        var unos = System.`in`.bufferedReader().readLine().toString()

        when (unos) {
            "1" -> {
                println(
                    "Izaberite eksporter:\n" +
                            "1.Excel\n" +
                            "2.CSV\n" +
                            "3.PDF\n" +
                            "4.TXT\n"
                )
                eksporter = userReader.readLine().toInt()-1
                if(eksporter == -1) continue
                println("Eksporter "+options[eksporter]+" izabran!")
            }

            "2" -> {
                println("Biranje formata")
            }

            "3" -> {
                println("Izaberite koju kalkulaciju zelite da uradite\n" +
                        "1.Count\n" +
                        "2.Sum\n" +
                        "3.Average\n" +
                        "-1. Nazad")
                when(userReader.readLine().toInt()){
                    1->{
                        println("Sta zelite da prebrojavate?\n" +
                                "1. Reci\n" +
                                "2. Brojeve")
                        val metoda = userReader.readLine().toInt()
                        if(metoda == -1) continue
                        println("Unesite ime kolone")
                        val kolona = userReader.readLine().toString()
                        if(kolona.equals("-1")) continue
                        when(metoda){
                            1 -> {
                                println("Koju rec trazite")
                                val kondicional = userReader.readLine().toString()
                                if(kondicional.equals("-1"))
                                data = calcEngine.calculateCount(data, kolona, 0, condition = {element -> element.contains(kondicional)})
                            }
                            2 -> {
                                println("Unesite broj po kojem poredimo")
                                val kondicional = userReader.readLine().toString()
                                if(kolona == "-1") continue
                                println("Da li prebrojavamo vece/manje brojeve od unetog broja? "+kondicional)
                                val vm = userReader.readLine().toString()
                                if(vm == "-1") continue
                                val veceManje = if(vm.contains("vece")) 1 else -1
                                if(veceManje==1){
                                    data = calcEngine.calculateCount(data, kolona, veceManje, condition = {element -> (element.toDouble() > kondicional.toDouble())})
                                }
                                else data = calcEngine.calculateCount(data, kolona, veceManje, condition = {element -> (element.toDouble() < kondicional.toDouble())})
                            }
                        }
                    }
                    2->{
                        println("Unesite ime kolone")
                        val kolona = userReader.readLine().toString()
                        if(kolona == "-1") continue
                        data = calcEngine.calculateSum(data, kolona)
                    }
                    3->{
                        println("Unesite ime kolone")
                        val kolona = userReader.readLine().toString()
                        if(kolona == "-1") continue
                        data = calcEngine.calculateAverage(data, kolona)
                    }
                }
            }

            "4" -> {
                if(eksporter == -1) {
                    println("Izaberite eksporter prvo")
                    continue
                }
                println(
                    "Da biste ispisali izvestaj potrebno je da ga imenujete\n" +
                            "potrebno je da pratite ovu konvenciju\n" +
                            "Excel -> .xlsx\n" +
                            "PDF -> .pdf\n" +
                            "CSV -> .csv\n" +
                            "TXT -> .txt"
                )
                var destinacija = userReader.readLine().toString()
                if (destinacija.equals("-1")) continue
                exporterServices[options[eksporter]]?.generateReport(data, destinacija, true)
            }

            "5" -> {
                println(tutorial)
            }

            "0" -> {
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

    exporterServices["XLS"]?.generateReport(data, "excelReport.xlsx", true)


}