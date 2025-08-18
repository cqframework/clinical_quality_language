package org.cqframework.cql.cql2elm

import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import kotlinx.io.Source
import kotlinx.io.asInputStream
import org.antlr.v4.kotlinruntime.CharStreams
import org.hl7.cql.model.NamespaceInfo
import org.hl7.elm.r1.Library
import org.hl7.elm.r1.VersionedIdentifier

class CqlCompiler(
    namespaceInfo: NamespaceInfo?,
    sourceInfo: VersionedIdentifier?,
    libraryManager: LibraryManager
) : BaseCqlCompiler(namespaceInfo, sourceInfo, libraryManager) {
    constructor(libraryManager: LibraryManager) : this(null, null, libraryManager)

    constructor(
        namespaceInfo: NamespaceInfo?,
        libraryManager: LibraryManager
    ) : this(namespaceInfo, null, libraryManager)

    override fun run(source: Source): Library {
        return run(CharStreams.fromStream(source.asInputStream()))
    }

    @Throws(IOException::class)
    fun run(cqlFile: File): Library {
        return run(CharStreams.fromStream(FileInputStream(cqlFile)))
    }

    @Throws(IOException::class)
    fun run(inputStream: InputStream): Library {
        return run(CharStreams.fromStream(inputStream))
    }
}
