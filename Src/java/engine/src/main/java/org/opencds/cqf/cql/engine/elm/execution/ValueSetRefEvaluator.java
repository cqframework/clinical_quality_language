package org.opencds.cqf.cql.engine.elm.execution;

import org.cqframework.cql.elm.execution.CodeSystemRef;
import org.cqframework.cql.elm.execution.ValueSetDef;
import org.cqframework.cql.elm.execution.ValueSetRef;
import org.opencds.cqf.cql.engine.execution.Context;
import org.opencds.cqf.cql.engine.runtime.CodeSystem;
import org.opencds.cqf.cql.engine.runtime.ValueSet;
import org.opencds.cqf.cql.engine.terminology.TerminologyProvider;
import org.opencds.cqf.cql.engine.terminology.ValueSetInfo;

public class ValueSetRefEvaluator extends org.cqframework.cql.elm.execution.ValueSetRef {

    public static ValueSet toValueSet(Context context, ValueSetRef vsr) {
        boolean enteredLibrary = context.enterLibrary(vsr.getLibraryName());
        try {
            ValueSetDef vsd = context.resolveValueSetRef(vsr.getName());
            ValueSet vs = new ValueSet().withId(vsd.getId()).withVersion(vsd.getVersion());
            for (CodeSystemRef csr : vsd.getCodeSystem()) {
                CodeSystem cs = CodeSystemRefEvaluator.toCodeSystem(context, csr);
                vs.addCodeSystem(cs);
            }
            return vs;
        }
        finally {
            context.exitLibrary(enteredLibrary);
        }
    }

    @Override
    protected Object internalEvaluate(Context context) {
        ValueSet vs = toValueSet(context, this);

        if (isPreserve() != null && isPreserve()) {
            return vs;
        }
        else {
            TerminologyProvider tp = context.resolveTerminologyProvider();
            return tp.expand(ValueSetInfo.fromValueSet(vs));
        }
    }
}
