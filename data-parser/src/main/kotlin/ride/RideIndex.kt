package ride

import org.apache.logging.log4j.LogManager
import java.io.File

private val logger = LogManager.getLogger()

class RideIndex {

    /**
     * Contains the absolute path for indexed files.
     */
    lateinit var index: Set<String>

    fun readIndexFromFile(indexFile: File?) {
        if (indexFile == null) {
            logger.debug("Given index file does not exist, creating empty index.")
            index = emptySet()
            return
        }
        index = indexFile.readLines().toSet()
    }

    fun saveIndex(indexFile: File) {
        indexFile.writeText(index.joinToString("\n"))
        logger.info("Saved index to ${indexFile.absolutePath}")
    }

    fun createIndexFromSourceFiles(rootDir: File) {
        require(rootDir.isDirectory) { "Cannot create index from source files since directory ${rootDir.absolutePath} does not exist" }
        index = rootDir.walk().toList()
            .filter { it.length() > 0L }
            .map { it.absolutePath }
            .filter { it.contains("/Rides/") }
            .filter { it.contains("VM") }
            .toSet()
    }

    fun getIndexDiff(otherIndex: RideIndex): Set<String> {
        return index subtract otherIndex.index
    }

}

fun main() {
    val ri = RideIndex()
    ri.createIndexFromSourceFiles(File("../data/"))
    println(ri.index)
    ri.saveIndex(File("./index.txt"))
    val ri2 = RideIndex()
    ri2.readIndexFromFile(File("./index.txt"))

    println(ri.index == ri2.index)
}