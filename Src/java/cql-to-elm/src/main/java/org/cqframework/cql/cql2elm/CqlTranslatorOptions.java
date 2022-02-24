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
    private String compatibilityLevel = "1.5";
    private CqlTranslatorException.ErrorSeverity errorLevel = CqlTranslatorException.ErrorSeverity.Info;
    private LibraryBuilder.SignatureLevel signatureLevel = LibraryBuilder.SignatureLevel.None;
    private boolean analyzeDataRequirements = false;
    private boolean collapseDataRequirements = false;

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

    public CqlTranslatorOptions(CqlTranslator.Options... options) {
        this(CqlTranslatorException.ErrorSeverity.Info, LibraryBuilder.SignatureLevel.None, options);
    }

    public CqlTranslatorOptions(CqlTranslatorException.ErrorSeverity errorLevel, LibraryBuilder.SignatureLevel signatureLevel, CqlTranslator.Options... options) {
        this.setOptions(options);
        this.errorLevel = errorLevel;
        this.signatureLevel = signatureLevel;
    }

    public CqlTranslatorOptions(CqlTranslator.Format format, boolean dateRangeOptimizations,
                                boolean annotations, boolean locators, boolean resultTypes, boolean verifyOnly,
                                boolean detailedErrors, CqlTranslatorException.ErrorSeverity errorLevel,
                                boolean disableListTraversal, boolean disableListDemotion, boolean disableListPromotion,
                                boolean enableIntervalDemotion, boolean enableIntervalPromotion,
                                boolean disableMethodInvocation, boolean requireFromKeyword, boolean validateUnits,
                                boolean disableDefaultModelInfoLoad,
                                LibraryBuilder.SignatureLevel signatureLevel, String compatibilityLevel) {

        formats.add(format);
        this.verifyOnly = verifyOnly;
        this.errorLevel = errorLevel;
        this.signatureLevel = signatureLevel;
        this.validateUnits = validateUnits;
        this.compatibilityLevel = compatibilityLevel;

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
        if (disableDefaultModelInfoLoad) {
            options.add(CqlTranslator.Options.DisableDefaultModelInfoLoad);
        }
    }

    public EnumSet<CqlTranslator.Options> getOptions() {
        return this.options;
    }

    public void setOptions(CqlTranslator.Options... options) {
        if (options != null) {
            for (CqlTranslator.Options option : options) {
                this.options.add(option);
            }
        }
    }

    public CqlTranslatorOptions withOptions(CqlTranslator.Options... options) {
        setOptions(options);
        return this;
    }

    public List<CqlTranslator.Format> getFormats() {
        return this.formats;
    }

    public CqlTranslatorOptions withFormat(CqlTranslator.Format format) {
        formats.add(format);
        return this;
    }

    public String getCompatibilityLevel() {
        return this.compatibilityLevel;
    }

    public void setCompatibilityLevel(String compatibilityLevel) {
        this.compatibilityLevel = compatibilityLevel;
    }

    public CqlTranslatorOptions withCompatibilityLevel(String compatibilityLevel) {
        setCompatibilityLevel(compatibilityLevel);
        return this;
    }

    public boolean getVerifyOnly() {
        return this.verifyOnly;
    }

    public void setVerifyOnly(boolean verifyOnly) {
        this.verifyOnly = verifyOnly;
    }

    public CqlTranslatorOptions withVerifyOnly(boolean verifyOnly) {
        setVerifyOnly(verifyOnly);
        return this;
    }

    public boolean getValidateUnits() {
        return this.validateUnits;
    }

    public void setValidateUnits(boolean validateUnits) {
        this.validateUnits = validateUnits;
    }

    public CqlTranslatorOptions withValidateUnits(boolean validateUnits) {
        setValidateUnits(validateUnits);
        return this;
    }

    public CqlTranslatorException.ErrorSeverity getErrorLevel() {
        return this.errorLevel;
    }

    public void setErrorLevel(CqlTranslatorException.ErrorSeverity errorLevel) {
        this.errorLevel = errorLevel;
    }

    public CqlTranslatorOptions withErrorLevel(CqlTranslatorException.ErrorSeverity errorLevel) {
        setErrorLevel(errorLevel);
        return this;
    }

    public LibraryBuilder.SignatureLevel getSignatureLevel() {
        return this.signatureLevel;
    }

    public void setSignatureLevel(LibraryBuilder.SignatureLevel signatureLevel) {
        this.signatureLevel = signatureLevel;
    }

    public CqlTranslatorOptions withSignatureLevel(LibraryBuilder.SignatureLevel signatureLevel) {
        setSignatureLevel(signatureLevel);
        return this;
    }

    public boolean getCollapseDataRequirements() {
        return this.collapseDataRequirements;
    }

    public void setCollapseDataRequirements(boolean collapseDataRequirements) {
        this.collapseDataRequirements = collapseDataRequirements;
    }

    public CqlTranslatorOptions withCollapseDataRequirements(boolean collapseDataRequirements) {
        setCollapseDataRequirements(collapseDataRequirements);
        return this;
    }

    public boolean getAnalyzeDataRequirements() {
        return this.analyzeDataRequirements;
    }

    public void setAnalyzeDataRequirements(boolean analyzeDataRequirements) {
        this.analyzeDataRequirements = analyzeDataRequirements;
    }

    public CqlTranslatorOptions withAnalyzeDataRequirements(boolean analyzeDataRequirements) {
        setAnalyzeDataRequirements(analyzeDataRequirements);
        return this;
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

