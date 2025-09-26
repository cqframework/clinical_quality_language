package org.cqframework.cql.elm

import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.net.URISyntaxException
import java.util.EnumSet
import java.util.stream.Collectors
import kotlinx.io.asSource
import kotlinx.io.buffered
import org.cqframework.cql.cql2elm.CompilerOptions
import org.cqframework.cql.cql2elm.CqlCompilerOptions
import org.cqframework.cql.cql2elm.CqlIncludeException
import org.cqframework.cql.cql2elm.LibraryBuilder
import org.cqframework.cql.elm.serializing.ElmJsonLibraryReader
import org.cqframework.cql.elm.serializing.ElmJsonLibraryWriter
import org.cqframework.cql.elm.serializing.ElmXmlLibraryReader
import org.cqframework.cql.elm.serializing.ElmXmlLibraryWriter
import org.hl7.cql_annotations.r1.Annotation
import org.hl7.cql_annotations.r1.CqlToElmError
import org.hl7.cql_annotations.r1.CqlToElmInfo
import org.hl7.cql_annotations.r1.ErrorSeverity
import org.hl7.cql_annotations.r1.ErrorType
import org.hl7.cql_annotations.r1.Narrative
import org.hl7.elm.r1.Library
import org.hl7.elm.r1.Literal
import org.hl7.elm.r1.Null
import org.hl7.elm.r1.Query
import org.hl7.elm.r1.Retrieve
import org.hl7.elm.r1.SingletonFrom
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

@Suppress(
    "CyclomaticComplexMethod",
    "TooGenericExceptionThrown",
    "PrintStackTrace",
    "SwallowedException",
    "ImplicitDefaultLocale"
)
internal class ElmDeserializeTests {
    @Test
    fun elmTests() {
        try {
            deserializeXmlLibrary("ElmDeserialize/ElmTests.xml")
        } catch (e: IOException) {
            e.printStackTrace()
            throw IllegalArgumentException("Error reading ELM: " + e.message)
        }
    }

    @Test
    fun jsonANCFHIRDummyLibraryLoad() {
        try {
            val library: Library = deserializeJsonLibrary("ElmDeserialize/ANCFHIRDummy.json")
            assertNotNull(library)

            val translatorOptions =
                EnumSet.of(
                    CqlCompilerOptions.Options.EnableDateRangeOptimization,
                    CqlCompilerOptions.Options.EnableAnnotations,
                    CqlCompilerOptions.Options.EnableLocators,
                    CqlCompilerOptions.Options.EnableResultTypes,
                    CqlCompilerOptions.Options.DisableListDemotion,
                    CqlCompilerOptions.Options.DisableListPromotion,
                    CqlCompilerOptions.Options.DisableMethodInvocation
                )

            assertEquals(CompilerOptions.getCompilerOptions(library), translatorOptions)

            assertNotNull(library.statements)
            assertNotNull(library.statements!!.def)
            assertTrue(library.statements!!.def.size >= 2)
            assertNotNull(library.statements!!.def[0])
            assertTrue(library.statements!!.def[0].expression is SingletonFrom)
            assertTrue(
                (library.statements!!.def[0].expression as SingletonFrom).operand is Retrieve
            )
            val observationsStatement = library.statements!!.def[1]
            assertNotNull(observationsStatement)
            assertTrue(observationsStatement.expression is Retrieve)

            assertTrue(observationsStatement.annotation[0] is Annotation)
            val annotation = observationsStatement.annotation[0] as Annotation
            assertNotNull(annotation.s)
            val narrative = annotation.s
            assertTrue(narrative!!.content[1] is Narrative)
            var nestedNarrative = narrative.content[1] as Narrative
            assertTrue(nestedNarrative.content[0] is Narrative)
            nestedNarrative = nestedNarrative.content[0] as Narrative
            assertTrue(nestedNarrative.content[0] is Narrative)
            nestedNarrative = nestedNarrative.content[0] as Narrative
            assertEquals("[", nestedNarrative.content[0])

            verifySigLevels(library, LibraryBuilder.SignatureLevel.All)
        } catch (e: IOException) {
            throw IllegalArgumentException("Error reading ELM: " + e.message)
        }
    }

    @Test
    fun jsonAdultOutpatientEncountersFHIR4LibraryLoad() {
        try {
            val library: Library =
                deserializeJsonLibrary(
                    "ElmDeserialize/fhir/AdultOutpatientEncounters_FHIR4-2.0.000.json"
                )
            assertNotNull(library)

            val translatorOptions = EnumSet.of(CqlCompilerOptions.Options.EnableAnnotations)
            assertEquals(CompilerOptions.getCompilerOptions(library), translatorOptions)
            assertEquals("AdultOutpatientEncounters_FHIR4", library.identifier!!.id)
            assertEquals("2.0.000", library.identifier!!.version)
            assertNotNull(library.usings)
            assertNotNull(library.usings!!.def)
            assertTrue(library.usings!!.def.size >= 2)
            assertNotNull(library.statements)
            assertNotNull(library.statements!!.def)
            assertNotNull(library.statements!!.def[0])
            assertTrue(library.statements!!.def[0].expression is SingletonFrom)
            assertTrue(
                (library.statements!!.def[0].expression as SingletonFrom).operand is Retrieve
            )
            assertEquals("Qualifying Encounters", library.statements!!.def[1].name)
            assertNotNull(library.statements!!.def[1])
            assertTrue(library.statements!!.def[1].expression is Query)

            verifySigLevels(library, LibraryBuilder.SignatureLevel.Differing)
        } catch (e: IOException) {
            throw IllegalArgumentException("Error reading ELM: " + e.message)
        }
    }

    @Test
    fun xmlLibraryLoad() {
        try {
            val library: Library =
                deserializeXmlLibrary(
                    "ElmDeserialize/fhir/AdultOutpatientEncounters_FHIR4-2.0.000.xml"
                )
            assertNotNull(library)
            assertEquals("AdultOutpatientEncounters_FHIR4", library.identifier!!.id)
            assertEquals("2.0.000", library.identifier!!.version)

            val translatorOptions =
                EnumSet.of(
                    CqlCompilerOptions.Options.EnableDateRangeOptimization,
                    CqlCompilerOptions.Options.EnableAnnotations,
                    CqlCompilerOptions.Options.EnableLocators,
                    CqlCompilerOptions.Options.EnableResultTypes,
                    CqlCompilerOptions.Options.DisableListDemotion,
                    CqlCompilerOptions.Options.DisableListPromotion,
                    CqlCompilerOptions.Options.DisableMethodInvocation
                )
            assertEquals(CompilerOptions.getCompilerOptions(library), translatorOptions)

            assertNotNull(library.usings)
            assertNotNull(library.usings!!.def)
            assertTrue(library.usings!!.def.size >= 2)
            assertNotNull(library.statements)
            assertNotNull(library.statements!!.def)
            assertNotNull(library.statements!!.def[0])
            assertTrue(library.statements!!.def[0].expression is SingletonFrom)
            assertTrue(
                (library.statements!!.def[0].expression as SingletonFrom).operand is Retrieve
            )
            val qualifyingEncountersStatement = library.statements!!.def[1]
            assertEquals("Qualifying Encounters", qualifyingEncountersStatement.name)
            assertNotNull(qualifyingEncountersStatement)
            assertTrue(qualifyingEncountersStatement.expression is Query)
            assertTrue(qualifyingEncountersStatement.annotation[0] is Annotation)
            val annotation = qualifyingEncountersStatement.annotation[0] as Annotation
            assertNotNull(annotation.s)
            val narrative = annotation.s
            assertEquals("\n               ", narrative!!.content[0])
            assertTrue(narrative.content[3] is Narrative)
            val nestedNarrative = narrative.content[3] as Narrative
            assertEquals("\n                  ", nestedNarrative.content[0])
            assertTrue(nestedNarrative.content[1] is Narrative)

            verifySigLevels(library, LibraryBuilder.SignatureLevel.Overloads)
        } catch (e: IOException) {
            e.printStackTrace()
            throw IllegalArgumentException("Error reading ELM: " + e.message)
        }
    }

    @Test
    fun jsonTerminologyLibraryLoad() {
        try {
            val library: Library =
                deserializeJsonLibrary("ElmDeserialize/ANCFHIRTerminologyDummy.json")
            assertNotNull(library)

            verifySigLevels(library, LibraryBuilder.SignatureLevel.None)
        } catch (e: IOException) {
            throw IllegalArgumentException("Error reading ELM: " + e.message)
        }
    }

    private fun testElmDeserialization(path: String?, xmlFileName: String?, jsonFileName: String?) {
        var xmlLibrary: Library?
        try {
            xmlLibrary =
                ElmXmlLibraryReader()
                    .read(FileInputStream("$path/$xmlFileName").asSource().buffered())
        } catch (e: Exception) {
            throw IllegalArgumentException(
                String.format("Errors occurred reading ELM from xml %s: %s", xmlFileName, e.message)
            )
        }

        val jsonLibrary: Library?
        try {
            jsonLibrary =
                ElmJsonLibraryReader()
                    .read(FileInputStream("$path/$jsonFileName").asSource().buffered())
        } catch (e: Exception) {
            throw IllegalArgumentException(
                String.format(
                    "Errors occurred reading ELM from json %s: %s",
                    jsonFileName,
                    e.message
                )
            )
        }

        if (!equivalent(xmlLibrary, jsonLibrary)) {
            println(xmlFileName)
        }
        assertTrue(equivalent(xmlLibrary, jsonLibrary))
    }

    @Throws(URISyntaxException::class)
    private fun testElmDeserialization(directoryName: String?) {
        val dirURL =
            ElmDeserializeTests::class
                .java
                .getResource(String.format("ElmDeserialize/%s/", directoryName))
        val file = File(dirURL!!.toURI())
        for (fileName in file.list()!!) {
            if (fileName.endsWith(".xml")) {
                try {
                    testElmDeserialization(
                        file.absolutePath,
                        fileName,
                        fileName.dropLast(4) + ".json"
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    throw IllegalArgumentException(
                        String.format("Errors occurred testing: %s", fileName)
                    )
                }
            }
        }
    }

    @Test
    @Throws(URISyntaxException::class)
    fun regressionTestJsonSerializer() {
        // This test validates that the ELM library deserialized from the Json matches the ELM
        // library deserialized from
        // Xml
        // Regression inputs are annual update measure Xml for QDM and FHIR
        testElmDeserialization("qdm")
        testElmDeserialization("fhir")
        testElmDeserialization("qdm2020")
    }

    private fun validateEmptyStringsTest(library: Library) {
        // Null
        // Empty
        // Space
        for (ed in library.statements!!.def) {
            when (ed.name) {
                "Null" -> assertTrue(ed.expression is Null)
                "Empty" -> {
                    assertTrue(ed.expression is Literal)
                    val l = ed.expression as Literal?
                    assertTrue(l!!.value != null && l.value.equals(""))
                }
                "Space" -> {
                    assertTrue(ed.expression is Literal)
                    val l = ed.expression as Literal?
                    assertTrue(l!!.value != null && l.value.equals(" "))
                }
            }
        }
    }

    private fun toXml(library: Library): String {
        return ElmXmlLibraryWriter().writeAsString(library)
    }

    private fun toJson(library: Library): String {
        return ElmJsonLibraryWriter().writeAsString(library)
    }

    @Test
    @Throws(IOException::class)
    fun emptyStringsTest() {
        val inputStream =
            ElmDeserializeTests::class
                .java
                .getResourceAsStream("ElmDeserialize/EmptyStringsTest.cql")
        val translator = TestUtils.createTranslatorFromStream(inputStream!!)
        assertEquals(0, translator.errors.size)

        val xml = toXml(translator.toELM()!!)

        val xmlLibrary = ElmXmlLibraryReader().read(xml)
        validateEmptyStringsTest(xmlLibrary)

        val json = toJson(translator.toELM()!!)
        val jsonLibrary = ElmJsonLibraryReader().read(json)
        validateEmptyStringsTest(jsonLibrary)
    }

    @Test
    @Throws(IOException::class)
    fun missingIncludeCreatesCqlToElmError() {
        val inputStream =
            ElmDeserializeTests::class
                .java
                .getResourceAsStream("ElmDeserialize/MissingIncludedLibraryTest.cql")
        val translator = TestUtils.createTranslatorFromStream(inputStream!!)
        assertEquals(1, translator.errors.size)

        val error = translator.errors[0]
        val e = assertInstanceOf(CqlIncludeException::class.java, error)
        assertEquals("MissingLibrary", e.libraryId)
        assertEquals("1.0.1", e.versionId)

        val library = translator.toELM()!!
        assertEquals(2, library.annotation.size)
        val a = library.annotation[1]
        val cqlToElmError = assertInstanceOf(CqlToElmError::class.java, a)
        assertEquals(ErrorSeverity.ERROR, cqlToElmError.errorSeverity)
        assertEquals(ErrorType.INCLUDE, cqlToElmError.errorType)
        assertEquals("MissingLibrary", cqlToElmError.targetIncludeLibraryId)
        assertEquals("1.0.1", cqlToElmError.targetIncludeLibraryVersionId)
    }

    companion object {
        private fun equivalent(xmlLibrary: Library?, jsonLibrary: Library?): Boolean {
            if (xmlLibrary == null && jsonLibrary == null) {
                return true
            }

            var result = true

            if (xmlLibrary != null) {
                result = result && xmlLibrary.identifier!! == jsonLibrary!!.identifier
            }

            if (xmlLibrary!!.includes != null) {
                result =
                    result && (xmlLibrary.includes!!.def.size == jsonLibrary!!.includes!!.def.size)
            }

            if (xmlLibrary.usings != null) {
                result = result && (xmlLibrary.usings!!.def.size == jsonLibrary!!.usings!!.def.size)
            }

            if (xmlLibrary.valueSets != null) {
                result =
                    result &&
                        (xmlLibrary.valueSets!!.def.size == jsonLibrary!!.valueSets!!.def.size)
            }

            if (xmlLibrary.codeSystems != null) {
                result =
                    result &&
                        (xmlLibrary.codeSystems!!.def.size == jsonLibrary!!.codeSystems!!.def.size)
            }

            if (xmlLibrary.codes != null) {
                result = result && (xmlLibrary.codes!!.def.size == jsonLibrary!!.codes!!.def.size)
            }

            if (xmlLibrary.concepts != null) {
                result =
                    result && (xmlLibrary.concepts!!.def.size == jsonLibrary!!.concepts!!.def.size)
            }

            if (xmlLibrary.parameters != null) {
                result =
                    result &&
                        (xmlLibrary.parameters!!.def.size == jsonLibrary!!.parameters!!.def.size)
            }

            if (xmlLibrary.statements != null) {
                result =
                    result &&
                        (xmlLibrary.statements!!.def.size == jsonLibrary!!.statements!!.def.size)
            }

            if (xmlLibrary.contexts != null) {
                result =
                    result && (xmlLibrary.contexts!!.def.size == jsonLibrary!!.contexts!!.def.size)
            }

            return result
        }

        @Throws(IOException::class)
        private fun deserializeJsonLibrary(filePath: String): Library {
            val resourceAsStream = ElmDeserializeTests::class.java.getResourceAsStream(filePath)
            assertNotNull(resourceAsStream)
            return ElmJsonLibraryReader().read(resourceAsStream!!.asSource().buffered())
        }

        @Throws(IOException::class)
        private fun deserializeXmlLibrary(filePath: String): Library {
            val resourceAsStream = ElmDeserializeTests::class.java.getResourceAsStream(filePath)
            assertNotNull(resourceAsStream)
            return ElmXmlLibraryReader().read(resourceAsStream!!.asSource().buffered())
        }

        private fun verifySigLevels(
            library: Library,
            expectedSignatureLevel: LibraryBuilder.SignatureLevel
        ) {
            val sigLevels =
                library.annotation
                    .stream()
                    .filter { obj: Any? -> CqlToElmInfo::class.java.isInstance(obj) }
                    .map { obj: Any? -> CqlToElmInfo::class.java.cast(obj) }
                    .map(CqlToElmInfo::signatureLevel)
                    .collect(Collectors.toList())

            Assertions.assertEquals(1, sigLevels.size)
            assertEquals(sigLevels[0], expectedSignatureLevel.name)
        }
    }
}
