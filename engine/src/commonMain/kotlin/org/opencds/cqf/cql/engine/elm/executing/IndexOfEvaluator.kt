package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.execution.CqlEngine
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.Integer
import org.opencds.cqf.cql.engine.runtime.List
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.toCqlInteger

/*
IndexOf(argument List<T>, element T) Integer

The IndexOf operator returns the 0-based index of the given element in the given source list.
The operator uses equality semantics to determine the index. The search is linear,
  and returns the index of the first element that is equal to the element being searched for.
If the list is empty, or no element is found, the result is -1.
If either argument is null, the result is null.
*/
object IndexOfEvaluator {
    @JvmStatic
    fun indexOf(source: Value?, elementToFind: Value?, state: State?): Integer? {
        if (source == null || elementToFind == null) {
            return null
        }

        var index = -1

        for (element in source as List) {
            index++
            // Equality semantics, or equivalence in HEDIS compatibility mode, as for In
            val found =
                if (
                    state
                        ?.engineOptions
                        ?.contains(CqlEngine.Options.EnableHedisCompatibilityMode) == true
                ) {
                    EquivalentEvaluator.equivalent(element, elementToFind, state).value
                } else {
                    EqualEvaluator.equal(element, elementToFind, state)?.value == true
                }

            if (found) {
                return index.toCqlInteger()
            }
        }

        return (-1).toCqlInteger()
    }
}
