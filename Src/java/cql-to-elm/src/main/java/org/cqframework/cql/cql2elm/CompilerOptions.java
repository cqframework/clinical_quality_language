package org.cqframework.cql.cql2elm;

import org.hl7.cql_annotations.r1.CqlToElmBase;
import org.hl7.cql_annotations.r1.CqlToElmInfo;
import org.hl7.elm.r1.Library;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static java.util.Objects.requireNonNull;

/**
 * This class provides functions for extracting and parsing CQL Compiler
 * Options from
 * a Library
 */
public class CompilerOptions {

    private CompilerOptions() {
        // intentionally empty
    }

    /**
     * Gets the compiler options used to generate an elm Library.
     *
     * Returns null if the compiler options could not be determined.
     * (for example, the Library was translated without annotations)
     *
     * @param library The library to extracts the options from.
     * @return The set of options used to translate the library.
     */
    public static CqlCompilerOptions getCompilerOptions(Library library) {
        requireNonNull(library, "library can not be null");
        if (library.getAnnotation() == null || library.getAnnotation().isEmpty()) {
            return null;
        }

        String compilerOptions = getCompilerOptions(library.getAnnotation());
        return parseCompilerOptions(compilerOptions);
    }

    private static String getCompilerOptions(List<CqlToElmBase> annotations) {
        for (CqlToElmBase base : annotations) {
            if (base instanceof CqlToElmInfo) {
                if (((CqlToElmInfo) base).getTranslatorOptions() != null) {
                    return ((CqlToElmInfo) base).getTranslatorOptions();
                }
            }
        }

        return null;
    }

    /**
     * Parses a string representing CQL compiler Options into an EnumSet. The
     * string is expected
     * to be a comma delimited list of values from the CqlCompiler.Options
     * enumeration, or property values of the CqlCompilerOptions class of the
     * form [property-name]=[property-value]
     * For example "EnableListPromotion, EnableListDemotion, validateUnits=true, signatureLevel=Overloads".
     *
     * @param compilerOptions the string to parse
     * @return the set of options
     */
    public static CqlCompilerOptions parseCompilerOptions(String compilerOptions) {
        if (compilerOptions == null || compilerOptions.isEmpty()) {
            return null;
        }

        EnumSet<CqlCompilerOptions.Options> optionSet = EnumSet.noneOf(CqlCompilerOptions.Options.class);
        String[] options = compilerOptions.trim().split(",");
        List<String> propertyOptions = new ArrayList<>();

        for (String option : options) {
            try {
                optionSet.add(CqlCompilerOptions.Options.valueOf(option));
            }
            catch (IllegalArgumentException E) {
                propertyOptions.add(option);
            }
        }

        CqlCompilerOptions result = new CqlCompilerOptions(optionSet.toArray(new CqlCompilerOptions.Options[optionSet.size()]));

        for (String propertyOption : propertyOptions) {
            if (propertyOption.indexOf("=") >= 0) {
                String[] parts = propertyOption.split("=");
                switch (parts[0]) {
                    case "validateUnits":
                        result.setValidateUnits(Boolean.valueOf(parts[1]));
                    break;
                    case "verifyOnly":
                        result.setVerifyOnly(Boolean.valueOf(parts[1]));
                    break;
                    case "enableCqlOnly":
                        result.setEnableCqlOnly(Boolean.valueOf(parts[1]));
                    break;
                    case "errorLevel":
                        result.setErrorLevel(CqlCompilerException.ErrorSeverity.valueOf(parts[1]));
                    break;
                    case "signatureLevel":
                        result.setSignatureLevel(LibraryBuilder.SignatureLevel.valueOf(parts[1]));
                    break;
                    case "compatibilityLevel":
                        result.setCompatibilityLevel(parts[1]);
                    break;
                    case "analyzeDataRequirements":
                        result.setAnalyzeDataRequirements(Boolean.valueOf(parts[1]));
                    break;
                    case "collapseDataRequirements":
                        result.setCollapseDataRequirements(Boolean.valueOf(parts[1]));
                    break;
                    default:
                        // TODO: Log a warning that an unknown property is being dropped
                    break;
                }
            }
            else {
                // TODO: Log a warning that an unknown property value is being dropped?
            }
        }
        return result;
    }

    /**
     * Gets the compiler version used to generate an elm Library.
     *
     * Returns null if the compiled version could not be determined. (for example,
     * the Library was
     * compiled without annotations)
     *
     * @param library The library to extracts the compiler version from.
     * @return The version of compiler used to compiler the library.
     */
    public static String getCompilerVersion(Library library) {
        requireNonNull(library, "library can not be null");
        if (library.getAnnotation() == null || library.getAnnotation().isEmpty()) {
            return null;
        }

        return getCompilerVersion(library.getAnnotation());
    }

    private static String getCompilerVersion(List<CqlToElmBase> annotations) {
        for (CqlToElmBase o : annotations) {
            if (o instanceof CqlToElmInfo) {
                CqlToElmInfo c = (CqlToElmInfo) o;
                return c.getTranslatorVersion();
            }
        }

        return null;
    }
}
