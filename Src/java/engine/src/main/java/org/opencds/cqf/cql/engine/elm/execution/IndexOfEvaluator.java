package org.opencds.cqf.cql.engine.elm.execution;

import org.opencds.cqf.cql.engine.execution.Context;

/*
IndexOf(argument List<T>, element T) Integer

The IndexOf operator returns the 0-based index of the given element in the given source list.
The operator uses the notion of equivalence to determine the index. The search is linear,
  and returns the index of the first element that is equivalent to the element being searched for.
If the list is empty, or no element is found, the result is -1.
If the list argument is null, the result is null.
*/

public class IndexOfEvaluator extends org.cqframework.cql.elm.execution.IndexOf {

    public static Object indexOf(Object source, Object elementToFind, Context context) {
        if (source == null) {
            return null;
        }

        int index = -1;
        boolean nullSwitch = false;

        for (Object element : (Iterable<?>)source) {
            index++;
            Boolean equiv = EquivalentEvaluator.equivalent(element, elementToFind, context);

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

    @Override
    protected Object internalEvaluate(Context context) {
        Object source = getSource().evaluate(context);
        Object elementToFind = getElement().evaluate(context);

        return indexOf(source, elementToFind, context);
    }
}
