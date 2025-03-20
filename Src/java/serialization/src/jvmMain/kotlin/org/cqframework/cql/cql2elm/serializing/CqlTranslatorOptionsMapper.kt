package org.cqframework.cql.cql2elm.serializing

import org.cqframework.cql.cql2elm.CqlTranslatorOptions
import org.cqframework.cql.elm.serializing.readTranslatorOptionsFromJsonString
import org.cqframework.cql.elm.serializing.writeTranslatorOptionsToJsonString

object CqlTranslatorOptionsMapper {

    @JvmStatic
    fun fromFile(fileName: String): CqlTranslatorOptions {
        try {
            val fr = java.io.FileReader(fileName)
            return fromReader(fr)
        } catch (e: java.io.IOException) {
            throw java.lang.RuntimeException(
                String.format("Errors occurred reading options: %s", e.message)
            )
        }
    }

    @JvmStatic
    fun fromReader(reader: java.io.Reader): CqlTranslatorOptions {
        try {
            return readTranslatorOptionsFromJsonString(reader.readText())
        } catch (e: java.io.IOException) {
            throw java.lang.RuntimeException(
                String.format("Errors occurred reading options: %s", e.message)
            )
        }
    }

    @JvmStatic
    fun toFile(fileName: String, options: CqlTranslatorOptions) {
        try {
            val fw = java.io.FileWriter(fileName)
            toWriter(fw, options)
        } catch (e: java.io.IOException) {
            throw java.lang.RuntimeException(
                String.format("Errors occurred writing options: %s", e.message)
            )
        }
    }

    @JvmStatic
    fun toWriter(writer: java.io.Writer, options: CqlTranslatorOptions) {
        try {
            writer.write(writeTranslatorOptionsToJsonString(options))
        } catch (e: java.io.IOException) {
            throw java.lang.RuntimeException(
                String.format("Errors occurred writing options: %s", e.message)
            )
        }
    }
}
