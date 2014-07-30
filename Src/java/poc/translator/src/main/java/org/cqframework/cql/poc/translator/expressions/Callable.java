package org.cqframework.cql.poc.translator.expressions;

import java.util.List;

/**
 * Created by bobd on 7/28/14.
 */
public interface Callable {

    public Object call(Context ctx, List<Object> args);
}
