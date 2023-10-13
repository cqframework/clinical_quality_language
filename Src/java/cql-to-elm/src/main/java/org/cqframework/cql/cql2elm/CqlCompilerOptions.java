package org.cqframework.cql.cql2elm;


import java.util.EnumSet;
import java.util.Set;

/**
 * translation options for Cql source files
 */
public class CqlCompilerOptions {
    public enum Options {
        EnableDateRangeOptimization,
        EnableAnnotations,
        EnableLocators,
        EnableResultTypes,
        EnableDetailedErrors,
        DisableListTraversal,
        DisableListDemotion,
        DisableListPromotion,
        EnableIntervalDemotion,
        EnableIntervalPromotion,
        DisableMethodInvocation,
        RequireFromKeyword,
        DisableDefaultModelInfoLoad
    }

    private EnumSet<Options> options = EnumSet.noneOf(Options.class);
    private boolean validateUnits = true;
    private boolean verifyOnly = false;
    private boolean enableCqlOnly = false;
    private String compatibilityLevel = "1.5";
    private CqlCompilerException.ErrorSeverity errorLevel = CqlCompilerException.ErrorSeverity.Info;
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
     * @return
     */
    public static CqlCompilerOptions defaultOptions() {
        // Default options based on recommended settings: http://build.fhir.org/ig/HL7/cqf-measures/using-cql.html#translation-to-elm
        CqlCompilerOptions result = new CqlCompilerOptions();
        result.options.add(Options.EnableAnnotations);
        result.options.add(Options.EnableLocators);
        result.options.add(Options.DisableListDemotion);
        result.options.add(Options.DisableListPromotion);
        return result;
    }

    public CqlCompilerOptions() {
    }

    /**
     * Constructor with arbitrary number of options utilizing default ErrorSeverity (Info) and SignatureLevel (None)
     * @param options
     */
    public CqlCompilerOptions(Options... options) {
        this(CqlCompilerException.ErrorSeverity.Info, LibraryBuilder.SignatureLevel.None, options);
    }

    public CqlCompilerOptions(CqlCompilerException.ErrorSeverity errorLevel, Options... options) {
        this(errorLevel, LibraryBuilder.SignatureLevel.None, options);
    }

    /**
     * Constructor with defined ErrorSeverity, SignatureLevel, and arbitrary number of options.
     *
     * @param errorLevel
     * @param signatureLevel
     * @param options
     */
    public CqlCompilerOptions(CqlCompilerException.ErrorSeverity errorLevel, LibraryBuilder.SignatureLevel signatureLevel, Options... options) {
        this.setOptions(options);
        this.errorLevel = errorLevel;
        this.signatureLevel = signatureLevel;
    }

    /**
     * Constructor using defined SignatureLevel, and Compatibility Level, boolean set to true denotes addition of predefined option
     *
     * @param dateRangeOptimizations boolean
     * @param annotations boolean
     * @param locators boolean
     * @param resultTypes boolean
     * @param verifyOnly boolean
     * @param detailedErrors boolean
     * @param errorLevel boolean
     * @param disableListTraversal boolean
     * @param disableListDemotion boolean
     * @param disableListPromotion boolean
     * @param enableIntervalDemotion boolean
     * @param enableIntervalPromotion boolean
     * @param disableMethodInvocation boolean
     * @param requireFromKeyword boolean
     * @param validateUnits boolean
     * @param signatureLevel LibraryBuilder.SignatureLevel
     * @param compatibilityLevel String
     */
    public CqlCompilerOptions(boolean dateRangeOptimizations,
                                boolean annotations, boolean locators, boolean resultTypes, boolean verifyOnly,
                                boolean detailedErrors, CqlCompilerException.ErrorSeverity errorLevel,
                                boolean disableListTraversal, boolean disableListDemotion, boolean disableListPromotion,
                                boolean enableIntervalDemotion, boolean enableIntervalPromotion,
                                boolean disableMethodInvocation, boolean requireFromKeyword, boolean validateUnits,
                                boolean disableDefaultModelInfoLoad,
                                LibraryBuilder.SignatureLevel signatureLevel, String compatibilityLevel) {
        this.verifyOnly = verifyOnly;
        this.errorLevel = errorLevel;
        this.signatureLevel = signatureLevel;
        this.validateUnits = validateUnits;
        this.compatibilityLevel = compatibilityLevel;

        if (dateRangeOptimizations) {
            options.add(Options.EnableDateRangeOptimization);
        }
        if (annotations) {
            options.add(Options.EnableAnnotations);
        }
        if (locators) {
            options.add(Options.EnableLocators);
        }
        if (resultTypes) {
            options.add(Options.EnableResultTypes);
        }
        if (detailedErrors) {
            options.add(Options.EnableDetailedErrors);
        }
        if (disableListTraversal) {
            options.add(Options.DisableListTraversal);
        }
        if (disableListDemotion) {
            options.add(Options.DisableListDemotion);
        }
        if (disableListPromotion) {
            options.add(Options.DisableListPromotion);
        }
        if (enableIntervalDemotion) {
            options.add(Options.EnableIntervalDemotion);
        }
        if (enableIntervalPromotion) {
            options.add(Options.EnableIntervalPromotion);
        }
        if (disableMethodInvocation) {
            options.add(Options.DisableMethodInvocation);
        }
        if (requireFromKeyword) {
            options.add(Options.RequireFromKeyword);
        }
        if (disableDefaultModelInfoLoad) {
            options.add(Options.DisableDefaultModelInfoLoad);
        }
    }

    /**
     * Returns instance of CqlTranslatorOptions options
     * @return
     */

    public Set<Options> getOptions() {
        return this.options;
    }

    /**
     * Set arbitrary number of options
     * @param options
     */
    public void setOptions(Options... options) {
        if (options != null) {
            for (Options option : options) {
                this.options.add(option);
            }
        }
    }

    /**
     * Return this instance of CqlTranslatorOptions using new collection of arbitrary number of options
     * @param options
     * @return
     */
    public CqlCompilerOptions withOptions(Options... options) {
        setOptions(options);
        return this;
    }

    /**
     * Return instance of CqlTranslatorOptions compatibilityLevel
     * @return
     */
    public String getCompatibilityLevel() {
        return this.compatibilityLevel;
    }

    /**
     * Set new compatibilityLevel
     * @param compatibilityLevel
     */
    public void setCompatibilityLevel(String compatibilityLevel) {
        this.compatibilityLevel = compatibilityLevel;
    }

    /**
     *  Return this instance of CqlTranslatorOptions with addition of newly assigned compatibilityLevel
     * @param compatibilityLevel
     * @return
     */
    public CqlCompilerOptions withCompatibilityLevel(String compatibilityLevel) {
        setCompatibilityLevel(compatibilityLevel);
        return this;
    }

    /**
     * Return instance of CqlTranslatorOptions verifyOnly boolean
     * @return
     */
    public boolean getVerifyOnly() {
        return this.verifyOnly;
    }

    /**
     * Set new verifyOnly boolean
     * @param verifyOnly
     */
    public void setVerifyOnly(boolean verifyOnly) {
        this.verifyOnly = verifyOnly;
    }

    /**
     * Return this instance of CqlTranslatorOptions with addition of newly assigned verifyOnly boolean
     * @param verifyOnly
     * @return
     */
    public CqlCompilerOptions withVerifyOnly(boolean verifyOnly) {
        setVerifyOnly(verifyOnly);
        return this;
    }

    /**
     * Return instance of CqlTranslatorOptions enableCqlOnly boolean
     * @return
     */
    public boolean getEnableCqlOnly() {
        return this.enableCqlOnly;
    }

    /**
     * Set new enableCqlOnly boolean
     * @param enableCqlOnly
     */
    public void setEnableCqlOnly(boolean enableCqlOnly) {
        this.enableCqlOnly = enableCqlOnly;
    }

    /**
     * Return instance of CqlTranslatorOptions validateUnits boolean
     * @return
     */
    public boolean getValidateUnits() {
        return this.validateUnits;
    }

    /**
     * Set new validateUnits boolean
     * @param validateUnits
     */
    public void setValidateUnits(boolean validateUnits) {
        this.validateUnits = validateUnits;
    }

    /**
     * Return this instance of CqlTranslatorOptions with addition of newly assigned validateUnits boolean
     * @param validateUnits
     * @return
     */
    public CqlCompilerOptions withValidateUnits(boolean validateUnits) {
        setValidateUnits(validateUnits);
        return this;
    }

    /**
     * Return instance of CqlTranslatorOptions errorLevel (CqlTranslatorException.ErrorSeverity)
     * @return
     */
    public CqlCompilerException.ErrorSeverity getErrorLevel() {
        return this.errorLevel;
    }

    /**
     * Set new errorLevel (CqlTranslatorException.ErrorSeverity)
     * @param errorLevel
     */
    public void setErrorLevel(CqlCompilerException.ErrorSeverity errorLevel) {
        this.errorLevel = errorLevel;
    }

    /**
     * Return this instance of CqlTranslatorOptions with addition of newly assigned errorLevel (CqlTranslatorException.ErrorSeverity)
     * @param errorLevel
     * @return
     */
    public CqlCompilerOptions withErrorLevel(CqlCompilerException.ErrorSeverity errorLevel) {
        setErrorLevel(errorLevel);
        return this;
    }

    /**
     * Return instance of CqlTranslatorOptions signatureLevel (LibraryBuilder.SignatureLevel)
     * @return
     */
    public LibraryBuilder.SignatureLevel getSignatureLevel() {
        return this.signatureLevel;
    }

    /**
     * Set new signatureLevel (LibraryBuilder.SignatureLevel)
     * @param signatureLevel
     */
    public void setSignatureLevel(LibraryBuilder.SignatureLevel signatureLevel) {
        this.signatureLevel = signatureLevel;
    }

    /**
     * Return this instance of CqlTranslatorOptions with addition of newly assigned signatureLevel (LibraryBuilder.SignatureLevel)
     * @param signatureLevel
     * @return
     */
    public CqlCompilerOptions withSignatureLevel(LibraryBuilder.SignatureLevel signatureLevel) {
        setSignatureLevel(signatureLevel);
        return this;
    }

    /**
     * Return instance of CqlTranslatorOptions collapseDataRequirements boolean
     * @return
     */
    public boolean getCollapseDataRequirements() {
        return this.collapseDataRequirements;
    }

    /**
     * Set new collapseDataRequirements boolean
     * @param collapseDataRequirements
     */
    public void setCollapseDataRequirements(boolean collapseDataRequirements) {
        this.collapseDataRequirements = collapseDataRequirements;
    }

    /**
     * Return this instance of CqlTranslatorOptions with addition of newly assigned collapseDataRequirements boolean
     * @param collapseDataRequirements
     * @return
     */
    public CqlCompilerOptions withCollapseDataRequirements(boolean collapseDataRequirements) {
        setCollapseDataRequirements(collapseDataRequirements);
        return this;
    }

    /**
     * Return instance of CqlTranslatorOptions analyzeDataRequirements boolean
     * @return
     */
    public boolean getAnalyzeDataRequirements() {
        return this.analyzeDataRequirements;
    }

    /**
     * Set new analyzeDataRequirements boolean
     * @param analyzeDataRequirements
     */
    public void setAnalyzeDataRequirements(boolean analyzeDataRequirements) {
        this.analyzeDataRequirements = analyzeDataRequirements;
    }

    /**git
     * Return this instance of CqlTranslatorOptions with addition of newly assigned analyzedDataRequirements boolean
     * @param analyzeDataRequirements
     * @return
     */
    public CqlCompilerOptions withAnalyzeDataRequirements(boolean analyzeDataRequirements) {
        setAnalyzeDataRequirements(analyzeDataRequirements);
        return this;
    }

    @Override
    public String toString() {
        if (this.getOptions() != null) {
            StringBuilder translatorOptions = new StringBuilder();
            for (Options option : this.getOptions()) {
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

