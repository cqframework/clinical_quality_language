package org.opencds.cqf.cql.engine.execution

import java.time.ZonedDateTime
import java.util.Objects
import java.util.function.Consumer
import java.util.function.IntFunction
import java.util.function.IntUnaryOperator
import java.util.function.Supplier
import java.util.stream.IntStream
import kotlin.Deprecated
import kotlin.Exception
import kotlin.RuntimeException
import org.apache.commons.lang3.tuple.Pair
import org.cqframework.cql.cql2elm.CqlCompilerException
import org.hl7.cql.model.NamespaceManager.Companion.getNamePart
import org.hl7.cql.model.NamespaceManager.Companion.getUriPart
import org.hl7.elm.r1.Element
import org.hl7.elm.r1.FunctionDef
import org.hl7.elm.r1.Library
import org.hl7.elm.r1.VersionedIdentifier
import org.opencds.cqf.cql.engine.debug.DebugAction
import org.opencds.cqf.cql.engine.debug.DebugMap
import org.opencds.cqf.cql.engine.debug.SourceLocator.Companion.fromNode
import org.opencds.cqf.cql.engine.exception.CqlException
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * NOTE: We have updated CqlEngine to adopt a visitor pattern approach to traversing the ELM tree
 * for execution:
 *
 * Visitor pattern reduces the process to convert EML Tree to Executable ELM tree and thus reduces a
 * potential maintenance issue.
 */
class CqlEngine
@JvmOverloads
constructor(val environment: Environment, engineOptions: MutableSet<Options>? = mutableSetOf()) {
    enum class Options {
        EnableExpressionCaching,
        EnableValidation,

        // HEDIS Compatibility Mode changes the behavior of the CQL
        // engine to match some expected behavior of the HEDIS
        // content that is not standards-complaint.
        // Currently, this includes:
        //  1. Making the default comparison semantics for lists to be "equivalent"
        //      (the standard behavior is to use "equal" semantics - note that this is
        //      expected to be the standard behavior in a future version of the CQL spec)
        //  2. Ignoring the "all" / "distinct" modifiers for the "return" clause of queries, always
        // return all elements
        //      (the standard behavior is to return distinct elements)
        EnableHedisCompatibilityMode,

        // Collect data on evaluation counts, timing and cache hit
        // ratio for certain elements such as expression and function
        // definitions and retrieves.
        EnableProfiling,
    }

    val state: State
    private val engineOptions: MutableSet<Options>

    /** @return the internal engine visitor */
    @get:Deprecated(
        """this is a temporary arrangement until we further refine the relationship
      between the engine, the environment, and the state
      """
    )
    val evaluationVisitor: EvaluationVisitor = EvaluationVisitor()

    init {
        this.engineOptions =
            if (engineOptions != null) engineOptions
            else mutableSetOf(Options.EnableExpressionCaching)
        this.state = State(environment, this.engineOptions)

        if (this.engineOptions.contains(Options.EnableExpressionCaching)) {
            this.cache.setExpressionCaching(true)
        }
        if (this.engineOptions.contains(Options.EnableProfiling)) {
            this.state.ensureDebugResult().ensureProfile()
        }
    }

    val cache: Cache
        get() = this.state.cache

    /**
     * @param libraryIdentifier the library where the expression is defined
     * @param expressionName the name of the expression to evaluate
     * @param evaluationDateTime the value for "Now()"
     * @return the result of the expression
     */
    @Deprecated(
        """I added to assist with unit testing, but really it's indicative of the fact
      that we need to further refine the engine API. Please use this sparingly as it will go away
      """
    )
    fun expression(
        libraryIdentifier: VersionedIdentifier,
        expressionName: String,
        evaluationDateTime: ZonedDateTime?,
    ): ExpressionResult? {
        val set = HashSet<String>()
        set.add(expressionName)
        val result = this.evaluate(libraryIdentifier, set, null, null, null, evaluationDateTime)
        return result.forExpression(expressionName)
    }

    /**
     * @param libraryIdentifier the library where the expression is defined
     * @param expressionName the name of the expression to evaluate
     * @return the result of the expression
     */
    @Deprecated(
        """I added to assist with unit testing, but really it's indicative of the fact
      that we need to further refine the engine API. Please use this sparingly as it will go away
      """
    )
    fun expression(
        libraryIdentifier: VersionedIdentifier,
        expressionName: String,
    ): ExpressionResult? {
        return this.expression(libraryIdentifier, expressionName, null)
    }

    fun evaluate(
        libraryName: String,
        expressions: MutableSet<String>?,
        parameters: MutableMap<String?, Any?>?,
    ): EvaluationResult {
        return this.evaluate(libraryName, expressions, null, parameters)
    }

    fun evaluate(libraryName: String, contextParameter: Pair<String?, Any?>?): EvaluationResult {
        return this.evaluate(libraryName, null, contextParameter, null)
    }

    fun evaluate(
        libraryName: String,
        contextParameter: Pair<String?, Any?>?,
        parameters: MutableMap<String?, Any?>?,
    ): EvaluationResult {
        return this.evaluate(libraryName, null, contextParameter, parameters)
    }

    fun evaluate(libraryName: String, parameters: MutableMap<String?, Any?>?): EvaluationResult {
        return this.evaluate(libraryName, null, null, parameters)
    }

    // TODO: Add debugging info as a parameter.
    @JvmOverloads
    fun evaluate(
        libraryName: String?,
        expressions: MutableSet<String>? = null,
        contextParameter: Pair<String?, Any?>? = null,
        parameters: MutableMap<String?, Any?>? = null,
    ): EvaluationResult {
        return this.evaluate(
            VersionedIdentifier().withId(libraryName),
            expressions,
            contextParameter,
            parameters,
            null,
        )
    }

    fun evaluate(
        libraryIdentifier: VersionedIdentifier,
        evaluationDateTime: ZonedDateTime?,
    ): EvaluationResult {
        return this.evaluate(libraryIdentifier, null, null, null, null, evaluationDateTime)
    }

    fun evaluate(
        libraryIdentifier: VersionedIdentifier,
        expressions: MutableSet<String>?,
        parameters: MutableMap<String?, Any?>?,
    ): EvaluationResult {
        return this.evaluate(libraryIdentifier, expressions, null, parameters, null)
    }

    fun evaluate(
        libraryIdentifier: VersionedIdentifier,
        contextParameter: Pair<String?, Any?>?,
    ): EvaluationResult {
        return this.evaluate(libraryIdentifier, null, contextParameter, null, null)
    }

    fun evaluate(
        libraryIdentifier: VersionedIdentifier,
        contextParameter: Pair<String?, Any?>?,
        parameters: MutableMap<String?, Any?>?,
    ): EvaluationResult {
        return this.evaluate(libraryIdentifier, null, contextParameter, parameters, null)
    }

    fun evaluate(
        libraryIdentifier: VersionedIdentifier,
        parameters: MutableMap<String?, Any?>?,
    ): EvaluationResult {
        return this.evaluate(libraryIdentifier, null, null, parameters, null)
    }

    @JvmOverloads
    fun evaluate(
        libraryIdentifier: VersionedIdentifier,
        expressions: Set<String>? = null,
        contextParameter: Pair<String?, Any?>? = null,
        parameters: Map<String?, Any?>? = null,
        debugMap: DebugMap? = null,
        evaluationDateTime: ZonedDateTime? = null,
    ): EvaluationResult {
        return evaluate(
                mutableListOf(libraryIdentifier),
                expressions,
                contextParameter,
                parameters,
                debugMap,
                evaluationDateTime,
            )
            .onlyResultOrThrow!!
    }

    @JvmOverloads
    fun evaluate(
        libraryIdentifiers: List<VersionedIdentifier>,
        expressions: Set<String>? = null,
        contextParameter: Pair<String?, Any?>? = null,
        parameters: Map<String?, Any?>? = null,
        debugMap: DebugMap? = null,
        nullableEvaluationDateTime: ZonedDateTime? = null,
    ): EvaluationResultsForMultiLib {
        require(
            !(libraryIdentifiers == null ||
                libraryIdentifiers.isEmpty() ||
                libraryIdentifiers.get(0) == null)
        ) {
            "libraryIdentifier can not be null or empty."
        }

        val loadMultiLibResult = this.loadAndValidate(libraryIdentifiers)

        initializeEvalTime(nullableEvaluationDateTime)

        // here we initialize all libraries without emptying the cache for each library
        this.state.init(loadMultiLibResult.allLibraries)

        // We must do this only once per library evaluation otherwise, we may clear the cache
        // prematurely
        if (contextParameter != null) {
            state.setContextValue(contextParameter.getLeft(), contextParameter.getRight()!!)
        }

        loadMultiLibResult.allLibraries.forEach(
            Consumer { library -> state.setParameters(library, parameters) }
        )

        initializeDebugMap(debugMap)

        // We need to reverse the order of Libraries since the CQL engine state has the last library
        // first
        val reversedOrderLibraryIdentifiers =
            IntStream.range(0, loadMultiLibResult.libraryCount())
                .map(
                    IntUnaryOperator { index: Int ->
                        loadMultiLibResult.allLibraries.size - 1 - index
                    }
                )
                .mapToObj<VersionedIdentifier>(
                    IntFunction { index: Int ->
                        loadMultiLibResult.getLibraryIdentifierAtIndex(index)
                    }
                )
                .toList()

        val resultBuilder: EvaluationResultsForMultiLib.Builder =
            EvaluationResultsForMultiLib.builder(loadMultiLibResult)

        for (libraryIdentifier in reversedOrderLibraryIdentifiers) {
            val library = loadMultiLibResult.retrieveLibrary(libraryIdentifier)
            val expressionSet =
                if (expressions == null) this.getExpressionSet(library!!) else expressions

            val joinedExpressions = expressionSet.joinToString(", ")
            log.debug(
                "Evaluating library: {} with expressions: [{}]",
                libraryIdentifier.id,
                joinedExpressions,
            )
            try {
                val evaluationResult = this.evaluateExpressions(expressionSet)
                resultBuilder.addResult(libraryIdentifier, evaluationResult)
            } catch (exception: RuntimeException) {
                val error =
                    "Exception for Library: ${libraryIdentifier.id}, Message: ${exception.message}"
                log.error(error)

                resultBuilder.addException(libraryIdentifier, exception)
            }
        }

        return resultBuilder.build()
    }

    private fun initializeEvalTime(nullableEvaluationDateTime: ZonedDateTime?) {
        this.state.setEvaluationDateTime(
            Objects.requireNonNullElseGet<ZonedDateTime?>(
                nullableEvaluationDateTime,
                Supplier { ZonedDateTime.now() },
            )
        )
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
    private fun evaluateExpressions(expressions: Set<String>): EvaluationResult {
        val result = EvaluationResult()

        this.state.beginEvaluation()
        try {
            for (expression in expressions) {
                val currentLibrary = this.state.getCurrentLibrary()
                val def = Libraries.resolveExpressionRef(expression, currentLibrary!!)

                if (def == null) {
                    throw CqlException(
                        String.format("Unable to resolve expression \"%s.\"", expression)
                    )
                }

                if (def is FunctionDef) {
                    continue
                }

                try {
                    val action = this.state.shouldDebug(def)
                    state.pushActivationFrame(def, def.context)
                    try {
                        val `object` = this.evaluationVisitor.visitExpressionDef(def, this.state)
                        result.expressionResults[expression] =
                            ExpressionResult(`object`, this.state.evaluatedResources)
                        this.state.logDebugResult(def, `object`, action)
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
                        String.format("Error evaluating expression %s: %s", expression, e.message),
                    )
                }
            }
        } finally {
            this.state.endEvaluation()
            // We are moving the evaluated resources off the stack so we can work on the next ones
            this.state.clearEvaluatedResources()
            // We are moving the library off the stack so we can work on the next one
            this.state.exitLibrary(true)
        }

        result.debugResult = (this.state.debugResult)

        return result
    }

    private fun loadAndValidate(libraryIdentifier: VersionedIdentifier) {
        val errors = ArrayList<CqlCompilerException>()
        val library: Library? =
            this.environment.libraryManager!!.resolveLibrary(libraryIdentifier, errors).library

        if (library == null) {
            throw CqlException(
                String.format(
                    "Unable to load library %s",
                    libraryIdentifier.id +
                        (if (libraryIdentifier.version != null) "-" + libraryIdentifier.version
                        else ""),
                )
            )
        }

        if (CqlCompilerException.hasErrors(errors)) {
            throw CqlException(
                String.format(
                    "library %s loaded, but had errors: %s",
                    libraryIdentifier.id +
                        (if (libraryIdentifier.version != null) "-" + libraryIdentifier.version
                        else ""),
                    errors.joinToString(",") { obj -> obj.message ?: "" },
                )
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
        if (library.includes != null && library.includes!!.def != null) {
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
    private fun loadAndValidate(libraryIdentifiers: List<VersionedIdentifier>): LoadMultiLibResult {
        val resolvedLibraryResults =
            this.environment.libraryManager!!.resolveLibraries(libraryIdentifiers)

        val resultBuilder: LoadMultiLibResult.Builder = LoadMultiLibResult.builder()

        for (libraryResult in resolvedLibraryResults.allResults()) {
            if (!libraryResult.errors.isEmpty()) {
                val identifier = libraryResult.compiledLibrary.identifier
                resultBuilder.addExceptionsOrWarnings(identifier!!, libraryResult.errors)
            }
        }

        val libraries = resolvedLibraryResults.allLibrariesWithoutErrorSeverity()

        validateLibrariesIfNeeded(libraries)

        // We probably want to just load all relevant libraries into
        // memory before we start evaluation. This will further separate
        // environment from state.
        for (library in libraries) {
            try {
                if (library!!.includes != null && library.includes!!.def != null) {
                    for (include in library.includes!!.def) {
                        this.loadAndValidate(
                            VersionedIdentifier()
                                .withSystem(getUriPart(include.path))
                                .withId(getNamePart(include.path))
                                .withVersion(include.version)
                        )
                    }
                }
                resultBuilder.addResult(library.identifier!!, library)
            } catch (exception: CqlException) {
                // As with previous code, per searched library identifier, this is an all or nothing
                // operation:
                // stop at the first Exception and don't capture subsequent errors for subsequent
                // included libraries.
                resultBuilder.addExceptionOrWarning(library!!.identifier!!, exception)
            } catch (exception: CqlCompilerException) {
                resultBuilder.addExceptionOrWarning(library!!.identifier!!, exception)
            }
        }

        return resultBuilder.build()
    }

    private fun validateLibrariesIfNeeded(libraries: List<Library?>) {
        if (this.engineOptions.contains(Options.EnableValidation)) {
            libraries.forEach(
                Consumer { library ->
                    this.validateTerminologyRequirements(library!!)
                    this.validateDataRequirements(library)
                }
            )
            // TODO: Validate Expressions as well?
        }
    }

    private fun validateDataRequirements(library: Library) {
        // TODO: What we actually need here is a check of the actual retrieves, based on data
        // requirements
        if (
            library.usings != null &&
                library.usings!!.def != null &&
                !library.usings!!.def.isEmpty()
        ) {
            for (using in library.usings!!.def) {
                // Skip system using since the context automatically registers that.
                if (using.uri.equals("urn:hl7-org:elm-types:r1")) {
                    continue
                }

                require(
                    !(this.environment.dataProviders == null ||
                        !this.environment.dataProviders.containsKey(using.uri))
                ) {
                    String.format(
                        "Library %1\$s is using %2\$s and no data provider is registered for uri %2\$s.",
                        this.getLibraryDescription(library.identifier!!),
                        using.uri,
                    )
                }
            }
        }
    }

    private fun validateTerminologyRequirements(library: Library) {
        // TODO: Smarter validation would be to checkout and see if any retrieves
        // Use terminology, and to check for any codesystem lookups.
        require(
            !((library.codeSystems != null &&
                library.codeSystems!!.def != null &&
                !library.codeSystems!!.def.isEmpty()) ||
                (library.codes != null &&
                    library.codes!!.def != null &&
                    !library.codes!!.def.isEmpty()) ||
                (library.valueSets != null &&
                    library.valueSets!!.def != null &&
                    !library.valueSets!!.def.isEmpty()) &&
                    this.environment.terminologyProvider == null)
        ) {
            String.format(
                "Library %s has terminology requirements and no terminology provider is registered.",
                this.getLibraryDescription(library.identifier!!),
            )
        }
    }

    private fun getLibraryDescription(libraryIdentifier: VersionedIdentifier): String {
        return (libraryIdentifier.id +
            (if (libraryIdentifier.version != null) ("-" + libraryIdentifier.version) else ""))
    }

    private fun getExpressionSet(library: Library): MutableSet<String> {
        val expressionNames = mutableSetOf<String>()
        if (library.statements != null && library.statements!!.def != null) {
            for (ed in library.statements!!.def) {
                expressionNames.add(ed.name!!)
            }
        }

        return expressionNames
    }

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

    fun processException(e: Exception?, element: Element, message: String?) {
        val ce = CqlException(message, e, fromNode(element, this.state.getCurrentLibrary()))
        val action = state.shouldDebug(ce)
        if (action != DebugAction.NONE) {
            state.logDebugError(ce)
        }
        throw ce
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(CqlEngine::class.java)
    }
}
