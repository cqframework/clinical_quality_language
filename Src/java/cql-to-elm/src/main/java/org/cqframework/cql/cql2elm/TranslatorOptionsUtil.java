package org.cqframework.cql.cql2elm;

import org.hl7.cql_annotations.r1.CqlToElmBase;
import org.hl7.cql_annotations.r1.CqlToElmInfo;
import org.hl7.elm.r1.Library;

import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * This class provides functions for extracting and parsing CQL Translator Options from
 * a Library
 */
public class TranslatorOptionsUtil {

    /**
     * Gets the translator options used to generate an elm Library.
     *
     * Returns null if the translator options could not be determined.
     * (for example, the Library was translated without annotations)
     * @param library The library to extracts the options from.
     * @return The set of options used to translate the library.
     */
    public static EnumSet<CqlTranslatorOptions.Options> getTranslatorOptions(Library library) {
        requireNonNull(library, "library can not be null");
        if (library.getAnnotation() == null || library.getAnnotation().isEmpty()) {
            return null;
        }

        String translatorOptions = getTranslatorOptions(library.getAnnotation());
        return parseTranslatorOptions(translatorOptions);
    }

    private static String getTranslatorOptions(List<CqlToElmBase> annotations){
        for (CqlToElmBase base : annotations) {
            if (base instanceof CqlToElmInfo) {
                if (((CqlToElmInfo)base).getTranslatorOptions() != null) {
                    return ((CqlToElmInfo)base).getTranslatorOptions();
                }
            }
        }

        return null;
    }

    /**
     * Parses a string representing CQL Translator Options into an EnumSet. The string is expected
     * to be a comma delimited list of values from the CqlTranslator.Options enumeration.
     * For example "EnableListPromotion, EnableListDemotion".
     * @param translatorOptions the string to parse
     * @return the set of options
     */
    public static EnumSet<CqlTranslatorOptions.Options> parseTranslatorOptions(String translatorOptions) {
        if (translatorOptions == null || translatorOptions.isEmpty()) {
            return null;
        }

        EnumSet<CqlTranslatorOptions.Options> optionSet = EnumSet.noneOf(CqlTranslatorOptions.Options.class);
        String[] options = translatorOptions.trim().split(",");

        for (String option : options) {
            optionSet.add(CqlTranslatorOptions.Options.valueOf(option));
        }

        return optionSet;
    }
}
