package org.cqframework.cql.cql2elm.model;

public class ResolvedIdentifier {

    private MatchType matchType;
    private Object resolvedElement;
    private String identifier;

    //class should only be initialized via ResolvedIdentifierList
    private ResolvedIdentifier() {}

    protected ResolvedIdentifier(String identifier,
                                 MatchType matchType,
                                 Object resolvedElement) {

        this.matchType = matchType;
        this.identifier = identifier;
        this.resolvedElement = resolvedElement;

    }

    public MatchType getMatchType() {
        return this.matchType;
    }

    public Object getResolvedElement() {
        return resolvedElement;
    }

    public String getIdentifier() {
        return identifier;
    }


}