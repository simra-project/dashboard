import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.mainBody
import dashboard.createDashboard
import dashboard.createNewTotalDashboard
import dashboard.readDashboardFromFile
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
    val previousDashboardFile = getPreviousDashboardFile(conf)
    val previousIndexFile = getPreviousIndexFile(conf)
    checkThatPreviousFilesAreFromSameDate(previousDashboardFile, previousIndexFile)

    // create index for current date and previous date
    val currentIndex = RideIndex()
    currentIndex.createIndexFromSourceFiles(conf.sourceFiles)
    val previousIndex = RideIndex()
    previousIndex.readIndexFromFile(previousIndexFile)
    logger.info("There are ${currentIndex.index.size} ride files, ${previousIndex.index.size} area found in previous index")

    // skip files that are present in previous index
    val rideFiles = currentIndex.getIndexDiff(previousIndex)

    // create ride objects for new files
    val coroutines = mutableListOf<Deferred<Ride?>>()
    val pb = ProgressBar("Processing Rides", rideFiles.size.toLong())

    for (path in rideFiles) {
        coroutines.add(async(Dispatchers.Default) {
            val ride = RideProcessor(File(path)).getRide()
            pb.step()
            ride
        })
    }
    val rides = coroutines.awaitAll().filterNotNull()
    pb.close()
    logger.info("Completed processing of ${rides.size} rides")

    // read in last dashboard.json and determine new totals
    val currentDashboard = createDashboard(rides)
    val previousDashboard = readDashboardFromFile(previousDashboardFile)
    val updatedDashboard = createNewTotalDashboard(previousDashboard, currentDashboard)

    // read in dashboard.json from 7-days ago and determine change

    // write dashboard.json and index.txt
    // TODO
    currentIndex.saveIndex(getTodaysIndexFile(conf))
    updatedDashboard.saveDashboardJson(getTodaysDashboardFile(conf))
}

fun checkThatPreviousFilesAreFromSameDate(previousDashboardFile: File?, previousIndexFile: File?) {
    if (previousDashboardFile == null) {
        check(previousIndexFile == null) { "There should be no index and no dashboard file" }
    }

    if (previousIndexFile == null) {
        check(previousDashboardFile == null) { "There should be no index and no dashboard file" }
    }

    if (previousIndexFile != null && previousDashboardFile != null) {
        check(
            previousDashboardFile.name.replace("-dashboard.json", "")
                    == previousIndexFile.name.replace("-index.txt", "")
        ) {
            "Previous dashboard and index file are not from the same date: ${previousDashboardFile.name}, ${previousIndexFile.name}"
        }
    }
}

fun getPreviousIndexFile(conf: Conf): File? {
    val allFiles = conf.outputDir.listFiles()?.toList() ?: emptyList<File>()

    val files = allFiles
        .filter { it.name.contains("index") }
        .filter { it.absolutePath != getTodaysIndexFile(conf).absolutePath }
        .sortedDescending()

    return if (files.isEmpty()) {
        logger.info("No previous index file exists, we have to process all rides.")
        null
    } else {
        logger.info("The previous index is from ${files.first()}")
        files.first()
    }
}

fun getPreviousDashboardFile(conf: Conf): File? {
    val allFiles = conf.outputDir.listFiles()?.toList() ?: emptyList<File>()

    val files = allFiles
        .filter { it.name.contains("dashboard") }
        .filter { it.absolutePath != getTodaysDashboardFile(conf).absolutePath }
        .sortedDescending()

    return if (files.isEmpty()) {
        logger.info("No previous dashboard file exists.")
        null
    } else {
        logger.info("The dashboard file is from ${files.first()}")
        files.first()
    }
}

fun getTodaysIndexFile(conf: Conf): File {
    val currentDateTime = LocalDateTime.now()
    return File("${conf.outputDir.absolutePath}/${currentDateTime.format(DateTimeFormatter.ISO_DATE)}-index.txt")
}

fun getTodaysDashboardFile(conf: Conf): File {
    val currentDateTime = LocalDateTime.now()
    return File("${conf.outputDir.absolutePath}/${currentDateTime.format(DateTimeFormatter.ISO_DATE)}-dashboard.json")
}