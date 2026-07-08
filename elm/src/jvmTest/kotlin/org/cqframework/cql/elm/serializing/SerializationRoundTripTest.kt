package org.cqframework.cql.elm.serializing

import java.io.IOException
import org.cqframework.cql.elm.TestUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class SerializationRoundTripTest {
    @ParameterizedTest
    @MethodSource("dataMethod")
    @Throws(IOException::class)
    fun roundTrip(cqlFile: String) {
        val translator = TestUtils.createTranslator(pathForFile(cqlFile))
        assertEquals(0, translator.errors.size)

        val xml = translator.toXml()
        var library = xmlReader.read(xml)
        assertEquals(xml, xmlWriter.writeAsString(library))

        val json = translator.toJson()
        library = jsonReader.read(json)
        assertEquals(json, jsonWriter.writeAsString(library))
    }

    companion object {
        @JvmStatic
        fun dataMethod(): Array<String> {
            return arrayOf(
                "OperatorTests/ArithmeticOperators.cql",
                "OperatorTests/ComparisonOperators.cql",
                "OperatorTests/ListOperators.cql",
                "OperatorTests/Aggregate.cql",
                "SignatureTests/GenericOverloadsTests.cql",
            )
        }

        private fun pathForFile(cqlFile: String): String {
            return "../cql-to-elm/src/jvmTest/resources/org/cqframework/cql/cql2elm/$cqlFile"
        }

        private val xmlReader = ElmXmlLibraryReader()
        private val xmlWriter = ElmXmlLibraryWriter()
        private val jsonReader = ElmJsonLibraryReader()
        private val jsonWriter = ElmJsonLibraryWriter()
    }
}
