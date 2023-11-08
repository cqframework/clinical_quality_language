package org.cqframework.cql.cql2elm.model;

import org.hl7.cql.model.DataType;
import org.hl7.cql.model.ListType;
import org.hl7.elm.r1.AliasedQuerySource;
import org.hl7.elm.r1.LetClause;

import java.util.Collection;
import java.util.HashMap;

public class QueryContext {
    private final HashMap<String, AliasedQuerySource> sources = new HashMap<>();
    private final HashMap<String, LetClause> lets = new HashMap<>();

    private void internalAddQuerySource(AliasedQuerySource source) {
        sources.put(source.getAlias(), source);
    }

    // Adds a related (i.e. with or without) source, which does not change cardinality of the query
    public void addRelatedQuerySource(AliasedQuerySource source) {
        internalAddQuerySource(source);
    }

    // Adds primary sources, which affect cardinality (any primary plural source results in a plural query)
    public void addPrimaryQuerySources(Collection<AliasedQuerySource> sources) {
        for (AliasedQuerySource source : sources) {
            internalAddQuerySource(source);
            if (source.getResultType() instanceof ListType) {
                isSingularValue = false;
            }
        }
    }

    public Collection<AliasedQuerySource> getQuerySources() {
        return sources.values();
    }

    public void removeQuerySource(AliasedQuerySource source) {
        sources.remove(source.getAlias());
    }

    public void removeQuerySources(Collection<AliasedQuerySource> sources) {
        for (AliasedQuerySource source : sources) {
            removeQuerySource(source);
        }
    }

    public void addLetClauses(Collection<LetClause> lets) {
        for (LetClause let : lets) {
            addLetClause(let);
        }
    }

    public void addLetClause(LetClause let) {
        lets.put(let.getIdentifier(), let);
    }

    public void removeLetClause(LetClause let) {
        lets.remove(let.getIdentifier());
    }

    public void removeLetClauses(Collection<LetClause> lets) {
        for (LetClause let : lets) {
            removeLetClause(let);
        }
    }

    public AliasedQuerySource resolveAlias(String identifier) {
        return sources.get(identifier);
    }

    public ResolvedIdentifierList resolveCaseIgnoredAliases(String identifier) {
        final ResolvedIdentifierList resolvedIdentifierList = new ResolvedIdentifierList();

        sources.entrySet()
                .stream()
                .filter(k -> k.getKey().equalsIgnoreCase(identifier) && !k.getKey().equals(identifier))
                .forEach(entry -> resolvedIdentifierList.addResolvedIdentifier(new ResolvedIdentifier(entry.getKey(), MatchType.CASE_IGNORED, entry.getValue())));

        return resolvedIdentifierList;
    }

    public LetClause resolveLet(String identifier) {
        return lets.get(identifier);
    }

    public ResolvedIdentifierList resolveCaseIgnoredLets(String identifier) {
        final ResolvedIdentifierList resolvedIdentifierList = new ResolvedIdentifierList();

        lets.entrySet()
                .stream()
                .filter(k -> k.getKey().equalsIgnoreCase(identifier) && !k.getKey().equals(identifier))
                .forEach(entry -> resolvedIdentifierList.addResolvedIdentifier(new ResolvedIdentifier(entry.getKey(), MatchType.CASE_IGNORED, entry.getValue())));

        return resolvedIdentifierList;
    }

    private boolean isSingularValue = true;
    public boolean isSingular() {
        return isSingularValue;
    }

    private boolean inSourceClauseValue;
    public void enterSourceClause() {
        inSourceClauseValue = true;
    }

    public void exitSourceClause() {
        inSourceClauseValue = false;
    }

    public boolean inSourceClause() {
        return inSourceClauseValue;
    }

    private boolean inSortClauseValue;
    public void enterSortClause() {
        inSortClauseValue = true;
    }

    public void exitSortClause() {
        inSortClauseValue = false;
    }

    public boolean inSortClause() {
        return inSortClauseValue;
    }

    private boolean isImplicitValue;
    public boolean isImplicit() { return isImplicitValue; }
    public void setIsImplicit(boolean isImplicitValue) {
        this.isImplicitValue = isImplicitValue;
    }

    private DataType resultElementType;
    public DataType getResultElementType() {
        return resultElementType;
    }

    public void setResultElementType(DataType resultElementType) {
        this.resultElementType = resultElementType;
    }

    private boolean referencesSpecificContextValue;
    public boolean referencesSpecificContext() {
        return referencesSpecificContextValue;
    }

    public void referenceSpecificContext() {
        referencesSpecificContextValue = true;
    }
}
