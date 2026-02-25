package org.opencds.cqf.cql.engine.elm.executing

import io.github.oshai.kotlinlogging.KotlinLogging
import org.cqframework.cql.elm.visiting.ElmLibraryVisitor
import org.hl7.elm.r1.ParameterRef
import org.opencds.cqf.cql.engine.execution.Libraries
import org.opencds.cqf.cql.engine.execution.State

object ParameterRefEvaluator {
    private val log = KotlinLogging.logger("ParameterRefEvaluator")

    fun internalEvaluate(
        parameterRef: ParameterRef?,
        state: State?,
        visitor: ElmLibraryVisitor<Any?, State?>,
    ): Any? {
        val enteredLibrary = state!!.enterLibrary(parameterRef!!.libraryName)
        try {
            val parameterDef =
                Libraries.resolveParameterRef(parameterRef.name, state.getCurrentLibrary()!!)
            val name: String? = parameterDef.name
            val libraryName: String? = state.getCurrentLibrary()!!.identifier!!.id

            val fullName = "${libraryName}.${name}"

            if (state.parameters.containsKey(fullName)) {
                return state.parameters.get(fullName)
            }

            if (state.parameters.containsKey(parameterDef.name)) {
                log.debug {
                    "Using global value for parameter \"${parameterDef.name}\" while evaluating in library \"${state.getCurrentLibrary()!!.identifier!!.id}\""
                }
                return state.parameters.get(parameterDef.name)
            }

            val result =
                if (parameterDef.default != null)
                    visitor.visitExpression(parameterDef.default!!, state)
                else null

            state.parameters.put(fullName, result)
            return result
        } finally {
            state.exitLibrary(enteredLibrary)
        }
    }
}
