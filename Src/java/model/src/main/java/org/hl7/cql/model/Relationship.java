package org.hl7.cql.model;

import java.util.ArrayList;

/**
 * Created by Bryn on 3/20/2019.
 */
public class Relationship {
    public Relationship(ModelContext context, Iterable<String> relatedKeys) {
        this.context = context;
        for (String key : relatedKeys) {
            this.relatedKeys.add(key);
        }
    }

    private ModelContext context;

    public ModelContext getContext() {
        return context;
    }

    private ArrayList<String> relatedKeys = new ArrayList<>();

    public Iterable<String> getRelatedKeys() {
        return relatedKeys;
    }
}
