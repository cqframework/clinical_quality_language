@file:Suppress("WildcardImport")

package org.cqframework.cql.cql2elm

import java.util.*
import kotlin.collections.ArrayList
import org.cqframework.cql.cql2elm.model.Invocation
import org.cqframework.cql.cql2elm.model.invocation.*
import org.cqframework.cql.cql2elm.model.invocation.DateInvocation.Companion.setDateFieldsFromOperands
import org.cqframework.cql.cql2elm.model.invocation.DateTimeInvocation.Companion.setDateTimeFieldsFromOperands
import org.cqframework.cql.cql2elm.model.invocation.TimeInvocation.Companion.setTimeFieldsFromOperands
import org.cqframework.cql.cql2elm.tracking.Trackable.resultType
import org.cqframework.cql.cql2elm.tracking.Trackable.trackbacks
import org.cqframework.cql.elm.IdObjectFactory
import org.hl7.elm.r1.*

@Suppress("LargeClass", "TooManyFunctions")
class SystemFunctionResolver(private val builder: LibraryBuilder, of: IdObjectFactory?) {
    private val of = builder.objectFactory

    @Suppress("LongMethod", "CyclomaticComplexMethod", "NestedBlockDepth", "ReturnCount")
    fun resolveSystemFunction(functionRef: FunctionRef): Invocation? {
        if (functionRef.libraryName == null || "System" == functionRef.libraryName) {
            when (functionRef.name) {
                "AllTrue",
                "AnyTrue",
                "Avg",
                "Count",
                "GeometricMean",
                "Max",
                "Median",
                "Min",
                "Mode",
                "PopulationStdDev",
                "PopulationVariance",
                "Product",
                "StdDev",
                "Sum",
                "Variance" -> {
                    return resolveAggregate(functionRef)
                }
                "Abs",
                "Ceiling",
                "Exp",
                "Floor",
                "Ln",
                "Negate",
                "Precision",
                "Predecessor",
                "Successor",
                "Truncate" -> {
                    return resolveUnary(functionRef)
                }
                "HighBoundary",
                "Log",
                "LowBoundary",
                "Modulo",
                "Power",
                "TruncatedDivide" -> {
                    return resolveBinary(functionRef)
                }
                "Round" -> {
                    return resolveRound(functionRef)
                }
                "AgeInYears",
                "AgeInMonths" -> {
                    checkNumberOfOperands(functionRef, 0)
                    return resolveCalculateAge(
                        builder.enforceCompatible(
                            patientBirthDateProperty,
                            builder.resolveTypeName("System", "Date")
                        ),
                        resolveAgeRelatedFunctionPrecision(functionRef)
                    )
                }
                "AgeInWeeks",
                "AgeInDays",
                "AgeInHours",
                "AgeInMinutes",
                "AgeInSeconds",
                "AgeInMilliseconds" -> {
                    checkNumberOfOperands(functionRef, 0)
                    return resolveCalculateAge(
                        builder.ensureCompatible(
                            patientBirthDateProperty,
                            builder.resolveTypeName("System", "DateTime")
                        ),
                        resolveAgeRelatedFunctionPrecision(functionRef)
                    )
                }
                "AgeInYearsAt",
                "AgeInMonthsAt",
                "AgeInWeeksAt",
                "AgeInDaysAt" -> {
                    checkNumberOfOperands(functionRef, 1)
                    val ops: MutableList<Expression?> = ArrayList()
                    var op = functionRef.operand[0]
                    // If the op is not a Date or DateTime, attempt to get it to convert it to a
                    // Date or DateTime
                    // If the op can be converted to both a Date and a DateTime, throw an ambiguous
                    // error
                    if (
                        !(op.resultType!!.isSubTypeOf(
                            builder.resolveTypeName("System", "Date")!!
                        ) ||
                            op.resultType!!.isSubTypeOf(
                                builder.resolveTypeName("System", "DateTime")!!
                            ))
                    ) {
                        val dateConversion =
                            builder.findConversion(
                                op.resultType!!,
                                builder.resolveTypeName("System", "Date")!!,
                                true,
                                false
                            )
                        val dateTimeConversion =
                            builder.findConversion(
                                op.resultType!!,
                                builder.resolveTypeName("System", "DateTime")!!,
                                true,
                                false
                            )
                        op =
                            when {
                                dateConversion != null && dateTimeConversion != null -> {
                                    require(dateConversion.score != dateTimeConversion.score) {
                                        "Ambiguous implicit conversion from %s to %s or %s."
                                            .format(
                                                Locale.US,
                                                op.resultType.toString(),
                                                dateConversion.toType.toString(),
                                                dateTimeConversion.toType.toString()
                                            )
                                    }

                                    if (dateConversion.score < dateTimeConversion.score) {
                                        builder.convertExpression(op, dateConversion)
                                    } else {
                                        builder.convertExpression(op, dateTimeConversion)
                                    }
                                }
                                dateConversion != null ->
                                    builder.convertExpression(op, dateConversion)
                                dateTimeConversion != null ->
                                    builder.convertExpression(op, dateTimeConversion)
                                else -> {
                                    // ERROR
                                    throw IllegalArgumentException(
                                        String.format(
                                            Locale.US,
                                            "Could not resolve call to operator %s with argument of type %s.",
                                            functionRef.name,
                                            op.resultType.toString()
                                        )
                                    )
                                }
                            }
                    }
                    ops.add(builder.enforceCompatible(patientBirthDateProperty, op.resultType))
                    ops.add(op)
                    return resolveCalculateAgeAt(
                        ops,
                        resolveAgeRelatedFunctionPrecision(functionRef)
                    )
                }
                "AgeInHoursAt",
                "AgeInMinutesAt",
                "AgeInSecondsAt",
                "AgeInMillisecondsAt" -> {
                    val ops: MutableList<Expression?> = ArrayList()
                    ops.add(patientBirthDateProperty)
                    ops.addAll(functionRef.operand)
                    return resolveCalculateAgeAt(
                        ops,
                        resolveAgeRelatedFunctionPrecision(functionRef)
                    )
                }
                "CalculateAgeInYears",
                "CalculateAgeInMonths",
                "CalculateAgeInWeeks",
                "CalculateAgeInDays",
                "CalculateAgeInHours",
                "CalculateAgeInMinutes",
                "CalculateAgeInSeconds",
                "CalculateAgeInMilliseconds" -> {
                    checkNumberOfOperands(functionRef, 1)
                    return resolveCalculateAge(
                        functionRef.operand[0],
                        resolveAgeRelatedFunctionPrecision(functionRef)
                    )
                }
                "CalculateAgeInYearsAt",
                "CalculateAgeInMonthsAt",
                "CalculateAgeInWeeksAt",
                "CalculateAgeInDaysAt",
                "CalculateAgeInHoursAt",
                "CalculateAgeInMinutesAt",
                "CalculateAgeInSecondsAt",
                "CalculateAgeInMillisecondsAt" -> {
                    return resolveCalculateAgeAt(
                        functionRef.operand,
                        resolveAgeRelatedFunctionPrecision(functionRef)
                    )
                }
                "DateTime" -> {
                    return resolveDateTime(functionRef)
                }
                "Date" -> {
                    return resolveDate(functionRef)
                }
                "Time" -> {
                    return resolveTime(functionRef)
                }
                "Now",
                "now" -> {
                    return resolveNow(functionRef)
                }
                "Today",
                "today" -> {
                    return resolveToday(functionRef)
                }
                "TimeOfDay",
                "timeOfDay" -> {
                    return resolveTimeOfDay(functionRef)
                }
                "IndexOf" -> {
                    return resolveIndexOf(functionRef)
                }
                "First" -> {
                    return resolveFirst(functionRef)
                }
                "Last" -> {
                    return resolveLast(functionRef)
                }
                "Skip" -> {
                    return resolveSkip(functionRef)
                }
                "Take" -> {
                    return resolveTake(functionRef)
                }
                "Tail" -> {
                    return resolveTail(functionRef)
                }
                "Contains",
                "Expand",
                "In",
                "Includes",
                "IncludedIn",
                "ProperIncludes",
                "ProperIncludedIn" -> {
                    return resolveBinary(functionRef)
                }
                "Distinct",
                "Exists",
                "Flatten",
                "Collapse",
                "SingletonFrom",
                "ExpandValueSet" -> {
                    return resolveUnary(functionRef)
                }
                "Coalesce",
                "Intersect",
                "Union",
                "Except" -> {
                    return resolveNary(functionRef)
                }
                "IsNull",
                "IsTrue",
                "IsFalse" -> {
                    return resolveUnary(functionRef)
                }
                "Length",
                "Width",
                "Size" -> {
                    return resolveUnary(functionRef)
                }
                "Indexer",
                "StartsWith",
                "EndsWith",
                "Matches" -> {
                    return resolveBinary(functionRef)
                }
                "ReplaceMatches" -> {
                    return resolveTernary(functionRef)
                }
                "Concatenate" -> {
                    return resolveNary(functionRef)
                }
                "Combine" -> {
                    return resolveCombine(functionRef)
                }
                "Split" -> {
                    return resolveSplit(functionRef)
                }
                "SplitOnMatches" -> {
                    return resolveSplitOnMatches(functionRef)
                }
                "Upper",
                "Lower" -> {
                    return resolveUnary(functionRef)
                }
                "PositionOf" -> {
                    return resolvePositionOf(functionRef)
                }
                "LastPositionOf" -> {
                    return resolveLastPositionOf(functionRef)
                }
                "Substring" -> {
                    return resolveSubstring(functionRef)
                }
                "Not" -> {
                    return resolveUnary(functionRef)
                }
                "And",
                "Or",
                "Xor",
                "Implies" -> {
                    return resolveBinary(functionRef)
                }
                "ConvertsToString",
                "ConvertsToBoolean",
                "ConvertsToInteger",
                "ConvertsToLong",
                "ConvertsToDecimal",
                "ConvertsToDateTime",
                "ConvertsToDate",
                "ConvertsToTime",
                "ConvertsToQuantity",
                "ConvertsToRatio",
                "ToString",
                "ToBoolean",
                "ToInteger",
                "ToLong",
                "ToDecimal",
                "ToDateTime",
                "ToDate",
                "ToTime",
                "ToQuantity",
                "ToRatio",
                "ToConcept",
                "ToChars" -> {
                    return resolveUnary(functionRef)
                }
                "CanConvertQuantity",
                "ConvertQuantity" -> {
                    return resolveBinary(functionRef)
                }
                "Equal",
                "NotEqual",
                "Greater",
                "GreaterOrEqual",
                "Less",
                "LessOrEqual",
                "Equivalent" -> {
                    return resolveBinary(functionRef)
                }
                "Message" -> return resolveMessage(functionRef)
            }
        }
        return null
    }

    // Age-Related Function Support
    private fun resolveCalculateAge(
        e: Expression?,
        p: DateTimePrecision
    ): UnaryExpressionInvocation<CalculateAge> {
        val operator = of.createCalculateAge().withPrecision(p).withOperand(e)
        val invocation = UnaryExpressionInvocation(operator)
        builder.resolveInvocation("System", "CalculateAge", invocation)
        return invocation
    }

    private fun resolveCalculateAgeAt(
        e: List<Expression?>,
        p: DateTimePrecision
    ): BinaryExpressionInvocation<CalculateAgeAt> {
        val operator = of.createCalculateAgeAt().withPrecision(p).withOperand(e)
        val invocation = BinaryExpressionInvocation(operator)
        builder.resolveInvocation("System", "CalculateAgeAt", invocation)
        return invocation
    }

    private val patientBirthDateProperty: Expression?
        get() {
            val source = builder.resolveIdentifier("Patient", true)!!
            val birthDateProperty = builder.defaultModel!!.modelInfo.patientBirthDatePropertyName
            // If the property has a qualifier, resolve it as a path (without model mapping)
            return if (birthDateProperty.indexOf('.') >= 1) {
                val property = of.createProperty().withSource(source).withPath(birthDateProperty)
                property.resultType = builder.resolvePath(source.resultType, property.path)
                property
            } else {
                val resolution = builder.resolveProperty(source.resultType, birthDateProperty)
                var result: Expression? =
                    builder.buildProperty(
                        source,
                        resolution!!.name,
                        resolution.isSearch,
                        resolution.type
                    )
                result = builder.applyTargetMap(result, resolution.targetMap)
                result
            }
        }

    // Arithmetic Function Support
    private fun resolveRound(functionRef: FunctionRef): RoundInvocation {
        require(!(functionRef.operand.isEmpty() || functionRef.operand.size > 2)) {
            "Could not resolve call to system operator Round.  Expected 1 or 2 arguments."
        }
        val round = of.createRound().withOperand(functionRef.operand[0])
        if (functionRef.operand.size == 2) {
            round.precision = functionRef.operand[1]
        }
        val invocation = RoundInvocation(round)
        builder.resolveInvocation("System", "Round", RoundInvocation(round))
        return invocation
    }

    // DateTime Function Support
    private fun resolveDateTime(functionRef: FunctionRef): DateTimeInvocation {
        val dt = of.createDateTime()
        setDateTimeFieldsFromOperands(dt, functionRef.operand)
        val invocation = DateTimeInvocation(dt)
        builder.resolveInvocation("System", "DateTime", invocation)
        return invocation
    }

    private fun resolveDate(functionRef: FunctionRef): DateInvocation {
        val d = of.createDate()
        setDateFieldsFromOperands(d, functionRef.operand)
        val invocation = DateInvocation(d)
        builder.resolveInvocation("System", "Date", invocation)
        return invocation
    }

    private fun resolveTime(functionRef: FunctionRef): TimeInvocation {
        val t = of.createTime()
        setTimeFieldsFromOperands(t, functionRef.operand)
        val invocation = TimeInvocation(t)
        builder.resolveInvocation("System", "Time", invocation)
        return invocation
    }

    private fun resolveNow(functionRef: FunctionRef): ZeroOperandExpressionInvocation {
        checkNumberOfOperands(functionRef, 0)
        val now = of.createNow()
        val invocation = ZeroOperandExpressionInvocation(now)
        builder.resolveInvocation("System", "Now", invocation)
        return invocation
    }

    private fun resolveToday(functionRef: FunctionRef): ZeroOperandExpressionInvocation {
        checkNumberOfOperands(functionRef, 0)
        val today = of.createToday()
        val invocation = ZeroOperandExpressionInvocation(today)
        builder.resolveInvocation("System", "Today", invocation)
        return invocation
    }

    private fun resolveTimeOfDay(functionRef: FunctionRef): ZeroOperandExpressionInvocation {
        checkNumberOfOperands(functionRef, 0)
        val timeOfDay = of.createTimeOfDay()
        val invocation = ZeroOperandExpressionInvocation(timeOfDay)
        builder.resolveInvocation("System", "TimeOfDay", invocation)
        return invocation
    }

    // List Function Support
    private fun resolveIndexOf(functionRef: FunctionRef): IndexOfInvocation {
        checkNumberOfOperands(functionRef, 2)
        val indexOf = of.createIndexOf()
        indexOf.source = functionRef.operand[0]
        indexOf.element = functionRef.operand[1]
        val invocation = IndexOfInvocation(indexOf)
        builder.resolveInvocation("System", "IndexOf", invocation)
        return invocation
    }

    private fun resolveFirst(functionRef: FunctionRef): FirstInvocation {
        checkNumberOfOperands(functionRef, 1)
        val first = of.createFirst()
        first.source = functionRef.operand[0]
        val invocation = FirstInvocation(first)
        builder.resolveInvocation("System", "First", invocation)
        return invocation
    }

    private fun resolveLast(functionRef: FunctionRef): LastInvocation {
        checkNumberOfOperands(functionRef, 1)
        val last = of.createLast()
        last.source = functionRef.operand[0]
        val invocation = LastInvocation(last)
        builder.resolveInvocation("System", "Last", invocation)
        return invocation
    }

    private fun resolveSkip(functionRef: FunctionRef): SkipInvocation {
        checkNumberOfOperands(functionRef, 2)
        val slice = of.createSlice()
        slice.source = functionRef.operand[0]
        slice.startIndex = functionRef.operand[1]
        slice.endIndex = builder.buildNull(functionRef.operand[1].resultType)
        val invocation = SkipInvocation(slice)
        builder.resolveInvocation("System", "Skip", invocation)
        return invocation
    }

    private fun resolveTake(functionRef: FunctionRef): TakeInvocation {
        checkNumberOfOperands(functionRef, 2)
        val slice = of.createSlice()
        slice.source = functionRef.operand[0]
        slice.startIndex = builder.createLiteral(0)
        val coalesce =
            of.createCoalesce().withOperand(functionRef.operand[1], builder.createLiteral(0))
        val naryInvocation = NaryExpressionInvocation(coalesce)
        builder.resolveInvocation("System", "Coalesce", naryInvocation)
        slice.endIndex = coalesce
        val invocation = TakeInvocation(slice)
        builder.resolveInvocation("System", "Take", invocation)
        return invocation
    }

    private fun resolveTail(functionRef: FunctionRef): TailInvocation {
        checkNumberOfOperands(functionRef, 1)
        val slice = of.createSlice()
        slice.source = functionRef.operand[0]
        slice.startIndex = builder.createLiteral(1)
        slice.endIndex = builder.buildNull(builder.resolveTypeName("System", "Integer"))
        val invocation = TailInvocation(slice)
        builder.resolveInvocation("System", "Tail", invocation)
        return invocation
    }

    // String Function Support
    private fun resolveCombine(functionRef: FunctionRef): CombineInvocation {
        require(!(functionRef.operand.isEmpty() || functionRef.operand.size > 2)) {
            "Could not resolve call to system operator Combine.  Expected 1 or 2 arguments."
        }
        val combine = of.createCombine().withSource(functionRef.operand[0])
        if (functionRef.operand.size == 2) {
            combine.separator = functionRef.operand[1]
        }
        val invocation = CombineInvocation(combine)
        builder.resolveInvocation("System", "Combine", invocation)
        return invocation
    }

    private fun resolveSplit(functionRef: FunctionRef): SplitInvocation {
        checkNumberOfOperands(functionRef, 2)
        val split =
            of.createSplit()
                .withStringToSplit(functionRef.operand[0])
                .withSeparator(functionRef.operand[1])
        val invocation = SplitInvocation(split)
        builder.resolveInvocation("System", "Split", invocation)
        return invocation
    }

    private fun resolveSplitOnMatches(functionRef: FunctionRef): SplitOnMatchesInvocation {
        checkNumberOfOperands(functionRef, 2)
        val splitOnMatches =
            of.createSplitOnMatches()
                .withStringToSplit(functionRef.operand[0])
                .withSeparatorPattern(functionRef.operand[1])
        val invocation = SplitOnMatchesInvocation(splitOnMatches)
        builder.resolveInvocation("System", "SplitOnMatches", invocation)
        return invocation
    }

    private fun resolvePositionOf(functionRef: FunctionRef): PositionOfInvocation {
        checkNumberOfOperands(functionRef, 2)
        val pos =
            of.createPositionOf()
                .withPattern(functionRef.operand[0])
                .withString(functionRef.operand[1])
        val invocation = PositionOfInvocation(pos)
        builder.resolveInvocation("System", "PositionOf", invocation)
        return invocation
    }

    private fun resolveLastPositionOf(functionRef: FunctionRef): LastPositionOfInvocation {
        checkNumberOfOperands(functionRef, 2)
        val pos =
            of.createLastPositionOf()
                .withPattern(functionRef.operand[0])
                .withString(functionRef.operand[1])
        val invocation = LastPositionOfInvocation(pos)
        builder.resolveInvocation("System", "LastPositionOf", invocation)
        return invocation
    }

    private fun resolveSubstring(functionRef: FunctionRef): SubstringInvocation {
        @Suppress("MagicNumber")
        require(!(functionRef.operand.size < 2 || functionRef.operand.size > 3)) {
            "Could not resolve call to system operator Substring.  Expected 2 or 3 arguments."
        }
        val substring =
            of.createSubstring()
                .withStringToSub(functionRef.operand[0])
                .withStartIndex(functionRef.operand[1])
        @Suppress("MagicNumber")
        if (functionRef.operand.size == 3) {
            substring.length = functionRef.operand[2]
        }
        val invocation = SubstringInvocation(substring)
        builder.resolveInvocation("System", "Substring", invocation)
        return invocation
    }

    // Error Functions
    private fun resolveMessage(functionRef: FunctionRef): MessageInvocation {
        @Suppress("MagicNumber")
        require(functionRef.operand.size == 5) {
            "Could not resolve call to system operator Message. Expected 5 arguments."
        }
        @Suppress("MagicNumber")
        val message =
            of.createMessage()
                .withSource(functionRef.operand[0])
                .withCondition(functionRef.operand[1])
                .withCode(functionRef.operand[2])
                .withSeverity(functionRef.operand[3])
                .withMessage(functionRef.operand[4])
        val invocation = MessageInvocation(message)
        builder.resolveInvocation("System", "Message", invocation)
        return invocation
    }

    // Type Functions
    @Suppress("UnusedPrivateMember")
    private fun resolveConvert(functionRef: FunctionRef): ConvertInvocation {
        checkNumberOfOperands(functionRef, 1)
        val convert = of.createConvert().withOperand(functionRef.operand[0])
        val sm = builder.systemModel
        when (functionRef.name) {
            "ToString" -> convert.toType = builder.dataTypeToQName(sm.string)
            "ToBoolean" -> convert.toType = builder.dataTypeToQName(sm.boolean)
            "ToInteger" -> convert.toType = builder.dataTypeToQName(sm.integer)
            "ToLong" -> convert.toType = builder.dataTypeToQName(sm.long)
            "ToDecimal" -> convert.toType = builder.dataTypeToQName(sm.decimal)
            "ToQuantity" -> convert.toType = builder.dataTypeToQName(sm.quantity)
            "ToRatio" -> convert.toType = builder.dataTypeToQName(sm.ratio)
            "ToDate" -> convert.toType = builder.dataTypeToQName(sm.date)
            "ToDateTime" -> convert.toType = builder.dataTypeToQName(sm.dateTime)
            "ToTime" -> convert.toType = builder.dataTypeToQName(sm.time)
            "ToConcept" -> convert.toType = builder.dataTypeToQName(sm.concept)
            else ->
                throw IllegalArgumentException(
                    String.format(
                        Locale.US,
                        "Could not resolve call to system operator %s. Unknown conversion type.",
                        functionRef.name
                    )
                )
        }
        val invocation = ConvertInvocation(convert)
        builder.resolveInvocation("System", functionRef.name, invocation)
        return invocation
    }

    // General Function Support
    private inline fun <reified T : Expression?> createExpression(functionRef: FunctionRef): T {
        return try {
            T::class.java.cast(of.javaClass.getMethod("create" + functionRef.name).invoke(of))
        } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
            throw CqlInternalException(
                String.format(
                    Locale.US,
                    "Could not create instance of Element \"%s\"",
                    functionRef.name
                ),
                if (functionRef.trackbacks.isNotEmpty()) functionRef.trackbacks[0] else null,
                e
            )
        }
    }

    private fun resolveUnary(functionRef: FunctionRef): UnaryExpressionInvocation<*> {
        val operator = createExpression<UnaryExpression>(functionRef)
        checkNumberOfOperands(functionRef, 1)
        operator.operand = functionRef.operand[0]
        val invocation = UnaryExpressionInvocation(operator)
        builder.resolveInvocation("System", functionRef.name, invocation)
        return invocation
    }

    private fun resolveBinary(functionRef: FunctionRef): BinaryExpressionInvocation<*> {
        val operator = createExpression<BinaryExpression>(functionRef)
        checkNumberOfOperands(functionRef, 2)
        operator.operand.addAll(functionRef.operand)
        val invocation = BinaryExpressionInvocation(operator)
        builder.resolveInvocation("System", functionRef.name, invocation)
        return invocation
    }

    private fun resolveTernary(functionRef: FunctionRef): TernaryExpressionInvocation<*> {
        val operator = createExpression<TernaryExpression>(functionRef)
        @Suppress("MagicNumber") checkNumberOfOperands(functionRef, 3)
        operator.operand.addAll(functionRef.operand)
        val invocation = TernaryExpressionInvocation(operator)
        builder.resolveInvocation("System", functionRef.name, invocation)
        return invocation
    }

    private fun resolveNary(functionRef: FunctionRef): NaryExpressionInvocation {
        val operator = createExpression<NaryExpression>(functionRef)
        operator.operand.addAll(functionRef.operand)
        val invocation = NaryExpressionInvocation(operator)
        builder.resolveInvocation("System", functionRef.name, invocation)
        return invocation
    }

    private fun resolveAggregate(functionRef: FunctionRef): AggregateExpressionInvocation<*> {
        val operator = createExpression<AggregateExpression>(functionRef)
        checkNumberOfOperands(functionRef, 1)
        operator.source = functionRef.operand[0]
        val invocation = AggregateExpressionInvocation(operator)
        builder.resolveInvocation("System", functionRef.name, invocation)
        return invocation
    }

    private fun checkNumberOfOperands(functionRef: FunctionRef, expectedOperands: Int) {
        require(functionRef.operand.size == expectedOperands) {
            String.format(
                Locale.US,
                "Could not resolve call to system operator %s.  Expected %d arguments.",
                functionRef.name,
                expectedOperands
            )
        }
    }

    companion object {
        private fun resolveAgeRelatedFunctionPrecision(
            functionRef: FunctionRef
        ): DateTimePrecision {
            val name = functionRef.name
            return when {
                name.contains("Years") -> DateTimePrecision.YEAR
                name.contains("Months") -> DateTimePrecision.MONTH
                name.contains("Weeks") -> DateTimePrecision.WEEK
                name.contains("Days") -> DateTimePrecision.DAY
                name.contains("Hours") -> DateTimePrecision.HOUR
                name.contains("Minutes") -> DateTimePrecision.MINUTE
                name.contains("Second") -> DateTimePrecision.SECOND
                name.contains("Milliseconds") -> DateTimePrecision.MILLISECOND
                else ->
                    throw IllegalArgumentException(
                        String.format(Locale.US, "Unknown precision '%s'.", name)
                    )
            }
        }
    }
}
