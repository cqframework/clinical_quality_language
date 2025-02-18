@file:Suppress("WildcardImport")

package org.cqframework.cql.cql2elm

import java.io.*
import org.antlr.v4.kotlinruntime.CharStream
import org.antlr.v4.kotlinruntime.CharStreams
import org.hl7.cql.model.*
import org.hl7.elm.r1.*

class CqlTranslator(
    namespaceInfo: NamespaceInfo?,
    sourceInfo: VersionedIdentifier?,
    `is`: CharStream,
    libraryManager: LibraryManager
) : CommonCqlTranslator(namespaceInfo, sourceInfo, `is`, libraryManager) {
    @Suppress("TooManyFunctions")
    companion object {
        @JvmStatic
        fun fromText(cqlText: String, libraryManager: LibraryManager): CqlTranslator {
            return CqlTranslator(null, null, CharStreams.fromString(cqlText), libraryManager)
        }

        @JvmStatic
        fun convertToXml(library: Library): String {
            return CommonCqlTranslator.convertToXml(library)
        }

        @JvmStatic
        fun convertToJson(library: Library): String {
            return CommonCqlTranslator.convertToJson(library)
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
    }
}
