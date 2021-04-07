package ride

import org.apache.logging.log4j.LogManager
import java.io.File

private val logger = LogManager.getLogger()

class RideIndex() {

    lateinit var index: Set<String>

    fun readIndexFromFile(indexFile: File) {
        require(indexFile.exists()) { "Cannot read index file since ${indexFile.absolutePath} does not exist" }
        index = indexFile.readLines().toSet()
    }

    fun saveIndex(indexFile: File) {
        indexFile.writeText(index.joinToString("\n"))
        logger.info("Saved index to ${indexFile.absolutePath}")
    }

    fun createIndexFromSourceFiles(rootDir: File) {
        require(rootDir.isDirectory) { "Cannot create index from source files since directory ${rootDir.absolutePath} does not exist" }
        index = rootDir.walk().toList()
            .map { it.absolutePath }
            .filter { it.contains("Rides/VM") }
            .toSet()
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