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
import java.time.LocalDate
import java.time.format.FormatStyle
import kotlin.system.exitProcess

private val logger = LogManager.getLogger()

// TODO add total stat calculation

fun main(args: Array<String>) = runBlocking {
    val conf = mainBody { ArgParser(args).parseInto(::Conf) }

    logger.info("------------------------------------------------------")
    logger.info("Starting Data Parser")
    logger.info("------------------------------------------------------")
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
    logger.info("There are ${currentIndex.index.size} ride files, ${previousIndex.index.size} are found in previous index")

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
    val tmpDashboard =
        createDashboard(rides, LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)))
    val previousDashboard = readDashboardFromFile(previousDashboardFile)
    val currentDashboard = createNewTotalDashboard(previousDashboard, tmpDashboard)

    // read in dashboard.json for diff and determine degree of change
    getDiffDashboardFile(conf)?.let {
        val dateString = it.name.replace("-dashboard.json", "")
        val formatted = LocalDate.parse(dateString, DateTimeFormatter.ISO_DATE)
            .format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL))
        currentDashboard.updateDiffs(previousDashboard, formatted)
    }

    // write dashboard.json and index.txt
    currentIndex.saveIndex(getTodaysIndexFile(conf))
    currentDashboard.sort()
    currentDashboard.updateTotals()

    currentDashboard.saveDashboardJson(getTodaysDashboardFile(conf))
    currentDashboard.saveDashboardJson(conf.copyTo)

    if (currentDashboard.totalRides != currentIndex.index.size) {
        logger.error("Rides in dashboard (${currentDashboard.totalRides}) does not match files in index (${currentIndex.index.size})")
    }

    logger.info("------------------------------------------------------")
    logger.info("Terminating Data Parser")
    logger.info("------------------------------------------------------")
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

fun getTodaysIndexFile(conf: Conf): File {
    val currentDateTime = LocalDateTime.now()
    return File("${conf.outputDir.absolutePath}/${currentDateTime.format(DateTimeFormatter.ISO_DATE)}-index.txt")
}

fun getTodaysDashboardFile(conf: Conf): File {
    val currentDateTime = LocalDateTime.now()
    return File("${conf.outputDir.absolutePath}/${currentDateTime.format(DateTimeFormatter.ISO_DATE)}-dashboard.json")
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


fun getDiffDashboardFile(conf: Conf): File? {
    val targetDate = LocalDateTime.now().minusDays(7)
    var file = File("${conf.outputDir.absolutePath}/${targetDate.format(DateTimeFormatter.ISO_DATE)}-dashboard.json")

    if (!file.exists()) {
        getPreviousDashboardFile(conf)?.let { file = it } // if there is a previous dashboard file, use it for diff
    }

    return if (file.exists()) {
        logger.info("The diff dashboard is ${file.name}")
        file
    } else {
        logger.info("There is no dashboard that could be used to calculate diff")
        null
    }
}