package org.hl7.cql.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bryn on 3/20/2019.
 */
public class Relationship {
    public Relationship(ModelContext context, List<String> relatedKeys) {
        this.context = context;
        this.relatedKeys.addAll(relatedKeys);
    }

    private final ModelContext context;

    public ModelContext getContext() {
        return context;
    }

    private final ArrayList<String> relatedKeys = new ArrayList<>();

    public List<String> getRelatedKeys() {
        return relatedKeys;
    }
}
