package org.opencds.cqf.cql.engine.elm.execution;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.Context;

/*
Substring(stringToSub String, startIndex Integer) String
Substring(stringToSub String, startIndex Integer, length Integer) String

The Substring operator returns the string within stringToSub, starting at the 0-based index startIndex,
  and consisting of length characters.
If length is ommitted, the substring returned starts at startIndex and continues to the end of stringToSub.
If stringToSub or startIndex is null, or startIndex is out of range, the result is null.
*/

public class SubstringEvaluator extends org.cqframework.cql.elm.execution.Substring {

    public static Object substring(Object stringValue, Object startIndexValue, Object lengthValue) {
        if (stringValue == null || startIndexValue == null) {
            return null;
        }

        if (stringValue instanceof String && startIndexValue instanceof Integer) {
            String string = (String) stringValue;
            Integer startIndex = (Integer) startIndexValue;

            if (startIndex < 0 || startIndex >= string.length()) {
                return null;
            }

            if (lengthValue == null) {
                return string.substring(startIndex);
            }

            else {
                int endIndex = startIndex + (Integer) lengthValue;
                if (endIndex > string.length()) {
                    endIndex = string.length();
                }

                if (endIndex < startIndex) {
                    endIndex = startIndex;
                }

                return string.substring(startIndex, endIndex);
            }
        }

        throw new InvalidOperatorArgument(
                "Substring(String, Integer) or Substring(String, Integer, Integer)",
                String.format(
                        "Substring(%s, %s%s)",
                        stringValue.getClass().getName(),
                        startIndexValue.getClass().getName(),
                        lengthValue == null ? "" : ", " + lengthValue.getClass().getName()
                )
        );
    }

    @Override
    protected Object internalEvaluate(Context context) {
        Object stringValue = getStringToSub().evaluate(context);
        Object startIndexValue = getStartIndex().evaluate(context);
        Object lengthValue = getLength() == null ? null : getLength().evaluate(context);

        return substring(stringValue, startIndexValue, lengthValue);
    }
}
