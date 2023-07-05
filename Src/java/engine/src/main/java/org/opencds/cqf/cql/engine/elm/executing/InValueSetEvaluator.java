package org.opencds.cqf.cql.engine.elm.executing;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.State;
import org.opencds.cqf.cql.engine.runtime.Code;
import org.opencds.cqf.cql.engine.runtime.Concept;
import org.opencds.cqf.cql.engine.runtime.ValueSet;
import org.opencds.cqf.cql.engine.terminology.TerminologyProvider;
import org.opencds.cqf.cql.engine.terminology.ValueSetInfo;

/*
in(code String, valueset ValueSetRef) Boolean
in(code Code, valueset ValueSetRef) Boolean
in(concept Concept, valueset ValueSetRef) Boolean

The in (Valueset) operators determine whether or not a given code is in a particular valueset.
For the String overload, if the given valueset contains a code with an equivalent code element, the result is true.
For the Code overload, if the given valueset contains an equivalent code, the result is true.
For the Concept overload, if the given valueset contains a code equivalent to any code in the given concept, the result is true.
If the code argument is null, the result is null.
*/

public class InValueSetEvaluator {
    public static Object inValueSet(Object code, Object valueset, State state) {

        if (code == null || valueset == null) {
            return null;
        }

        if (valueset instanceof ValueSet) {
            ValueSetInfo vsi = ValueSetInfo.fromValueSet((ValueSet)valueset);
            TerminologyProvider provider = state.getEnvironment().getTerminologyProvider();

            // perform operation
            if (code instanceof String) {
                if (provider.in(new Code().withCode((String)code), vsi)) {
                    return true;
                }
                return false;
            }
            else if (code instanceof Code) {
                if (provider.in((Code)code, vsi)) {
                    return true;
                }
                return false;
            }
            else if (code instanceof Concept) {
                for (Code codes : ((Concept)code).getCodes()) {
                    if (codes == null) return null;
                    if (provider.in(codes, vsi)) return true;
                }
                return false;
            }
        }

        throw new InvalidOperatorArgument(
            "In(String, ValueSetRef), In(Code, ValueSetRef) or In(Concept, ValueSetRef)",
            String.format("In(%s, %s)", code.getClass().getName(), valueset.getClass().getName())
        );
    }
}
