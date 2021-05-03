package dashboard

import org.apache.logging.log4j.LogManager

private val logger = LogManager.getLogger()

/**
 * Holds the region json that is consumed by table.js of the simra-dashboard website:
{
name: "Berlin",
rides: [1300, 400],
incidents: [270, 21],
scaryIncidents: [74, 3],
km: [4800, 728]
}
 * The first value is the total, the second value the change, i.e., diff to previous snapshot.
 *
 */
data class Region(
    val name: String,
    var rides: List<Int>,
    var incidents: List<Int>,
    var scaryIncidents: List<Int>,
    var km: List<Int>,
) {
    init {
        if (rides.size == 1) {
            rides = listOf(rides[0], 0)
        }
        if (incidents.size == 1) {
            incidents = listOf(incidents[0], 0)
        }
        if (scaryIncidents.size == 1) {
            scaryIncidents = listOf(scaryIncidents[0], 0)
        }
        if (km.size == 1) {
            km = listOf(km[0], 0)
        }

        require(rides.size == 2 && incidents.size == 2 && scaryIncidents.size == 2 && km.size == 2) {
            "Statistic fields must have two slots, does not seem to have two: $this"
        }
    }
}

fun List<Region>.calculateNewTotalsBasedOnTwo(): Region {
    require(this.isNotEmpty()) { "$this does not contain a single region " }
    require(this.size <= 2) { "$this should contain at most two regions" }
    require(this.map { it.name }.distinct().size == 1) { "Can only merge regions with the same name: $this" }

    val r1 = this[0]
    val r2 = this.getOrNull(1)

    if (r2 == null) {
        // this output
        logger.debug("Region ${r1.name} has not any new rides since the previous dashboard creation.")
        return Region(r1.name,
            listOf(r1.rides[0], 0),
            listOf(r1.incidents[0], 0),
            listOf(r1.scaryIncidents[0], 0),
            listOf(r1.km[0], 0))
    } else {
        logger.debug("Region ${r1.name} has ${r2.rides[0]} new rides since the previous dashboard creation.")
        return Region(
            r1.name,
            listOf(r1.rides[0] + r2.rides[0]),
            listOf(r1.incidents[0] + r2.incidents[0]),
            listOf(r1.scaryIncidents[0] + r2.scaryIncidents[0]),
            listOf(r1.km[0] + r2.km[0])
        )
    }

}