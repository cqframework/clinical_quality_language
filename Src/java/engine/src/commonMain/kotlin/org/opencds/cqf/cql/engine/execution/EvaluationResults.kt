package org.opencds.cqf.cql.engine.execution

import org.hl7.elm.r1.VersionedIdentifier

/**
 * Track evaluation results and exceptions for multiple libraries in a single evaluation, to support
 * partial successes and partial failures across libraries.
 */
class EvaluationResults private constructor(builder: Builder) {
    val results = builder.results.toMap()
    val exceptions = builder.exceptions.toMap()
    val warnings = builder.warnings.toMap()

    fun containsResultsFor(libraryIdentifier: VersionedIdentifier): Boolean {
        return getResultFor(libraryIdentifier) != null
    }

    fun containsExceptionsFor(libraryIdentifier: VersionedIdentifier): Boolean {
        return getExceptionFor(libraryIdentifier) != null
    }

    fun containsWarningsFor(libraryIdentifier: VersionedIdentifier): Boolean {
        return getWarningFor(libraryIdentifier) != null
    }

    @Suppress("ReturnCount")
    fun getResultFor(libraryIdentifier: VersionedIdentifier?): EvaluationResult? {
        if (results.containsKey(libraryIdentifier)) {
            return results[libraryIdentifier]
        }

        if (libraryIdentifier!!.version == null || libraryIdentifier.version!!.isEmpty()) {
            // If the version is not specified, try to match by ID only
            return results.entries
                .firstOrNull { entry -> matchIdentifiersForResults(libraryIdentifier, entry) }
                ?.value
        }

        return null
    }

    val onlyResultOrThrow: EvaluationResult
        get() {
            check(!(results.size > 1 || exceptions.size > 1)) {
                @Suppress("MaxLineLength")
                "Did you run an evaluation for multiple libraries?  Expected exactly one result or error, but found results: ${results.size} errors: ${exceptions.size}: "
            }

            val firstException = this.firstException

            if (firstException != null) {
                throw firstException
            }

            return this.firstResult!!
        }

    @Suppress("ReturnCount")
    fun getExceptionFor(libraryIdentifier: VersionedIdentifier): RuntimeException? {
        if (exceptions.containsKey(libraryIdentifier)) {
            return exceptions[libraryIdentifier]
        }

        if (libraryIdentifier.version == null || libraryIdentifier.version!!.isEmpty()) {
            // If the version is not specified, try to match by ID only
            return exceptions.entries
                .firstOrNull { entry -> matchIdentifiersForExceptions(libraryIdentifier, entry) }
                ?.value
        }

        return null
    }

    @Suppress("ReturnCount")
    fun getWarningFor(libraryIdentifier: VersionedIdentifier): RuntimeException? {
        if (warnings.containsKey(libraryIdentifier)) {
            return warnings[libraryIdentifier]
        }

        if (libraryIdentifier.version == null || libraryIdentifier.version!!.isEmpty()) {
            // If the version is not specified, try to match by ID only
            return warnings.entries
                .firstOrNull { entry -> matchIdentifiersForExceptions(libraryIdentifier, entry) }
                ?.value
        }

        return null
    }

    private fun matchIdentifiersForResults(
        libraryIdentifier: VersionedIdentifier,
        entry: Map.Entry<VersionedIdentifier?, EvaluationResult?>,
    ): Boolean {
        return entry.key!!.id.equals(libraryIdentifier.id)
    }

    private fun matchIdentifiersForExceptions(
        libraryIdentifier: VersionedIdentifier,
        entry: Map.Entry<VersionedIdentifier?, RuntimeException?>,
    ): Boolean {
        return entry.key!!.id.equals(libraryIdentifier.id)
    }

    private val firstResult: EvaluationResult?
        get() = results.values.firstOrNull()

    private val firstException: RuntimeException?
        get() {
            if (exceptions.isEmpty()) {
                return null
            }

            return exceptions.values.iterator().next()
        }

    fun hasExceptions(): Boolean {
        return !exceptions.isEmpty()
    }

    fun hasWarnings(): Boolean {
        return !warnings.isEmpty()
    }

    class Builder(loadAndValidateLibrariesResult: LoadAndValidateLibrariesResult) {
        val results = mutableMapOf<VersionedIdentifier, EvaluationResult>()
        val exceptions = mutableMapOf<VersionedIdentifier, RuntimeException>()
        val warnings = mutableMapOf<VersionedIdentifier, RuntimeException>()

        init {
            exceptions.putAll(loadAndValidateLibrariesResult.exceptions)
            warnings.putAll(loadAndValidateLibrariesResult.warnings)
        }

        fun addResult(libraryId: VersionedIdentifier, evaluationResult: EvaluationResult) {
            results[libraryId] = evaluationResult
        }

        fun addException(libraryId: VersionedIdentifier, exception: RuntimeException) {
            exceptions[withIdOnly(libraryId)] = exception
        }

        fun build(): EvaluationResults {
            return EvaluationResults(this)
        }

        private fun withIdOnly(libraryId: VersionedIdentifier): VersionedIdentifier {
            return VersionedIdentifier().withId(libraryId.id)
        }
    }

    companion object {
        fun builder(loadAndValidateLibrariesResult: LoadAndValidateLibrariesResult): Builder {
            return Builder(loadAndValidateLibrariesResult)
        }
    }
}
