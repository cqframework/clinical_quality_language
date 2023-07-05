package org.opencds.cqf.cql.engine.elm.executing;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.State;
import org.opencds.cqf.cql.engine.runtime.Code;
import org.opencds.cqf.cql.engine.runtime.CodeSystem;
import org.opencds.cqf.cql.engine.runtime.Concept;
import org.opencds.cqf.cql.engine.terminology.CodeSystemInfo;
import org.opencds.cqf.cql.engine.terminology.TerminologyProvider;

/*
in(code String, codesystem CodeSystemRef) Boolean
in(code Code, codesystem CodeSystemRef) Boolean
in(concept Concept, codesystem CodeSystemRef) Boolean

The in (Codesystem) operators determine whether or not a given code is in a particular codesystem.
For the String overload, if the given code system contains a code with an equivalent code element, the result is true.
For the Code overload, if the given code system contains an equivalent code, the result is true.
For the Concept overload, if the given code system contains a code equivalent to any code in the given concept, the result is true.
If the code argument is null, the result is null.
*/

public class InCodeSystemEvaluator {

    public static Object inCodeSystem( Object code, Object codeSystem, State state) {
        if (code == null || codeSystem == null) {
            return null;
        }

        if (codeSystem instanceof CodeSystem) {
            CodeSystemInfo csi = CodeSystemInfo.fromCodeSystem((CodeSystem)codeSystem);

            TerminologyProvider provider = state.getEnvironment().getTerminologyProvider();

            if (code instanceof String) {
                return provider.lookup(new Code().withCode((String) code), csi) != null;
            }

            else if (code instanceof Code) {
                return provider.lookup((Code) code, csi) != null;
            }

            else if (code instanceof Concept) {
                for (Code codes : ((Concept)code).getCodes()) {
                    if (provider.lookup(codes, csi) != null) {
                        return true;
                    }
                }
                return false;
            }
        }

        throw new InvalidOperatorArgument(
                "In(String, CodeSystemRef), In(Code, CodeSystemRef) or In(Concept, CodeSystemRef)",
                String.format("In(%s, %s)", code.getClass().getName(), codeSystem.getClass().getName())
        );
    }

}
