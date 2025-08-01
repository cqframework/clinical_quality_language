package org.cqframework.cql.cql2elm

import kotlin.jvm.JvmOverloads

/*
Defines a language capability
*/
class CqlCapability
@JvmOverloads
constructor( // A unique code identifying the capability
    var code: String, // A short string providing a user-friendly display name for the capability
    var display:
        String, // A definition of the capability, including description of possible values for the
    // capability
    var definition:
        String, // The version in which the capability was introduced, drawn from release versions,
    // specifying as <Major>.<Minor>
    var sinceVersion: String =
        "1.0", // The version in which the capability was removed, drawn from release versions,
    // specifying as <Major>.<Minor>
    var upToVersion: String? = null
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        val that = other as CqlCapability
        return code == that.code
    }

    override fun hashCode(): Int {
        return code.hashCode()
    }

    override fun toString(): String {
        return ("CqlCapability{" +
            "code='" +
            code +
            '\'' +
            ", display='" +
            display +
            '\'' +
            ", definition='" +
            definition +
            '\'' +
            '}')
    }

    companion object {
        @Suppress("MaxLineLength")
        var capabilities: Set<CqlCapability> =
            setOf(
                CqlCapability(
                    "decimal-precision",
                    "Decimal Precision",
                    "Maximum number of digits of precision that can be represented in decimal values. Conformant implementations SHALL support at least 28 digits of precision for decimal values."
                ),
                CqlCapability(
                    "decimal-scale",
                    "Decimal Scale",
                    "Maximum number of digits of scale that can be represented in decimal values (i.e. the maximum number of digits after the decimal point). Conformant implementations SHALL support at least 8 digits of scale for decimal values."
                ),
                CqlCapability(
                    "datetime-precision",
                    "DateTime Precision",
                    "The maximum number of digits of precision that can be represented for DateTime values, where each numeric place, beginning with years, is counted as a single digit. Conformant implementations SHALL support at least 17 digits of precision for datetime values (YYYYMMDDHHmmss.fff)."
                ),
                CqlCapability(
                    "datetime-scale",
                    "DateTime Scale",
                    "The maximum number of digits of scale that can be represented in datetime values (i.e. the maximum number of digits after the decimal point in the seconds component). Conformant implementations SHALL support at least 3 digits of scale for datetime values."
                ),
                CqlCapability(
                    "ucum-unit-conversion",
                    "UCUM Unit Conversion",
                    "Whether or not the implementation supports conversion of Unified Code for Units of Measure (UCUM) units. Conformant implementations SHOULD support UCUM unit conversion."
                ),
                CqlCapability(
                    "regex-dialect",
                    "Regular Expression Dialect",
                    "The dialect of regular expressions used by the implementation. Conformant implementations SHOULD use the Perl Compatible Regular Expression (PCRE) dialect. Values for this feature should be drawn from the Name of the regular expression language list here: https://en.wikipedia.org/wiki/Comparison_of_regular-expression_engines"
                ),
                CqlCapability(
                    "supported-data-model",
                    "Supported Data Model",
                    "A supported data model, specified as the URI of the model information."
                ),
                CqlCapability(
                    "supported-function",
                    "Supported Function",
                    "A supported function, specified as the fully qualified name of the function."
                ),
                CqlCapability(
                    "unfiltered-context-retrieve",
                    "Unfiltered Context Retrieve",
                    "Whether or not the implementation supports evaluating retrieves in the unfiltered context."
                ),
                CqlCapability(
                    "related-context-retrieve",
                    "Related Context Retrieve",
                    "Whether or not the implementation supports related-context retrieves.",
                    "1.4"
                )
            )
    }
}
