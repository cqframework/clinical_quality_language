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
        val `object` = state!!.environment.createInstance(instance!!.classType!!)
        for (element in instance.element) {
            val value = visitor.visitExpression(element.value!!, state)
            setValue(`object`, element.name!!, value)
        }

        return `object`
    }

    fun setValue(target: Value?, path: kotlin.String, value: Value?) {
        if (target == null) {
            return
        }

        when (target) {
            is Quantity -> {
                when (path) {
                    "value" -> target.value = (value as Decimal?)?.value
                    "unit" -> target.unit = (value as String?)?.value
                    else -> throw IllegalArgumentException("Could not set $path on Quantity.")
                }
            }
            is Ratio -> {
                when (path) {
                    "numerator" -> target.numerator = value as Quantity?
                    "denominator" -> target.denominator = value as Quantity?
                    else -> throw IllegalArgumentException("Could not set $path on Ratio.")
                }
            }
            is Code -> {
                when (path) {
                    "code" -> target.code = (value as String?)?.value
                    "display" -> target.display = (value as String?)?.value
                    "system" -> target.system = (value as String?)?.value
                    "version" -> target.version = (value as String?)?.value
                    else -> throw IllegalArgumentException("Could not set $path on Code.")
                }
            }
            is Concept -> {
                when (path) {
                    "display" -> target.display = (value as String?)?.value
                    "codes" ->
                        target.codes = (value as List?)?.value?.map { it as Code? }?.toMutableList()
                    else -> throw IllegalArgumentException("Could not set $path on Concept.")
                }
            }
            is CodeSystem -> {
                when (path) {
                    "id" -> target.id = (value as String?)?.value
                    "version" -> target.version = (value as String?)?.value
                    "name" -> target.name = (value as String?)?.value
                    else -> throw IllegalArgumentException("Could not set $path on CodeSystem.")
                }
            }
            is ValueSet -> {
                when (path) {
                    "id" -> target.id = (value as String?)?.value
                    "version" -> target.version = (value as String?)?.value
                    "name" -> target.name = (value as String?)?.value
                    "codesystems" ->
                        target.setCodeSystems((value as List?)?.value?.map { it as CodeSystem? })
                    else -> throw IllegalArgumentException("Could not set $path on ValueSet.")
                }
            }
            is Interval -> {
                when (path) {
                    "low" -> target.low = value
                    "high" -> target.high = value
                    else -> throw IllegalArgumentException("Could not set $path on $target.")
                }
            }
            is ClassInstance -> target.elements[path] = value
            else -> throw IllegalArgumentException("Could not set $path on $target.")
        }
    }
}
