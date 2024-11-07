package calc

class CalculationEngine {

    // Funkcija za COUNT - prebrojava sve vrednosti u zadatoj koloni (može imati opcioni uslov)
    fun calculateCount(data: Map<String, List<Any?>>, columnName: String, condition: (Any?) -> Boolean = { true }): Map<String, List<Any?>>? {
        val columnData = data[columnName]

        if (columnData == null) {
            println("Greška: Kolona '$columnName' ne postoji.")
            return null
        }

        val count = columnData.count(condition)
        val updatedData = data.toMutableMap()
        updatedData["${columnName}_Count"] = listOf(count) + List(columnData.size - 1) { null }

        return updatedData
    }

    // Funkcija za SUM - sabira sve numeričke vrednosti u zadatoj koloni
    fun calculateSum(data: Map<String, List<String?>>, columnName: String): Map<String, List<String?>> {
        val columnData = data[columnName]

        if (columnData == null) {
            println("Greška: Kolona '$columnName' ne postoji.")
            return data
        }

        // Konvertovanje svih vrednosti u Double, filtracija nevalidnih vrednosti
        val validNumbers = columnData.mapNotNull { it?.toDoubleOrNull() }

        if (validNumbers.size != columnData.size) {
            println("Greška: Neke vrednosti u koloni '$columnName' nisu validni brojevi.")
            return data
        }

        val sum = validNumbers.sum()

        // Konvertovanje rezultata u string
        val sumAsString = sum.toString()

        // Ažuriranje podataka sa sumom kao string
        val updatedData = data.toMutableMap()
        updatedData["${columnName}_Sum"] = listOf(sumAsString) + List(columnData.size - 1) { null }

        return updatedData
    }


    // Funkcija za AVERAGE - računa prosečnu vrednost numeričke kolone
    fun calculateAverage(data: Map<String, List<Any?>>, columnName: String): Map<String, List<Any?>>? {
        val columnData = data[columnName]

        if (columnData == null) {
            println("Greška: Kolona '$columnName' ne postoji.")
            return data
        }

        if (!columnData.all { it is Number }) {
            println("Greška: Kolona '$columnName' nije numerička i ne može se koristiti za računanje proseka.")
            return data
        }
        // TODO :: NEMAMO AVERAGE JER LISTA BROJEVA IME DEFAULT-NU AVERAGE FUNKCIJU
        //      NEMOJ DA ZABORAVIS
        val average = columnData.filterIsInstance<Number>()
        val updatedData = data.toMutableMap()
        updatedData["${columnName}_Average"] = listOf(average) + List(columnData.size - 1) { null }

        return updatedData
    }

    fun isDouble(value: Any?): Boolean {
        if(value is String) return value.toDoubleOrNull() != null else return false
    }
}
