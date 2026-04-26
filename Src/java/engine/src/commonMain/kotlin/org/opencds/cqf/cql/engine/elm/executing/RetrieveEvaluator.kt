package org.opencds.cqf.cql.engine.elm.executing

import org.cqframework.cql.elm.visiting.ElmLibraryVisitor
import org.hl7.elm.r1.Retrieve
import org.hl7.elm.r1.ValueSetRef
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.runtime.Concept
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.List
import org.opencds.cqf.cql.engine.runtime.String
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.ValueSet
import org.opencds.cqf.cql.engine.runtime.getNamedTypeForCqlValue
import org.opencds.cqf.cql.engine.runtime.toCqlList

object RetrieveEvaluator {
    fun internalEvaluate(
        elm: Retrieve?,
        state: State?,
        visitor: ElmLibraryVisitor<Value?, State?>,
    ): List? {
        val context = elm!!.context

        var isEnteredContext = false
        var result: Iterable<Value?>?

        if (context != null) {
            /*
               This whole block is a bit a hack in the sense that the need to switch to the context (e.g. Practitioner) identifies itself in a non-domain specific way
            */
            val contextValue = visitor.visitExpression(context, state)!!
            val dataProvider =
                state!!
                    .environment
                    .resolveDataProviderByModelUri(
                        getNamedTypeForCqlValue(contextValue)?.getNamespaceURI()
                    )
            val contextTypeName = getNamedTypeForCqlValue(contextValue)!!.getLocalPart()
            val contextId = dataProvider.resolveId(contextValue)

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
            var valueSet: kotlin.String? = null
            if (elm.codes != null) {
                if (elm.codes is ValueSetRef) {
                    val vs = ValueSetRefEvaluator.toValueSet(state, elm.codes as ValueSetRef)
                    valueSet = vs.id
                } else {
                    val codesResult = visitor.visitExpression(elm.codes!!, state)!!

                    when (codesResult) {
                        is ValueSet -> valueSet = codesResult.id
                        is String -> codes = mutableListOf(Code().withCode(codesResult.value))
                        is Code -> codes = mutableListOf(codesResult)
                        is Concept -> codes = codesResult.codes?.filterNotNull() ?: emptyList()
                        is List ->
                            codes =
                                codesResult.map {
                                    when (it) {
                                        is String -> Code().withCode(it.value)
                                        is Code -> it
                                        else ->
                                            throw IllegalArgumentException(
                                                "Expected String or Code. Found '${it?.typeAsString}'."
                                            )
                                    }
                                }
                        else ->
                            throw IllegalArgumentException(
                                "The codes argument to Retrieve must be a ValueSet, Code, Concept, String," +
                                    " or List of those types. Found '${codesResult.typeAsString}'."
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
                    dataProvider.getContextPath(state.getCurrentContext(), dataType.getLocalPart()),
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
            if (result != null) {
                state.evaluatedResources!!.addAll(result)
            }
        } finally {
            // Need to effectively reverse the context change we did at the beginning of this method
            state!!.exitContext(isEnteredContext)

            // The activation frame was pushed for tracking execution
            // time and should not have any local variables in it.
            state.popActivationFrame()
        }

        return result?.toCqlList()
    }
}
