package spec

import formatting.FormattingConfig
import java.sql.ResultSet
import java.sql.ResultSetMetaData
/**
 * An interface for generating formatted or non-formatted reports from a map of column data to different formats.
 *
 * Implementations of this interface should define how the report is formatted and saved.
 */
interface ReportInterface {

    abstract val implName: String

    abstract val supportsFormatting: Boolean
    /**
     * Generates a report based on the provided data and writes it to the specified destination.
     *
     * @param data A map where the key is the column name and the value is a list of strings representing the column data.
     *             All lists in the map should have the same size to ensure proper row alignment.
     * @param destination The file path where the report will be saved.
     * @param header Indicates if header is provided in data
     * @param title An optional title for the report, used only in the formatted reports.
     * @param summary An optional summary for the report, used only in the formatted reports.
     */

    /**
     * Sets up formatting configuration to be used during report generation.
     * Implementations can choose to ignore it if `supportsFormatting` is false.
     */
    fun applyFormatting(config: FormattingConfig)

    fun generateReport(data: Map<String, List<String>>, destination: String, header: Boolean, title: String? = null, summary: String? = null)

    fun generateReportWithFormatting(data: Map<String, List<String>>, destination: String, header: Boolean, title: String? = null, summary: String? = null, config: FormattingConfig)


    fun generateReport(data: ResultSet, destination: String, header: Boolean, title: String? = null, summary: String? = null){
        val preparedData = prepareData(data)
        generateReport(preparedData, destination, header, title, summary)
    }

    private fun prepareData(resultSet: ResultSet): Map<String, List<String>> {
        val reportData = mutableMapOf<String, MutableList<String>>()

        val metaData: ResultSetMetaData = resultSet.metaData
        val columnCount = metaData.columnCount

        for (i in 1..columnCount) {
            val columnName = metaData.getColumnName(i)
            reportData[columnName] = mutableListOf()
        }

        while (resultSet.next()) {
            for (i in 1..columnCount) {
                val columnName = metaData.getColumnName(i)
                reportData[columnName]!!.add(resultSet.getString(i))
            }
        }

        return reportData
    }

}