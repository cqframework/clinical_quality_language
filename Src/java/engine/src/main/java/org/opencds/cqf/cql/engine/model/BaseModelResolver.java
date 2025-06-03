package org.opencds.cqf.cql.engine.model;

import org.opencds.cqf.cql.engine.exception.InvalidCast;

public abstract class BaseModelResolver implements ModelResolver {
    public Boolean is(Object value, Class<?> type) {
        if (value == null) {
            return null;
        }

        return type.isAssignableFrom(value.getClass());
    }

    public Object as(Object value, Class<?> type, boolean isStrict) {
        if (value == null) {
            return null;
        }

        if (type.isAssignableFrom(value.getClass())) {
            return value;
        }

        if (isStrict) {
            throw new InvalidCast(String.format(
                    "Cannot cast a value of type %s as %s.", value.getClass().getName(), type.getName()));
        }

        return null;
    }
}
