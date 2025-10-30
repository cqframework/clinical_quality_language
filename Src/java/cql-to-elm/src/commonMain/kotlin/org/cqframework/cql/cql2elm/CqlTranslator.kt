@file:OptIn(ExperimentalJsStatic::class)

package org.cqframework.cql.cql2elm

import kotlin.js.ExperimentalJsExport
import kotlin.js.ExperimentalJsStatic
import kotlin.js.JsExport
import kotlin.js.JsStatic
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic
import kotlinx.io.IOException
import kotlinx.io.Source
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readString
import org.antlr.v4.kotlinruntime.CharStream
import org.antlr.v4.kotlinruntime.CharStreams
import org.cqframework.cql.cql2elm.model.CompiledLibrary
import org.cqframework.cql.elm.serializing.DefaultElmLibraryWriterProvider
import org.cqframework.cql.elm.serializing.ElmLibraryWriterProvider
import org.cqframework.cql.shared.JsOnlyExport
import org.hl7.cql.model.*
import org.hl7.elm.r1.Library
import org.hl7.elm.r1.VersionedIdentifier

/**
 * Wraps [CqlCompiler] and produces ELM outputs in different formats. Exposes compilation exceptions
 * and filtered views for errors, warnings, and messages.
 */
@OptIn(ExperimentalJsExport::class)
@JsOnlyExport
@Suppress("NON_EXPORTABLE_TYPE")
class CqlTranslator
private constructor(
    namespaceInfo: NamespaceInfo?,
    sourceInfo: VersionedIdentifier?,
    `is`: CharStream,
    libraryManager: LibraryManager,
) {
    enum class Format {
        XML,
        JSON,
        COFFEE,
    }

    private val compiler = CqlCompiler(namespaceInfo, sourceInfo, libraryManager)

    init {
        compiler.run(`is`)
    }

    private fun toXml(
        library: Library,
        elmLibraryWriterProvider: ElmLibraryWriterProvider,
    ): String {
        return convertToXml(library, elmLibraryWriterProvider)
    }

    private fun toJson(
        library: Library,
        elmLibraryWriterProvider: ElmLibraryWriterProvider,
    ): String {
        return convertToJson(library, elmLibraryWriterProvider)
    }

    @JvmOverloads
    fun toXml(
        elmLibraryWriterProvider: ElmLibraryWriterProvider = DefaultElmLibraryWriterProvider
    ): String {
        return toXml(compiler.library!!, elmLibraryWriterProvider)
    }

    @JvmOverloads
    fun toJson(
        elmLibraryWriterProvider: ElmLibraryWriterProvider = DefaultElmLibraryWriterProvider
    ): String {
        return toJson(compiler.library!!, elmLibraryWriterProvider)
    }

    fun toELM(): Library? {
        return compiler.library
    }

    val translatedLibrary: CompiledLibrary?
        get() = compiler.compiledLibrary

    val root: Any?
        get() = compiler.root

    val libraries: Map<VersionedIdentifier, Library?>
        get() = compiler.libraryManager.compiledLibraries.mapValues { it.value.library!! }

    val exceptions: List<CqlCompilerException>
        get() = compiler.exceptions

    val errors: List<CqlCompilerException>
        get() = exceptions.filter { it.severity == CqlCompilerException.ErrorSeverity.Error }

    val warnings: List<CqlCompilerException>
        get() = exceptions.filter { it.severity == CqlCompilerException.ErrorSeverity.Warning }

    val messages: List<CqlCompilerException>
        get() = exceptions.filter { it.severity == CqlCompilerException.ErrorSeverity.Info }

    @Suppress("TooManyFunctions")
    companion object {

        @JsStatic
        @JvmStatic
        fun fromText(cqlText: String, libraryManager: LibraryManager): CqlTranslator {
            return CqlTranslator(null, null, CharStreams.fromString(cqlText), libraryManager)
        }

        @JvmStatic
        @JsExport.Ignore
        @Throws(IOException::class)
        fun fromSource(
            namespaceInfo: NamespaceInfo?,
            source: Source,
            libraryManager: LibraryManager,
        ): CqlTranslator {
            return CqlTranslator(
                namespaceInfo,
                null,
                CharStreams.fromString(source.readString()),
                libraryManager,
            )
        }

        @JvmStatic
        @JsExport.Ignore
        @Throws(IOException::class)
        fun fromSource(source: Source, libraryManager: LibraryManager): CqlTranslator {
            return CqlTranslator(
                null,
                null,
                CharStreams.fromString(source.readString()),
                libraryManager,
            )
        }

        @JsStatic
        @JvmStatic
        @Throws(IOException::class)
        fun fromSource(
            namespaceInfo: NamespaceInfo?,
            sourceInfo: VersionedIdentifier?,
            source: Source,
            libraryManager: LibraryManager,
        ): CqlTranslator {
            return CqlTranslator(
                namespaceInfo,
                sourceInfo,
                CharStreams.fromString(source.readString()),
                libraryManager,
            )
        }

        @JvmStatic
        @JsExport.Ignore
        @Throws(IOException::class)
        fun fromFile(cqlFileName: String, libraryManager: LibraryManager): CqlTranslator {
            return SystemFileSystem.source(Path(cqlFileName)).buffered().use {
                fromSource(null, getSourceInfo(cqlFileName), it, libraryManager)
            }
        }

        @JvmStatic
        @JsExport.Ignore
        @Throws(IOException::class)
        fun fromFile(
            namespaceInfo: NamespaceInfo?,
            cqlFileName: String,
            libraryManager: LibraryManager,
        ): CqlTranslator {
            return SystemFileSystem.source(Path(cqlFileName)).buffered().use {
                fromSource(namespaceInfo, getSourceInfo(cqlFileName), it, libraryManager)
            }
        }

        @JvmStatic
        @JsExport.Ignore
        @Throws(IOException::class)
        fun fromFile(cqlFile: Path, libraryManager: LibraryManager): CqlTranslator {
            return SystemFileSystem.source(cqlFile).buffered().use {
                fromSource(null, getSourceInfo(cqlFile), it, libraryManager)
            }
        }

        @JvmStatic
        @JsExport.Ignore
        @Throws(IOException::class)
        fun fromFile(
            namespaceInfo: NamespaceInfo?,
            cqlFile: Path,
            libraryManager: LibraryManager,
        ): CqlTranslator {
            return SystemFileSystem.source(cqlFile).buffered().use {
                fromSource(namespaceInfo, getSourceInfo(cqlFile), it, libraryManager)
            }
        }

        @JsStatic
        @JvmStatic
        @Throws(IOException::class)
        fun fromFile(
            namespaceInfo: NamespaceInfo?,
            sourceInfo: VersionedIdentifier?,
            cqlFile: Path,
            libraryManager: LibraryManager,
        ): CqlTranslator {
            return SystemFileSystem.source(cqlFile).buffered().use {
                fromSource(namespaceInfo, sourceInfo, it, libraryManager)
            }
        }

        private fun getSourceInfo(cqlFileName: String): VersionedIdentifier {
            return getSourceInfo(Path(cqlFileName))
        }

        private fun getSourceInfo(cqlFile: Path): VersionedIdentifier {
            var name = cqlFile.name
            val extensionIndex = name.lastIndexOf('.')
            if (extensionIndex > 0) {
                name = name.take(extensionIndex)
            }
            val system: String? =
                try {
                    SystemFileSystem.resolve(cqlFile).toString()
                } catch (_: IOException) {
                    null
                }
            return VersionedIdentifier().withId(name).withSystem(system)
        }

        @JvmOverloads
        @JvmStatic
        @JsStatic
        fun convertToXml(
            library: Library,
            elmLibraryWriterProvider: ElmLibraryWriterProvider = DefaultElmLibraryWriterProvider,
        ): String {
            return elmLibraryWriterProvider
                .create(LibraryContentType.XML.mimeType())
                .writeAsString(library)
        }

        @JvmOverloads
        @JvmStatic
        @JsStatic
        fun convertToJson(
            library: Library,
            elmLibraryWriterProvider: ElmLibraryWriterProvider = DefaultElmLibraryWriterProvider,
        ): String {
            return elmLibraryWriterProvider
                .create(LibraryContentType.JSON.mimeType())
                .writeAsString(library)
        }
    }
}
