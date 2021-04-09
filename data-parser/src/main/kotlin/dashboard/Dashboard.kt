package dashboard

import Ride
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.*
import me.tongfei.progressbar.ProgressBar
import org.apache.logging.log4j.LogManager
import java.io.File

private val logger = LogManager.getLogger()
private val gson = GsonBuilder().setPrettyPrinting().create();
//private val gson = Gson()

data class Dashboard(var regions: List<Region>, val sourceDate: String) {

    var totalRides = -1
    var totalIncidents = -1
    var totalKm = -1
    var diffDate: String? = null

    fun saveDashboardJson(dashboardFile: File) {
        dashboardFile.writeText(gson.toJson(this))
        logger.info("Saved dashboard json to ${dashboardFile.absolutePath}")
    }

    fun sort() {
        regions = regions.sortedByDescending { it.rides[0] }
    }

    fun updateTotals() {
        totalRides = regions.sumOf { it.rides[0] }
        totalIncidents = regions.sumOf { it.incidents[0] }
        totalKm = regions.sumOf { it.km[0] }
    }

    /**
     * Update the diff fields, i.e., put the change between [previous] and [origin] stats and the second position for each region
     */
    fun updateDiffs(previous: Dashboard, diffDate: String) {
        for (region in regions) {
            val pre = previous.find(region.name) ?: continue

            val diffRides = region.rides[0] - pre.rides[0]
            if (diffRides > 0) {
                logger.debug("Region ${region.name} has $diffRides new rides.")
                region.rides = listOf(region.rides[0], diffRides)
            }

            val diffIncidents = region.incidents[0] - pre.incidents[0]
            if (diffIncidents > 0) {
                logger.debug("Region ${region.name} has $diffIncidents new incidents.")
                region.incidents = listOf(region.incidents[0], diffIncidents)
            }

            val diffScaryIncidents = region.scaryIncidents[0] - pre.scaryIncidents[0]
            if (diffScaryIncidents > 0) {
                logger.debug("Region ${region.name} has $diffScaryIncidents new scary incidents.")
                region.scaryIncidents = listOf(region.scaryIncidents[0], diffScaryIncidents)
            }

            val diffKm = region.km[0] - pre.km[0]
            if (diffKm > 0) {
                logger.debug("Region ${region.name} has $diffKm new km.")
                region.km = listOf(region.km[0], diffKm)
            }
        }
        this.diffDate = diffDate
        logger.info("Update all diffs from dashboard")
    }

}

suspend fun createDashboard(rides: List<Ride>, sourceDate: String): Dashboard = coroutineScope {
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

    Dashboard(regionData, sourceDate)
}

fun readDashboardFromFile(dashboardFile: File?): Dashboard {
    if (dashboardFile == null) {
        logger.debug("Given dashboard file does not exist, creating empty dashboard.")
        return Dashboard(emptyList(), "empty")
    }
    val json = dashboardFile.readLines().joinToString("")
    return gson.fromJson(json, Dashboard::class.java)
}

/**
 * Create a new dashboard by summarizing the regions values from the both given dashboards.
 */
fun createNewTotalDashboard(pD: Dashboard, cD: Dashboard): Dashboard {
    val mergedRegions = pD.regions.toList() + cD.regions.toList()
    val regionNames = mergedRegions.map { it.name }.distinct()

    val newRegions = mutableListOf<Region>()

    for (regionName in regionNames) {
        newRegions.add(mergedRegions.filter { it.name == regionName }.calculateNewTotalsBasedOnTwo())
    }

    return Dashboard(newRegions, cD.sourceDate)
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

fun Dashboard.find(regionName: String): Region? {
    val regions = this.regions.filter { it.name == regionName }
    if (regions.size != 1) {
        logger.debug("There is not exactly one regions with name $regionName in $regions")
    }

    return regions.firstOrNull()
}
