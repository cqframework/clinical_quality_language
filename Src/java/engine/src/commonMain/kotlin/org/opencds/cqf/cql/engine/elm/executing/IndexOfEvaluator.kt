package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.CqlType
import org.opencds.cqf.cql.engine.runtime.Integer
import org.opencds.cqf.cql.engine.runtime.List
import org.opencds.cqf.cql.engine.runtime.toCqlInteger

/*
IndexOf(argument List<T>, element T) Integer

The IndexOf operator returns the 0-based index of the given element in the given source list.
The operator uses the notion of equivalence to determine the index. The search is linear,
  and returns the index of the first element that is equivalent to the element being searched for.
If the list is empty, or no element is found, the result is -1.
If either argument is null, the result is null.
*/
object IndexOfEvaluator {
    @JvmStatic
    fun indexOf(source: CqlType?, elementToFind: CqlType?, state: State?): Integer? {
        if (source == null || elementToFind == null) {
            return null
        }

        var index = -1

        for (element in source as List) {
            index++
            val equiv = EquivalentEvaluator.equivalent(element, elementToFind, state)

            if (equiv.value) {
                return index.toCqlInteger()
            }
        }

        return (-1).toCqlInteger()
    }
}
