package org.cqframework.cql.cql2elm

import org.hl7.cql_annotations.r1.CqlToElmBase
import org.hl7.cql_annotations.r1.CqlToElmInfo
import org.hl7.elm.r1.Library
import kotlin.jvm.JvmStatic

/** This class provides functions for extracting and parsing CQL Compiler Options from a Library */
object CompilerOptions {
    /**
     * Gets the compiler options used to generate an elm Library.
     *
     * Returns null if the compiler options could not be determined. (for example, the Library was
     * translated without annotations)
     *
     * @param library The library to extracts the options from.
     * @return The set of options used to translate the library.
     */
    @JvmStatic
    fun getCompilerOptions(library: Library): Set<CqlCompilerOptions.Options>? {
        if (library.annotation.isEmpty()) {
            return null
        }
        val compilerOptions = getCompilerOptions(library.annotation)
        return parseCompilerOptions(compilerOptions)
    }

    private fun getCompilerOptions(annotations: List<CqlToElmBase>): String? {
        for (base: CqlToElmBase in annotations) {
            if (base is CqlToElmInfo) {
                if (base.translatorOptions != null) {
                    return base.translatorOptions
                }
            }
        }
        return null
    }

    /**
     * Parses a string representing CQL compiler Options into a Set. The string is expected to
     * be a comma-delimited list of values from the CqlCompiler.Options enumeration. For example
     * "EnableListPromotion, EnableListDemotion".
     *
     * @param compilerOptions the string to parse
     * @return the set of options
     */
    @JvmStatic
    fun parseCompilerOptions(compilerOptions: String?): Set<CqlCompilerOptions.Options>? {
        if (compilerOptions.isNullOrEmpty()) {
            return null
        }
        val optionSet =
            mutableSetOf<CqlCompilerOptions.Options>()
        val options =
            compilerOptions.trim { it <= ' ' }.split(",".toRegex()).dropLastWhile { it.isEmpty() }
        for (option in options) {
            optionSet.add(CqlCompilerOptions.Options.valueOf(option))
        }
        return optionSet
    }

    /**
     * Gets the compiler version used to generate an elm Library.
     *
     * Returns null if the compiled version could not be determined. (for example, the Library was
     * compiled without annotations)
     *
     * @param library The library to extracts the compiler version from.
     * @return The version of compiler used to compiler the library.
     */
    @JvmStatic
    fun getCompilerVersion(library: Library): String? {
        if (library.annotation.isEmpty()) {
            return null
        }
        return getCompilerVersion(library.annotation)
    }

    private fun getCompilerVersion(annotations: List<CqlToElmBase>): String? {
        for (o: CqlToElmBase in annotations) {
            if (o is CqlToElmInfo) {
                return o.translatorVersion
            }
        }
        return null
    }
}
