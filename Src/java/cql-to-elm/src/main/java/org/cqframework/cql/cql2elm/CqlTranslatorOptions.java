package org.cqframework.cql.cql2elm;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * translation options for Cql source files
 */
public class CqlTranslatorOptions {
    private EnumSet<CqlTranslator.Options> options = EnumSet.noneOf(CqlTranslator.Options.class);
    private List<CqlTranslator.Format> formats = new ArrayList<>();
    private boolean validateUnits = true;
    private boolean verifyOnly = false;
    private CqlTranslatorException.ErrorSeverity errorLevel = CqlTranslatorException.ErrorSeverity.Info;
    private LibraryBuilder.SignatureLevel signatureLevel = LibraryBuilder.SignatureLevel.None;

    /**
     * Returns default translator options:
     * EnableAnnotations
     * EnableLocators
     * DisableListDemotion
     * DisableListPromotion
     * ErrorSeverity.Info
     * SignatureLevel.None
     * Format.XML
     * @return
     */
    public static CqlTranslatorOptions defaultOptions() {
        // Default options based on recommended settings: http://build.fhir.org/ig/HL7/cqf-measures/using-cql.html#translation-to-elm
        CqlTranslatorOptions result = new CqlTranslatorOptions();
        result.options.add(CqlTranslator.Options.EnableAnnotations);
        result.options.add(CqlTranslator.Options.EnableLocators);
        result.options.add(CqlTranslator.Options.DisableListDemotion);
        result.options.add(CqlTranslator.Options.DisableListPromotion);
        return result;
//    private CqlTranslator.Options[] getDefaultOptions() {
//      ArrayList<CqlTranslator.Options> options = new ArrayList<>();
//      return options.toArray(new CqlTranslator.Options[options.size()]);
//    }
    }

    public CqlTranslatorOptions() {
    }

    public CqlTranslatorOptions(CqlTranslatorException.ErrorSeverity errorLevel, LibraryBuilder.SignatureLevel signatureLevel, CqlTranslator.Options... options) {
        if (options != null) {
            for (CqlTranslator.Options option : options) {
                this.options.add(option);
            }
        }
        this.errorLevel = errorLevel;
        this.signatureLevel = signatureLevel;
    }

    public CqlTranslatorOptions(CqlTranslator.Format format, boolean dateRangeOptimizations,
                                boolean annotations, boolean locators, boolean resultTypes, boolean verifyOnly,
                                boolean detailedErrors, CqlTranslatorException.ErrorSeverity errorLevel,
                                boolean disableListTraversal, boolean disableListDemotion, boolean disableListPromotion,
                                boolean enableIntervalDemotion, boolean enableIntervalPromotion,
                                boolean disableMethodInvocation, boolean requireFromKeyword, boolean validateUnits,
                                LibraryBuilder.SignatureLevel signatureLevel) {

        formats.add(format);
        this.verifyOnly = verifyOnly;
        this.errorLevel = errorLevel;
        this.signatureLevel = signatureLevel;
        this.validateUnits = validateUnits;

        if (dateRangeOptimizations) {
            options.add(CqlTranslator.Options.EnableDateRangeOptimization);
        }
        if (annotations) {
            options.add(CqlTranslator.Options.EnableAnnotations);
        }
        if (locators) {
            options.add(CqlTranslator.Options.EnableLocators);
        }
        if (resultTypes) {
            options.add(CqlTranslator.Options.EnableResultTypes);
        }
        if (detailedErrors) {
            options.add(CqlTranslator.Options.EnableDetailedErrors);
        }
        if (disableListTraversal) {
            options.add(CqlTranslator.Options.DisableListTraversal);
        }
        if (disableListDemotion) {
            options.add(CqlTranslator.Options.DisableListDemotion);
        }
        if (disableListPromotion) {
            options.add(CqlTranslator.Options.DisableListPromotion);
        }
        if (enableIntervalDemotion) {
            options.add(CqlTranslator.Options.EnableIntervalDemotion);
        }
        if (enableIntervalPromotion) {
            options.add(CqlTranslator.Options.EnableIntervalPromotion);
        }
        if (disableMethodInvocation) {
            options.add(CqlTranslator.Options.DisableMethodInvocation);
        }
        if (requireFromKeyword) {
            options.add(CqlTranslator.Options.RequireFromKeyword);
        }
    }

    public EnumSet<CqlTranslator.Options> getOptions() {
        return this.options;
    }

    public List<CqlTranslator.Format> getFormats() {
        return this.formats;
    }

    public boolean getVerifyOnly() {
        return this.verifyOnly;
    }

    public boolean getValidateUnits() {
        return this.validateUnits;
    }

    public void setValidateUnits(boolean validateUnits) {
        this.validateUnits = validateUnits;
    }

    public CqlTranslatorException.ErrorSeverity getErrorLevel() {
        return this.errorLevel;
    }

    public void setErrorLevel(CqlTranslatorException.ErrorSeverity errorLevel) {
        this.errorLevel = errorLevel;
    }

    public LibraryBuilder.SignatureLevel getSignatureLevel() {
        return this.signatureLevel;
    }

    public void setSignatureLevel(LibraryBuilder.SignatureLevel signatureLevel) {
        this.signatureLevel = signatureLevel;
    }

    @Override
    public String toString() {
        if (this.getOptions() != null) {
            StringBuilder translatorOptions = new StringBuilder();
            for (CqlTranslator.Options option : this.getOptions()) {
                if (translatorOptions.length() > 0) {
                    translatorOptions.append(",");
                }
                translatorOptions.append(option.name());
            }
            return translatorOptions.toString();
        }
        return null;
    }
}

