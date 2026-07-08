package org.cqframework.cql.elm.serializing

import java.io.IOException
import java.io.StringReader
import java.nio.file.Path
import javax.xml.XMLConstants
import javax.xml.transform.stream.StreamSource
import javax.xml.validation.SchemaFactory
import javax.xml.validation.Validator
import org.cqframework.cql.cql2elm.CqlTranslator
import org.cqframework.cql.elm.TestUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.xml.sax.SAXException

internal class XSDValidationTest {
    @Test
    fun ensureValidatorFailsForBadXml() {
        val xml = "<library xmlns=\"urn:hl7-org:elm:r1\"></library>"
        val source = StreamSource(StringReader(xml))
        Assertions.assertThrows(SAXException::class.java) { validator.validate(source) }
    }

    @ParameterizedTest
    @MethodSource("dataMethod")
    @Throws(IOException::class)
    fun validateXML(cqlFile: String) {
        val path: String = pathForFile(cqlFile)
        val t = TestUtils.createTranslator(path)
        assertTrue(t.errors.isEmpty())

        val xml = CqlTranslator.convertToXml(t.toELM()!!)
        assertTrue(validateXMLAgainstXSD(cqlFile, xml))
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(XSDValidationTest::class.java)

        @JvmStatic
        fun dataMethod(): Array<String> {
            return arrayOf(
                "OperatorTests/ArithmeticOperators.cql",
                "OperatorTests/ComparisonOperators.cql",
                "OperatorTests/ListOperators.cql",
            )
        }

        private val validator: Validator = createValidator()

        @Suppress("TooGenericExceptionThrown")
        private fun createValidator(): Validator {
            try {
                val schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
                val schema =
                    schemaFactory.newSchema(Path.of("../../cql-lm/schema/elm/library.xsd").toFile())
                return schema.newValidator()
            } catch (e: SAXException) {
                throw RuntimeException(e)
            }
        }

        private fun pathForFile(cqlFile: String): String {
            return "../cql-to-elm/src/jvmTest/resources/org/cqframework/cql/cql2elm/$cqlFile"
        }

        private fun validateXMLAgainstXSD(cqlFile: String?, xml: String): Boolean {
            try {
                validator.validate(StreamSource(StringReader(xml)))
                return true
            } catch (e: IOException) {
                logger.error("error validating XML against XSD for file {}", cqlFile, e)
            } catch (e: SAXException) {
                logger.error("error validating XML against XSD for file {}", cqlFile, e)
            }
            return false
        }
    }
}
