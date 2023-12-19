package org.cqframework.cql.cql2elm;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/*
Defines a language capability
 */
public class CqlCapability {
    public CqlCapability(String code, String display, String definition) {
        this(code, display, definition, "1.0", null);
    }

    public CqlCapability(String code, String display, String definition, String sinceVersion) {
        this(code, display, definition, sinceVersion, null);
    }

    public CqlCapability(String code, String display, String definition, String sinceVersion, String upToVersion) {
        this.code = code;
        this.display = display;
        this.definition = definition;
        this.sinceVersion = sinceVersion;
        this.upToVersion = upToVersion;
    }

    // A unique code identifying the capability
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    // A short string providing a user-friendly display name for the capability
    private String display;

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    // A definition of the capability, including description of possible values for the capability
    private String definition;

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    // The version in which the capability was introduced, drawn from release versions, specifying as <Major>.<Minor>
    private String sinceVersion;

    public String getSinceVersion() {
        return sinceVersion;
    }

    public void setSinceVersion(String sinceVersion) {
        this.sinceVersion = sinceVersion;
    }

    // The version in which the capability was removed, drawn from release versions, specifying as <Major>.<Minor>
    private String upToVersion;

    public String getUpToVersion() {
        return upToVersion;
    }

    public void setUpToVersion(String upToVersion) {
        this.upToVersion = upToVersion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CqlCapability that = (CqlCapability) o;
        return code.equals(that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public String toString() {
        return "CqlCapability{" + "code='"
                + code + '\'' + ", display='"
                + display + '\'' + ", definition='"
                + definition + '\'' + '}';
    }

    public static Set<CqlCapability> capabilities = new HashSet<CqlCapability>() {
        {
            add(
                    new CqlCapability(
                            "decimal-precision",
                            "Decimal Precision",
                            "Maximum number of digits of precision that can be represented in decimal values. Conformant implementations SHALL support at least 28 digits of precision for decimal values."));
            add(
                    new CqlCapability(
                            "decimal-scale",
                            "Decimal Scale",
                            "Maximum number of digits of scale that can be represented in decimal values (i.e. the maximum number of digits after the decimal point). Conformant implementations SHALL support at least 8 digits of scale for decimal values."));
            add(
                    new CqlCapability(
                            "datetime-precision",
                            "DateTime Precision",
                            "The maximum number of digits of precision that can be represented for DateTime values, where each numeric place, beginning with years, is counted as a single digit. Conformant implementations SHALL support at least 17 digits of precision for datetime values (YYYYMMDDHHmmss.fff)."));
            add(
                    new CqlCapability(
                            "datetime-scale",
                            "DateTime Scale",
                            "The maximum number of digits of scale that can be represented in datetime values (i.e. the maximum number of digits after the decimal point in the seconds component). Conformant implementations SHALL support at least 3 digits of scale for datetime values."));
            add(
                    new CqlCapability(
                            "ucum-unit-conversion",
                            "UCUM Unit Conversion",
                            "Whether or not the implementation supports conversion of Unified Code for Units of Measure (UCUM) units. Conformant implementations SHOULD support UCUM unit conversion."));
            add(
                    new CqlCapability(
                            "regex-dialect",
                            "Regular Expression Dialect",
                            "The dialect of regular expressions used by the implementation. Conformant implementations SHOULD use the Perl Compatible Regular Expression (PCRE) dialect. Values for this feature should be drawn from the Name of the regular expression language list here: https://en.wikipedia.org/wiki/Comparison_of_regular-expression_engines"));
            add(new CqlCapability(
                    "supported-data-model",
                    "Supported Data Model",
                    "A supported data model, specified as the URI of the model information."));
            add(new CqlCapability(
                    "supported-function",
                    "Supported Function",
                    "A supported function, specified as the fully qualified name of the function."));
            add(new CqlCapability(
                    "unfiltered-context-retrieve",
                    "Unfiltered Context Retrieve",
                    "Whether or not the implementation supports evaluating retrieves in the unfiltered context."));
            add(new CqlCapability(
                    "related-context-retrieve",
                    "Related Context Retrieve",
                    "Whether or not the implementation supports related-context retrieves.",
                    "1.4"));
        }
    };
}
