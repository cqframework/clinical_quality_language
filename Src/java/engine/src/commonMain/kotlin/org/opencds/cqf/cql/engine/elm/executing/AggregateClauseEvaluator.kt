package org.opencds.cqf.cql.engine.elm.executing

import org.cqframework.cql.elm.visiting.ElmLibraryVisitor
import org.hl7.elm.r1.AggregateClause
import org.opencds.cqf.cql.engine.exception.CqlException
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.execution.Variable
import org.opencds.cqf.cql.engine.runtime.List
import org.opencds.cqf.cql.engine.runtime.Tuple
import org.opencds.cqf.cql.engine.runtime.Value

/*
CQL provides support for a limited class of recursive problems
using the aggregate clause of the query construct.
This clause is similar in function to the JavaScript .reduce() function,
in that it allows an expression to be repeatedly evaluated for each element of a list,
and that expression can access the current value of the aggregation.

https://cql.hl7.org/03-developersguide.html#aggregate-queries
*/
object AggregateClauseEvaluator {
    fun aggregate(
        elm: AggregateClause,
        state: State?,
        visitor: ElmLibraryVisitor<Value?, State?>,
        elements: List,
    ): Value? {
        var elements = elements

        if (elm.isDistinct() == true) {
            elements = DistinctEvaluator.distinct(elements, state)!!
        }

        var aggregatedValue: Value? = null
        if (elm.starting != null) {
            aggregatedValue = visitor.visitExpression(elm.starting!!, state)
        }

        for (e in elements) {
            if (e !is Tuple) {
                throw CqlException("expected aggregation source to be a Tuple")
            }
            val tuple = e

            var pushes = 0

            try {
                state!!.push(Variable(elm.identifier!!).withValue(aggregatedValue))
                pushes++

                for (p in tuple.elements.entries) {
                    state.push(Variable(p.key).withValue(p.value))
                    pushes++
                }

                aggregatedValue = visitor.visitExpression(elm.expression!!, state)
            } finally {
                while (pushes > 0) {
                    state!!.pop()
                    pushes--
                }
            }
        }

        return aggregatedValue
    }
}
