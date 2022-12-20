package org.opencds.cqf.cql.engine.elm.execution;

import org.cqframework.cql.elm.execution.CodeRef;
import org.cqframework.cql.elm.execution.ConceptDef;
import org.cqframework.cql.elm.execution.ConceptRef;
import org.opencds.cqf.cql.engine.execution.Context;
import org.opencds.cqf.cql.engine.runtime.Code;
import org.opencds.cqf.cql.engine.runtime.Concept;

import java.util.ArrayList;
import java.util.List;

public class ConceptRefEvaluator extends org.cqframework.cql.elm.execution.ConceptRef {

    public static Concept toConcept(Context context, ConceptRef cr) {
        boolean enteredLibrary = context.enterLibrary(cr.getLibraryName());
        try {
            ConceptDef cd = context.resolveConceptRef(cr.getName());

            List<Code> codeList = new ArrayList<Code>();
            for (CodeRef r : cd.getCode()) {
                Code c = CodeRefEvaluator.toCode(context, r);
                codeList.add(c);
            }

            return new Concept().withDisplay(cd.getDisplay()).withCodes(codeList);
        }
        finally {
            context.exitLibrary(enteredLibrary);
        }
    }

    @Override
    protected Object internalEvaluate(Context context) {
        return toConcept(context, this);
    }

}
