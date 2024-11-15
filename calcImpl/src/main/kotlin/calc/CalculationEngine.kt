package calc

class CalculationEngine {

    // Funkcija za COUNT - prebrojava sve vrednosti u zadatoj koloni (može imati opcioni uslov)
    fun calculateCount(data: Map<String, List<String>>, columnName: String, condition: (String) -> Boolean = { true }): Map<String, List<String>> {
        val columnData = data[columnName]

        if (columnData == null) {
            println("Greška: Kolona '$columnName' ne postoji.")
            return data
        }

        val count = columnData.count(condition)
        val updatedData = data.toMutableMap()
        updatedData["${columnName}_Count"] = listOf(count.toString()) + List(columnData.size - 1) { "" }

        return updatedData
    }

    // Funkcija za COUNT - prebrojava sve vrednosti u zadatoj koloni (može imati opcioni uslov)
    fun calculateCount(data: Map<String, List<String>>, columnName: String, flag: Int, condition: (String) -> Boolean = { true }): Map<String, List<String>> {
        val columnData = data[columnName]

        if (columnData == null) {
            println("Greška: Kolona '$columnName' ne postoji.")
            return data
        }

        if(flag!=0){
            // Konvertovanje svih vrednosti u Double, filtracija nevalidnih vrednosti
            val validNumbers = columnData.mapNotNull { it.toDoubleOrNull() }

            if (validNumbers.size != columnData.size) {
                println("Greška: Kolona '$columnName' nije kolona sa brojevima i ne moze biti poredjena sa brojevima.")
                return data
            }
        }

        val count = columnData.count(condition)
        val updatedData = data.toMutableMap()
        updatedData["${columnName}_Count"] = listOf(count.toString()) + List(columnData.size - 1) { "" }

        return updatedData
    }

    // Funkcija za SUM - sabira sve numeričke vrednosti u zadatoj koloni
    fun calculateSum(data: Map<String, List<String>>, columnName: String): Map<String, List<String>> {
        val columnData = data[columnName]

        if (columnData == null) {
            println("Greška: Kolona '$columnName' ne postoji.")
            return data
        }

        // Konvertovanje svih vrednosti u Double, filtracija nevalidnih vrednosti
        val validNumbers = columnData.mapNotNull { it.toDoubleOrNull() }

        if (validNumbers.size != columnData.size) {
            println("Greška: Neke vrednosti u koloni '$columnName' nisu validni brojevi.")
            return data
        }

        val sum = validNumbers.sum()

        // Konvertovanje rezultata u string
        val sumAsString = sum.toString()

        // Ažuriranje podataka sa sumom kao string
        val updatedData = data.toMutableMap()
        updatedData["${columnName}_Sum"] = listOf(sumAsString) + List(columnData.size - 1) { "" }

        return updatedData
    }


    // Funkcija za AVERAGE - računa prosečnu vrednost numeričke kolone
    fun calculateAverage(data: Map<String, List<String>>, columnName: String): Map<String, List<String>> {
        val columnData = data[columnName]

        if (columnData == null) {
            println("Greška: Kolona '$columnName' ne postoji.")
            return data
        }

        // Konvertovanje svih vrednosti u Double, filtracija nevalidnih vrednosti
        val validNumbers = columnData.mapNotNull { it.toDoubleOrNull() }

        if (validNumbers.size != columnData.size) {
            println("Greška: Neke vrednosti u koloni '$columnName' nisu validni brojevi.")
            return data
        }

        val sum = validNumbers.sum()
        val average = sum/validNumbers.size

        val averageAsString = average.toString()

        val updatedData = data.toMutableMap()
        updatedData["${columnName}_Average"] = listOf(averageAsString) + List(columnData.size - 1) { "" }

        return updatedData
    }

    fun calculateAddition(data: Map<String, List<String>>, columnName1:String, columnName2:String):Map <String, List<String>> {
        val columnData1 = data[columnName1]
        val columnData2 = data[columnName2]

        if(columnData1 == null || columnData2 == null) {
            println("Greška: Jedna od kolona ne postoji.")
            return data
        }

        val validNumbers1 = columnData1.mapNotNull { it.toDoubleOrNull() }
        val validNumbers2 = columnData2.mapNotNull { it.toDoubleOrNull() }

        if (validNumbers1.size != columnData1.size || validNumbers2.size != columnData2.size) {
            println("Greška: Neka od kolona nisu brojevi")
            return data
        }

        val zbir = validNumbers1.toMutableList()

        for(i in 0 until validNumbers1.size) {
            zbir[i] += validNumbers2[i]
        }
        var zbirString :List<String> = emptyList()
        for(i in 0 until zbir.size) {
            zbirString+= listOf(zbir[i].toString())
        }
        val updatedData = data.toMutableMap()
        updatedData["${columnName1}+${columnName2}"]=zbirString
        return updatedData
    }

}
