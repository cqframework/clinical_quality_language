package org.cqframework.cql.elm.execution;

import org.apache.commons.lang3.NotImplementedException;
import org.cqframework.cql.execution.Context;

/**
 * Created by Bryn on 1/11/2016.
 */
public class Executable {
    public Object evaluate(Context context) {
        throw new NotImplementedException(String.format("evaluate not implemented for class %s", this.getClass().getSimpleName()));
    }
}
