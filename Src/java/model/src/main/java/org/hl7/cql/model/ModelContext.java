package org.hl7.cql.model;

import java.util.ArrayList;

/**
 * Created by Bryn on 3/20/2019.
 */
public class ModelContext {
    public ModelContext(String name, ClassType type, Iterable<String> keys, String birthDateElement) {
        this.name = name;
        this.type = type;
        this.birthDateElement = birthDateElement;
        if (keys != null) {
            for (String key : keys) {
                this.keys.add(key);
            }
        }
    }

    private String name;

    public String getName() {
        return name;
    }

    private ClassType type;

    public ClassType getType() {
        return type;
    }

    private String birthDateElement;

    public String getBirthDateElement() {
        return birthDateElement;
    }

    private ArrayList<String> keys = new ArrayList<>();

    public Iterable<String> getKeys() {
        return keys;
    }
}
