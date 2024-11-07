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
    fun calculateSum(data: Map<String, List<Any?>>, columnName: String): Map<String, List<Any?>>? {
        val columnData = data[columnName]

        if (columnData == null) {
            println("Greška: Kolona '$columnName' ne postoji.")
            return null
        }

        if (!columnData.all { it is Number }) {
            println("Greška: Kolona '$columnName' nije numerička i ne može se sumirati.")
            return null
        }

        val sum = columnData.filterIsInstance<Number>().sumOf { it.toDouble() }
        val updatedData = data.toMutableMap()
        updatedData["${columnName}_Sum"] = listOf(sum) + List(columnData.size - 1) { null }

        return updatedData
    }

    // Funkcija za AVERAGE - računa prosečnu vrednost numeričke kolone
    fun calculateAverage(data: Map<String, List<Any?>>, columnName: String): Map<String, List<Any?>>? {
        val columnData = data[columnName]

        if (columnData == null) {
            println("Greška: Kolona '$columnName' ne postoji.")
            return null
        }

        if (!columnData.all { it is Number }) {
            println("Greška: Kolona '$columnName' nije numerička i ne može se koristiti za računanje proseka.")
            return null
        }
        // TODO :: NEMAMO AVERAGE JER LISTA BROJEVA IME DEFAULT-NU AVERAGE FUNKCIJU
        //      NEMOJ DA ZABORAVIS
        val average = columnData.filterIsInstance<Number>()
        val updatedData = data.toMutableMap()
        updatedData["${columnName}_Average"] = listOf(average) + List(columnData.size - 1) { null }

        return updatedData
    }
}
