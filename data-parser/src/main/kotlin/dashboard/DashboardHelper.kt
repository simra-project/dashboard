package dashboard

import Ride
import kotlinx.coroutines.*
import me.tongfei.progressbar.ProgressBar

suspend fun createDashboard(rides: List<Ride>): Dashboard = coroutineScope {
    val regionNames = rides.map { it.region }.distinct()

    val coroutines = mutableListOf<Deferred<Region>>()
    val pb = ProgressBar("Aggregating Rides", regionNames.size.toLong())

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
        listOf(km.toInt()),
        "https://www.google.com"
    )
}