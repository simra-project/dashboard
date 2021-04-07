package de.hasenburg.broker.simulation.main

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.InvalidArgumentException
import com.xenomachina.argparser.default
import java.io.File

class Conf(parser: ArgParser) {
    val sourceFiles by parser
        .storing("-s", "--sources", help = "path to source files") { File(this) }
        .default(File("../data/"))
        .addValidator {
            if (!value.exists()) {
                throw InvalidArgumentException("${value.absolutePath} does not exist")
            }
            if (!value.isDirectory) {
                throw InvalidArgumentException("${value.absolutePath} is not a directory")
            }
        }

    val outputDir by parser
        .storing("-r", "--results", help = "path to dir in which results will be stored") { File(this) }
        .default(File("./results/"))
        .addValidator {
            if (!value.exists()) {
                throw InvalidArgumentException("${value.absolutePath} does not exist")
            }
            if (!value.isDirectory) {
                throw InvalidArgumentException("${value.absolutePath} is not a directory")
            }
        }

    val o by parser
        .storing("-o", "--overwrite", help = "whether today's files should be overwritten")
        .default(true)

    override fun toString(): String {
        return "Configuration: source files (${sourceFiles.absolutePath}), output directory (${outputDir.absolutePath}), today's file overwriting ($o)"
    }


}