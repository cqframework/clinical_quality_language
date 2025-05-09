@file:Suppress("ImplicitDefaultLocale", "SwallowedException", "TooGenericExceptionThrown")

package org.cqframework.cql.cql2elm

import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.io.Reader
import java.io.Writer
import java.lang.RuntimeException

object CqlTranslatorOptionsMapper {

    @JvmStatic
    fun fromFile(fileName: String): CqlTranslatorOptions {
        try {
            val fr = FileReader(fileName)
            return fromReader(fr)
        } catch (e: IOException) {
            throw RuntimeException(String.format("Errors occurred reading options: %s", e.message))
        }
    }

    @JvmStatic
    fun fromReader(reader: Reader): CqlTranslatorOptions {
        try {
            return readTranslatorOptionsFromJsonString(reader.readText())
        } catch (e: IOException) {
            throw RuntimeException(String.format("Errors occurred reading options: %s", e.message))
        }
    }

    @JvmStatic
    fun toFile(fileName: String, options: CqlTranslatorOptions) {
        try {
            val fw = FileWriter(fileName)
            toWriter(fw, options)
        } catch (e: IOException) {
            throw RuntimeException(String.format("Errors occurred writing options: %s", e.message))
        }
    }

    @JvmStatic
    fun toWriter(writer: Writer, options: CqlTranslatorOptions) {
        try {
            writer.write(writeTranslatorOptionsToJsonString(options))
        } catch (e: IOException) {
            throw RuntimeException(String.format("Errors occurred writing options: %s", e.message))
        }
    }
}
