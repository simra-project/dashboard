import util.Geo
import util.toIntPair
import java.io.BufferedReader
import java.io.File
import java.lang.Exception

data class Ride(
    val region: String,
    val version: Pair<Int, Int>,
    val distance: Double,
    val normalIncidents: Int,
    val scaryIncidents: Int
)

class RideProcessor(private val file: File) {

    fun getRide(): Ride? {
        try {
            val region = getRegion()
            val version = getVersion()
            val incidents = getIncidents()
            val distance = getDistance()

            return Ride(region, version, distance, incidents.first, incidents.second)
        } catch (e: Exception) {
            println("Exception for file " + file.absolutePath)
            e.printStackTrace()
        }
        return null
    }

    /**
     * @return region
     */
    fun getRegion(file: File = this.file): String {
        return file.absolutePath.split("data/")[1].split("/Rides")[0]
    }

    /**
     * @return major version, minor version
     */
    fun getVersion(file: File = this.file): Pair<Int, Int> {
        val versionLine = file.bufferedReader().use { it.readLine() }
        return versionLine.split("#").toIntPair()
    }

    /**
     * @return normalIncidents, scaryIncidents
     */
    fun getIncidents(file: File = this.file): Pair<Int, Int> {

        var incidentIndex = -1
        var scaryIndex = -1

        var normalIncidents = 0
        var scaryIncidents = 0

        val reader = file.bufferedReader()
        while (true) {
            val line = reader.readLine()
            val split = line.split(",")

            if (line.startsWith("key")) {
                incidentIndex = split.indexOf("incident")
                scaryIndex = split.indexOf("scary")
                check(incidentIndex >= -0 && scaryIndex >= 0) { "Indices must be > 0, are $incidentIndex and $scaryIndex" }
            } else if (incidentIndex >= 0 && split.size > 1) {
                var incidentTypeString = split[incidentIndex]
                var scaryTypeString = split[scaryIndex]
                if (incidentTypeString.equals("")) incidentTypeString = "0" // since empty has the same meaning as 0
                if (scaryTypeString.equals("")) scaryTypeString = "0" // since empty has the same meaning as 0

                val incidentType = incidentTypeString.toInt()
                val scaryType = scaryTypeString.toInt()

                if (incidentType > 0) { // valid incident types are larger than 0
                    if (scaryType == 0) normalIncidents++
                    if (scaryType == 1) scaryIncidents++
                }
            } else if (line.startsWith("===")) {
                break // we finished the incidents section
            }
        }
        reader.close()

        return Pair(normalIncidents, scaryIncidents)
    }

    fun getDistance(file: File = this.file): Double {
        val reader = file.bufferedReader()

        while (!reader.readLine().startsWith("lat")) {
            // keep skipping until we read the header
        }

        var lastGeo: Geo? = null
        var currentGeo: Geo?
        var distance = 0.0

        while (true) {
            val line = reader.readLine() ?: break
            val split = line.split(",")

            val lat = split[0].toDoubleOrNull()
            val lon = split[1].toDoubleOrNull()

            if (lat == null || lon == null) continue // line without coordinates

            currentGeo = Geo(lat, lon)
            if (lastGeo != null) {
                distance += lastGeo.haversine(currentGeo)
            }
            lastGeo = currentGeo

        }
        reader.close()

        return distance
    }
}

fun main() {
    val file = File("/Users/jhasenburg/git/simra-dashboard/data-parser/../data/Stuttgart/Rides/VM2_-375498443")
    val ride = RideProcessor(file).getRide()
    println(ride)
}
