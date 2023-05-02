package org.opencds.cqf.cql.engine.elm.visiting;

import org.hl7.elm.r1.CodeSystemDef;
import org.hl7.elm.r1.CodeSystemRef;
import org.opencds.cqf.cql.engine.execution.State;

/*
structured type Code
{
  code String,
  display String,
  system String,
  version String
}

The Code type represents single terminology codes within CQL.
*/

public class CodeEvaluator {

    public static Object internalEvaluate(CodeSystemRef codeSystemRef, String c, String display, State state) {
        org.opencds.cqf.cql.engine.runtime.Code code = new org.opencds.cqf.cql.engine.runtime.Code().withCode(c).withDisplay(display);
        if (codeSystemRef != null) {
            boolean enteredLibrary = state.enterLibrary(codeSystemRef.getLibraryName());
            try {
                CodeSystemDef codeSystemDef = state.resolveCodeSystemRef(codeSystemRef.getName());
                code.setSystem(codeSystemDef.getId());
                code.setVersion(codeSystemDef.getVersion());
            } finally {
                state.exitLibrary(enteredLibrary);
            }
        }

        return code;
    }
}
