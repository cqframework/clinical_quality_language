@file:Suppress("WildcardImport")

package org.cqframework.cql.cq2elm

import com.fasterxml.jackson.databind.ObjectMapper
import java.io.*
import java.util.*
import org.cqframework.cql.cql2elm.CqlTranslatorOptions

object CqlTranslatorOptionsMapper {
    private val om: ObjectMapper =
        ObjectMapper()
            .setMixIns(
                mapOf(CqlTranslatorOptions::class.java to CqlTranslatorOptionsMixin::class.java)
            )

    @JvmStatic
    fun fromFile(fileName: String?): CqlTranslatorOptions {
        var fr: FileReader? = null
        try {
            fr = FileReader(fileName)
            return fromReader(fr)
        } catch (@Suppress("SwallowedException") e: IOException) {
            @Suppress("TooGenericExceptionThrown")
            throw RuntimeException(
                String.format(Locale.US, "Errors occurred reading options: %s", e.message)
            )
        }
    }

    @JvmStatic
    fun fromReader(reader: Reader?): CqlTranslatorOptions {
        try {
            return om.readValue(reader, CqlTranslatorOptions::class.java)
        } catch (@Suppress("SwallowedException") e: IOException) {
            @Suppress("TooGenericExceptionThrown")
            throw RuntimeException(
                String.format(Locale.US, "Errors occurred reading options: %s", e.message)
            )
        }
    }

    @JvmStatic
    fun toFile(fileName: String?, options: CqlTranslatorOptions?) {
        var fw: FileWriter? = null
        try {
            fw = FileWriter(fileName)
            toWriter(fw, options)
        } catch (@Suppress("SwallowedException") e: IOException) {
            @Suppress("TooGenericExceptionThrown")
            throw RuntimeException(
                String.format(Locale.US, "Errors occurred writing options: %s", e.message)
            )
        }
    }

    @JvmStatic
    fun toWriter(writer: Writer?, options: CqlTranslatorOptions?) {
        val om: ObjectMapper = ObjectMapper()
        try {
            om.writeValue(writer, options)
        } catch (@Suppress("SwallowedException") e: IOException) {
            @Suppress("TooGenericExceptionThrown")
            throw RuntimeException(
                String.format(Locale.US, "Errors occurred writing options: %s", e.message)
            )
        }
    }
}
