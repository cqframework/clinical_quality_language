package org.opencds.cqf.cql.engine.elm.executing;

import org.hl7.elm.r1.CodeDef;
import org.hl7.elm.r1.CodeRef;
import org.hl7.elm.r1.ConceptDef;
import org.hl7.elm.r1.ConceptRef;
import org.opencds.cqf.cql.engine.execution.State;
import org.opencds.cqf.cql.engine.runtime.Code;
import org.opencds.cqf.cql.engine.runtime.CodeSystem;
import org.opencds.cqf.cql.engine.runtime.Concept;

import java.util.ArrayList;
import java.util.List;

public class ConceptRefEvaluator {

    public static Concept toConcept(ConceptRef cr, State state) {
        boolean enteredLibrary = state.enterLibrary(cr.getLibraryName());
        try {
            ConceptDef cd = state.resolveConceptRef(cr.getName());

            List<Code> codeList = new ArrayList<Code>();
            for (CodeRef r : cd.getCode()) {
                CodeDef codeDef = state.resolveCodeRef(r.getName());
                CodeSystem cs = CodeSystemRefEvaluator.toCodeSystem(codeDef.getCodeSystem(), state);
                Code c = CodeRefEvaluator.toCode(r, cs, state);
                codeList.add(c);
            }

            return new Concept().withDisplay(cd.getDisplay()).withCodes(codeList);
        } finally {
            state.exitLibrary(enteredLibrary);
        }
    }

}
