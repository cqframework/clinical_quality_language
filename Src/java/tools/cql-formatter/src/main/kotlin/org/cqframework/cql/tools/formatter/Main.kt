package org.cqframework.cql.tools.formatter

import java.io.FileInputStream
import java.io.IOException

/** A simple wrapper around the ANTLR4 testrig. */
@Suppress("MemberNameEqualsClassName")
object Main {
    @Throws(IOException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        var inputFile: String? = null
        if (args.isNotEmpty()) {
            inputFile = args[0]
        }
        var `is` = System.`in`
        if (inputFile != null) {
            `is` = FileInputStream(inputFile)
        }

        try {
            val result = CqlFormatterVisitor.getFormattedOutput(`is`)
            if (result.errors.isNotEmpty()) {
                for (ex in result.errors) {
                    println(ex.message)
                }
            } else {
                print(result.output)
            }
        } finally {
            if (`is` !== System.`in`) {
                try {
                    `is`.close()
                } catch (_: IOException) {
                    // intentional noop
                }
            }
        }
    }
}
