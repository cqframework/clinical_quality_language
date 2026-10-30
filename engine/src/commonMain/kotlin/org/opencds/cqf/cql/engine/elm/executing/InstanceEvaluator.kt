package org.opencds.cqf.cql.engine.elm.executing

import org.cqframework.cql.elm.visiting.ElmLibraryVisitor
import org.hl7.elm.r1.Instance
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.ClassInstance
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.runtime.CodeSystem
import org.opencds.cqf.cql.engine.runtime.Concept
import org.opencds.cqf.cql.engine.runtime.Decimal
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.List
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.runtime.Ratio
import org.opencds.cqf.cql.engine.runtime.String
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.ValueSet

object InstanceEvaluator {
    fun internalEvaluate(
        instance: Instance?,
        state: State?,
        visitor: ElmLibraryVisitor<Value?, State?>,
    ): Value? {
        var `object` = state!!.environment.createInstance(instance!!.classType!!)
        for (element in instance.element) {
            val value = visitor.visitExpression(element.value!!, state)
            `object` = withValue(`object`, element.name!!, value)
        }

        return `object`
    }

    fun withValue(target: Value?, path: kotlin.String, value: Value?): Value? {
        return when (target) {
            null -> null
            is Quantity -> {
                when (path) {
                    "value" -> target.withValue((value as Decimal?)?.value)
                    "unit" -> target.withUnit((value as String?)?.value)
                    else -> throw IllegalArgumentException("Could not set $path on Quantity.")
                }
            }
            is Ratio -> {
                when (path) {
                    "numerator" -> target.withNumerator(value as Quantity?)
                    "denominator" -> target.withDenominator(value as Quantity?)
                    else -> throw IllegalArgumentException("Could not set $path on Ratio.")
                }
            }
            is Code -> {
                when (path) {
                    "code" -> target.withCode((value as String?)?.value)
                    "display" -> target.withDisplay((value as String?)?.value)
                    "system" -> target.withSystem((value as String?)?.value)
                    "version" -> target.withVersion((value as String?)?.value)
                    else -> throw IllegalArgumentException("Could not set $path on Code.")
                }
            }
            is Concept -> {
                when (path) {
                    "display" -> target.withDisplay((value as String?)?.value)
                    "codes" -> target.withCodes((value as List?)?.value?.map { it as Code? })
                    else -> throw IllegalArgumentException("Could not set $path on Concept.")
                }
            }
            is CodeSystem -> {
                when (path) {
                    "id" -> target.withId((value as String?)?.value)
                    "version" -> target.withVersion((value as String?)?.value)
                    "name" -> target.withName((value as String?)?.value)
                    else -> throw IllegalArgumentException("Could not set $path on CodeSystem.")
                }
            }
            is ValueSet -> {
                when (path) {
                    "id" -> target.withId((value as String?)?.value)
                    "version" -> target.withVersion((value as String?)?.value)
                    "name" -> target.withName((value as String?)?.value)
                    "codesystems" ->
                        target.withCodeSystems(
                            (value as List?)?.value?.filterIsInstance<CodeSystem>()
                        )
                    else -> throw IllegalArgumentException("Could not set $path on ValueSet.")
                }
            }
            is Interval -> {
                when (path) {
                    "low" -> target.withLow(value)
                    "high" -> target.withHigh(value)
                    else -> throw IllegalArgumentException("Could not set $path on $target.")
                }
            }
            is ClassInstance -> ClassInstance(target.type, target.elements + (path to value))
            else -> throw IllegalArgumentException("Could not set $path on $target.")
        }
    }
}
