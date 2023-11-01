package org.opencds.cqf.cql.engine.elm.executing;

import org.opencds.cqf.cql.engine.execution.State;

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

public class AggregateEvaluator {

    public static Object aggregate(List<Object> source, Object initial, Object iteration, State state) {
        Objects.requireNonNull(source, "source can not be null");
        Objects.requireNonNull(iteration, "iteration can not be null");
        Objects.requireNonNull(state, "state can not be null");

        // Initial value _may_ be null, but the iteration function is expected to account
        // for that if so.

        // for value in source

        return null;
    }
}