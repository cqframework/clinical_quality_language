package org.cqframework.cql.cql2elm.model;

import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import org.cqframework.cql.cql2elm.LibraryBuilder;
import org.hl7.elm.r1.Element;
import org.hl7.elm.r1.ExpressionDef;

// LUKETODO:  javadoc
public class ResolvedIdentifierContext {
    private final String identifier;
    private final Element nullableElement;

    private enum ResolvedIdentifierMatchType {
        EXACT,
        CASE_INSENSITIVE
    }

    // TODO:  enum instead?
    private boolean isExactMatch;
    private final ResolvedIdentifierMatchType matchType;

    public static ResolvedIdentifierContext exactMatch(String identifier, Element nullableElement) {
        return new ResolvedIdentifierContext(identifier, nullableElement, ResolvedIdentifierMatchType.EXACT);
    }

    public static ResolvedIdentifierContext caseInsensitiveMatch(String identifier, Element nullableElement) {
        return new ResolvedIdentifierContext(identifier, nullableElement, ResolvedIdentifierMatchType.CASE_INSENSITIVE);
    }

    private ResolvedIdentifierContext(
            String identifier, Element nullableElement, ResolvedIdentifierMatchType matchType) {
        this.identifier = identifier;
        this.nullableElement = nullableElement;
        this.matchType = matchType;
    }

    public Element getExactMatchElement() {
        if (isExactMatch()) {
            return nullableElement;
        }

        return null;
    }

    public Optional<Element> getExactMatchElement2() {
        if (isExactMatch()) {
            return Optional.ofNullable(nullableElement);
        }

        return Optional.empty();
    }

    public Optional<Element> getCaseInsensitiveMatchElement() {
        if (!isExactMatch()) {
            return Optional.ofNullable(nullableElement);
        }

        return Optional.empty();
    }

    private boolean isExactMatch() {
        //        return isExactMatch;
        return ResolvedIdentifierMatchType.EXACT == matchType;
    }

    // LUKETODO:  figure out where to call this from
    public void warnCaseInsensitiveIfApplicable(LibraryBuilder libraryBuilder) {
        if (nullableElement != null && !isExactMatch)
            if (nullableElement instanceof ExpressionDef) {
                final ExpressionDef caseInsensitiveExpressionDef = (ExpressionDef) nullableElement;
                libraryBuilder.reportWarning(
                        String.format(
                                "Could not find identifier: [%s].  Did you mean [%s]?",
                                identifier, caseInsensitiveExpressionDef.getName()),
                        nullableElement);
            }
        // LUKETODO:  what about other Element types?
    }

    @Override
    public boolean equals(Object theO) {
        if (this == theO) {
            return true;
        }
        if (theO == null || getClass() != theO.getClass()) {
            return false;
        }
        ResolvedIdentifierContext that = (ResolvedIdentifierContext) theO;
        return isExactMatch == that.isExactMatch
                && Objects.equals(identifier, that.identifier)
                && Objects.equals(nullableElement, that.nullableElement)
                && matchType == that.matchType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, nullableElement, isExactMatch, matchType);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ResolvedIdentifierContext.class.getSimpleName() + "[", "]")
                .add("identifier='" + identifier + "'")
                .add("nullableElement=" + nullableElement)
                .add("isExactMatch=" + isExactMatch)
                .add("matchType=" + matchType)
                .toString();
    }
}
