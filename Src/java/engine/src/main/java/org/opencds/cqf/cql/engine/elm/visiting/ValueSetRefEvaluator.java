package org.opencds.cqf.cql.engine.elm.visiting;

import org.hl7.elm.r1.CodeSystemRef;
import org.hl7.elm.r1.ValueSetDef;
import org.hl7.elm.r1.ValueSetRef;
import org.opencds.cqf.cql.engine.execution.State;
import org.opencds.cqf.cql.engine.runtime.CodeSystem;
import org.opencds.cqf.cql.engine.runtime.ValueSet;
import org.opencds.cqf.cql.engine.terminology.TerminologyProvider;
import org.opencds.cqf.cql.engine.terminology.ValueSetInfo;

public class ValueSetRefEvaluator {

    public static ValueSet toValueSet(State state, ValueSetRef vsr) {
        boolean enteredLibrary = state.enterLibrary(vsr.getLibraryName());
        try {
            ValueSetDef vsd = state.resolveValueSetRef(vsr.getName());
            ValueSet vs = new ValueSet().withId(vsd.getId()).withVersion(vsd.getVersion());
            for (CodeSystemRef csr : vsd.getCodeSystem()) {
                CodeSystem cs = CodeSystemRefEvaluator.toCodeSystem(csr, state);
                vs.addCodeSystem(cs);
            }
            return vs;
        }
        finally {
            state.exitLibrary(enteredLibrary);
        }
    }

    public static Object internalEvaluate(State state, ValueSetRef vsr) {
        ValueSet vs = toValueSet(state, vsr);

        if (vsr.isPreserve() != null && vsr.isPreserve()) {
            return vs;
        }
        else {
            TerminologyProvider tp = state.resolveTerminologyProvider();
            return tp.expand(ValueSetInfo.fromValueSet(vs));
        }
    }
}
