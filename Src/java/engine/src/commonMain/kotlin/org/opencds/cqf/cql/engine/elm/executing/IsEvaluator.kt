package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.hl7.elm.r1.ChoiceTypeSpecifier
import org.hl7.elm.r1.IntervalTypeSpecifier
import org.hl7.elm.r1.Is
import org.hl7.elm.r1.ListTypeSpecifier
import org.hl7.elm.r1.NamedTypeSpecifier
import org.hl7.elm.r1.TupleTypeSpecifier
import org.hl7.elm.r1.TypeSpecifier
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.Boolean
import org.opencds.cqf.cql.engine.runtime.CqlType
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.List
import org.opencds.cqf.cql.engine.runtime.Tuple
import org.opencds.cqf.cql.engine.runtime.anyTypeName
import org.opencds.cqf.cql.engine.runtime.getNamedTypeForCqlValue

/*
is<T>(argument Any) Boolean

The is operator allows the type of a result to be tested.
If the run-time type of the argument is of the type being tested, the result of the operator is true;
  otherwise, the result is false.
*/
object IsEvaluator {

    @JvmStatic
    fun internalEvaluate(`is`: Is?, operand: CqlType?, state: State?): CqlType? {
        val type = `is`?.isTypeSpecifier ?: NamedTypeSpecifier().withName(`is`?.isType)

        return `is`(operand, type, state)?.let { Boolean(it) }
    }

    /**
     * Returns true if the value is of the specified type. Returns null if type relationship cannot
     * be determined. This is not the same as type compatibility (see
     * [FunctionRefEvaluator.isCompatible]).
     */
    fun `is`(value: CqlType?, type: TypeSpecifier, state: State?): kotlin.Boolean? {
        // System.Any is a supertype of all types
        if (type is NamedTypeSpecifier && type.name == anyTypeName) {
            return true
        }

        if (value == null) {
            // `null is X` is true if X is System.Any (handled above) and false otherwise
            return false
        }

        when (type) {
            is NamedTypeSpecifier -> {
                val valueNamedType = getNamedTypeForCqlValue(value)

                if (valueNamedType == null) {
                    // value is not an instance of a named type
                    return false
                }

                if (valueNamedType == type.name) {
                    // Types are the same
                    return true
                }

                val provider =
                    state!!
                        .environment
                        .resolveDataProviderByModelUriOrNull(valueNamedType.getNamespaceURI())

                if (provider == null) {
                    // Cannot determine relationship between types
                    return null
                }

                return provider.`is`(valueNamedType.getLocalPart(), type.name!!)
            }
            is ListTypeSpecifier -> {
                if (value is List) {
                    if (value.any()) {
                        for (item in value) {
                            val result = `is`(item, type.elementType!!, state)
                            if (result == null) {
                                // Found an element for which we cannot determine type relationship
                                return null
                            }
                            if (result == false) {
                                // Found an element that is not of the correct type
                                return false
                            }
                        }
                        return true
                    }

                    // An empty list has type List<System.Any>. Return true if and only if the
                    // element type of the type specifier is System.Any.
                    return type.elementType == NamedTypeSpecifier().withName(anyTypeName)
                }

                // Must be an Iterable to be of a list type
                return false
            }
            is IntervalTypeSpecifier -> {
                if (value is Interval) {
                    val lowResult = `is`(value.low, type.pointType!!, state)
                    if (lowResult == false) {
                        return false
                    }

                    val highResult = `is`(value.high, type.pointType!!, state)
                    if (highResult == false) {
                        return false
                    }

                    if (lowResult == true || highResult == true) {
                        return true
                    }

                    return null
                }

                // Must be an interval to be of an interval type
                return false
            }
            is TupleTypeSpecifier -> {
                if (
                    value is Tuple && value.elements.keys == type.element.map { it.name!! }.toSet()
                ) {
                    for (elementDefinition in type.element) {
                        val elementValue = value.elements[elementDefinition.name!!]
                        val result = `is`(elementValue, elementDefinition.elementType!!, state)
                        if (result == null) {
                            // Found an element for which we cannot determine type relationship
                            return null
                        }
                        if (result == false) {
                            // Found an element that is not of the correct type
                            return false
                        }
                    }
                    return true
                }

                // Must be a tuple with the matching element names to be of the specified tuple type
                return false
            }
            is ChoiceTypeSpecifier -> {
                var foundNull = false
                for (choice in type.choice) {
                    val result = `is`(value, choice, state)
                    if (result == null) {
                        foundNull = true
                    }
                    if (result == true) {
                        // Found a match
                        return true
                    }
                }
                return if (foundNull) {
                    null
                } else {
                    false
                }
            }
            else -> {
                throw IllegalArgumentException("Unexpected type specifier: $type.")
            }
        }
    }
}
