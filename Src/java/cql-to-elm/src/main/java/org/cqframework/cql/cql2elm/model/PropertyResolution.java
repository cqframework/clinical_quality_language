package org.cqframework.cql.cql2elm.model;

import org.hl7.cql.model.ClassTypeElement;
import org.hl7.cql.model.DataType;
import org.hl7.cql.model.TupleTypeElement;

/**
 * Created by Bryn on 4/19/2019.
 */
public class PropertyResolution {
    private DataType type;
    private String target;

    public PropertyResolution(ClassTypeElement e) {
        this.type = e.getType();
        if (e.getTarget() != null) {
            this.target = e.getTarget();
        }
        else {
            this.target = e.getName();
        }
    }

    public PropertyResolution(TupleTypeElement e) {
        this.type = e.getType();
        this.target = e.getName();
    }

    public PropertyResolution(DataType type, String target) {
        if (type == null) {
            throw new IllegalArgumentException("type cannot be null");
        }

        if (target == null) {
            throw new IllegalArgumentException("target cannot be null");
        }
        this.type = type;
        this.target = target;
    }

    public DataType getType() {
        return this.type;
    }

    public String getTarget() {
        return this.target;
    }
}
