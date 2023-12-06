package org.cqframework.cql.cql2elm;

import org.hl7.cql_annotations.r1.CqlToElmBase;
import org.hl7.cql_annotations.r1.CqlToElmInfo;
import org.hl7.elm.r1.Library;

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
    public static Set<CqlCompilerOptions.Options> getCompilerOptions(Library library) {
        requireNonNull(library, "library required");
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
     * enumeration.
     * For example "EnableListPromotion, EnableListDemotion".
     *
     * @param compilerOptions the string to parse
     * @return the set of options
     */
    public static Set<CqlCompilerOptions.Options> parseCompilerOptions(String compilerOptions) {
        if (compilerOptions == null || compilerOptions.isEmpty()) {
            return null;
        }

        EnumSet<CqlCompilerOptions.Options> optionSet = EnumSet.noneOf(CqlCompilerOptions.Options.class);
        String[] options = compilerOptions.trim().split(",");

        for (String option : options) {
            optionSet.add(CqlCompilerOptions.Options.valueOf(option));
        }

        return optionSet;
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
        requireNonNull(library, "library required");
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
