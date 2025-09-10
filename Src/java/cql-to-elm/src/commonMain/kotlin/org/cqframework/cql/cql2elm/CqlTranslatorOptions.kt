package org.cqframework.cql.cql2elm

import kotlin.js.ExperimentalJsExport
import kotlin.js.ExperimentalJsStatic
import kotlin.js.JsStatic
import kotlin.jvm.JvmStatic
import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.serialization.Serializable
import org.cqframework.cql.shared.JsOnlyExport

@OptIn(ExperimentalJsExport::class, ExperimentalJsStatic::class)
@JsOnlyExport
@Serializable
@Suppress("NON_EXPORTABLE_TYPE")
class CqlTranslatorOptions {
    enum class Format {
        XML,
        JSON,
        COFFEE
    }

    var cqlCompilerOptions: CqlCompilerOptions? = null
    var formats: Set<Format>? = null

    fun withCqlCompilerOptions(cqlCompilerOptions: CqlCompilerOptions?): CqlTranslatorOptions {
        this.cqlCompilerOptions = cqlCompilerOptions
        return this
    }

    fun withFormats(formats: Set<Format>?): CqlTranslatorOptions {
        this.formats = formats
        return this
    }

    fun toSink(sink: Sink) {
        writeTranslatorOptionsToJson(this, sink)
    }

    fun toFile(path: Path) {
        SystemFileSystem.sink(path).buffered().use { toSink(it) }
    }

    companion object {
        @JvmStatic
        @JsStatic
        fun defaultOptions(): CqlTranslatorOptions {
            return CqlTranslatorOptions()
                .withCqlCompilerOptions(CqlCompilerOptions.defaultOptions())
                .withFormats(setOf(Format.XML))
        }

        @JvmStatic
        @JsStatic
        fun fromFile(path: Path): CqlTranslatorOptions {
            return SystemFileSystem.source(path).buffered().use { fromSource(it) }
        }

        @JvmStatic
        @JsStatic
        fun fromSource(source: Source): CqlTranslatorOptions {
            return readTranslatorOptionsFromJson(source)
        }
    }
}
