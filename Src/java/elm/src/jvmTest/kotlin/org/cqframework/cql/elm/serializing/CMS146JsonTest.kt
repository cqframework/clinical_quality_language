package org.cqframework.cql.elm.serializing

import java.io.File
import java.io.IOException
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.util.Scanner
import org.cqframework.cql.cql2elm.CqlCompilerException
import org.cqframework.cql.cql2elm.CqlCompilerOptions
import org.cqframework.cql.cql2elm.CqlTranslator
import org.cqframework.cql.cql2elm.LibraryBuilder
import org.cqframework.cql.cql2elm.LibraryManager
import org.cqframework.cql.cql2elm.ModelManager
import org.json.JSONException
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.skyscreamer.jsonassert.JSONAssert

internal class CMS146JsonTest {
    @ParameterizedTest
    @MethodSource("sigFileAndSigLevel")
    @Throws(IOException::class, JSONException::class)
    fun cms146SignatureLevels(
        fileName: String,
        expectedSignatureLevel: LibraryBuilder.SignatureLevel
    ) {
        val expectedJson: String? = getJson(fileName)

        val cms146: File = getFile("CMS146v2_Test_CQM.cql")
        val modelManager = ModelManager()
        val translator =
            CqlTranslator.fromFile(
                cms146.path,
                LibraryManager(
                    modelManager,
                    CqlCompilerOptions(
                        CqlCompilerException.ErrorSeverity.Warning,
                        expectedSignatureLevel
                    )
                )
            )
        val jsonWithVersion = translator.toJson()
        val actualJson = jsonWithVersion.replace("\"translatorVersion\":\"[^\"]*\",".toRegex(), "")
        JSONAssert.assertEquals(expectedJson, actualJson, true)
    }

    companion object {
        @JvmStatic
        fun sigFileAndSigLevel(): Array<Array<Any>> {
            return arrayOf(
                arrayOf(
                    "CMS146v2_Expected_SignatureLevel_None.json",
                    LibraryBuilder.SignatureLevel.None
                ),
                arrayOf(
                    "CMS146v2_Expected_SignatureLevel_Differing.json",
                    LibraryBuilder.SignatureLevel.Differing
                ),
                arrayOf(
                    "CMS146v2_Expected_SignatureLevel_Overloads.json",
                    LibraryBuilder.SignatureLevel.Overloads
                ),
                arrayOf(
                    "CMS146v2_Expected_SignatureLevel_All.json",
                    LibraryBuilder.SignatureLevel.All
                )
            )
        }

        @Throws(IOException::class)
        private fun getJson(name: String): String? {
            return Scanner(getFile(name), StandardCharsets.UTF_8).useDelimiter("\\Z").next()
        }

        private fun getFile(name: String): File {
            val resource = CMS146JsonTest::class.java.getResource(name)

            requireNotNull(resource) { "Cannot find file with name: $name" }

            return File(URLDecoder.decode(resource.file, StandardCharsets.UTF_8))
        }
    }
}
