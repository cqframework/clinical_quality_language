package org.opencds.cqf.cql.engine.elm.execution;

import org.cqframework.cql.elm.execution.CodeDef;
import org.cqframework.cql.elm.execution.CodeRef;
import org.opencds.cqf.cql.engine.execution.Context;
import org.opencds.cqf.cql.engine.runtime.Code;
import org.opencds.cqf.cql.engine.runtime.CodeSystem;

public class CodeRefEvaluator extends org.cqframework.cql.elm.execution.CodeRef {

    public static Code toCode(Context context, CodeRef cr) {
        boolean enteredLibrary = context.enterLibrary(cr.getLibraryName());
        try {
            CodeDef cd = context.resolveCodeRef(cr.getName());
            CodeSystem cs = (CodeSystem)cd.getCodeSystem().evaluate(context);
            return new Code().withCode(cd.getId()).withSystem(cs.getId()).withDisplay(cd.getDisplay()).withVersion(cs.getVersion());
        }
        finally {
            context.exitLibrary(enteredLibrary);
        }
    }

    @Override
    protected Object internalEvaluate(Context context) {
        return toCode(context, this);
    }
}
