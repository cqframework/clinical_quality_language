package org.opencds.cqf.cql.engine.elm.execution;

import org.apache.commons.lang3.NotImplementedException;
import org.opencds.cqf.cql.engine.exception.CqlException;
public class Executable
{
    public Object evaluate() throws CqlException
    {
        return null;
    }

    protected Object internalEvaluate() {
        throw new NotImplementedException(String.format("evaluate not implemented for class %s",
                this.getClass().getSimpleName()));
    }
}
