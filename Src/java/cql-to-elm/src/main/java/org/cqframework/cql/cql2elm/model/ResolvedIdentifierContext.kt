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
data class ResolvedIdentifierContext
private constructor(
    private val identifier: String,
    private val element: Element?,
    private val matchType: ResolvedIdentifierMatchType
) {
    private enum class ResolvedIdentifierMatchType {
        EXACT,
        CASE_INSENSITIVE
    }

    val exactMatchElement: Element?
        get() {
            return if (isExactMatch) element else null
        }

    private val isExactMatch: Boolean
        get() = ResolvedIdentifierMatchType.EXACT == matchType

    fun warnCaseInsensitiveIfApplicable(): String? {
        if (element != null && !isExactMatch) {
            return getName(element)?.let {
                String.format(
                    Locale.US,
                    "Could not find identifier: [%s].  Did you mean [%s]?",
                    identifier,
                    it
                )
            }
        }

        return null
    }

    fun <T : Element> resolveIdentifier(clazz: Class<T>): T? {
        return if (exactMatchElement != null && clazz.isInstance(element)) {
            clazz.cast(element)
        } else {
            null
        }
    }

    fun <T : Element> getElementOfType(clazz: Class<T>): Optional<T> {
        if (clazz.isInstance(element)) {
            return Optional.of(clazz.cast(element))
        }

        return Optional.empty()
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

        private fun getName(element: Element): String? {
            return when (element) {
                is ExpressionDef -> element.name
                is ValueSetDef -> element.name
                is OperandDef -> element.name
                is TupleElementDefinition -> element.name
                is CodeDef -> element.name
                is ConceptDef -> element.name
                is ParameterDef -> element.name
                is CodeSystemDef -> element.name
                is ContextDef -> element.name
                else -> null
            }
        }
    }
}
