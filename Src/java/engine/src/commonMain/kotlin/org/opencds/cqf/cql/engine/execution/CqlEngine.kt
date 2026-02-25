package org.opencds.cqf.cql.engine.execution

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.jvm.JvmOverloads
import org.cqframework.cql.cql2elm.CompiledLibraryResult
import org.cqframework.cql.cql2elm.CqlCompilerException
import org.cqframework.cql.shared.JsOnlyExport
import org.hl7.cql.model.NamespaceManager.Companion.getNamePart
import org.hl7.cql.model.NamespaceManager.Companion.getUriPart
import org.hl7.elm.r1.Element
import org.hl7.elm.r1.ExpressionDef
import org.hl7.elm.r1.FunctionDef
import org.hl7.elm.r1.Library
import org.hl7.elm.r1.VersionedIdentifier
import org.opencds.cqf.cql.engine.debug.DebugAction
import org.opencds.cqf.cql.engine.debug.DebugMap
import org.opencds.cqf.cql.engine.debug.SourceLocator.Companion.fromNode
import org.opencds.cqf.cql.engine.elm.executing.FunctionRefEvaluator.evaluateFunctionDef
import org.opencds.cqf.cql.engine.exception.CqlException
import org.opencds.cqf.cql.engine.util.ZonedDateTime
import org.opencds.cqf.cql.engine.util.zonedDateTimeNow

/**
 * NOTE: We have updated CqlEngine to adopt a visitor pattern approach to traversing the ELM tree
 * for execution:
 *
 * Visitor pattern reduces the process to convert EML Tree to Executable ELM tree and thus reduces a
 * potential maintenance issue.
 */
@OptIn(ExperimentalJsExport::class)
@JsOnlyExport
class CqlEngine
@JvmOverloads
constructor(val environment: Environment, engineOptions: MutableSet<Options>? = mutableSetOf()) {

    enum class Options {
        EnableExpressionCaching,
        EnableValidation,

        /**
         * HEDIS Compatibility Mode changes the behavior of the CQL engine to match some expected
         * behavior of the HEDIS content that is not standards-complaint. Currently, this includes:
         * 1. Making the default comparison semantics for lists to be "equivalent" (the standard
         *    behavior is to use "equal" semantics - note that this is expected to be the standard
         *    behavior in a future version of the CQL spec)
         * 2. Ignoring the "all" / "distinct" modifiers for the "return" clause of queries, always
         *    return all elements (the standard behavior is to return distinct elements)
         */
        EnableHedisCompatibilityMode,

        /**
         * Collect data on evaluation counts, timing and cache hit ratio for certain elements such
         * as expression and function definitions and retrieves.
         */
        EnableProfiling,

        /**
         * Collect trace information during evaluation (expressions and function calls with
         * intermediate results). Trace data can be exported after evaluation.
         */
        EnableTracing,

        /**
         * Collect coverage information during execution. Coverage data can be exported in LCOV
         * format after execution.
         */
        EnableCoverageCollection,

        /** Check runtime types against declared ELM result types and log any type mismatches. */
        EnableTypeChecking,
    }

    @JsExport.Ignore val state: State
    private val engineOptions: MutableSet<Options> =
        engineOptions ?: mutableSetOf(Options.EnableExpressionCaching)

    /** @return the internal engine visitor */
    @JsExport.Ignore
    @get:Deprecated(
        """this is a temporary arrangement until we further refine the relationship
      between the engine, the environment, and the state
      """
    )
    val evaluationVisitor: EvaluationVisitor = EvaluationVisitor()

    init {
        this.state = State(environment, this.engineOptions)

        if (this.engineOptions.contains(Options.EnableExpressionCaching)) {
            this.cache.setExpressionCaching(true)
        }
        if (this.engineOptions.contains(Options.EnableProfiling)) {
            this.state.ensureDebugResult().ensureProfile()
        }
    }

    @JsExport.Ignore
    val cache: Cache
        get() = this.state.cache

    /** Evaluates expressions and/or functions from multiple libraries. */
    @JsExport.Ignore
    fun evaluate(block: EvaluationParams.Builder.() -> Unit): EvaluationResults {
        return evaluate(EvaluationParams.Builder().apply(block).build())
    }

    /**
     * Evaluates expressions and/or functions from multiple libraries.
     *
     * @param evaluationParams The parameters for the evaluation, including library identifiers,
     *   context, parameters, etc.
     * @return A result object containing the evaluation results and any exceptions encountered
     *   during the process.
     */
    fun evaluate(evaluationParams: EvaluationParams): EvaluationResults {
        require(evaluationParams.expressions.isNotEmpty()) { "expressions can not be empty." }

        val loadAndValidateLibrariesResult =
            this.loadAndValidate(evaluationParams.expressions.keys.toList())

        initializeEvalTime(evaluationParams.evaluationDateTime)

        // here we initialize all libraries without emptying the cache for each library
        this.state.init(loadAndValidateLibrariesResult.allLibraries)

        // We must do this only once per library evaluation otherwise, we may clear the cache
        // prematurely
        if (evaluationParams.contextParameter != null) {
            state.setContextValue(
                evaluationParams.contextParameter.first,
                evaluationParams.contextParameter.second,
            )
        }

        loadAndValidateLibrariesResult.allLibraries.forEach { library ->
            state.setParameters(library, evaluationParams.parameters)
        }

        initializeDebugMap(evaluationParams.debugMap)

        // We need to reverse the order of Libraries since the CQL engine state has the last library
        // first
        val reversedOrderLibraryIdentifiers =
            loadAndValidateLibrariesResult.allLibraryIds.reversed()

        val resultBuilder: EvaluationResults.Builder =
            EvaluationResults.builder(loadAndValidateLibrariesResult)

        for (libraryIdentifier in reversedOrderLibraryIdentifiers) {
            val library = loadAndValidateLibrariesResult.retrieveLibrary(libraryIdentifier)
            val expressions =
                evaluationParams.expressions[libraryIdentifier] ?: this.getExpressions(library!!)

            val joinedExpressions = expressions.joinToString(", ", transform = { it.name })
            log.debug {
                "Evaluating library: ${libraryIdentifier.id} with expressions/functions: [$joinedExpressions]"
            }
            try {
                val evaluationResult = this.evaluateExpressions(expressions)
                resultBuilder.addResult(libraryIdentifier, evaluationResult)
            } catch (exception: RuntimeException) {
                log.error {
                    "Exception for Library: ${libraryIdentifier.id}, Message: ${exception.message}"
                }

                resultBuilder.addException(libraryIdentifier, exception)
            }
        }

        return resultBuilder.build()
    }

    private fun initializeEvalTime(nullableEvaluationDateTime: ZonedDateTime?) {
        this.state.setEvaluationDateTime(nullableEvaluationDateTime ?: zonedDateTimeNow())
    }

    private fun initializeDebugMap(debugMap: DebugMap?) {
        if (debugMap != null) {
            this.state.debugMap = debugMap
        }
    }

    /**
     * Evaluate one library within either a single library or multiple library context. Once
     * evaluation is done, ensure the stack for the library and evaluated resources is popped, so as
     * to permit evaluation of the next library, if applicable.
     *
     * @param expressions Expressions to evaluate
     * @return The EvaluationResult containing the results of the evaluated expressions for the
     *   current library.
     */
    @Suppress("NestedBlockDepth")
    private fun evaluateExpressions(expressions: List<EvaluationExpressionRef>): EvaluationResult {
        val result = EvaluationResult()

        this.state.beginEvaluation()
        try {
            for (expression in expressions) {
                val currentLibrary = this.state.getCurrentLibrary()

                if (expression is EvaluationFunctionRef) {
                    val functionDef =
                        Libraries.resolveFunctionDef(
                            expression.name,
                            expression.signature,
                            currentLibrary!!,
                        )

                    evaluateExpression(functionDef, expression, result) {
                        evaluateFunctionDef(
                            functionDef,
                            this.state,
                            this.evaluationVisitor,
                            expression.arguments.toMutableList(),
                        )
                    }
                } else {
                    val def = Libraries.resolveExpressionRef(expression.name, currentLibrary!!)
                    if (def is FunctionDef) {
                        continue
                    }

                    evaluateExpression(def, expression, result) {
                        this.evaluationVisitor.visitExpressionDef(def, this.state)
                    }
                }
            }
        } finally {
            result.trace = this.state.endEvaluation()
            // We are moving the evaluated resources off the stack so we can work on the next ones
            this.state.clearEvaluatedResources()
            // We are moving the library off the stack so we can work on the next one
            this.state.exitLibrary(true)
        }

        result.debugResult = (this.state.debugResult)

        return result
    }

    private fun evaluateExpression(
        def: ExpressionDef,
        expression: EvaluationExpressionRef,
        result: EvaluationResult,
        eval: () -> Any?,
    ) {
        try {
            val action = this.state.shouldDebug(def)
            state.pushActivationFrame(def, def.context!!)
            try {
                val value = eval()
                result.results[expression] = ExpressionResult(value, this.state.evaluatedResources)
                this.state.logDebugResult(def, value, action)
                state.storeIntermediateResultForTracing(value)
            } finally {
                this.state.popActivationFrame()
                // this avoids spill over of evaluatedResources from previous/next
                // expression evaluations
                this.state.clearEvaluatedResources()
            }
        } catch (ce: CqlException) {
            processException(ce, def)
        } catch (e: Exception) {
            processException(
                e,
                def,
                @Suppress("MaxLineLength")
                "Error evaluating expression/function ${expression.name}: ${e.message}",
            )
        }
    }

    private fun loadAndValidate(libraryIdentifier: VersionedIdentifier) {
        val errors = ArrayList<CqlCompilerException>()
        val library: Library =
            this.environment.libraryManager!!.resolveLibrary(libraryIdentifier, errors).library
                ?: throw CqlException(
                    "Unable to load library ${libraryIdentifier.id +
                            (if (libraryIdentifier.version != null) "-" + libraryIdentifier.version
                            else "")}"
                )

        if (CqlCompilerException.hasErrors(errors)) {
            throw CqlException(
                "library ${libraryIdentifier.id +
                        (if (libraryIdentifier.version != null) "-" + libraryIdentifier.version
                        else "")} loaded, but had errors: ${errors.joinToString(",") { obj -> obj.message ?: "" }}"
            )
        }

        if (this.engineOptions.contains(Options.EnableValidation)) {
            this.validateTerminologyRequirements(library)
            this.validateDataRequirements(library)
            // TODO: Validate Expressions as well?
        }

        // We probably want to just load all relevant libraries into
        // memory before we start evaluation. This will further separate
        // environment from state.
        if (library.includes != null) {
            for (include in library.includes!!.def) {
                this.loadAndValidate(
                    VersionedIdentifier()
                        .withSystem(getUriPart(include.path))
                        .withId(getNamePart(include.path))
                        .withVersion(include.version)
                )
            }
        }
    }

    /**
     * Evaluate multiple libraries, loading and validating them as needed.
     *
     * In most cases, errors in one Library will not halt the evaluation of other libraries, but
     * will merely be captured in the result object. However, in some cases, such as when a library
     * cannot be loaded at all, this method will throw an exception.
     *
     * @param libraryIdentifiers The list of library identifiers to load and evaluate.
     * @return A result object containing the evaluation results and any exceptions encountered
     *   during the process.
     */
    @Suppress("NestedBlockDepth")
    private fun loadAndValidate(
        libraryIdentifiers: List<VersionedIdentifier>
    ): LoadAndValidateLibrariesResult {
        val resolvedLibraryResults =
            this.environment.libraryManager!!.resolveLibraries(libraryIdentifiers)

        // The results, exceptions, and warnings are keyed by identifiers from the provided
        // `libraryIdentifiers` list.
        val resultBuilder: LoadAndValidateLibrariesResult.Builder =
            LoadAndValidateLibrariesResult.builder()

        for (libraryResult in resolvedLibraryResults.allResults()) {
            if (!libraryResult.errors.isEmpty()) {
                resultBuilder.addExceptionsOrWarnings(
                    libraryResult.identifier,
                    libraryResult.errors,
                )
            }
        }

        val compiledLibraryResults = resolvedLibraryResults.allResultsWithoutErrorSeverity()

        validateLibrariesIfNeeded(compiledLibraryResults)

        // We probably want to just load all relevant libraries into
        // memory before we start evaluation. This will further separate
        // environment from state.
        for (compiledLibraryResult in compiledLibraryResults) {
            val library = compiledLibraryResult.compiledLibrary.library!!
            try {
                if (library.includes != null) {
                    for (include in library.includes!!.def) {
                        this.loadAndValidate(
                            VersionedIdentifier()
                                .withSystem(getUriPart(include.path))
                                .withId(getNamePart(include.path))
                                .withVersion(include.version)
                        )
                    }
                }
                resultBuilder.addResult(compiledLibraryResult.identifier, library)
            } catch (exception: CqlException) {
                // As with previous code, per searched library identifier, this is an all or nothing
                // operation:
                // stop at the first Exception and don't capture subsequent errors for subsequent
                // included libraries.
                resultBuilder.addExceptionOrWarning(compiledLibraryResult.identifier, exception)
            } catch (exception: CqlCompilerException) {
                resultBuilder.addExceptionOrWarning(compiledLibraryResult.identifier, exception)
            }
        }

        return resultBuilder.build()
    }

    private fun validateLibrariesIfNeeded(compiledLibraryResults: List<CompiledLibraryResult>) {
        if (this.engineOptions.contains(Options.EnableValidation)) {
            compiledLibraryResults.forEach { compiledLibraryResult ->
                this.validateTerminologyRequirements(
                    compiledLibraryResult.compiledLibrary.library!!
                )
                this.validateDataRequirements(compiledLibraryResult.compiledLibrary.library!!)
            }
            // TODO: Validate Expressions as well?
        }
    }

    private fun validateDataRequirements(library: Library) {
        // TODO: What we actually need here is a check of the actual retrieves, based on data
        // requirements
        if (library.usings != null && !library.usings!!.def.isEmpty()) {
            for (using in library.usings!!.def) {
                // Skip system using since the context automatically registers that.
                if (using.uri.equals("urn:hl7-org:elm-types:r1")) {
                    continue
                }

                require(this.environment.dataProviders.containsKey(using.uri)) {
                    "Library ${this.getLibraryDescription(library.identifier!!)} is using ${using.uri} and no data provider is registered for uri ${using.uri}."
                }
            }
        }
    }

    private fun validateTerminologyRequirements(library: Library) {
        // TODO: Smarter validation would be to checkout and see if any retrieves
        // Use terminology, and to check for any codesystem lookups.
        require(
            !(((library.codeSystems != null && !library.codeSystems!!.def.isEmpty()) ||
                (library.codes != null && !library.codes!!.def.isEmpty()) ||
                (library.valueSets != null && !library.valueSets!!.def.isEmpty())) &&
                this.environment.terminologyProvider == null)
        ) {
            "Library ${this.getLibraryDescription(library.identifier!!)} has terminology requirements and no terminology provider is registered."
        }
    }

    private fun getLibraryDescription(libraryIdentifier: VersionedIdentifier): String {
        return (libraryIdentifier.id +
            (if (libraryIdentifier.version != null) ("-" + libraryIdentifier.version) else ""))
    }

    private fun getExpressions(library: Library): List<EvaluationExpressionRef> {
        return library.statements?.def?.map { EvaluationExpressionRef(it.name!!) }?.toList()
            ?: emptyList()
    }

    @JsExport.Ignore
    fun processException(e: CqlException, element: Element) {
        if (e.sourceLocator == null) {
            e.sourceLocator = fromNode(element, this.state.getCurrentLibrary())
            val action = state.shouldDebug(e)
            if (action != DebugAction.NONE) {
                state.logDebugError(e)
            }
        }

        throw e
    }

    @JsExport.Ignore
    fun processException(e: Exception?, element: Element, message: String?) {
        val ce = CqlException(message, e, fromNode(element, this.state.getCurrentLibrary()))
        val action = state.shouldDebug(ce)
        if (action != DebugAction.NONE) {
            state.logDebugError(ce)
        }
        throw ce
    }

    /**
     * Resolves the default value of a named parameter in a CQL library.
     *
     * Checks whether the given library can be resolved by this engine's environment.
     *
     * @param libraryIdentifier the versioned identifier of the library to check
     * @return `true` if the library can be resolved, `false` otherwise
     */
    @JsExport.Ignore
    fun hasLibrary(libraryIdentifier: VersionedIdentifier): Boolean {
        return try {
            environment.resolveLibrary(libraryIdentifier) != null
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Checks whether the given library contains a parameter with the specified name.
     *
     * @param libraryIdentifier the versioned identifier of the library to check
     * @param parameterName the name of the parameter to look for
     * @return `true` if the library contains the named parameter, `false` otherwise
     * @throws CqlException if the library cannot be resolved
     */
    @JsExport.Ignore
    fun hasParameter(libraryIdentifier: VersionedIdentifier, parameterName: String): Boolean {
        val library =
            environment.resolveLibrary(libraryIdentifier)
                ?: throw CqlException("Unable to resolve library: ${libraryIdentifier.id}")
        return Libraries.hasParameterDef(parameterName, library)
    }

    /**
     * This method evaluates the `default` expression of the given parameter definition within the
     * specified library. It uses the standard engine lifecycle — initializing the library on the
     * state stack, beginning an evaluation, and cleaning up afterward — so that the evaluation
     * visitor has the full context it needs (current library, evaluated resource stack, activation
     * frames, evaluation time).
     *
     * If the parameter has no default expression, this method returns `null`.
     *
     * **Typical usage** — resolving a CQL-defined default `Measurement Period` before measure
     * evaluation, so that callers do not need to manipulate the library stack themselves:
     * ```kotlin
     * val defaultPeriod = engine.resolveParameterDefault(libraryId, "Measurement Period")
     * ```
     *
     * @param libraryIdentifier the versioned identifier of the library containing the parameter
     * @param parameterName the name of the parameter whose default to resolve
     * @param evaluationDateTime the evaluation timestamp; defaults to `ZonedDateTime.now()`
     * @return the evaluated default value, or `null` if the parameter has no default
     * @throws CqlException if the library or parameter cannot be resolved, or if the default
     *   expression evaluation fails
     */
    @JsExport.Ignore
    @JvmOverloads
    fun resolveParameterDefault(
        libraryIdentifier: VersionedIdentifier,
        parameterName: String,
        evaluationDateTime: ZonedDateTime? = null,
    ): Any? {
        val library =
            environment.resolveLibrary(libraryIdentifier)
                ?: throw CqlException("Unable to resolve library: ${libraryIdentifier.id}")

        val parameterDef = Libraries.resolveParameterRef(parameterName, library)
        if (parameterDef.default == null) {
            return null
        }

        initializeEvalTime(evaluationDateTime)
        state.init(library)
        state.beginEvaluation()
        return try {
            evaluationVisitor.visitExpression(parameterDef.default!!, state)
        } catch (ce: CqlException) {
            processException(ce, parameterDef.default!!)
        } catch (e: Exception) {
            processException(
                e,
                parameterDef.default!!,
                "Error resolving default for parameter $parameterName: ${e.message}",
            )
        } finally {
            state.endEvaluation()
            state.clearEvaluatedResources()
            state.exitLibrary(true)
        }
    }

    companion object {
        private val log = KotlinLogging.logger("CqlEngine")
    }
}
