package org.opencds.cqf.cql.engine.elm.visiting;

import org.opencds.cqf.cql.engine.execution.Context;
import org.opencds.cqf.cql.engine.execution.State;

/*
IndexOf(argument List<T>, element T) Integer

The IndexOf operator returns the 0-based index of the given element in the given source list.
The operator uses the notion of equivalence to determine the index. The search is linear,
  and returns the index of the first element that is equivalent to the element being searched for.
If the list is empty, or no element is found, the result is -1.
If the list argument is null, the result is null.
*/

public class IndexOfEvaluator {

    public static Object indexOf(Object source, Object elementToFind, State state) {
        if (source == null) {
            return null;
        }

        int index = -1;
        boolean nullSwitch = false;

        for (Object element : (Iterable<?>)source) {
            index++;
            Boolean equiv = EquivalentEvaluator.equivalent(element, elementToFind, state);

            if (equiv == null) {
                nullSwitch = true;
            }

            else if (equiv) {
                return index;
            }
        }

        if (nullSwitch) {
            return null;
        }

        return -1;
    }

}
