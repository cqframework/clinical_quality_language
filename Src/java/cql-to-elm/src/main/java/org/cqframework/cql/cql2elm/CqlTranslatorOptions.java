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

    /**
     * Constructor with arbitrary number of options utilizing default ErrorSeverity (Info) and SignatureLevel (None)
     * @param options
     */
    public CqlTranslatorOptions(CqlTranslator.Options... options) {
        this(CqlTranslatorException.ErrorSeverity.Info, LibraryBuilder.SignatureLevel.None, options);
    }

    /**
     * Constructor with defined ErrorSeverity, SignatureLevel, and arbitrary number of options.
     *
     * @param errorLevel
     * @param signatureLevel
     * @param options
     */
    public CqlTranslatorOptions(CqlTranslatorException.ErrorSeverity errorLevel, LibraryBuilder.SignatureLevel signatureLevel, CqlTranslator.Options... options) {
        this.setOptions(options);
        this.errorLevel = errorLevel;
        this.signatureLevel = signatureLevel;
    }

    /**
     * Constructor using defined Format, SignatureLevel, and Compatibility Level, boolean set to true denotes addition of predefined option
     *
     *
     * @param format CqlTranslator.Format
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
    public CqlTranslatorOptions(CqlTranslator.Format format, boolean dateRangeOptimizations,
                                boolean annotations, boolean locators, boolean resultTypes, boolean verifyOnly,
                                boolean detailedErrors, CqlTranslatorException.ErrorSeverity errorLevel,
                                boolean disableListTraversal, boolean disableListDemotion, boolean disableListPromotion,
                                boolean enableIntervalDemotion, boolean enableIntervalPromotion,
                                boolean disableMethodInvocation, boolean requireFromKeyword, boolean validateUnits,
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
    }

    /**
     * Returns instance of CqlTranslatorOptions options
     * @return
     */
    public EnumSet<CqlTranslator.Options> getOptions() {
        return this.options;
    }

    /**
     * Set arbitrary number of options
     * @param options
     */
    public void setOptions(CqlTranslator.Options... options) {
        if (options != null) {
            for (CqlTranslator.Options option : options) {
                this.options.add(option);
            }
        }
    }

    /**
     * Return this instance of CqlTranslatorOptions using new collection of arbitrary number of options
     * @param options
     * @return
     */
    public CqlTranslatorOptions withOptions(CqlTranslator.Options... options) {
        setOptions(options);
        return this;
    }

    /**
     * Returns instance of CqlTranslatorOptions formats
     *
     * @return
     */
    public List<CqlTranslator.Format> getFormats() {
        return this.formats;
    }

    /**
     * Return this instance of CqlTranslatorOptions with addition of newly assigned format
     * @param format
     * @return
     */
    public CqlTranslatorOptions withFormat(CqlTranslator.Format format) {
        formats.add(format);
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
    public CqlTranslatorOptions withCompatibilityLevel(String compatibilityLevel) {
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
    public CqlTranslatorOptions withVerifyOnly(boolean verifyOnly) {
        setVerifyOnly(verifyOnly);
        return this;
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
    public CqlTranslatorOptions withValidateUnits(boolean validateUnits) {
        setValidateUnits(validateUnits);
        return this;
    }

    /**
     * Return instance of CqlTranslatorOptions errorLevel (CqlTranslatorException.ErrorSeverity)
     * @return
     */
    public CqlTranslatorException.ErrorSeverity getErrorLevel() {
        return this.errorLevel;
    }

    /**
     * Set new errorLevel (CqlTranslatorException.ErrorSeverity)
     * @param errorLevel
     */
    public void setErrorLevel(CqlTranslatorException.ErrorSeverity errorLevel) {
        this.errorLevel = errorLevel;
    }

    /**
     * Return this instance of CqlTranslatorOptions with addition of newly assigned errorLevel (CqlTranslatorException.ErrorSeverity) 
     * @param errorLevel
     * @return
     */
    public CqlTranslatorOptions withErrorLevel(CqlTranslatorException.ErrorSeverity errorLevel) {
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
    public CqlTranslatorOptions withSignatureLevel(LibraryBuilder.SignatureLevel signatureLevel) {
        setSignatureLevel(signatureLevel);
        return this;
    }

    /**
     * Return instance of CqlTranslatorOptions cllapseDataRequirements boolean
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
    public CqlTranslatorOptions withCollapseDataRequirements(boolean collapseDataRequirements) {
        setCollapseDataRequirements(collapseDataRequirements);
        return this;
    }

    /**
     * Return instance of CqlTranslatorOptions analayzedDataRequirements boolean
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

    /**
     * Return this instance of CqlTranslatorOptions with addition of newly assigned analyzedDataRequirements boolean
     * @param analyzeDataRequirements
     * @return
     */
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

