package org.cqframework.cql.cql2elm.model;

import org.hl7.elm.r1.AliasedQuerySource;

import java.util.Objects;
import java.util.StringJoiner;

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

    @Override
    public boolean equals(Object theO) {
        if (this == theO) {
            return true;
        }
        if (theO == null || getClass() != theO.getClass()) {
            return false;
        }
        ResolvedIdentifier that = (ResolvedIdentifier) theO;
        return matchType == that.matchType && Objects.equals(resolvedElement, that.resolvedElement) && Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(matchType, resolvedElement, identifier);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ResolvedIdentifier.class.getSimpleName() + "[", "]")
                .add("matchType=" + matchType)
                .add("resolvedElement=" + resolvedElement)
                .add("identifier='" + identifier + "'")
                .toString();
    }
}