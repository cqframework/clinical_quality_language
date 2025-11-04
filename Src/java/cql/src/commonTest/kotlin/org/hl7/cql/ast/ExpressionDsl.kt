package org.hl7.cql.ast

import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.cqframework.cql.shared.BigDecimal

typealias ExpressionSpec = ExpressionBuilder.() -> Expression

fun buildExpressionSpec(block: ExpressionSpec): Expression = ExpressionBuilder().block()

class ExpressionBuilder {
    private fun expression(block: ExpressionSpec): Expression = ExpressionBuilder().block()

    fun long(value: Long): Expression = LiteralExpression(LongLiteral(value = value))

    fun int(value: Int): Expression = LiteralExpression(IntLiteral(value = value))

    fun decimal(value: Double): Expression =
        LiteralExpression(DecimalLiteral(value = BigDecimal(value)))

    fun string(value: String): Expression = LiteralExpression(StringLiteral(value))

    fun boolean(value: Boolean): Expression = LiteralExpression(BooleanLiteral(value))

    fun nullValue(): Expression = LiteralExpression(NullLiteral())

    fun dateTime(text: String): Expression = LiteralExpression(DateTimeLiteral(text))

    fun time(text: String): Expression = LiteralExpression(TimeLiteral(text))

    fun quantity(value: String, unit: String? = null): Expression =
        LiteralExpression(QuantityLiteral(value, unit))

    fun quantityLiteral(value: String, unit: String? = null): QuantityLiteral =
        QuantityLiteral(value, unit)

    fun ratio(numerator: QuantityLiteral, denominator: QuantityLiteral): Expression =
        LiteralExpression(RatioLiteral(numerator, denominator))

    fun terminology(identifier: String, library: String? = null): TerminologyReference =
        TerminologyReference(Identifier(identifier), library?.let { Identifier(it) })

    fun codeLiteral(
        code: String,
        system: TerminologyReference,
        display: String? = null,
    ): CodeLiteral = CodeLiteral(code = code, system = system, display = display)

    fun code(
        code: String,
        system: String,
        library: String? = null,
        display: String? = null,
    ): Expression = LiteralExpression(codeLiteral(code, terminology(system, library), display))

    fun concept(vararg codes: CodeLiteral, display: String? = null): Expression =
        LiteralExpression(ConceptLiteral(codes = codes.toList(), display = display))

    fun list(vararg elements: ExpressionSpec, elementType: TypeSpecifier? = null): Expression =
        LiteralExpression(
            ListLiteral(elements = elements.map { expression(it) }, elementType = elementType)
        )

    fun tuple(vararg elements: Pair<String, ExpressionSpec>): Expression =
        LiteralExpression(
            TupleLiteral(
                elements =
                    elements.map { (name, spec) ->
                        TupleElementValue(Identifier(name), expression(spec))
                    }
            )
        )

    fun instance(
        type: NamedTypeSpecifier? = null,
        vararg elements: Pair<String, ExpressionSpec>,
    ): Expression =
        LiteralExpression(
            InstanceLiteral(
                type = type,
                elements =
                    elements.map { (name, spec) ->
                        TupleElementValue(Identifier(name), expression(spec))
                    },
            )
        )

    fun interval(
        lower: ExpressionSpec,
        upper: ExpressionSpec,
        lowerClosed: Boolean = true,
        upperClosed: Boolean = true,
    ): Expression =
        LiteralExpression(
            IntervalLiteral(
                lower = expression(lower),
                upper = expression(upper),
                lowerClosed = lowerClosed,
                upperClosed = upperClosed,
            )
        )

    fun identifier(vararg parts: String): Expression =
        IdentifierExpression(QualifiedIdentifier(parts.toList()))

    fun externalConstant(name: String): Expression = ExternalConstantExpression(name)

    fun index(target: ExpressionSpec, index: ExpressionSpec): Expression =
        IndexExpression(expression(target), expression(index))

    fun unary(operator: UnaryOperator, operand: ExpressionSpec): Expression =
        OperatorUnaryExpression(operator, expression(operand))

    fun binary(operator: BinaryOperator, left: ExpressionSpec, right: ExpressionSpec): Expression =
        OperatorBinaryExpression(operator, expression(left), expression(right))

    fun exists(block: ExpressionSpec): Expression = ExistsExpression(expression(block))

    fun membership(
        operator: MembershipOperator,
        left: ExpressionSpec,
        right: ExpressionSpec,
        precision: String? = null,
    ): Expression = MembershipExpression(operator, precision, expression(left), expression(right))

    fun ifThenElse(
        condition: ExpressionSpec,
        thenBranch: ExpressionSpec,
        elseBranch: ExpressionSpec,
    ): Expression =
        IfExpression(expression(condition), expression(thenBranch), expression(elseBranch))

    fun case(block: CaseExpressionBuilder.() -> Unit): Expression =
        CaseExpressionBuilder().apply(block).build()

    fun between(
        input: ExpressionSpec,
        lower: ExpressionSpec,
        upper: ExpressionSpec,
        properly: Boolean = false,
    ): Expression =
        BetweenExpression(expression(input), expression(lower), expression(upper), properly)

    fun durationBetween(
        precision: String,
        lower: ExpressionSpec,
        upper: ExpressionSpec,
    ): Expression = DurationBetweenExpression(precision, expression(lower), expression(upper))

    fun differenceBetween(
        precision: String,
        lower: ExpressionSpec,
        upper: ExpressionSpec,
    ): Expression = DifferenceBetweenExpression(precision, expression(lower), expression(upper))

    fun durationOf(precision: String, operand: ExpressionSpec): Expression =
        DurationOfExpression(precision, expression(operand))

    fun differenceOf(precision: String, operand: ExpressionSpec): Expression =
        DifferenceOfExpression(precision, expression(operand))

    fun widthOf(operand: ExpressionSpec): Expression = WidthExpression(expression(operand))

    fun elementExtractor(kind: ElementExtractorKind, operand: ExpressionSpec): Expression =
        ElementExtractorExpression(kind, expression(operand))

    fun typeExtent(kind: TypeExtentKind, type: NamedTypeSpecifier): Expression =
        TypeExtentExpression(kind, type)

    fun convert(
        operand: ExpressionSpec,
        destinationType: TypeSpecifier? = null,
        destinationUnit: String? = null,
    ): Expression = ConversionExpression(expression(operand), destinationType, destinationUnit)

    fun timeBoundary(kind: TimeBoundaryKind, operand: ExpressionSpec): Expression =
        TimeBoundaryExpression(kind, expression(operand))

    fun dateTimeComponent(component: DateTimeComponent, operand: ExpressionSpec): Expression =
        DateTimeComponentExpression(component, expression(operand))

    fun listTransform(kind: ListTransformKind, operand: ExpressionSpec): Expression =
        ListTransformExpression(kind, expression(operand))

    fun expandCollapse(
        kind: ExpandCollapseKind,
        operand: ExpressionSpec,
        perPrecision: String? = null,
        perExpression: ExpressionSpec? = null,
    ): Expression =
        ExpandCollapseExpression(
            kind = kind,
            operand = expression(operand),
            perPrecision = perPrecision,
            perExpression = perExpression?.let { expression(it) },
        )

    fun intervalRelation(
        left: ExpressionSpec,
        phrase: IntervalPhraseBuilder.() -> IntervalOperatorPhrase,
        right: ExpressionSpec,
    ): Expression =
        IntervalRelationExpression(
            left = expression(left),
            phrase = IntervalPhraseBuilder().phrase(),
            right = expression(right),
        )

    fun typeIs(operand: ExpressionSpec, type: TypeSpecifier, negated: Boolean = false): Expression =
        IsExpression(expression(operand), type, negated)

    fun typeAs(operand: ExpressionSpec, type: TypeSpecifier): Expression =
        AsExpression(expression(operand), type)

    fun cast(operand: ExpressionSpec, type: TypeSpecifier): Expression =
        CastExpression(expression(operand), type)

    fun namedType(vararg parts: String): NamedTypeSpecifier =
        NamedTypeSpecifier(QualifiedIdentifier(parts.toList()))

    fun listType(elementType: TypeSpecifier): ListTypeSpecifier = ListTypeSpecifier(elementType)

    fun intervalType(pointType: TypeSpecifier): IntervalTypeSpecifier =
        IntervalTypeSpecifier(pointType)

    fun tupleType(vararg elements: TupleElement): TupleTypeSpecifier =
        TupleTypeSpecifier(elements.toList())

    fun tupleElement(name: String, type: TypeSpecifier): TupleElement =
        TupleElement(Identifier(name), type)

    fun choiceType(vararg choices: TypeSpecifier): ChoiceTypeSpecifier =
        ChoiceTypeSpecifier(choices.toList())
}

class CaseExpressionBuilder {
    private var comparand: Expression? = null
    private val items = mutableListOf<CaseItem>()
    private var elseResult: Expression? = null

    fun comparand(block: ExpressionSpec) {
        comparand = buildExpressionSpec(block)
    }

    fun whenThen(condition: ExpressionSpec, result: ExpressionSpec) {
        items +=
            CaseItem(
                condition = buildExpressionSpec(condition),
                result = buildExpressionSpec(result),
            )
    }

    fun elseResult(block: ExpressionSpec) {
        elseResult = buildExpressionSpec(block)
    }

    fun build(): CaseExpression {
        val fallback = elseResult ?: error("case expression must define an elseResult")
        return CaseExpression(comparand = comparand, cases = items.toList(), elseResult = fallback)
    }
}

class IntervalPhraseBuilder {

    fun concurrent(
        qualifier: ConcurrentQualifier = ConcurrentQualifier.AS,
        leftBoundary: IntervalBoundarySelector? = null,
        rightBoundary: IntervalBoundarySelector? = null,
        precision: String? = null,
    ): IntervalOperatorPhrase =
        ConcurrentIntervalPhrase(
            leftBoundary = leftBoundary,
            precision = precision,
            qualifier = qualifier,
            rightBoundary = rightBoundary,
        )

    fun includes(
        proper: Boolean,
        precision: String? = null,
        rightBoundary: IntervalBoundarySelector? = null,
    ): IntervalOperatorPhrase =
        IncludesIntervalPhrase(
            proper = proper,
            precision = precision,
            rightBoundary = rightBoundary,
        )

    fun includedIn(
        proper: Boolean,
        variant: InclusionVariant,
        precision: String? = null,
        leftBoundary: IntervalBoundarySelector? = null,
    ): IntervalOperatorPhrase =
        IncludedInIntervalPhrase(
            leftBoundary = leftBoundary,
            proper = proper,
            variant = variant,
            precision = precision,
        )

    @Suppress("LongParameterList")
    fun beforeOrAfter(
        direction: TemporalRelationshipDirection,
        leftBoundary: IntervalBoundarySelector? = null,
        rightBoundary: IntervalBoundarySelector? = null,
        precision: String? = null,
        inclusive: Boolean = false,
        offset: QuantityOffset? = null,
        leadingQualifier: ExclusiveRelativeQualifier? = null,
    ): IntervalOperatorPhrase =
        BeforeOrAfterIntervalPhrase(
            leftBoundary = leftBoundary,
            offset = offset,
            relationship = TemporalRelationshipPhrase(direction, inclusive, leadingQualifier),
            precision = precision,
            rightBoundary = rightBoundary,
        )

    fun within(
        proper: Boolean,
        quantity: QuantityLiteral,
        leftBoundary: IntervalBoundarySelector? = null,
        rightBoundary: IntervalBoundarySelector? = null,
    ): IntervalOperatorPhrase =
        WithinIntervalPhrase(
            leftBoundary = leftBoundary,
            proper = proper,
            quantity = quantity,
            rightBoundary = rightBoundary,
        )

    fun meets(
        direction: TemporalRelationshipDirection? = null,
        precision: String? = null,
    ): IntervalOperatorPhrase = MeetsIntervalPhrase(direction, precision)

    fun overlaps(
        direction: TemporalRelationshipDirection? = null,
        precision: String? = null,
    ): IntervalOperatorPhrase = OverlapsIntervalPhrase(direction, precision)

    fun starts(precision: String? = null): IntervalOperatorPhrase = StartsIntervalPhrase(precision)

    fun ends(precision: String? = null): IntervalOperatorPhrase = EndsIntervalPhrase(precision)
}

fun ExpressionResult.assertMatches(expected: ExpressionSpec) {
    assertTrue(
        problems.isEmpty(),
        "Expected no problems but found: ${problems.joinToString { it.message }}",
    )
    val expectedExpression = buildExpressionSpec(expected).transform(NormalizingTransformer)
    val actualExpression = expression.transform(NormalizingTransformer)
    assertEquals(expectedExpression, actualExpression)
}
