package org.opencds.cqf.cql.engine.elm.executing;

import org.hl7.elm.r1.CodeDef;
import org.hl7.elm.r1.CodeRef;
import org.opencds.cqf.cql.engine.execution.State;
import org.opencds.cqf.cql.engine.runtime.Code;
import org.opencds.cqf.cql.engine.runtime.CodeSystem;

public class CodeRefEvaluator {

    public static Code toCode(CodeRef cr, CodeSystem cs, State state) {
        boolean enteredLibrary = state.enterLibrary(cr.getLibraryName());
        try {
            CodeDef cd = state.resolveCodeRef(cr.getName());
            return new Code().withCode(cd.getId()).withSystem(cs.getId()).withDisplay(cd.getDisplay()).withVersion(cs.getVersion());
        } finally {
            state.exitLibrary(enteredLibrary);
        }
    }

}
