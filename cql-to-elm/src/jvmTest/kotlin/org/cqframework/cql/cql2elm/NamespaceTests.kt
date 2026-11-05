package org.cqframework.cql.cql2elm

import java.io.IOException
import java.util.Locale
import kotlinx.io.Source
import kotlinx.io.asSource
import kotlinx.io.buffered
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.not
import org.hl7.cql.model.NamespaceInfo
import org.hl7.cql.model.NamespaceManager
import org.hl7.cql_annotations.r1.CqlToElmError
import org.hl7.elm.r1.Library
import org.hl7.elm.r1.VersionedIdentifier
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

@Suppress("PrintStackTrace")
internal class NamespaceTests {
    internal class NamespaceTestsLibrarySourceProvider : LibrarySourceProvider {
        override fun getLibrarySource(libraryIdentifier: VersionedIdentifier): Source {
            var namespacePath = "NamespaceTests/"
            if (libraryIdentifier.system != null) {
                val namespaceInfo: NamespaceInfo? =
                    libraryManager!!
                        .namespaceManager
                        .getNamespaceInfoFromUri(libraryIdentifier.system!!)
                if (namespaceInfo != null && namespaceInfo.name != "Public") {
                    namespacePath += namespaceInfo.name + "/"
                }
            }
            val libraryFileName =
                String.format(
                    Locale.US,
                    "%s%s%s.cql",
                    namespacePath,
                    libraryIdentifier.id,
                    if (libraryIdentifier.version != null) ("-" + libraryIdentifier.version) else "",
                )
            return NamespaceTests::class
                .java
                .getResourceAsStream(libraryFileName)!!
                .asSource()
                .buffered()
        }

        override fun getLibraryContent(
            libraryIdentifier: VersionedIdentifier,
            type: LibraryContentType,
        ): Source? {
            if (LibraryContentType.CQL == type) {
                return getLibrarySource(libraryIdentifier)
            }

            return null
        }
    }

    /* Unit test namespace functionality */
    @Test
    fun namespacePath() {
        assertThat(
            NamespaceManager.getPath(defaultNamespaceInfo!!.uri, "Main"),
            `is`("http://cql.hl7.org/public/Main"),
        )
    }

    @Test
    fun namespaceNamePart() {
        assertThat<String?>(
            NamespaceManager.getNamePart("http://cql.hl7.org/public/Main"),
            `is`<String?>("Main"),
        )
    }

    @Test
    fun namespaceUriPart() {
        assertThat<String?>(
            NamespaceManager.getUriPart("http://cql.hl7.org/public/Main"),
            `is`<String?>("http://cql.hl7.org/public"),
        )
    }

    /* Ensure base functionality with a defaulted namespace uri */
    @Test
    fun libraryReferences() {
        var translator: CqlTranslator?
        try {
            translator =
                CqlTranslator.fromSource(
                    defaultNamespaceInfo,
                    NamespaceTests::class
                        .java
                        .getResourceAsStream("NamespaceTests/ReferencingLibrary.cql")!!
                        .asSource()
                        .buffered(),
                    libraryManager!!,
                )
            assertThat(translator.errors.size, `is`(0))
            assertThat(translator.toELM()!!.identifier!!.system, `is`(defaultNamespaceInfo!!.uri))
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @Test
    fun invalidLibraryReferences() {
        var translator: CqlTranslator?
        try {
            translator =
                CqlTranslator.fromSource(
                    defaultNamespaceInfo,
                    NamespaceTests::class
                        .java
                        .getResourceAsStream("NamespaceTests/InvalidReferencingLibrary.cql")!!
                        .asSource()
                        .buffered(),
                    libraryManager!!,
                )
            assertThat(translator.errors.size, Matchers.`is`(not(0)))
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @Test
    fun invalidLibraryReference() {
        var translator: CqlTranslator?
        try {
            translator =
                CqlTranslator.fromSource(
                    defaultNamespaceInfo,
                    NamespaceTests::class
                        .java
                        .getResourceAsStream("NamespaceTests/InvalidLibraryReference.cql")!!
                        .asSource()
                        .buffered(),
                    libraryManager!!,
                )
            assertThat(translator.errors.size, Matchers.`is`(not(0)))
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @Test
    fun invalidBaseLibrary() {
        var translator: CqlTranslator?
        try {
            translator =
                CqlTranslator.fromSource(
                    defaultNamespaceInfo,
                    NamespaceTests::class
                        .java
                        .getResourceAsStream("NamespaceTests/ReferencingInvalidBaseLibrary.cql")!!
                        .asSource()
                        .buffered(),
                    libraryManager!!,
                )
            assertThat(translator.errors.size, `is`(1))
            assertThat(translator.errors[0], Matchers.instanceOf(CqlCompilerException::class.java))
            assertThat(translator.errors[0].locator, Matchers.notNullValue())
            assertThat(translator.errors[0].locator!!.library, Matchers.notNullValue())
            assertThat(
                translator.errors[0].locator!!.library!!.system,
                `is`(defaultNamespaceInfo!!.uri),
            )
            assertThat(translator.errors[0].locator!!.library!!.id, `is`("InvalidBaseLibrary"))

            assertThat<Library?>(translator.toELM(), Matchers.notNullValue())
            assertThat<Any?>(translator.toELM()!!.annotation, Matchers.notNullValue())
            assertThat(translator.toELM()!!.annotation.size, Matchers.greaterThan(0))
            var invalidBaseLibraryError: CqlToElmError? = null
            for (o in translator.toELM()!!.annotation) {
                if (o is CqlToElmError) {
                    invalidBaseLibraryError = o
                    break
                }
            }
            assertThat<CqlToElmError?>(invalidBaseLibraryError, Matchers.notNullValue())
            assertThat(invalidBaseLibraryError!!.librarySystem, `is`(defaultNamespaceInfo!!.uri))
            assertThat<String?>(
                invalidBaseLibraryError.libraryId,
                `is`<String?>("InvalidBaseLibrary"),
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @Test
    fun main() {
        try {
            val translator =
                CqlTranslator.fromSource(
                    defaultNamespaceInfo,
                    NamespaceTests::class
                        .java
                        .getResourceAsStream("NamespaceTests/Main.cql")!!
                        .asSource()
                        .buffered(),
                    libraryManager!!,
                )
            assertThat(translator.errors.size, `is`(0))
            assertThat<String?>(translator.toELM()!!.identifier!!.id, `is`<String?>("Main"))
            assertThat(translator.toELM()!!.identifier!!.system, `is`(defaultNamespaceInfo!!.uri))
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @Test
    fun referencingMain() {
        try {
            val translator =
                CqlTranslator.fromSource(
                    defaultNamespaceInfo,
                    NamespaceTests::class
                        .java
                        .getResourceAsStream("NamespaceTests/ReferencingMain.cql")!!
                        .asSource()
                        .buffered(),
                    libraryManager!!,
                )
            assertThat(translator.errors.size, `is`(0))
            assertThat<String?>(
                translator.toELM()!!.identifier!!.id,
                `is`<String?>("ReferencingMain"),
            )
            assertThat(translator.toELM()!!.identifier!!.system, `is`(defaultNamespaceInfo!!.uri))
            assertThat<Library.Includes?>(translator.toELM()!!.includes, Matchers.notNullValue())
            assertThat<Any?>(translator.toELM()!!.includes!!.def, Matchers.notNullValue())
            assertThat(translator.toELM()!!.includes!!.def.size, Matchers.greaterThanOrEqualTo(1))
            val i = translator.toELM()!!.includes!!.def[0]
            assertThat<String?>(i.localIdentifier, `is`<String?>("Main"))
            assertThat<String?>(i.path, `is`<String?>("http://cql.hl7.org/public/Main"))
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @Test
    fun coreMain() {
        try {
            val translator =
                CqlTranslator.fromSource(
                    coreNamespaceInfo,
                    NamespaceTests::class
                        .java
                        .getResourceAsStream("NamespaceTests/Core/Main.cql")!!
                        .asSource()
                        .buffered(),
                    libraryManager!!,
                )
            assertThat(translator.errors.size, `is`(0))
            assertThat<String?>(translator.toELM()!!.identifier!!.id, `is`<String?>("Main"))
            assertThat(translator.toELM()!!.identifier!!.system, `is`(coreNamespaceInfo!!.uri))
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @Test
    fun coreReferencingMain() {
        try {
            val translator =
                CqlTranslator.fromSource(
                    coreNamespaceInfo,
                    NamespaceTests::class
                        .java
                        .getResourceAsStream("NamespaceTests/Core/ReferencingMain.cql")!!
                        .asSource()
                        .buffered(),
                    libraryManager!!,
                )
            assertThat(translator.errors.size, `is`(0))
            assertThat<String?>(
                translator.toELM()!!.identifier!!.id,
                `is`<String?>("ReferencingMain"),
            )
            assertThat(translator.toELM()!!.identifier!!.system, `is`(coreNamespaceInfo!!.uri))
            assertThat<Library.Includes?>(translator.toELM()!!.includes, Matchers.notNullValue())
            assertThat<Any?>(translator.toELM()!!.includes!!.def, Matchers.notNullValue())
            assertThat(translator.toELM()!!.includes!!.def.size, Matchers.greaterThanOrEqualTo(1))
            val i = translator.toELM()!!.includes!!.def[0]
            assertThat<String?>(i.localIdentifier, `is`<String?>("Main"))
            assertThat<String?>(i.path, `is`<String?>("http://cql.hl7.org/core/Main"))
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @Test
    fun sharedMain() {
        try {
            val translator =
                CqlTranslator.fromSource(
                    sharedNamespaceInfo,
                    NamespaceTests::class
                        .java
                        .getResourceAsStream("NamespaceTests/Shared/Main.cql")!!
                        .asSource()
                        .buffered(),
                    libraryManager!!,
                )
            assertThat(translator.errors.size, `is`(0))
            assertThat<String?>(translator.toELM()!!.identifier!!.id, `is`<String?>("Main"))
            assertThat(translator.toELM()!!.identifier!!.system, `is`(sharedNamespaceInfo!!.uri))

            assertThat<Library.Includes?>(translator.toELM()!!.includes, Matchers.notNullValue())
            assertThat<Any?>(translator.toELM()!!.includes!!.def, Matchers.notNullValue())
            assertThat(translator.toELM()!!.includes!!.def.size, Matchers.greaterThanOrEqualTo(1))
            val i = translator.toELM()!!.includes!!.def[0]
            assertThat<String?>(i.localIdentifier, `is`<String?>("Main"))
            assertThat<String?>(i.path, `is`<String?>("http://cql.hl7.org/core/Main"))
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @Test
    fun sharedReferencingMain() {
        try {
            val translator =
                CqlTranslator.fromSource(
                    sharedNamespaceInfo,
                    NamespaceTests::class
                        .java
                        .getResourceAsStream("NamespaceTests/Shared/ReferencingMain.cql")!!
                        .asSource()
                        .buffered(),
                    libraryManager!!,
                )
            assertThat(translator.errors.size, `is`(0))
            assertThat<String?>(
                translator.toELM()!!.identifier!!.id,
                `is`<String?>("ReferencingMain"),
            )
            assertThat(translator.toELM()!!.identifier!!.system, `is`(sharedNamespaceInfo!!.uri))
            assertThat<Library.Includes?>(translator.toELM()!!.includes, Matchers.notNullValue())
            assertThat<Any?>(translator.toELM()!!.includes!!.def, Matchers.notNullValue())
            assertThat(translator.toELM()!!.includes!!.def.size, Matchers.greaterThanOrEqualTo(1))
            val i = translator.toELM()!!.includes!!.def[0]
            assertThat<String?>(i.localIdentifier, `is`<String?>("Main"))
            assertThat<String?>(i.path, `is`<String?>("http://cql.hl7.org/shared/Main"))
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @Test
    fun contentMain() {
        try {
            val translator =
                CqlTranslator.fromSource(
                    contentNamespaceInfo,
                    NamespaceTests::class
                        .java
                        .getResourceAsStream("NamespaceTests/Content/Main.cql")!!
                        .asSource()
                        .buffered(),
                    libraryManager!!,
                )
            assertThat(translator.errors.size, `is`(0))
            assertThat<String?>(translator.toELM()!!.identifier!!.id, `is`<String?>("Main"))
            assertThat(translator.toELM()!!.identifier!!.system, `is`(contentNamespaceInfo!!.uri))

            assertThat<Library.Includes?>(translator.toELM()!!.includes, Matchers.notNullValue())
            assertThat<Any?>(translator.toELM()!!.includes!!.def, Matchers.notNullValue())
            assertThat(translator.toELM()!!.includes!!.def.size, Matchers.greaterThanOrEqualTo(2))
            var i = translator.toELM()!!.includes!!.def[0]
            assertThat<String?>(i.localIdentifier, `is`<String?>("CoreMain"))
            assertThat<String?>(i.path, `is`<String?>("http://cql.hl7.org/core/Main"))

            i = translator.toELM()!!.includes!!.def[1]
            assertThat<String?>(i.localIdentifier, `is`<String?>("SharedMain"))
            assertThat<String?>(i.path, `is`<String?>("http://cql.hl7.org/shared/Main"))
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @Test
    fun contentReferencingMain() {
        try {
            val translator =
                CqlTranslator.fromSource(
                    contentNamespaceInfo,
                    NamespaceTests::class
                        .java
                        .getResourceAsStream("NamespaceTests/Content/ReferencingMain.cql")!!
                        .asSource()
                        .buffered(),
                    libraryManager!!,
                )
            assertThat(translator.errors.size, `is`(0))
            assertThat<String?>(
                translator.toELM()!!.identifier!!.id,
                `is`<String?>("ReferencingMain"),
            )
            assertThat(translator.toELM()!!.identifier!!.system, `is`(contentNamespaceInfo!!.uri))
            assertThat<Library.Includes?>(translator.toELM()!!.includes, Matchers.notNullValue())
            assertThat<Any?>(translator.toELM()!!.includes!!.def, Matchers.notNullValue())
            assertThat(translator.toELM()!!.includes!!.def.size, Matchers.greaterThanOrEqualTo(3))

            var i = translator.toELM()!!.includes!!.def[0]
            assertThat<String?>(i.localIdentifier, `is`<String?>("Main"))
            assertThat<String?>(i.path, `is`<String?>("http://cql.hl7.org/shared/Main"))

            i = translator.toELM()!!.includes!!.def[1]
            assertThat<String?>(i.localIdentifier, `is`<String?>("ReferencingMain"))
            assertThat<String?>(i.path, `is`<String?>("http://cql.hl7.org/core/ReferencingMain"))

            i = translator.toELM()!!.includes!!.def[2]
            assertThat<String?>(i.localIdentifier, `is`<String?>("SharedReferencingMain"))
            assertThat<String?>(i.path, `is`<String?>("http://cql.hl7.org/shared/ReferencingMain"))
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    companion object {
        private var modelManager: ModelManager? = null
        private var libraryManager: LibraryManager? = null
        private var defaultNamespaceInfo: NamespaceInfo? = null
        private var coreNamespaceInfo: NamespaceInfo? = null
        private var sharedNamespaceInfo: NamespaceInfo? = null
        private var contentNamespaceInfo: NamespaceInfo? = null

        @JvmStatic
        @BeforeAll
        fun setup() {
            modelManager = ModelManager()
            libraryManager = LibraryManager(modelManager!!)
            libraryManager!!
                .librarySourceLoader
                .registerProvider(NamespaceTestsLibrarySourceProvider())
            defaultNamespaceInfo = NamespaceInfo("Public", "http://cql.hl7.org/public")
            coreNamespaceInfo = NamespaceInfo("Core", "http://cql.hl7.org/core")
            sharedNamespaceInfo = NamespaceInfo("Shared", "http://cql.hl7.org/shared")
            contentNamespaceInfo = NamespaceInfo("Content", "http://cql.hl7.org/content")
            libraryManager!!.namespaceManager.addNamespace(defaultNamespaceInfo!!)
            libraryManager!!.namespaceManager.addNamespace(coreNamespaceInfo!!)
            libraryManager!!.namespaceManager.addNamespace(sharedNamespaceInfo!!)
            libraryManager!!.namespaceManager.addNamespace(contentNamespaceInfo!!)
        }
    }
}
