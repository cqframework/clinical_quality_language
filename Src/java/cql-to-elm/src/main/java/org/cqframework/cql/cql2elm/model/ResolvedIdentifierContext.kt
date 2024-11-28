package org.cqframework.cql.cql2elm.model

import java.util.*
import org.hl7.elm.r1.CodeDef
import org.hl7.elm.r1.CodeSystemDef
import org.hl7.elm.r1.ConceptDef
import org.hl7.elm.r1.ContextDef
import org.hl7.elm.r1.Element
import org.hl7.elm.r1.ExpressionDef
import org.hl7.elm.r1.OperandDef
import org.hl7.elm.r1.ParameterDef
import org.hl7.elm.r1.TupleElementDefinition
import org.hl7.elm.r1.ValueSetDef

/**
 * Context for resolved identifiers containing the identifier, the resolved element (if non-null) as
 * well as the type of matching done to retrieve the element, whether case-sensitive or
 * case-insensitive.
 */
class ResolvedIdentifierContext
private constructor(
    private val identifier: String,
    private val element: Element?,
    private val matchType: ResolvedIdentifierMatchType
) {
    private enum class ResolvedIdentifierMatchType {
        EXACT,
        CASE_INSENSITIVE
    }

    val exactMatchElement: Optional<Element>
        get() {
            if (isExactMatch) {
                return Optional.ofNullable(element)
            }

            return Optional.empty()
        }

    private val isExactMatch: Boolean
        get() = ResolvedIdentifierMatchType.EXACT == matchType

    fun warnCaseInsensitiveIfApplicable(): Optional<String> {
        if (element != null && !isExactMatch) {
            return getName(element).map { name: String? ->
                String.format(
                    Locale.US,
                    "Could not find identifier: [%s].  Did you mean [%s]?",
                    identifier,
                    name
                )
            }
        }

        return Optional.empty()
    }

    fun <T : Element> resolveIdentifier(clazz: Class<T>): T? {
        return exactMatchElement.filter { clazz.isInstance(it) }.map { clazz.cast(it) }.orElse(null)
    }

    fun <T : Element> getElementOfType(clazz: Class<T>): Optional<T> {
        if (clazz.isInstance(element)) {
            return Optional.of(clazz.cast(element))
        }

        return Optional.empty()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || javaClass != other.javaClass) {
            return false
        }
        val that = other as ResolvedIdentifierContext
        return identifier == that.identifier &&
            element == that.element &&
            matchType == that.matchType
    }

    override fun hashCode(): Int {
        return Objects.hash(identifier, element, matchType)
    }

    override fun toString(): String {
        return StringJoiner(", ", ResolvedIdentifierContext::class.java.simpleName + "[", "]")
            .add("identifier='$identifier'")
            .add("nullableElement=$element")
            .add("matchType=$matchType")
            .toString()
    }

    companion object {
        fun exactMatch(identifier: String, element: Element?): ResolvedIdentifierContext {
            return ResolvedIdentifierContext(identifier, element, ResolvedIdentifierMatchType.EXACT)
        }

        fun caseInsensitiveMatch(identifier: String, element: Element?): ResolvedIdentifierContext {
            return ResolvedIdentifierContext(
                identifier,
                element,
                ResolvedIdentifierMatchType.CASE_INSENSITIVE
            )
        }

        private fun getName(element: Element): Optional<String> {
            return when (element) {
                is ExpressionDef -> Optional.of(element.name)
                is ValueSetDef -> Optional.of(element.name)
                is OperandDef -> Optional.of(element.name)
                is TupleElementDefinition -> Optional.of(element.name)
                is CodeDef -> Optional.of(element.name)
                is ConceptDef -> Optional.of(element.name)
                is ParameterDef -> Optional.of(element.name)
                is CodeSystemDef -> Optional.of(element.name)
                is ContextDef -> Optional.of(element.name)
                else -> Optional.empty()
            }
        }
    }
}
