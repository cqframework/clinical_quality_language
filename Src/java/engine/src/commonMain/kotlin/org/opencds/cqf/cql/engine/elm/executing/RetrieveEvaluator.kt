package org.opencds.cqf.cql.engine.elm.executing

import org.cqframework.cql.elm.visiting.ElmLibraryVisitor
import org.hl7.elm.r1.Retrieve
import org.hl7.elm.r1.ValueSetRef
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.runtime.Concept
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.ValueSet
import org.opencds.cqf.cql.engine.util.javaClassName
import org.opencds.cqf.cql.engine.util.javaClassPackageName

object RetrieveEvaluator {
    fun internalEvaluate(
        elm: Retrieve?,
        state: State?,
        visitor: ElmLibraryVisitor<Any?, State?>,
    ): Any {
        val context = elm!!.context

        var isEnteredContext = false
        var result: Iterable<Any?>?

        if (context != null) {
            /*
               This whole block is a bit a hack in the sense that the need to switch to the context (e.g. Practitioner) identifies itself in a non-domain specific way
            */
            val contextValue: Any = visitor.visitExpression(context, state)!!
            val name = contextValue.javaClassPackageName
            val dataProvider = state!!.environment.resolveDataProvider(name)
            val contextTypeName = contextValue::class.simpleName!!
            val contextId = dataProvider!!.resolveId(contextValue)

            state.setContextValue(contextTypeName, contextId!!)
            isEnteredContext = state.enterContext(contextTypeName)
        }

        try {
            // Push an activation frame so that the execution of the
            // retrieve can be tracked. Mainly in terms of start and
            // end time for integration into a profile.
            state!!.pushActivationFrame(elm)

            val dataType = state.environment.fixupQName(elm.dataType!!)
            val dataProvider = state.environment.resolveDataProvider(dataType)
            var codes: Iterable<Code>? = null
            var valueSet: String? = null
            if (elm.codes != null) {
                if (elm.codes is ValueSetRef) {
                    val vs = ValueSetRefEvaluator.toValueSet(state, elm.codes as ValueSetRef)
                    valueSet = vs.id
                } else {
                    val codesResult: Any = visitor.visitExpression(elm.codes!!, state)!!

                    // Due to erased generics this is the best we can do here.
                    @Suppress("UNCHECKED_CAST")
                    when (codesResult) {
                        is ValueSet -> valueSet = codesResult.id
                        is String -> codes = mutableListOf(Code().withCode(codesResult as String?))
                        is Code -> codes = mutableListOf(codesResult)
                        is Concept -> codes = codesResult.codes?.filterNotNull() ?: emptyList()
                        is Iterable<*> -> codes = codesResult as Iterable<Code>
                        else ->
                            throw IllegalArgumentException(
                                "The codes argument to Retrieve must be a ValueSet, Code, Concept, String," +
                                    " or List of those types. Found '${codesResult.javaClassName}'."
                            )
                    }
                }
            }
            var dateRange: Interval? = null
            if (elm.dateRange != null) {
                dateRange = visitor.visitExpression(elm.dateRange!!, state) as Interval?
            }

            result =
                dataProvider.retrieve(
                    state.getCurrentContext(),
                    dataProvider.getContextPath(state.getCurrentContext(), dataType.getLocalPart())
                        as String?,
                    state.currentContextValue,
                    dataType.getLocalPart(),
                    elm.templateId,
                    elm.codeProperty,
                    codes,
                    valueSet,
                    elm.dateProperty,
                    elm.dateLowProperty,
                    elm.dateHighProperty,
                    dateRange,
                )

            // TODO: We probably shouldn't eagerly load this, but we need to track
            // this throughout the engine and only add it to the list when it's actually used
            val evaluatedResource = state.evaluatedResources
            if (result is MutableList<*>) {
                evaluatedResource!!.addAll(result)
            } else {
                for (o in result!!) {
                    evaluatedResource!!.add(o)
                }
            }
        } finally {
            // Need to effectively reverse the context change we did at the beginning of this method
            state!!.exitContext(isEnteredContext)

            // The activation frame was pushed for tracking execution
            // time and should not have any local variables in it.
            state.popActivationFrame()
        }

        return result
    }
}
