package org.cqframework.cql.cql2elm

import java.util.*

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

    companion object {
        @JvmStatic
        fun defaultOptions(): CqlTranslatorOptions {
            return CqlTranslatorOptions()
                .withCqlCompilerOptions(CqlCompilerOptions.defaultOptions())
                .withFormats(EnumSet.of(Format.XML))
        }
    }
}
