package org.cqframework.cql.elm.evaluation

import java.time.ZonedDateTime
import org.cqframework.cql.cql2elm.LibraryManager
import org.cqframework.cql.cql2elm.ModelManager
import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.Library
import org.opencds.cqf.cql.engine.execution.CqlEngine
import org.opencds.cqf.cql.engine.execution.Environment
import org.opencds.cqf.cql.engine.runtime.Value

object ElmEvaluationHelper {
    @JvmStatic
    fun evaluate(
        library: Library?,
        value: Expression?,
        parameters: MutableMap<String, Value?>?,
        evaluationDateTime: ZonedDateTime?,
    ): Value? {
        // NOTE: Consider caching for libraries in the future.

        val engine = getEngine(library, parameters, evaluationDateTime)
        return engine.evaluationVisitor.visitExpression(value!!, engine.state)
    }

    private fun getEngine(
        library: Library?,
        parameters: MutableMap<String, Value?>?,
        evaluationDateTime: ZonedDateTime?,
    ): CqlEngine {
        val environment = Environment(libraryManager)
        val engine = CqlEngine(environment)
        if (evaluationDateTime != null) {
            engine.state.setEvaluationDateTime(evaluationDateTime)
        }
        engine.state.setParameters(library, parameters)
        return engine
    }

    internal val libraryManager: LibraryManager
        get() {
            val libraryManager = LibraryManager(modelManager)
            libraryManager.librarySourceLoader.registerProvider(TestLibrarySourceProvider())
            return libraryManager
        }

    internal val modelManager: ModelManager
        get() = ModelManager()
}
