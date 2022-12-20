package org.opencds.cqf.cql.engine.elm.execution;

import org.opencds.cqf.cql.engine.execution.Context;

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

public class CodeEvaluator extends org.cqframework.cql.elm.execution.Code {
    @Override
    protected Object internalEvaluate(Context context) {
        org.opencds.cqf.cql.engine.runtime.Code code = new org.opencds.cqf.cql.engine.runtime.Code().withCode(this.getCode()).withDisplay(this.getDisplay());
        org.cqframework.cql.elm.execution.CodeSystemRef codeSystemRef = this.getSystem();
        if (codeSystemRef != null) {
            boolean enteredLibrary = context.enterLibrary(codeSystemRef.getLibraryName());
            try {
                org.cqframework.cql.elm.execution.CodeSystemDef codeSystemDef = context.resolveCodeSystemRef(codeSystemRef.getName());
                code.setSystem(codeSystemDef.getId());
                code.setVersion(codeSystemDef.getVersion());
            }
            finally {
                context.exitLibrary(enteredLibrary);
            }
        }

        return code;
    }
}
