package org.opencds.cqf.cql.engine.runtime;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.opencds.cqf.cql.engine.elm.execution.EqualEvaluator;
import org.opencds.cqf.cql.engine.elm.execution.EquivalentEvaluator;
import org.opencds.cqf.cql.engine.elm.execution.ToStringEvaluator;
import org.opencds.cqf.cql.engine.execution.Context;

public class Tuple implements CqlType {

    protected LinkedHashMap<String, Object> elements;

    private Context context;

    public Tuple() {
        this(null);
    }

    public Tuple(Context context) {
        this.context = context;
        this.elements = new LinkedHashMap<>();
    }

    public Object getElement(String key) {
        return elements.get(key);
    }

    public HashMap<String, Object> getElements() {
        if (elements == null) { return new HashMap<>(); }
        return elements;
    }

    public void setElements(LinkedHashMap<String, Object> elements) {
        this.elements = elements;
    }

    public Tuple withElements(LinkedHashMap<String, Object> elements) {
        setElements(elements);
        return this;
    }

    public Context getContext() {
        return this.context;
    }

    @Override
    public Boolean equivalent(Object other) {
        if (this.getElements().size() != ((Tuple) other).getElements().size()) {
            return false;
        }

        for (String key : ((Tuple) other).getElements().keySet()) {
            if (this.getElements().containsKey(key)) {
                Object areKeyValsSame = EquivalentEvaluator.equivalent(((Tuple) other).getElements().get(key), this.getElements().get(key), context);
                if (!(Boolean) areKeyValsSame) {
                    return false;
                }
            }
            else {
                return false;
            }
        }
        return true;
    }

    @Override
    public Boolean equal(Object other) {
        if (this.getElements().size() != ((Tuple) other).getElements().size()) {
            return false;
        }

        for (String key : ((Tuple) other).getElements().keySet()) {
            if (this.getElements().containsKey(key)) {
                if (((Tuple) other).getElements().get(key) == null
                        && this.getElements().get(key) == null)
                {
                    continue;
                }
                Boolean equal = EqualEvaluator.equal(((Tuple) other).getElements().get(key), this.getElements().get(key), context);
                if (equal == null) { return null; }
                else if (!equal) { return false; }
            }
            else { return false; }
        }

        return true;
    }

    @Override
    public String toString() {
        if (elements.size() == 0) {
            return "Tuple { : }";
        }

        StringBuilder builder = new StringBuilder("Tuple {\n");
        for (Map.Entry<String, Object> entry : elements.entrySet()) {
            builder.append("\t\"").append(entry.getKey()).append("\": ").append(ToStringEvaluator.toString(entry.getValue())).append("\n");
        }
        return builder.append("}").toString();
    }
}
