package org.cqframework.cql.cql2elm.model;

import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import org.hl7.elm.r1.CodeDef;
import org.hl7.elm.r1.CodeSystemDef;
import org.hl7.elm.r1.ConceptDef;
import org.hl7.elm.r1.ContextDef;
import org.hl7.elm.r1.Element;
import org.hl7.elm.r1.ExpressionDef;
import org.hl7.elm.r1.OperandDef;
import org.hl7.elm.r1.ParameterDef;
import org.hl7.elm.r1.TupleElementDefinition;
import org.hl7.elm.r1.ValueSetDef;

/**
 * Context for resolved identifiers containing the identifier, the resolved element (if non-null) as well as the type
 * of matching done to retrieve the element, whether case-sensitive or case-insensitive.
 */
public class ResolvedIdentifierContext {
    private final String identifier;
    private final Element nullableElement;

    private enum ResolvedIdentifierMatchType {
        EXACT,
        CASE_INSENSITIVE
    }

    // TODO:  enum instead?
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

    public Optional<Element> getExactMatchElement() {
        if (isExactMatch()) {
            return Optional.ofNullable(nullableElement);
        }

        return Optional.empty();
    }

    private boolean isExactMatch() {
        return ResolvedIdentifierMatchType.EXACT == matchType;
    }

    public Optional<String> warnCaseInsensitiveIfApplicable() {
        if (nullableElement != null && !isExactMatch()) {
            return getName(nullableElement)
                    .map(name -> String.format(
                            "Could not resolve identifier %s. Consider whether the identifier %s (differing only in case) was intended.",
                            identifier, name));
        }

        return Optional.empty();
    }

    public <T extends Element> T resolveIdentifier(Class<T> clazz) {
        return getExactMatchElement().filter(clazz::isInstance).map(clazz::cast).orElse(null);
    }

    public <T extends Element> Optional<T> getElementOfType(Class<T> clazz) {
        if (clazz.isInstance(nullableElement)) {
            return Optional.of(clazz.cast(nullableElement));
        }

        return Optional.empty();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        ResolvedIdentifierContext that = (ResolvedIdentifierContext) other;
        return Objects.equals(identifier, that.identifier)
                && Objects.equals(nullableElement, that.nullableElement)
                && matchType == that.matchType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, nullableElement, matchType);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ResolvedIdentifierContext.class.getSimpleName() + "[", "]")
                .add("identifier='" + identifier + "'")
                .add("nullableElement=" + nullableElement)
                .add("matchType=" + matchType)
                .toString();
    }

    private static Optional<String> getName(Element element) {
        // TODO:  consider other Elements that don't have getName()
        if (element instanceof ExpressionDef) {
            return Optional.of(((ExpressionDef) element).getName());
        }

        if (element instanceof ValueSetDef) {
            return Optional.of(((ValueSetDef) element).getName());
        }

        if (element instanceof OperandDef) {
            return Optional.of(((OperandDef) element).getName());
        }

        if (element instanceof TupleElementDefinition) {
            return Optional.of(((TupleElementDefinition) element).getName());
        }

        if (element instanceof CodeDef) {
            return Optional.of(((CodeDef) element).getName());
        }

        if (element instanceof ConceptDef) {
            return Optional.of(((ConceptDef) element).getName());
        }

        if (element instanceof ParameterDef) {
            return Optional.of(((ParameterDef) element).getName());
        }

        if (element instanceof CodeSystemDef) {
            return Optional.of(((CodeSystemDef) element).getName());
        }

        if (element instanceof ContextDef) {
            return Optional.of(((ContextDef) element).getName());
        }

        return Optional.empty();
    }
}
