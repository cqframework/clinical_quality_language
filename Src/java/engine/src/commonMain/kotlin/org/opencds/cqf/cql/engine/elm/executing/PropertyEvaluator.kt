package org.opencds.cqf.cql.engine.elm.executing

import org.cqframework.cql.elm.visiting.ElmLibraryVisitor
import org.hl7.elm.r1.Property
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.ClassInstance
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.runtime.CodeSystem
import org.opencds.cqf.cql.engine.runtime.Concept
import org.opencds.cqf.cql.engine.runtime.CqlType
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.List
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.runtime.Ratio
import org.opencds.cqf.cql.engine.runtime.Tuple
import org.opencds.cqf.cql.engine.runtime.ValueSet
import org.opencds.cqf.cql.engine.runtime.toCqlBoolean
import org.opencds.cqf.cql.engine.runtime.toCqlDecimal
import org.opencds.cqf.cql.engine.runtime.toCqlList
import org.opencds.cqf.cql.engine.runtime.toCqlString

object PropertyEvaluator {
    fun internalEvaluate(
        elm: Property?,
        state: State?,
        visitor: ElmLibraryVisitor<CqlType?, State?>,
    ): CqlType? {
        var target: CqlType? = null

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
    fun resolvePath(target: CqlType?, path: kotlin.String): CqlType? {
        var target = target
        val qualifiersAndIndexers =
            path.split('.', '[', ']').map { it.trim() }.filter { it.isNotEmpty() }
        for (qualifierOrIndexer in qualifiersAndIndexers) {
            val indexer = qualifierOrIndexer.toIntOrNull()
            target =
                if (indexer == null) {
                    resolveProperty(target, qualifierOrIndexer)
                } else {
                    (target as? List)?.elementAtOrNull(indexer)
                }
        }
        return target
    }

    /** Resolves a property on a target object. */
    private fun resolveProperty(target: CqlType?, property: kotlin.String): CqlType? {
        if (target == null) {
            return null
        }

        return when (target) {
            is Quantity -> {
                when (property) {
                    "value" -> target.value?.toCqlDecimal()
                    "unit" -> target.unit?.toCqlString()
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
                    "code" -> target.code?.toCqlString()
                    "display" -> target.display?.toCqlString()
                    "system" -> target.system?.toCqlString()
                    "version" -> target.version?.toCqlString()
                    else -> null
                }
            }
            is Concept -> {
                when (property) {
                    "display" -> target.display?.toCqlString()
                    "codes" -> target.codes?.toCqlList()
                    else -> null
                }
            }
            is CodeSystem -> {
                when (property) {
                    "id" -> target.id?.toCqlString()
                    "version" -> target.version?.toCqlString()
                    "name" -> target.name?.toCqlString()
                    else -> null
                }
            }
            is ValueSet -> {
                when (property) {
                    "id" -> target.id?.toCqlString()
                    "version" -> target.version?.toCqlString()
                    "name" -> target.name?.toCqlString()
                    "codesystems" -> target.codeSystems.toCqlList()
                    else -> null
                }
            }
            is Interval -> {
                when (property) {
                    "low" -> target.low
                    "lowClosed" -> target.lowClosed.toCqlBoolean()
                    "high" -> target.high
                    "highClosed" -> target.highClosed.toCqlBoolean()
                    else -> null
                }
            }
            is Tuple -> target.getElement(property)
            is ClassInstance -> target.elements[property]
            else -> throw IllegalArgumentException("Could not resolve path '$property' on $target.")
        }
    }
}
