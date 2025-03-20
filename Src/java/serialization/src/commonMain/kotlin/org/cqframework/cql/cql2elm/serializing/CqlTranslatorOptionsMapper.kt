package org.cqframework.cql.cql2elm.serializing
//
//
// object CqlTranslatorOptionsMapper {
//    private val om: ObjectMapper = ObjectMapper()
//
//    fun fromFile(fileName: String): CqlTranslatorOptions {
//        var fr: java.io.FileReader? = null
//        try {
//            fr = java.io.FileReader(fileName)
//            return fromReader(fr)
//        } catch (e: java.io.IOException) {
//            throw java.lang.RuntimeException(String.format("Errors occurred reading options: %s",
// e.message))
//        }
//    }
//
//    fun fromReader(reader: java.io.Reader?): CqlTranslatorOptions {
//        try {
//            return om.readValue(
//                reader,
//                CqlTranslatorOptions::class.java
//            )
//        } catch (e: java.io.IOException) {
//            throw java.lang.RuntimeException(String.format("Errors occurred reading options: %s",
// e.message))
//        }
//    }
//
//    fun toFile(fileName: String, options: CqlTranslatorOptions?) {
//        var fw: java.io.FileWriter? = null
//        try {
//            fw = java.io.FileWriter(fileName)
//            toWriter(fw, options)
//        } catch (e: java.io.IOException) {
//            throw java.lang.RuntimeException(String.format("Errors occurred writing options: %s",
// e.message))
//        }
//    }
//
//    fun toWriter(writer: java.io.Writer?, options: CqlTranslatorOptions?) {
//        val om: ObjectMapper = ObjectMapper()
//        try {
//            om.writeValue(writer, options)
//        } catch (e: java.io.IOException) {
//            throw java.lang.RuntimeException(String.format("Errors occurred writing options: %s",
// e.message))
//        }
//    }
// }
