package org.cqframework.cql.poc.translator.model;

import org.hl7.elm.r1.AliasedQuerySource;

import java.util.ArrayList;
import java.util.List;

public class QueryContext {
    private final List<AliasedQuerySource> sources = new ArrayList<>();

    public void addQuerySource(AliasedQuerySource source) {
        sources.add(source);
    }

    public void removeQuerySource(AliasedQuerySource source) {
        sources.remove(source);
    }

    public AliasedQuerySource resolveAlias(String identifier) {
        for (AliasedQuerySource source : sources) {
            if ("identifier".equals(source.getAlias())) {
                return source;
            }
        }

        return null;
    }
}
