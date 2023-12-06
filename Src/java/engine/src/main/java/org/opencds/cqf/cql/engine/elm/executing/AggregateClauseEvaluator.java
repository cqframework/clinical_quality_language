package org.opencds.cqf.cql.engine.elm.executing;

import org.cqframework.cql.elm.visiting.ElmLibraryVisitor;
import org.hl7.elm.r1.AggregateClause;
import org.opencds.cqf.cql.engine.exception.CqlException;
import org.opencds.cqf.cql.engine.execution.State;
import org.opencds.cqf.cql.engine.execution.Variable;
import org.opencds.cqf.cql.engine.runtime.Tuple;

import java.util.List;
import java.util.Objects;

/*
CQL provides support for a limited class of recursive problems
using the aggregate clause of the query construct.
This clause is similar in function to the JavaScript .reduce() function,
in that it allows an expression to be repeatedly evaluated for each element of a list,
and that expression can access the current value of the aggregation.

https://cql.hl7.org/03-developersguide.html#aggregate-queries
*/

public class AggregateClauseEvaluator {

    public static Object aggregate(AggregateClause elm, State state, ElmLibraryVisitor<Object, State> visitor, List<Object> elements) {
        Objects.requireNonNull(elm, "elm can not be null");
        Objects.requireNonNull(visitor, "visitor can not be null");
        Objects.requireNonNull(elements, "elements can not be null");
        Objects.requireNonNull(state, "state can not be null");

        if (elm.isDistinct()) {
            elements = DistinctEvaluator.distinct(elements, state);
        }

        Object aggregatedValue = null;
        if (elm.getStarting() != null) {
            aggregatedValue = visitor.visitExpression(elm.getStarting(), state);
        }

        for(var e : elements) {
            if (!(e instanceof Tuple)) {
                throw new CqlException("expected aggregation source to be a Tuple");
            }
            var tuple = (Tuple)e;

            int pushes = 0;

            try {
                state.push(new Variable().withName(elm.getIdentifier()).withValue(aggregatedValue));
                pushes++;

                for (var p : tuple.getElements().entrySet()) {
                    state.push(new Variable().withName(p.getKey()).withValue(p.getValue()));
                    pushes++;
                }

                aggregatedValue = visitor.visitExpression(elm.getExpression(), state);
            }
            finally {
                while(pushes > 0) {
                    state.pop();
                    pushes--;
                }
            }
        }

        return aggregatedValue;
    }
}