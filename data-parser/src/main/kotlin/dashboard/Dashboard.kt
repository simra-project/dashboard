package dashboard

import Ride
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.*
import me.tongfei.progressbar.ProgressBar
import org.apache.logging.log4j.LogManager
import java.io.File

private val logger = LogManager.getLogger()
//private val gson = GsonBuilder().setPrettyPrinting().create();
private val gson = Gson()

data class Dashboard(val regions: List<Region>) {

    fun saveDashboardJson(dashboardFile: File) {
        dashboardFile.writeText(gson.toJson(this))
        logger.info("Saved dashboard json to ${dashboardFile.absolutePath}")
    }

}

suspend fun createDashboard(rides: List<Ride>): Dashboard = coroutineScope {
    val regionNames = rides.map { it.region }.distinct()

    val coroutines = mutableListOf<Deferred<Region>>()
    val pb = ProgressBar("Aggregating Regions", regionNames.size.toLong())

    for (regionName in regionNames) {
        val regionRides = rides.filter { it.region == regionName }
        coroutines.add(async(Dispatchers.Default) {
            val region = processRidesForRegion(regionName, regionRides)
            pb.step()
            region
        })
    }
    val regionData = coroutines.awaitAll()
    pb.close()

    Dashboard(regionData)
}

fun readDashboardFromFile(dashboardFile: File?): Dashboard {
    if (dashboardFile == null) {
        logger.debug("Given dashboard file does not exist, creating empty dashboard.")
        return Dashboard(emptyList())
    }
    val json = dashboardFile.readLines().joinToString()
    return gson.fromJson(json, Dashboard::class.java)
}

fun createNewTotalDashboard(pD: Dashboard, cD: Dashboard): Dashboard {
    val mergedRegions = (pD.regions union cD.regions)
    val regionNames = mergedRegions.map { it.name }

    val newRegions = mutableListOf<Region>()

    for (regionName in regionNames) {
        newRegions.add(mergedRegions.filter { it.name == regionName }.mergeTwo())
    }

    return Dashboard(newRegions)
}

/*****************************************************************/
// Private Helper
/*****************************************************************/

private fun processRidesForRegion(regionName: String, rideList: List<Ride>): Region {
    var rides = 0
    var incidents = 0
    var scaryIncidents = 0
    var km = 0.0

    for (ride in rideList) {
        rides++
        incidents += ride.normalIncidents + ride.scaryIncidents
        scaryIncidents += ride.scaryIncidents
        km += ride.distance
    }

    return Region(
        regionName,
        listOf(rides),
        listOf(incidents),
        listOf(scaryIncidents),
        listOf(km.toInt())
    )
}

fun Dashboard.find(regionName: String): Region {
    val regions = this.regions.filter { it.name == regionName }
    if (regions.size > 1) {
        logger.error("There is not exactly one regions with name $regionName in $regions")
    }

    return regions.first()
}
