package org.cqframework.cql.cql2elm.model;

import org.cqframework.cql.elm.tracking.ListType;
import org.hl7.elm.r1.AliasedQuerySource;
import org.hl7.elm.r1.DefineClause;

import java.util.Collection;
import java.util.HashMap;

public class QueryContext {
    private final HashMap<String, AliasedQuerySource> sources = new HashMap<>();
    private final HashMap<String, DefineClause> defines = new HashMap<>();

    public void addQuerySources(Collection<AliasedQuerySource> sources) {
        for (AliasedQuerySource source : sources) {
            addQuerySource(source);
        }
    }

    public void addQuerySource(AliasedQuerySource source) {
        sources.put(source.getAlias(), source);
        if (source.getResultType() instanceof ListType) {
            isSingularValue = false;
        }
    }

    public void removeQuerySource(AliasedQuerySource source) {
        sources.remove(source.getAlias());
    }

    public void addDefineClauses(Collection<DefineClause> defines) {
        for (DefineClause define : defines) {
            addDefineClause(define);
        }
    }

    public void addDefineClause(DefineClause define) {
        defines.put(define.getIdentifier(), define);
    }

    public AliasedQuerySource resolveAlias(String identifier) {
        return sources.get(identifier);
    }

    public DefineClause resolveDefine(String identifier) {
        return defines.get(identifier);
    }

    private boolean isSingularValue = true;
    public boolean isSingular() {
        return isSingularValue;
    }
}
