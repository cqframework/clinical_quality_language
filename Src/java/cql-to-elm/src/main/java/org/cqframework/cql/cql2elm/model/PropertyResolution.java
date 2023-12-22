package org.cqframework.cql.cql2elm.model;

import java.util.Map;
import org.hl7.cql.model.ClassTypeElement;
import org.hl7.cql.model.DataType;
import org.hl7.cql.model.SearchType;
import org.hl7.cql.model.TupleTypeElement;

/**
 * Created by Bryn on 4/19/2019.
 */
public class PropertyResolution {
    private DataType type;
    private String name;
    private String targetMap;
    private boolean isSearchValue = false;

    public PropertyResolution(ClassTypeElement e) {
        this.type = e.getType();
        this.name = e.getName();
        if (e.getTarget() != null) {
            this.targetMap = e.getTarget();
        }
    }

    public PropertyResolution(TupleTypeElement e) {
        this.type = e.getType();
        this.name = e.getName();
    }

    public PropertyResolution(SearchType s) {
        this.type = s.getType();
        this.name = s.getName();
        this.isSearchValue = true;
    }

    public PropertyResolution(DataType type, String name) {
        this(type, name, null);
    }

    public PropertyResolution(DataType type, String name, Map<DataType, String> targetMaps) {
        if (type == null) {
            throw new IllegalArgumentException("type cannot be null");
        }

        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }
        this.type = type;
        this.name = name;

        if (targetMaps != null && targetMaps.size() > 0) {
            StringBuilder builder = new StringBuilder();
            for (Map.Entry<DataType, String> entry : targetMaps.entrySet()) {
                if (builder.length() > 0) {
                    builder.append(";");
                }
                if (targetMaps.size() > 1) {
                    builder.append(entry.getKey().toString());
                    builder.append(":");
                }
                builder.append(entry.getValue());
            }
            this.targetMap = builder.toString();
        }
    }

    public DataType getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

    public String getTargetMap() {
        return this.targetMap;
    }

    public boolean isSearch() {
        return this.isSearchValue;
    }
}
