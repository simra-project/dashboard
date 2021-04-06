/**
 * The distance field only started to exist with version 24.
 * It is given in meter.
 */
data class Profile(val version: Pair<Int, Int>, val distance: Int?)
