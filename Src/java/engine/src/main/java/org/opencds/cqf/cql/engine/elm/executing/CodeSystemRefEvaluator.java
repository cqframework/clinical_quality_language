package org.opencds.cqf.cql.engine.elm.executing;

import org.hl7.elm.r1.CodeSystemDef;
import org.hl7.elm.r1.CodeSystemRef;
import org.opencds.cqf.cql.engine.execution.Libraries;
import org.opencds.cqf.cql.engine.execution.State;
import org.opencds.cqf.cql.engine.runtime.CodeSystem;

// References a code system by its previously defined name

public class CodeSystemRefEvaluator {

    public static CodeSystem toCodeSystem(CodeSystemRef csr, State state) {
        boolean enteredLibrary = state.enterLibrary(csr.getLibraryName());
        try {
            CodeSystemDef csd = Libraries.resolveCodeSystemRef(csr.getName(), state.getCurrentLibrary());
            return new CodeSystem().withId(csd.getId()).withVersion(csd.getVersion()).withName(csd.getName());
        } finally {
            state.exitLibrary(enteredLibrary);
        }
    }
}
