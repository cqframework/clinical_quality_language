@file:Suppress("WildcardImport")

package org.cqframework.cql.cql2elm

import java.io.*
import org.antlr.v4.kotlinruntime.CharStream
import org.antlr.v4.kotlinruntime.CharStreams
import org.cqframework.cql.cql2elm.model.CompiledLibrary
import org.cqframework.cql.elm.serializing.ElmLibraryWriterFactory
import org.hl7.cql.model.*
import org.hl7.elm.r1.*

class CqlTranslator(
    namespaceInfo: NamespaceInfo?,
    sourceInfo: VersionedIdentifier?,
    `is`: CharStream,
    libraryManager: LibraryManager
) {
    enum class Format {
        XML,
        JSON,
        COFFEE
    }

    private val compiler = CqlCompiler(namespaceInfo, sourceInfo, libraryManager)

    init {
        compiler.run(`is`)
    }

    private fun toXml(library: Library): String {
        return try {
            convertToXml(library)
        } catch (e: IOException) {
            throw IllegalArgumentException("Could not convert library to XML.", e)
        }
    }

    private fun toJson(library: Library): String {
        return try {
            convertToJson(library)
        } catch (e: IOException) {
            throw IllegalArgumentException(
                "Could not convert library to JSON using JAXB serializer.",
                e
            )
        }
    }

    fun toXml(): String {
        return toXml(compiler.library!!)
    }

    fun toJson(): String {
        return toJson(compiler.library!!)
    }

    fun toELM(): Library? {
        return compiler.library
    }

    val translatedLibrary: CompiledLibrary?
        get() = compiler.compiledLibrary

    fun toObject(): Any? {
        return compiler.toObject()
    }

    fun toRetrieves(): List<Retrieve?>? {
        return compiler.toRetrieves()
    }

    val libraries: Map<VersionedIdentifier, Library?>
        get() = compiler.libraries

    val exceptions: List<CqlCompilerException?>?
        // public Map<String, String> getLibrariesAsXML() {
        get() = compiler.exceptions

    val errors: List<CqlCompilerException?>?
        get() = compiler.errors

    val warnings: List<CqlCompilerException?>?
        get() = compiler.warnings

    val messages: List<CqlCompilerException?>?
        get() = compiler.messages

    @Suppress("TooManyFunctions")
    companion object {

        @JvmStatic
        fun fromText(cqlText: String, libraryManager: LibraryManager): CqlTranslator {
            return CqlTranslator(null, null, CharStreams.fromString(cqlText), libraryManager)
        }

        @JvmStatic
        @Throws(IOException::class)
        fun fromStream(
            namespaceInfo: NamespaceInfo?,
            cqlStream: InputStream,
            libraryManager: LibraryManager
        ): CqlTranslator {
            return CqlTranslator(
                namespaceInfo,
                null,
                CharStreams.fromStream(cqlStream),
                libraryManager
            )
        }

        @JvmStatic
        @Throws(IOException::class)
        fun fromStream(cqlStream: InputStream, libraryManager: LibraryManager): CqlTranslator {
            return CqlTranslator(null, null, CharStreams.fromStream(cqlStream), libraryManager)
        }

        @JvmStatic
        @Throws(IOException::class)
        fun fromStream(
            namespaceInfo: NamespaceInfo?,
            sourceInfo: VersionedIdentifier?,
            cqlStream: InputStream,
            libraryManager: LibraryManager
        ): CqlTranslator {
            return CqlTranslator(
                namespaceInfo,
                sourceInfo,
                CharStreams.fromStream(cqlStream),
                libraryManager
            )
        }

        @JvmStatic
        @Throws(IOException::class)
        fun fromFile(cqlFileName: String, libraryManager: LibraryManager): CqlTranslator {
            return CqlTranslator(
                null,
                getSourceInfo(cqlFileName),
                CharStreams.fromStream(FileInputStream(cqlFileName)),
                libraryManager
            )
        }

        @JvmStatic
        @Throws(IOException::class)
        fun fromFile(
            namespaceInfo: NamespaceInfo?,
            cqlFileName: String,
            libraryManager: LibraryManager
        ): CqlTranslator {
            return CqlTranslator(
                namespaceInfo,
                getSourceInfo(cqlFileName),
                CharStreams.fromStream(FileInputStream(cqlFileName)),
                libraryManager
            )
        }

        @JvmStatic
        @Throws(IOException::class)
        fun fromFile(cqlFile: File, libraryManager: LibraryManager): CqlTranslator {
            return CqlTranslator(
                null,
                getSourceInfo(cqlFile),
                CharStreams.fromStream(FileInputStream(cqlFile)),
                libraryManager
            )
        }

        @JvmStatic
        @Throws(IOException::class)
        fun fromFile(
            namespaceInfo: NamespaceInfo?,
            cqlFile: File,
            libraryManager: LibraryManager
        ): CqlTranslator {
            return CqlTranslator(
                namespaceInfo,
                getSourceInfo(cqlFile),
                CharStreams.fromStream(FileInputStream(cqlFile)),
                libraryManager
            )
        }

        @JvmStatic
        @Throws(IOException::class)
        fun fromFile(
            namespaceInfo: NamespaceInfo?,
            sourceInfo: VersionedIdentifier?,
            cqlFile: File,
            libraryManager: LibraryManager
        ): CqlTranslator {
            return CqlTranslator(
                namespaceInfo,
                sourceInfo,
                CharStreams.fromStream(FileInputStream(cqlFile)),
                libraryManager
            )
        }

        private fun getSourceInfo(cqlFileName: String): VersionedIdentifier {
            return getSourceInfo(File(cqlFileName))
        }

        private fun getSourceInfo(cqlFile: File): VersionedIdentifier {
            var name = cqlFile.name
            val extensionIndex = name.lastIndexOf('.')
            if (extensionIndex > 0) {
                name = name.substring(0, extensionIndex)
            }
            val system: String? =
                try {
                    cqlFile.canonicalPath
                } catch (@Suppress("SwallowedException") e: IOException) {
                    cqlFile.absolutePath
                }
            return VersionedIdentifier().withId(name).withSystem(system)
        }

        @Throws(IOException::class)
        fun convertToXml(library: Library): String {
            val writer = StringWriter()
            ElmLibraryWriterFactory.getWriter(LibraryContentType.XML.mimeType())
                .write(library, writer)
            return writer.buffer.toString()
        }

        @JvmStatic
        @Throws(IOException::class)
        fun convertToJson(library: Library): String {
            val writer = StringWriter()
            ElmLibraryWriterFactory.getWriter(LibraryContentType.JSON.mimeType())
                .write(library, writer)
            return writer.buffer.toString()
        }
    }
}
