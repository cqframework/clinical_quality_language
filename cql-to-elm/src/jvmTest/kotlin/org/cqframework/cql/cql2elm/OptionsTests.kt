package org.cqframework.cql.cql2elm

import java.io.IOException
import kotlinx.io.Buffer
import kotlinx.io.asSource
import kotlinx.io.buffered
import kotlinx.io.readString
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class OptionsTests {
    @Test
    @Throws(IOException::class)
    fun translatorOptions() {
        val options = CqlTranslatorOptions.defaultOptions()
        val buffer = Buffer()
        options.toSink(buffer)
        val result = buffer.readString()
        Assertions.assertNotNull(result)

        val input = OptionsTests::class.java.getResourceAsStream("options.json")
        val readOptions = CqlTranslatorOptions.fromSource(input!!.asSource().buffered())

        val buffer2 = Buffer()
        readOptions.toSink(buffer2)
        val result2 = buffer2.readString()
        Assertions.assertEquals(result, result2)
    }
}
