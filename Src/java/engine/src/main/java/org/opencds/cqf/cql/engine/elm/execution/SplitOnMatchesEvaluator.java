package org.opencds.cqf.cql.engine.elm.execution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.Context;

/*

    SplitOnMatches(stringToSplit String, separatorPattern String) List<String>

    The SplitOnMatches operator splits a string into a list of strings using a separator that is defined by a regular
        expression pattern.

    If the stringToSplit argument is null, the result is null.

    The separatorPattern argument is interpreted with the same regular expression semantics as defined for the Matches operator.

    If the stringToSplit argument does not contain any matches for the separatorPattern, the result is a list of strings
        containing one element that is the value of the stringToSplit argument.

*/

public class SplitOnMatchesEvaluator extends org.cqframework.cql.elm.execution.SplitOnMatches {

    public static Object splitOnMatches(Object stringToSplit, Object separator) {
        if (stringToSplit == null) {
            return null;
        }

        if (stringToSplit instanceof String) {
            List<Object> result = new ArrayList<>();
            if (separator == null) {
                result.add(stringToSplit);
            }
            else {
                Collections.addAll(result, (((String) stringToSplit).split((String) separator)));
            }
            return result;
        }

        throw new InvalidOperatorArgument(
                "SplitOnMatches(String, String)",
                String.format("SplitOnMatches(%s, %s)", stringToSplit.getClass().getName(), separator.getClass().getName())
        );
    }

    @Override
    protected Object internalEvaluate(Context context) {
        Object stringToSplit = getStringToSplit().evaluate(context);
        Object separator = getSeparatorPattern().evaluate(context);
        return splitOnMatches(stringToSplit, separator);
    }
}
