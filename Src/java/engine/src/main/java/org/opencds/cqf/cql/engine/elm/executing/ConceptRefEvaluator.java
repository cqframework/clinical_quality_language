package org.opencds.cqf.cql.engine.elm.executing;

import java.util.ArrayList;
import org.hl7.elm.r1.CodeDef;
import org.hl7.elm.r1.CodeRef;
import org.hl7.elm.r1.ConceptDef;
import org.hl7.elm.r1.ConceptRef;
import org.opencds.cqf.cql.engine.execution.Libraries;
import org.opencds.cqf.cql.engine.execution.State;
import org.opencds.cqf.cql.engine.runtime.Code;
import org.opencds.cqf.cql.engine.runtime.CodeSystem;
import org.opencds.cqf.cql.engine.runtime.Concept;

public class ConceptRefEvaluator {

    public static Concept toConcept(ConceptRef cr, State state) {
        boolean enteredLibrary = state.enterLibrary(cr.getLibraryName());
        try {
            ConceptDef cd = Libraries.resolveConceptRef(cr.getName(), state.getCurrentLibrary());

            var codeList = new ArrayList<Code>();
            for (CodeRef r : cd.getCode()) {
                CodeDef codeDef = Libraries.resolveCodeRef(r.getName(), state.getCurrentLibrary());
                CodeSystem cs = CodeSystemRefEvaluator.toCodeSystem(codeDef.getCodeSystem(), state);
                Code c = CodeRefEvaluator.toCode(codeDef, cs);
                codeList.add(c);
            }

            return new Concept().withDisplay(cd.getDisplay()).withCodes(codeList);
        } finally {
            state.exitLibrary(enteredLibrary);
        }
    }
}
