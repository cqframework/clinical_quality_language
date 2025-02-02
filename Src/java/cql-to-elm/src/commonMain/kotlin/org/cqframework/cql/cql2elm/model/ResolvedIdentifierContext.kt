package org.cqframework.cql.cql2elm.model

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
import kotlin.reflect.KClass
import kotlin.reflect.cast

/**
 * Context for resolved identifiers containing the identifier, the resolved element (if non-null) as
 * well as the type of matching done to retrieve the element, whether case-sensitive or
 * case-insensitive.
 */
// TODO @ExposedCopyVisibility
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
                "Could not find identifier: [$identifier].  Did you mean [$it]?"
            }
        }

        return null
    }

    fun <T : Element> resolveIdentifier(clazz: KClass<T>): T? {
        return if (exactMatchElement != null && clazz.isInstance(element)) {
            clazz.cast(element)
        } else {
            null
        }
    }

    fun <T : Element> getElementOfType(clazz: KClass<T>): T? {
        if (clazz.isInstance(element)) {
            return clazz.cast(element)
        }

        return null
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
