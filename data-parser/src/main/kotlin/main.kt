import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.mainBody
import dashboard.createDashboard
import de.hasenburg.broker.simulation.main.Conf
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.*
import me.tongfei.progressbar.ProgressBar
import org.apache.logging.log4j.LogManager
import ride.RideIndex
import ride.RideProcessor
import kotlin.system.exitProcess

private val logger = LogManager.getLogger()

// TODO add total stat calculation

fun main(args: Array<String>) = runBlocking {
    val conf = mainBody { ArgParser(args).parseInto(::Conf) }
    logger.info(conf.toString())

    if (conf.o == false) {
        if (getTodaysIndexFile(conf).exists() || getTodaysDashboardFile(conf).exists()) {
            logger.info("Today's files already exists and shall not be overwritten, shutting down")
            exitProcess(0)
        }
    }

    // create index for current date
    val ri = RideIndex()
    ri.createIndexFromSourceFiles(conf.sourceFiles)

    // calculate file diffs for index and last index
    // TODO

    // create ride objects for new files
    val coroutines = mutableListOf<Deferred<Ride?>>()
    val pb = ProgressBar("Reading Rides", ri.index.size.toLong())

    for (path in ri.index) {
        coroutines.add(async(Dispatchers.Default) {
            val ride = RideProcessor(File(path)).getRide()
            pb.step()
            ride
        })
    }
    val rides = coroutines.awaitAll().filterNotNull()
    pb.close()
    logger.info("Completed reading ${rides.size} files")

    // read in last dashboard.json and determine new totals
    // TODO consider last dashboard
    val dashboard = createDashboard(rides)

    // read in dashboard.json from 7-days ago and determine change

    // write dashboard.json and index.txt
    // TODO
    ri.saveIndex(getTodaysIndexFile(conf))
    dashboard.saveDashboardJson(getTodaysDashboardFile(conf))
}

fun getTodaysIndexFile(conf: Conf): File {
    val currentDateTime = LocalDateTime.now()
    return File("${conf.outputDir.absolutePath}/${currentDateTime.format(DateTimeFormatter.ISO_DATE)}-index.txt")
}

fun getTodaysDashboardFile(conf: Conf): File {
    val currentDateTime = LocalDateTime.now()
    return File("${conf.outputDir.absolutePath}/${currentDateTime.format(DateTimeFormatter.ISO_DATE)}-dashboard.json")
}