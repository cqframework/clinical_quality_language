package org.opencds.cqf.cql.engine.elm.executing

import org.cqframework.cql.elm.visiting.ElmLibraryVisitor
import org.hl7.elm.r1.Property
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.runtime.CodeSystem
import org.opencds.cqf.cql.engine.runtime.Concept
import org.opencds.cqf.cql.engine.runtime.CqlClassInstance
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.runtime.Ratio
import org.opencds.cqf.cql.engine.runtime.Tuple
import org.opencds.cqf.cql.engine.runtime.ValueSet

object PropertyEvaluator {
    fun internalEvaluate(
        elm: Property?,
        state: State?,
        visitor: ElmLibraryVisitor<Any?, State?>,
    ): Any? {
        var target: Any? = null

        if (elm!!.source != null) {
            target = visitor.visitExpression(elm.source!!, state)
        } else if (elm.scope != null) {
            target = state!!.resolveVariable(elm.scope, true)!!.value
        }

        if (target == null) {
            return null
        }

        return resolvePath(target, elm.path!!)
    }

    /**
     * Resolves a path on a target object. The path may include qualifiers (`.`) and indexers
     * (`[x]`).
     */
    fun resolvePath(target: Any?, path: String): Any? {
        var target = target
        val qualifiersAndIndexers =
            path.split('.', '[', ']').map { it.trim() }.filter { it.isNotEmpty() }
        for (qualifierOrIndexer in qualifiersAndIndexers) {
            val indexer = qualifierOrIndexer.toIntOrNull()
            target =
                if (indexer == null) {
                    resolveProperty(target, qualifierOrIndexer)
                } else {
                    (target as? Iterable<*>)?.elementAtOrNull(indexer)
                }
        }
        return target
    }

    /** Resolves a property on a target object. */
    private fun resolveProperty(target: Any?, property: String): Any? {
        if (target == null) {
            return null
        }

        return when (target) {
            is Quantity -> {
                when (property) {
                    "value" -> target.value
                    "unit" -> target.unit
                    else -> null
                }
            }
            is Ratio -> {
                when (property) {
                    "numerator" -> target.numerator
                    "denominator" -> target.denominator
                    else -> null
                }
            }
            is Code -> {
                when (property) {
                    "code" -> target.code
                    "display" -> target.display
                    "system" -> target.system
                    "version" -> target.version
                    else -> null
                }
            }
            is Concept -> {
                when (property) {
                    "display" -> target.display
                    "codes" -> target.codes
                    else -> null
                }
            }
            is CodeSystem -> {
                when (property) {
                    "id" -> target.id
                    "version" -> target.version
                    "name" -> target.name
                    else -> null
                }
            }
            is ValueSet -> {
                when (property) {
                    "id" -> target.id
                    "version" -> target.version
                    "name" -> target.name
                    "codesystems" -> target.codeSystems
                    else -> null
                }
            }
            is Interval -> {
                when (property) {
                    "low" -> target.low
                    "lowClosed" -> target.lowClosed
                    "high" -> target.high
                    "highClosed" -> target.highClosed
                    else -> null
                }
            }
            is Tuple -> target.getElement(property)
            is CqlClassInstance -> target.elements[property]
            else -> throw IllegalArgumentException("Could not resolve path '$property' on $target.")
        }
    }
}
