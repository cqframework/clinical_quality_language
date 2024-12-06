@file:Suppress("WildcardImport")

package org.cqframework.cql.cql2elm

import com.fasterxml.jackson.databind.ObjectMapper
import java.io.*

object CqlTranslatorOptionsMapper {
    private val om: ObjectMapper = ObjectMapper()

    @JvmStatic
    fun fromFile(fileName: String?): CqlTranslatorOptions {
        var fr: FileReader? = null
        try {
            fr = FileReader(fileName)
            return fromReader(fr)
        } catch (@Suppress("SwallowedException") e: IOException) {
            @Suppress("TooGenericExceptionThrown", "ImplicitDefaultLocale")
            throw RuntimeException(String.format("Errors occurred reading options: %s", e.message))
        }
    }

    @JvmStatic
    fun fromReader(reader: Reader?): CqlTranslatorOptions {
        try {
            return om.readValue(reader, CqlTranslatorOptions::class.java)
        } catch (@Suppress("SwallowedException") e: IOException) {
            @Suppress("TooGenericExceptionThrown", "ImplicitDefaultLocale")
            throw RuntimeException(String.format("Errors occurred reading options: %s", e.message))
        }
    }

    @JvmStatic
    fun toFile(fileName: String?, options: CqlTranslatorOptions?) {
        var fw: FileWriter? = null
        try {
            fw = FileWriter(fileName)
            toWriter(fw, options)
        } catch (@Suppress("SwallowedException") e: IOException) {
            @Suppress("TooGenericExceptionThrown", "ImplicitDefaultLocale")
            throw RuntimeException(String.format("Errors occurred writing options: %s", e.message))
        }
    }

    @JvmStatic
    fun toWriter(writer: Writer?, options: CqlTranslatorOptions?) {
        val om: ObjectMapper = ObjectMapper()
        try {
            om.writeValue(writer, options)
        } catch (@Suppress("SwallowedException") e: IOException) {
            @Suppress("TooGenericExceptionThrown", "ImplicitDefaultLocale")
            throw RuntimeException(String.format("Errors occurred writing options: %s", e.message))
        }
    }
}
