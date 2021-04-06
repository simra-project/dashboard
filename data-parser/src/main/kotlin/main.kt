import util.toIntPair
import java.io.File

fun main(args: Array<String>) {
    println("Hello World!")

    val profile = processProfile(File("../data/Freiburg/Profiles/VM2_165440750"))
    println(profile)
}

fun processProfile(file: File): Profile {
    require(file.exists()) { "File ${file.absolutePath} does not exist" }

    val lines = file.bufferedReader().readLines()
    check(lines.size == 3) { "Profile ${file.absolutePath} has more than 3 lines, has ${lines.size}" }

    val version = lines[0].split("#").toIntPair()
    val distanceIndex = lines[1].split(",").indexOf("distance")
    val distance = if (distanceIndex > -1) lines[2].split(",")[distanceIndex].toInt() else null

    if (version.first >= 24) {
        check(distance != null) { "For $version there should be a distance field, but there is none."}
    }

    return Profile(version, distance)
}
