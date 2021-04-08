package dashboard

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