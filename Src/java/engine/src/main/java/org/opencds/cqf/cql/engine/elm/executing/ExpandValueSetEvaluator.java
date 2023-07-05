package org.opencds.cqf.cql.engine.elm.executing;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.State;
import org.opencds.cqf.cql.engine.runtime.ValueSet;
import org.opencds.cqf.cql.engine.terminology.TerminologyProvider;
import org.opencds.cqf.cql.engine.terminology.ValueSetInfo;

/*
expand(valueSet ValueSet) List<Code>

The ExpandValueSet function expands the given value set using the terminology provider.
*/

public class ExpandValueSetEvaluator {

    public static Object expand(Object valueset, State state) {
        if (valueset == null) {
            return null;
        }

        if (valueset instanceof ValueSet) {
            TerminologyProvider tp = state.getEnvironment().getTerminologyProvider();
            return tp.expand(ValueSetInfo.fromValueSet((ValueSet)valueset));
        }

        throw new InvalidOperatorArgument(
            "ExpandValueSet(ValueSet)",
            String.format("ExpandValueSet(%s)", valueset.getClass().getName())
        );
    }

}
