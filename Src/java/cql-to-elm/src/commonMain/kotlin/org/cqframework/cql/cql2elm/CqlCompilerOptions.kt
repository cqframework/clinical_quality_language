package org.cqframework.cql.cql2elm

import kotlin.js.ExperimentalJsExport
import kotlin.js.ExperimentalJsStatic
import kotlin.js.JsExport
import kotlin.js.JsStatic
import kotlin.jvm.JvmStatic
import kotlinx.serialization.Serializable
import org.cqframework.cql.cql2elm.LibraryBuilder.SignatureLevel
import org.cqframework.cql.shared.JsOnlyExport

/** Translation options for CQL source files */
@OptIn(ExperimentalJsExport::class)
@JsOnlyExport
@Suppress("NON_EXPORTABLE_TYPE")
@Serializable
class CqlCompilerOptions() {
    enum class Options {
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
        DisableDefaultModelInfoLoad,
    }

    val options = mutableSetOf<Options>()
    var validateUnits: Boolean = true
    var verifyOnly: Boolean = false
    var enableCqlOnly: Boolean = false
    var compatibilityLevel: String = "1.5"
    var errorLevel: CqlCompilerException.ErrorSeverity? = CqlCompilerException.ErrorSeverity.Info
    var signatureLevel: SignatureLevel = SignatureLevel.Overloads
    var analyzeDataRequirements: Boolean = false
    var collapseDataRequirements: Boolean = false

    /**
     * Constructor with arbitrary number of options utilizing default ErrorSeverity (Info) and
     * SignatureLevel (None)
     *
     * @param options
     */
    @JsExport.Ignore
    @Suppress("SpreadOperator")
    constructor(
        vararg options: Options
    ) : this(CqlCompilerException.ErrorSeverity.Info, SignatureLevel.None, *options)

    @JsExport.Ignore
    @Suppress("SpreadOperator")
    constructor(
        errorLevel: CqlCompilerException.ErrorSeverity?,
        vararg options: Options,
    ) : this(errorLevel, SignatureLevel.None, *options)

    /**
     * Constructor with defined ErrorSeverity, SignatureLevel, and arbitrary number of options.
     *
     * @param errorLevel
     * @param signatureLevel
     * @param options
     */
    @JsExport.Ignore
    @Suppress("SpreadOperator")
    constructor(
        errorLevel: CqlCompilerException.ErrorSeverity?,
        signatureLevel: SignatureLevel,
        vararg options: Options,
    ) : this() {
        setOptions(*options)
        this.errorLevel = errorLevel
        this.signatureLevel = signatureLevel
    }

    /**
     * Constructor using defined SignatureLevel, and Compatibility Level, boolean set to true
     * denotes addition of predefined option
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
    @JsExport.Ignore
    @Suppress("LongParameterList")
    constructor(
        dateRangeOptimizations: Boolean,
        annotations: Boolean,
        locators: Boolean,
        resultTypes: Boolean,
        verifyOnly: Boolean,
        detailedErrors: Boolean,
        errorLevel: CqlCompilerException.ErrorSeverity?,
        disableListTraversal: Boolean,
        disableListDemotion: Boolean,
        disableListPromotion: Boolean,
        enableIntervalDemotion: Boolean,
        enableIntervalPromotion: Boolean,
        disableMethodInvocation: Boolean,
        requireFromKeyword: Boolean,
        validateUnits: Boolean,
        disableDefaultModelInfoLoad: Boolean,
        signatureLevel: SignatureLevel,
        compatibilityLevel: String,
    ) : this() {
        this.verifyOnly = verifyOnly
        this.errorLevel = errorLevel
        this.signatureLevel = signatureLevel
        this.validateUnits = validateUnits
        this.compatibilityLevel = compatibilityLevel
        if (dateRangeOptimizations) {
            options.add(Options.EnableDateRangeOptimization)
        }
        if (annotations) {
            options.add(Options.EnableAnnotations)
        }
        if (locators) {
            options.add(Options.EnableLocators)
        }
        if (resultTypes) {
            options.add(Options.EnableResultTypes)
        }
        if (detailedErrors) {
            options.add(Options.EnableDetailedErrors)
        }
        if (disableListTraversal) {
            options.add(Options.DisableListTraversal)
        }
        if (disableListDemotion) {
            options.add(Options.DisableListDemotion)
        }
        if (disableListPromotion) {
            options.add(Options.DisableListPromotion)
        }
        if (enableIntervalDemotion) {
            options.add(Options.EnableIntervalDemotion)
        }
        if (enableIntervalPromotion) {
            options.add(Options.EnableIntervalPromotion)
        }
        if (disableMethodInvocation) {
            options.add(Options.DisableMethodInvocation)
        }
        if (requireFromKeyword) {
            options.add(Options.RequireFromKeyword)
        }
        if (disableDefaultModelInfoLoad) {
            options.add(Options.DisableDefaultModelInfoLoad)
        }
    }

    /**
     * Set arbitrary number of options
     *
     * @param options
     */
    fun setOptions(vararg options: Options) {
        for (option: Options in options) {
            this.options.add(option)
        }
    }

    /**
     * Return this instance of CqlTranslatorOptions using new collection of arbitrary number of
     * options
     *
     * @param options
     * @return
     */
    fun withOptions(vararg options: Options): CqlCompilerOptions {
        setOptions(*options)
        return this
    }

    /**
     * Return this instance of CqlTranslatorOptions with addition of newly assigned
     * compatibilityLevel
     *
     * @param compatibilityLevel
     * @return
     */
    fun withCompatibilityLevel(compatibilityLevel: String): CqlCompilerOptions {
        this.compatibilityLevel = compatibilityLevel
        return this
    }

    /**
     * Return this instance of CqlTranslatorOptions with addition of newly assigned verifyOnly
     * boolean
     *
     * @param verifyOnly
     * @return
     */
    fun withVerifyOnly(verifyOnly: Boolean): CqlCompilerOptions {
        this.verifyOnly = verifyOnly
        return this
    }

    /**
     * Return this instance of CqlTranslatorOptions with addition of newly assigned validateUnits
     * boolean
     *
     * @param validateUnits
     * @return
     */
    fun withValidateUnits(validateUnits: Boolean): CqlCompilerOptions {
        this.validateUnits = validateUnits
        return this
    }

    /**
     * Return this instance of CqlTranslatorOptions with addition of newly assigned errorLevel
     * (CqlTranslatorException.ErrorSeverity)
     *
     * @param errorLevel
     * @return
     */
    fun withErrorLevel(errorLevel: CqlCompilerException.ErrorSeverity?): CqlCompilerOptions {
        this.errorLevel = errorLevel
        return this
    }

    /**
     * Return this instance of CqlTranslatorOptions with addition of newly assigned signatureLevel
     * (LibraryBuilder.SignatureLevel)
     *
     * @param signatureLevel
     * @return
     */
    fun withSignatureLevel(signatureLevel: SignatureLevel): CqlCompilerOptions {
        this.signatureLevel = signatureLevel
        return this
    }

    /**
     * Return this instance of CqlTranslatorOptions with addition of newly assigned
     * collapseDataRequirements boolean
     *
     * @param collapseDataRequirements
     * @return
     */
    fun withCollapseDataRequirements(collapseDataRequirements: Boolean): CqlCompilerOptions {
        this.collapseDataRequirements = collapseDataRequirements
        return this
    }

    /**
     * git Return this instance of CqlTranslatorOptions with addition of newly assigned
     * analyzedDataRequirements boolean
     *
     * @param analyzeDataRequirements
     * @return
     */
    fun withAnalyzeDataRequirements(analyzeDataRequirements: Boolean): CqlCompilerOptions {
        this.analyzeDataRequirements = analyzeDataRequirements
        return this
    }

    override fun toString(): String {
        val translatorOptions: StringBuilder = StringBuilder()
        for (option: Options in options) {
            if (translatorOptions.isNotEmpty()) {
                translatorOptions.append(",")
            }
            translatorOptions.append(option.name)
        }
        return translatorOptions.toString()
    }

    companion object {
        /**
         * Returns default translator options: EnableAnnotations EnableLocators DisableListDemotion
         * DisableListPromotion ErrorSeverity.Info SignatureLevel.None
         *
         * @return
         */
        @OptIn(ExperimentalJsStatic::class)
        @JvmStatic
        @JsStatic
        fun defaultOptions(): CqlCompilerOptions {
            // Default options based on recommended settings:
            // http://build.fhir.org/ig/HL7/cqf-measures/using-cql.html#translation-to-elm
            val result = CqlCompilerOptions()
            result.options.add(Options.EnableAnnotations)
            result.options.add(Options.EnableLocators)
            result.options.add(Options.DisableListDemotion)
            result.options.add(Options.DisableListPromotion)
            return result
        }
    }
}
