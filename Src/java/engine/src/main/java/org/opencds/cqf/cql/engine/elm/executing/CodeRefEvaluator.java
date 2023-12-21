package org.opencds.cqf.cql.engine.elm.executing;

import org.hl7.elm.r1.CodeDef;
import org.hl7.elm.r1.CodeRef;
import org.opencds.cqf.cql.engine.execution.Libraries;
import org.opencds.cqf.cql.engine.execution.State;
import org.opencds.cqf.cql.engine.runtime.Code;
import org.opencds.cqf.cql.engine.runtime.CodeSystem;

public class CodeRefEvaluator {

    public static Code toCode(CodeDef cd, CodeSystem cs) {
        return new Code()
                .withCode(cd.getId())
                .withSystem(cs.getId())
                .withDisplay(cd.getDisplay())
                .withVersion(cs.getVersion());
    }

    public static Code toCode(CodeRef cr, State state) {
        var enteredLibrary = state.enterLibrary(cr.getLibraryName());
        try {

            CodeDef cd = Libraries.resolveCodeRef(cr.getName(), state.getCurrentLibrary());
            CodeSystem cs = CodeSystemRefEvaluator.toCodeSystem(cd.getCodeSystem(), state);
            return toCode(cd, cs);
        } finally {
            state.exitLibrary(enteredLibrary);
        }
    }
}
