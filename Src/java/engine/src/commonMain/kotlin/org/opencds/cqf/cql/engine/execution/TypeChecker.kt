package org.opencds.cqf.cql.engine.execution

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.collections.containsKey
import kotlin.collections.get
import org.cqframework.cql.shared.BigDecimal
import org.cqframework.cql.shared.QName
import org.hl7.elm.r1.ChoiceTypeSpecifier
import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.IntervalTypeSpecifier
import org.hl7.elm.r1.ListTypeSpecifier
import org.hl7.elm.r1.NamedTypeSpecifier
import org.hl7.elm.r1.TupleTypeSpecifier
import org.hl7.elm.r1.TypeSpecifier
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.runtime.CodeSystem
import org.opencds.cqf.cql.engine.runtime.Concept
import org.opencds.cqf.cql.engine.runtime.Date
import org.opencds.cqf.cql.engine.runtime.DateTime
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.runtime.Ratio
import org.opencds.cqf.cql.engine.runtime.Time
import org.opencds.cqf.cql.engine.runtime.Tuple
import org.opencds.cqf.cql.engine.runtime.ValueSet

/**
 * Implements runtime type checking and reports any type mismatches (type drift). The dynamic
 * runtime types are compared to compile-time static types (ELM result types) if present.
 */
@Suppress("LongMethod", "ReturnCount", "CyclomaticComplexMethod", "LoopWithTooManyJumpStatements")
object TypeChecker {

    private val logger = KotlinLogging.logger("TypeChecker")

    /** Represents the result of a type check. */
    enum class TypeCheckResult {
        /**
         * A possible type match is found. Also used when the declared ELM result type is
         * `System.Any` or not available, the runtime value is null, or the check is not
         * implemented.
         */
        MATCH,

        /** A definite type mismatch is found. */
        MISMATCH,
    }

    /** Checks the runtime value type against the expected Java type T. */
    private inline fun <reified T : Any> Any.checkType(
        logMismatch: Boolean,
        additionalCheck: (T) -> TypeCheckResult = { TypeCheckResult.MATCH },
    ): TypeCheckResult {
        if (this is T) {
            return additionalCheck(this)
        }
        if (logMismatch) {
            logger.warn {
                "Type mismatch: expected ${T::class.simpleName}, got ${this::class.simpleName}"
            }
        }
        return TypeCheckResult.MISMATCH
    }

    /**
     * Checks the value type against the expected type defined in the ELM expression. Logs any type
     * mismatches.
     */
    fun checkType(expressionWithExpectedResultType: Expression, actualValue: Any?) {
        actualValue?.let {
            expressionWithExpectedResultType.resultTypeSpecifier?.let { expectedResultTypeSpecifier
                ->
                checkType(expectedResultTypeSpecifier, actualValue, true)
            }
            expressionWithExpectedResultType.resultTypeName?.let { expectedResultTypeName ->
                checkType(expectedResultTypeName, actualValue, true)
            }
        }
    }

    fun checkType(
        expectedResultTypeName: QName,
        actualValue: Any,
        logMismatch: Boolean = false,
    ): TypeCheckResult {
        return when (expectedResultTypeName.getNamespaceURI()) {
            "urn:hl7-org:elm-types:r1" ->
                when (expectedResultTypeName.getLocalPart()) {
                    "Any" -> TypeCheckResult.MATCH
                    "Boolean" -> actualValue.checkType<Boolean>(logMismatch)
                    "Integer" -> actualValue.checkType<Int>(logMismatch)
                    "Long" -> actualValue.checkType<Long>(logMismatch)
                    "Decimal" -> actualValue.checkType<BigDecimal>(logMismatch)
                    "Quantity" -> actualValue.checkType<Quantity>(logMismatch)
                    "String" -> actualValue.checkType<String>(logMismatch)
                    "Date" -> actualValue.checkType<Date>(logMismatch)
                    "DateTime" -> actualValue.checkType<DateTime>(logMismatch)
                    "Time" -> actualValue.checkType<Time>(logMismatch)
                    "Ratio" -> actualValue.checkType<Ratio>(logMismatch)
                    "Concept" -> actualValue.checkType<Concept>(logMismatch)
                    "Code" -> actualValue.checkType<Code>(logMismatch)
                    "CodeSystem" -> actualValue.checkType<CodeSystem>(logMismatch)
                    "ValueSet" -> actualValue.checkType<ValueSet>(logMismatch)
                    else -> TypeCheckResult.MATCH
                }
            "http://hl7.org/fhir" -> TypeCheckResult.MATCH
            else -> TypeCheckResult.MATCH
        }
    }

    fun checkType(
        expectedTypeSpecifier: TypeSpecifier,
        actualValue: Any,
        logMismatch: Boolean = false,
    ): TypeCheckResult {
        when (expectedTypeSpecifier) {
            is NamedTypeSpecifier -> {
                val expectedTypeName = expectedTypeSpecifier.name
                if (expectedTypeName == null) {
                    return TypeCheckResult.MATCH
                }
                return checkType(expectedTypeName, actualValue, logMismatch)
            }
            is ListTypeSpecifier -> {
                return actualValue.checkType<Iterable<*>>(logMismatch) iterableCheck@{
                    actualIterable ->
                    val expectedListElementType = expectedTypeSpecifier.elementType
                    if (expectedListElementType == null) {
                        return@iterableCheck TypeCheckResult.MATCH
                    }
                    for (actualListElement in actualIterable) {
                        if (actualListElement != null) {
                            if (
                                checkType(
                                    expectedListElementType,
                                    actualListElement,
                                    logMismatch,
                                ) == TypeCheckResult.MISMATCH
                            ) {
                                return@iterableCheck TypeCheckResult.MISMATCH
                            }
                        }
                    }
                    return@iterableCheck TypeCheckResult.MATCH
                }
            }
            is IntervalTypeSpecifier -> {
                return actualValue.checkType<Interval>(logMismatch) intervalCheck@{ actualInterval
                    ->
                    val expectedPointType = expectedTypeSpecifier.pointType
                    if (expectedPointType == null) {
                        return@intervalCheck TypeCheckResult.MATCH
                    }
                    actualInterval.start?.let { actualStart ->
                        if (
                            checkType(expectedPointType, actualStart, logMismatch) ==
                                TypeCheckResult.MISMATCH
                        ) {
                            return@intervalCheck TypeCheckResult.MISMATCH
                        }
                    }
                    actualInterval.end?.let { actualEnd ->
                        if (
                            checkType(expectedPointType, actualEnd, logMismatch) ==
                                TypeCheckResult.MISMATCH
                        ) {
                            return@intervalCheck TypeCheckResult.MISMATCH
                        }
                    }
                    return@intervalCheck TypeCheckResult.MATCH
                }
            }
            is TupleTypeSpecifier -> {
                return actualValue.checkType<Tuple>(logMismatch) tupleCheck@{ actualTuple ->
                    for (elementDefinition in expectedTypeSpecifier.element) {
                        if (!actualTuple.elements.containsKey(elementDefinition.name)) {
                            if (logMismatch) {
                                logger.warn {
                                    "Type mismatch: Tuple is missing expected element: ${elementDefinition.name}"
                                }
                            }
                            return@tupleCheck TypeCheckResult.MISMATCH
                        }
                        val actualElementValue = actualTuple.elements[elementDefinition.name]
                        if (actualElementValue == null) {
                            continue
                        }
                        val expectedElementType = elementDefinition.elementType
                        if (expectedElementType == null) {
                            continue
                        }
                        if (
                            checkType(expectedElementType, actualElementValue, logMismatch) ==
                                TypeCheckResult.MISMATCH
                        ) {
                            return@tupleCheck TypeCheckResult.MISMATCH
                        }
                    }
                    return@tupleCheck TypeCheckResult.MATCH
                }
            }
            is ChoiceTypeSpecifier -> {
                if (
                    expectedTypeSpecifier.choice.any { candidateType ->
                        checkType(candidateType, actualValue, false) == TypeCheckResult.MATCH
                    }
                ) {
                    return TypeCheckResult.MATCH
                }
                if (logMismatch) {
                    logger.warn {
                        "Type mismatch: Value does not match any type in choice type specifier."
                    }
                }
                return TypeCheckResult.MISMATCH
            }
            else -> {
                return TypeCheckResult.MATCH
            }
        }
    }
}
