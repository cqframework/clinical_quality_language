package org.cqframework.cql.poc.translator.expressions;

import org.cqframework.cql.poc.translator.model.logger.Trackable;

/**
 * Created by bobd on 7/23/14.
 */
public abstract class Expression extends Trackable {


    public Object evaluate(Context ctx) {
        throw new RuntimeException("not implemented");
    }

    public abstract String toCql();
}
