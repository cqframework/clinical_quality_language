@file:Suppress("MatchingDeclarationName")

package org.cqframework.cql.cql2elm

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.jvm.JvmStatic
import org.antlr.v4.kotlinruntime.CharStream
import org.antlr.v4.kotlinruntime.CharStreams
import org.cqframework.cql.cql2elm.model.CompiledLibrary
import org.cqframework.cql.elm.serializing.ElmLibraryWriterProvider
import org.hl7.cql.model.*
import org.hl7.elm.r1.*

@OptIn(ExperimentalJsExport::class)
@JsExport
open class CommonCqlTranslator(
    namespaceInfo: NamespaceInfo?,
    sourceInfo: VersionedIdentifier?,
    `is`: CharStream,
    libraryManager: CommonLibraryManager,
    val writerProvider: ElmLibraryWriterProvider
) {
    enum class Format {
        XML,
        JSON,
        COFFEE
    }

    private val compiler = CommonCqlCompiler(namespaceInfo, sourceInfo, libraryManager)

    init {
        compiler.run(`is`)
    }

    private fun toXml(library: Library): String {
        return convertToXml(library)
    }

    private fun toJson(library: Library): String {
        return convertToJson(library)
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

    fun toRetrieves(): kotlin.collections.List<Retrieve?>? {
        return compiler.toRetrieves()
    }

    val libraries: Map<VersionedIdentifier, Library?>
        get() = compiler.libraries

    val exceptions: kotlin.collections.List<CqlCompilerException?>?
        // public Map<String, String> getLibrariesAsXML() {
        get() = compiler.exceptions

    val errors: kotlin.collections.List<CqlCompilerException?>?
        get() = compiler.errors

    val warnings: kotlin.collections.List<CqlCompilerException?>?
        get() = compiler.warnings

    val messages: kotlin.collections.List<CqlCompilerException?>?
        get() = compiler.messages

    fun convertToXml(library: Library): String {
        return this.writerProvider.create(LibraryContentType.XML.mimeType()).writeAsString(library)
    }

    fun convertToJson(library: Library): String {
        return this.writerProvider.create(LibraryContentType.JSON.mimeType()).writeAsString(library)
    }

    @Suppress("TooManyFunctions")
    companion object {

        @JvmStatic
        fun fromText(
            cqlText: String,
            libraryManager: CommonLibraryManager,
            writerProvider: ElmLibraryWriterProvider
        ): CommonCqlTranslator {
            return CommonCqlTranslator(
                null,
                null,
                CharStreams.fromString(cqlText),
                libraryManager,
                writerProvider
            )
        }
    }
}
