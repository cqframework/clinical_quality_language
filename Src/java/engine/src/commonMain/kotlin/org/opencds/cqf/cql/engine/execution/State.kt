package org.opencds.cqf.cql.engine.execution

import kotlin.Deprecated
import kotlin.Exception
import kotlin.IllegalStateException
import kotlin.RuntimeException
import kotlin.check
import kotlin.checkNotNull
import kotlin.jvm.JvmOverloads
import kotlin.text.StringBuilder
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import org.hl7.elm.r1.Element
import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.ExpressionDef
import org.hl7.elm.r1.Library
import org.hl7.elm.r1.Retrieve
import org.hl7.elm.r1.VersionedIdentifier
import org.opencds.cqf.cql.engine.debug.DebugAction
import org.opencds.cqf.cql.engine.debug.DebugMap
import org.opencds.cqf.cql.engine.debug.DebugResult
import org.opencds.cqf.cql.engine.debug.SourceLocator
import org.opencds.cqf.cql.engine.exception.CqlException
import org.opencds.cqf.cql.engine.exception.Severity
import org.opencds.cqf.cql.engine.execution.CqlEngine.Options
import org.opencds.cqf.cql.engine.execution.trace.Trace
import org.opencds.cqf.cql.engine.runtime.DateTime
import org.opencds.cqf.cql.engine.runtime.Tuple
import org.opencds.cqf.cql.engine.util.ZonedDateTime
import org.opencds.cqf.cql.engine.util.zonedDateTimeNow

/** State represents the internal state of the CqlEngine. */
class State
@JvmOverloads
constructor(
    val environment: Environment,
    val engineOptions: MutableSet<CqlEngine.Options> = HashSet<CqlEngine.Options>(),
) {
    class ActivationFrame(
        /**
         * The expression that is being evaluated in this activation frame. Either null for the
         * "root" activation frame, an ExpressionDef (which can be a FunctionDef) or a Retrieve.
         */
        var element: Element?,
        /** The library containing the element being evaluated. */
        var library: VersionedIdentifier?,
        var contextName: String?,
        /** The time at which the evaluation to which this activation frame belongs started. */
        var startTime: Long,
    ) {
        // Arguments and/or local variables of the active function
        // call or expression definition evaluation. This is the only
        // field that is strictly required for evaluation. The other
        // fields are for pragmatic purposes like friendly backtraces
        // and profiling.
        var variables = ArrayDeque<Variable>(4)

        /** The time at which the evaluation to which this activation frame belongs ended. */
        var endTime: Long = 0

        // If the activation frame belongs to an ExpressionDef that is
        // not a FunctionDef, this field indicates whether the
        // evaluation result was computed for this activation frame or
        // taken from the cache.
        var isCached: Boolean = false

        /**
         * The result of the expression evaluation for this frame. Only used when tracing is
         * enabled.
         */
        var result: Any? = null

        /**
         * Frames representing nested expressions, function calls, and retrieves. Only used when
         * tracing is enabled.
         */
        val innerActivationFrames: MutableList<ActivationFrame> = mutableListOf()

        override fun toString(): String {
            val result = StringBuilder().append("Frame{element=")
            if (this.element == null) {
                result.append("«root»")
            } else if (this.element is ExpressionDef) {
                result.append((element as ExpressionDef).name)
            } else if (this.element is Retrieve) {
                result.append("[${(element as Retrieve).dataType!!.getLocalPart()}]")
            } else {
                result.append(this.element!!::class.simpleName)
            }
            if (this.endTime == 0L) {
                result.append(", active")
            } else {
                result.append(", ${(this.endTime - this.startTime) / 1000000} ms")
            }
            if (this.isCached) {
                result.append(", cached")
            }
            return result.append("}").toString()
        }
    }

    val cache: Cache = Cache()

    private val currentContext = ArrayDeque<String>()

    private val currentLibrary = ArrayDeque<Library?>()

    val stack = ArrayDeque<ActivationFrame>()

    private val evaluatedResourceStack = ArrayDeque<MutableSet<Any?>>()

    val parameters = mutableMapOf<String, Any?>()
    var contextValues = mutableMapOf<String, Any?>()

    var evaluationZonedDateTime: ZonedDateTime? = null
        private set

    var evaluationDateTime: DateTime? = null
        private set

    var debugMap: DebugMap? = null

    val globalCoverage by lazy { GlobalCoverage() }

    fun getCurrentLibrary(): Library? {
        return currentLibrary.firstOrNull()
    }

    fun setParameters(library: Library?, parameters: Map<String, Any?>?) {
        if (parameters != null) {
            for (parameterValue in parameters.entries) {
                setParameter(null, parameterValue.key, parameterValue.value)
            }
        }
    }

    fun setParameter(libraryName: String?, name: String, value: Any?) {
        val enteredLibrary = enterLibrary(libraryName)
        try {
            val fullName =
                if (libraryName != null) "${getCurrentLibrary()!!.identifier!!.id}.${name}"
                else name
            parameters[fullName] = value
        } finally {
            exitLibrary(enteredLibrary)
        }
    }

    fun enterLibrary(libraryName: String?): Boolean {
        if (libraryName != null) {
            val includeDef = Libraries.resolveLibraryRef(libraryName, getCurrentLibrary()!!)
            val identifier = Libraries.toVersionedIdentifier(includeDef)

            // We probably want to just load all relevant libraries into
            // memory before we start evaluation. This will further separate
            // environment from state.
            val library = this.environment.resolveLibrary(identifier)

            currentLibrary.addFirst(library)

            return true
        }

        return false
    }

    fun exitLibrary(enteredLibrary: Boolean) {
        if (enteredLibrary) {
            currentLibrary.removeFirst()
        }
    }

    @get:Deprecated("")
    @set:Deprecated("")
    var windows: ArrayDeque<ArrayDeque<Variable>?>?
        get() {
            val result = ArrayDeque<ArrayDeque<Variable>?>()
            this.stack.forEach { frame -> result.addFirst(frame!!.variables) }
            return result
        }
        set(windows) {
            throw RuntimeException("Not supported")
        }

    var debugResult: DebugResult? = null
        private set

    init {
        this.setEvaluationDateTime(zonedDateTimeNow())
    }

    fun ensureDebugResult(): DebugResult {
        if (this.debugResult == null) {
            debugResult = DebugResult()
        }
        return debugResult!!
    }

    fun shouldDebug(e: Exception): DebugAction? {
        if (this.debugMap == null) {
            return DebugAction.NONE
        }

        return debugMap!!.shouldDebug(e)
    }

    fun shouldDebug(node: Element): DebugAction? {
        if (this.debugMap == null) {
            return DebugAction.NONE
        }

        return debugMap!!.shouldDebug(node, this.getCurrentLibrary()!!)
    }

    fun setEvaluationDateTime(evaluationZonedDateTime: ZonedDateTime) {
        this.evaluationZonedDateTime = evaluationZonedDateTime
        this.evaluationDateTime = DateTime(evaluationZonedDateTime.toOffsetDateTime())
    }

    fun init(library: Library?) {
        check(this.stack.isEmpty())

        currentLibrary.addFirst(library)

        this.pushEvaluatedResourceStack()
    }

    /**
     * Sets up a list of libraries on the currentLibrary stack and sets up the evaluated resource
     * stack at the same position.
     *
     * @param libraries the list of libraries to initialize
     */
    fun init(libraries: List<Library>) {
        check(this.stack.isEmpty())

        for (library in libraries) {
            // Ensure the current libraries and evaluated resources are at the same positions in the
            // stack,
            // so that we can exit each together to evaluate the next library

            currentLibrary.addFirst(library)

            this.pushEvaluatedResourceStack()
        }
    }

    fun pop() {
        val topActivationFrame = this.stack.firstOrNull()

        checkNotNull(topActivationFrame) { "Stack underflow" }

        topActivationFrame.variables.removeFirst()
    }

    fun push(variable: Variable?) {
        val topActivationFrame = this.stack.firstOrNull()

        checkNotNull(topActivationFrame) { "Stack underflow: No activation frame available." }

        topActivationFrame.variables.addFirst(variable!!)
    }

    fun beginEvaluation() {
        // This method must be called on an initialized but inactivate
        // state: there must be no activation frames besides the dummy
        // "root" activation frame. This method simply resets the
        // start time of the root activation frame.
        check(this.stack.isEmpty())
        pushActivationFrame(null)
    }

    fun endEvaluation(): Trace? {
        check(this.stack.size == 1)

        val trace =
            if (engineOptions.contains(Options.EnableTracing))
                Trace.fromActivationFrames(
                    this.stack.first().innerActivationFrames,
                    this.contextValues,
                )
            else null

        // TODO(jmoringe): maybe assert this.stack.getLast().variables.isEmpty();
        // Pop (and possibly process) the root activation frame.
        popActivationFrame()

        return trace
    }

    fun resolveVariable(name: String?): Variable? {
        for (frame in this.stack) {
            for (v in frame.variables) {
                if (v.name == name) {
                    return v
                }
            }
        }

        return null
    }

    fun resolveVariable(name: String?, mustResolve: Boolean): Variable? {
        val result = resolveVariable(name)
        if (mustResolve && result == null) {
            throw CqlException("Could not resolve variable reference ${name}")
        }

        return result
    }

    @OptIn(ExperimentalTime::class)
    @JvmOverloads
    fun pushActivationFrame(
        element: Element?,
        contextName: String?,
        startTime: Long = Clock.System.now().toEpochMilliseconds(),
    ) {
        val newActivationFrame =
            ActivationFrame(element, this.getCurrentLibrary()?.identifier, contextName, startTime)
        if (engineOptions.contains(Options.EnableTracing) && this.stack.isNotEmpty()) {
            topActivationFrame.innerActivationFrames.add(newActivationFrame)
        }

        this.stack.addFirst(newActivationFrame)

        if (this.debugResult != null) {
            val profile = this.debugResult!!.profile
            if (profile != null) {
                profile.enter(newActivationFrame)
            }
        }
    }

    fun pushActivationFrame(element: Element?) {
        val contextName = this.currentContext.firstOrNull()

        pushActivationFrame(element, contextName)
    }

    @OptIn(ExperimentalTime::class)
    fun popActivationFrame() {
        val topActivationFrame = this.stack.firstOrNull()

        if (topActivationFrame == null) {
            throw RuntimeException("Stack underflow")
        }
        check(topActivationFrame.endTime == 0L)
        // If a profile is being built (controlled via an engine
        // option), register the invocation chain that terminates at
        // topActivationFrame.
        if (this.debugResult != null) {
            val profile = this.debugResult!!.profile
            if (profile != null) {
                topActivationFrame.endTime = Clock.System.now().toEpochMilliseconds()
                profile.leave(topActivationFrame)
            }
        }

        this.stack.removeFirst()
    }

    /** Stores the intermediate result in the activation frame. */
    fun storeIntermediateResultForTracing(result: Any?) {
        if (engineOptions.contains(Options.EnableTracing)) {
            topActivationFrame.result = result
        }
    }

    val topActivationFrame: ActivationFrame
        get() {
            val topActivationFrame = this.stack.firstOrNull()

            if (topActivationFrame == null) {
                throw RuntimeException("Stack underflow")
            }
            return topActivationFrame
        }

    fun setContextValue(context: String, contextValue: Any?) {
        val containsKey = contextValues.containsKey(context)
        val valueFromContextValues = contextValues[context]
        val valuesAreEqual = contextValue == valueFromContextValues

        if (!containsKey || !valuesAreEqual) {
            contextValues[context] = contextValue
            clearCacheExpressions()
        }
    }

    private fun clearCacheExpressions() {
        cache.expressions.clear()
    }

    fun enterContext(context: String?): Boolean {
        if (context != null) {
            currentContext.addFirst(context)

            return true
        }

        return false
    }

    fun exitContext(isEnteredContext: Boolean) {
        if (isEnteredContext) {
            currentContext.removeFirst()
        }
    }

    fun getCurrentContext(): String? {
        if (currentContext.isEmpty()) {
            return null
        }

        return currentContext.firstOrNull()
    }

    val currentContextValue: Any?
        get() {
            val context = getCurrentContext()
            if (context != null && this.contextValues.containsKey(context)) {
                return this.contextValues[context]
            }

            return null
        }

    val evaluatedResources: MutableSet<Any?>?
        get() {
            check(!evaluatedResourceStack.isEmpty()) {
                "Attempted to get the evaluatedResource stack when it's empty"
            }

            return this.evaluatedResourceStack.firstOrNull()
        }

    fun clearEvaluatedResources() {
        this.evaluatedResourceStack.clear()
        this.pushEvaluatedResourceStack()
    }

    fun pushEvaluatedResourceStack() {
        evaluatedResourceStack.addFirst(HashSet<Any?>())
    }

    /**
     * Ensure that as we recurse more deeply into nested expressions and recursive calls to
     * expression def evaluation, we carry over the evaluated resources from the current layer to
     * the previous layer.
     */
    fun popEvaluatedResourceStack() {
        check(!evaluatedResourceStack.isEmpty()) {
            "Attempted to pop the evaluatedResource stack when it's empty"
        }

        check(evaluatedResourceStack.size != 1) {
            "Attempted to pop the evaluatedResource stack when only the root remains"
        }

        carryOverEvaluatedResourcesUpCallStack()
    }

    private fun carryOverEvaluatedResourcesUpCallStack() {
        val previousStackEvaluatedResources = evaluatedResourceStack.removeFirst()
        val currentStackEvaluatedResources = evaluatedResourceStack.firstOrNull()

        checkNotNull(currentStackEvaluatedResources) {
            "Attempted to carry over evaluated resources when the current stack is empty"
        }
        currentStackEvaluatedResources.addAll(previousStackEvaluatedResources)
    }

    fun resolveAlias(name: String?): Any? {
        // This method needs to account for multiple variables on the stack with the same name
        for (v in this.topActivationFrame.variables) {
            if (v.name == name) {
                return v.value
            }
        }

        throw IllegalStateException(
            "Could not resolve alias reference $name in the current context"
        )
    }

    fun resolveIdentifierRef(name: String): Any? {
        for (frame in this.stack) {
            for (v in frame.variables) {
                if (v.name == name) {
                    return v.value
                }

                val value = v.value
                if (value is Tuple) {
                    for (key in value.elements.keys) {
                        if (key == name) {
                            return value.elements[key]
                        }
                    }
                }
                try {
                    return environment.resolvePath(value, name)
                } catch (ignored: Exception) {}
            }
        }

        throw CqlException("Cannot resolve identifier $name")
    }

    fun logDebugResult(node: Element, result: Any?, action: DebugAction?) {
        ensureDebugResult()
        debugResult!!.logDebugResult(node, this.getCurrentLibrary()!!, result, action)
    }

    fun logDebugMessage(locator: SourceLocator?, message: String?) {
        ensureDebugResult()
        debugResult!!.logDebugError(CqlException(message, locator, Severity.MESSAGE))
    }

    fun logDebugWarning(locator: SourceLocator?, message: String?) {
        ensureDebugResult()
        debugResult!!.logDebugError(CqlException(message, locator, Severity.WARNING))
    }

    fun logDebugTrace(locator: SourceLocator?, message: String?) {
        ensureDebugResult()
        debugResult!!.logDebugError(CqlException(message, locator, Severity.TRACE))
    }

    fun logDebugError(e: CqlException?) {
        ensureDebugResult()
        debugResult!!.logDebugError(e)
    }

    fun markElementAsVisitedForCoverageReport(elm: Element) {
        if (engineOptions.contains(Options.EnableCoverageCollection)) {
            val library =
                checkNotNull(getCurrentLibrary()) {
                    "No current library available when marking element for coverage report"
                }
            globalCoverage.markElementAsVisitedForCoverageReport(elm, library)
        }
    }

    fun checkType(expressionWithExpectedResultType: Expression, actualValue: Any?) {
        if (engineOptions.contains(Options.EnableTypeChecking)) {
            TypeChecker.checkType(expressionWithExpectedResultType, actualValue)
        }
    }
}
