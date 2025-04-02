package org.opencds.cqf.cql.engine.runtime;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.opencds.cqf.cql.engine.elm.executing.EqualEvaluator;
import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator;
import org.opencds.cqf.cql.engine.elm.executing.ToStringEvaluator;
import org.opencds.cqf.cql.engine.execution.State;

public class Tuple implements CqlType {

    protected LinkedHashMap<String, Object> elements;

    private State state;

    public Tuple() {
        this(null);
    }

    public Tuple(State state) {
        this.state = state;
        this.elements = new LinkedHashMap<>();
    }

    public Object getElement(String key) {
        return elements.get(key);
    }

    public HashMap<String, Object> getElements() {
        if (elements == null) {
            return new HashMap<>();
        }
        return elements;
    }

    public void setElements(LinkedHashMap<String, Object> elements) {
        this.elements = elements;
    }

    public Tuple withElements(LinkedHashMap<String, Object> elements) {
        setElements(elements);
        return this;
    }

    public State getState() {
        return this.state;
    }

    @Override
    public Boolean equivalent(Object other) {
        if (this.getElements().size() != ((Tuple) other).getElements().size()) {
            return false;
        }

        for (String key : ((Tuple) other).getElements().keySet()) {
            if (this.getElements().containsKey(key)) {
                Object areKeyValsSame = EquivalentEvaluator.equivalent(
                        ((Tuple) other).getElements().get(key),
                        this.getElements().get(key),
                        state);
                if (!(Boolean) areKeyValsSame) {
                    return false;
                }
            } else {
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
                        && this.getElements().get(key) == null) {
                    continue;
                }
                Boolean equal = EqualEvaluator.equal(
                        ((Tuple) other).getElements().get(key),
                        this.getElements().get(key),
                        state);
                if (equal == null) {
                    return null;
                } else if (!equal) {
                    return false;
                }
            } else {
                return false;
            }
        }

        return true;
    }

    @Override
    public String toString() {
        // Kick off recursion at indent level 0
        return toPrettyString(elements, 0);
    }

    /**
     * Recursively builds a nicely-indented string representation of a Tuple's elements.
     */
    private String toPrettyString(Map<String, Object> tupleElements, int indentLevel) {
        if (tupleElements.isEmpty()) {
            return "Tuple {}";
        }
        var sb = new StringBuilder();
        var currentIndent = indent(indentLevel); // indentation for "Tuple {" and closing brace
        var childIndent = indent(indentLevel + 1); // indentation for fields within the tuple

        sb.append("Tuple {\n");

        // We can iterate with an index to detect the last element or simply not add an extra newline
        var i = 0;
        var size = tupleElements.size();
        for (Map.Entry<String, Object> entry : tupleElements.entrySet()) {
            var fieldName = entry.getKey();
            var fieldValue = entry.getValue();

            // Print the field name, indented one level more than "Tuple {"
            sb.append(childIndent).append(fieldName).append(": ");

            // If the field value is itself a nested Tuple, recurse
            if (fieldValue instanceof Tuple) {
                // Recursively build nested representation
                sb.append(((Tuple) fieldValue).toPrettyString(((Tuple) fieldValue).elements, indentLevel + 1));
            } else {
                // Otherwise, use a function that handles quoting, escaping, etc.
                sb.append(ToStringEvaluator.toString(entry.getValue()));
            }

            // Add a newline for each field, except possibly the last
            if (++i < size) {
                sb.append("\n");
            }
        }

        // Close the Tuple with matching indentation
        sb.append("\n").append(currentIndent).append("}");

        return sb.toString();
    }

    /**
     * Helper method to produce indentation spaces. For each indent level, we add two spaces.
     */
    private static String indent(int level) {
        // With Android API level 28 (Java 8), we cannot use Java 11's String::repeat.
        // After upgrading, this can be replaced with:
        // return "  ".repeat(Math.max(0, level));

        var sb = new StringBuilder();
        for (int i = 0; i < level; i++) {
            sb.append("  ");
        }
        return sb.toString();
    }
}
