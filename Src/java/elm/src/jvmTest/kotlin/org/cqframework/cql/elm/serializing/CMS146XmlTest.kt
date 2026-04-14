package org.cqframework.cql.elm.serializing

import java.io.File
import java.io.IOException
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.util.Scanner
import org.cqframework.cql.cql2elm.Cql2ElmContext
import org.cqframework.cql.cql2elm.CqlCompilerException
import org.cqframework.cql.cql2elm.CqlCompilerOptions
import org.cqframework.cql.cql2elm.CqlTranslator
import org.cqframework.cql.cql2elm.LibraryManager
import org.cqframework.cql.cql2elm.ModelManager
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.xmlunit.assertj.XmlAssert

internal class CMS146XmlTest {
    @ParameterizedTest
    @MethodSource("sigFileAndSigLevel")
    @Throws(IOException::class)
    fun cms146SignatureLevels(
        fileName: String,
        expectedSignatureLevel: Cql2ElmContext.SignatureLevel,
    ) {
        val expectedXml: String? = getXml(fileName)

        val cms146: File = getFile("CMS146v2_Test_CQM.cql")
        val modelManager = ModelManager()
        val translator =
            CqlTranslator.fromFile(
                cms146.path,
                LibraryManager(
                    modelManager,
                    CqlCompilerOptions(
                        CqlCompilerException.ErrorSeverity.Warning,
                        expectedSignatureLevel,
                    ),
                ),
            )
        // The compiler version changes release-to-release
        // so we strip it out of the XML before comparison
        val xmlWithVersion = translator.toXml().trim()
        val actualXml = xmlWithVersion.replace("translatorVersion=\"[^\"]*\"".toRegex(), "")

        XmlAssert.assertThat(actualXml).and(expectedXml).ignoreWhitespace().areIdentical()
    }

    companion object {
        @JvmStatic
        fun sigFileAndSigLevel(): Array<Array<Any>> {
            return arrayOf(
                arrayOf(
                    "CMS146v2_Expected_SignatureLevel_None.xml",
                    Cql2ElmContext.SignatureLevel.None,
                ),
                arrayOf(
                    "CMS146v2_Expected_SignatureLevel_Differing.xml",
                    Cql2ElmContext.SignatureLevel.Differing,
                ),
                arrayOf(
                    "CMS146v2_Expected_SignatureLevel_Overloads.xml",
                    Cql2ElmContext.SignatureLevel.Overloads,
                ),
                arrayOf(
                    "CMS146v2_Expected_SignatureLevel_All.xml",
                    Cql2ElmContext.SignatureLevel.All,
                ),
            )
        }

        @Throws(IOException::class)
        private fun getXml(name: String): String? {
            Scanner(getFile(name), StandardCharsets.UTF_8).useDelimiter("\\Z").use { scanner ->
                return scanner.next()
            }
        }

        private fun getFile(name: String): File {
            val resource = CMS146XmlTest::class.java.getResource(name)

            requireNotNull(resource) { "Cannot find file with name: $name" }

            return File(URLDecoder.decode(resource.file, StandardCharsets.UTF_8))
        }
    }
}
