import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.*
import me.tongfei.progressbar.ProgressBar


val dataDir = "../data"
val outputDir = "./out"

fun main(args: Array<String>) = runBlocking {
    println("Hello World!")

    // check if there is an index.txt and a dashboard.json for the current date -> abort if yes
    require(!getTodaysIndexFile().exists()) { "There already is an index file for today, aborting" }
    require(!getTodaysDashboardFile().exists()) { "There already is a dashboard file for today, aborting" }

    // create index for current date
    val ri = RideIndex()
    ri.createIndexFromSourceFiles(File(dataDir))

    // calculate file diffs for index and last index
    // TODO

    // create ride objects for new files
    val coroutines = mutableListOf<Deferred<Ride?>>()
    val pb = ProgressBar("Rides", ri.index.size.toLong())

    for (path in ri.index) {
        coroutines.add(async(Dispatchers.Default) {
            val ride = RideProcessor(File(path)).getRide()
            pb.step()
            ride
        })
    }
    val rides = coroutines.awaitAll().filterNotNull()
    println(rides)

    // read in last dashboard.json and determine new totals

    // read in dashboard.json from 7-days ago and determine change

    // write dashboard.json and index.txt
    // TODO
    // ri.saveIndex(getTodaysIndexFile())
}

fun getTodaysIndexFile(): File {
    val currentDateTime = LocalDateTime.now()
    return File("$outputDir/${currentDateTime.format(DateTimeFormatter.ISO_DATE)}-index.txt")
}

fun getTodaysDashboardFile(): File {
    val currentDateTime = LocalDateTime.now()
    return File("$outputDir/${currentDateTime.format(DateTimeFormatter.ISO_DATE)}-dashboard.json")
}