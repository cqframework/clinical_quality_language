package org.opencds.cqf.cql.engine.execution

import java.math.BigDecimal
import org.cqframework.cql.cql2elm.tracking.Trackable.resultType
import org.cqframework.cql.elm.visiting.BaseElmLibraryVisitor
import org.hl7.cql.model.IntervalType
import org.hl7.cql.model.ListType
import org.hl7.elm.r1.*
import org.hl7.elm.r1.List
import org.opencds.cqf.cql.engine.debug.SourceLocator.Companion.fromNode
import org.opencds.cqf.cql.engine.elm.executing.*
import org.opencds.cqf.cql.engine.elm.executing.AbsEvaluator.abs
import org.opencds.cqf.cql.engine.elm.executing.AddEvaluator.add
import org.opencds.cqf.cql.engine.elm.executing.AfterEvaluator.after
import org.opencds.cqf.cql.engine.elm.executing.AllTrueEvaluator.allTrue
import org.opencds.cqf.cql.engine.elm.executing.AndEvaluator.and
import org.opencds.cqf.cql.engine.elm.executing.AnyInCodeSystemEvaluator.internalEvaluate
import org.opencds.cqf.cql.engine.elm.executing.AnyInValueSetEvaluator.internalEvaluate
import org.opencds.cqf.cql.engine.elm.executing.AnyTrueEvaluator.anyTrue
import org.opencds.cqf.cql.engine.elm.executing.AvgEvaluator.avg
import org.opencds.cqf.cql.engine.elm.executing.BeforeEvaluator.before
import org.opencds.cqf.cql.engine.elm.executing.CalculateAgeAtEvaluator.calculateAgeAt
import org.opencds.cqf.cql.engine.elm.executing.CalculateAgeEvaluator.internalEvaluate
import org.opencds.cqf.cql.engine.elm.executing.CeilingEvaluator.ceiling
import org.opencds.cqf.cql.engine.elm.executing.ChildrenEvaluator.children
import org.opencds.cqf.cql.engine.elm.executing.CoalesceEvaluator.coalesce
import org.opencds.cqf.cql.engine.elm.executing.CodeRefEvaluator.toCode
import org.opencds.cqf.cql.engine.elm.executing.CollapseEvaluator.collapse
import org.opencds.cqf.cql.engine.elm.executing.CombineEvaluator.combine
import org.opencds.cqf.cql.engine.elm.executing.ConcatenateEvaluator.concatenate
import org.opencds.cqf.cql.engine.elm.executing.ConceptEvaluator.internalEvaluate
import org.opencds.cqf.cql.engine.elm.executing.ConceptRefEvaluator.toConcept
import org.opencds.cqf.cql.engine.elm.executing.ContainsEvaluator.internalEvaluate
import org.opencds.cqf.cql.engine.elm.executing.ConvertEvaluator.internalEvaluate
import org.opencds.cqf.cql.engine.elm.executing.ConvertQuantityEvaluator.convertQuantity
import org.opencds.cqf.cql.engine.elm.executing.ConvertsToBooleanEvaluator.convertsToBoolean
import org.opencds.cqf.cql.engine.elm.executing.ConvertsToDateEvaluator.convertsToDate
import org.opencds.cqf.cql.engine.elm.executing.ConvertsToDateTimeEvaluator.convertsToDateTime
import org.opencds.cqf.cql.engine.elm.executing.ConvertsToDecimalEvaluator.convertsToDecimal
import org.opencds.cqf.cql.engine.elm.executing.ConvertsToIntegerEvaluator.convertsToInteger
import org.opencds.cqf.cql.engine.elm.executing.ConvertsToLongEvaluator.convertsToLong
import org.opencds.cqf.cql.engine.elm.executing.ConvertsToQuantityEvaluator.convertsToQuantity
import org.opencds.cqf.cql.engine.elm.executing.ConvertsToStringEvaluator.convertsToString
import org.opencds.cqf.cql.engine.elm.executing.ConvertsToTimeEvaluator.convertsToTime
import org.opencds.cqf.cql.engine.elm.executing.CountEvaluator.count
import org.opencds.cqf.cql.engine.elm.executing.DateEvaluator.internalEvaluate
import org.opencds.cqf.cql.engine.elm.executing.DateFromEvaluator.dateFrom
import org.opencds.cqf.cql.engine.elm.executing.DateTimeComponentFromEvaluator.dateTimeComponentFrom
import org.opencds.cqf.cql.engine.elm.executing.DateTimeEvaluator.internalEvaluate
import org.opencds.cqf.cql.engine.elm.executing.DescendentsEvaluator.descendents
import org.opencds.cqf.cql.engine.elm.executing.DifferenceBetweenEvaluator.difference
import org.opencds.cqf.cql.engine.elm.executing.DistinctEvaluator.distinct
import org.opencds.cqf.cql.engine.elm.executing.DivideEvaluator.divide
import org.opencds.cqf.cql.engine.elm.executing.DurationBetweenEvaluator.duration
import org.opencds.cqf.cql.engine.elm.executing.EndEvaluator.end
import org.opencds.cqf.cql.engine.elm.executing.EndsEvaluator.ends
import org.opencds.cqf.cql.engine.elm.executing.EndsWithEvaluator.endsWith
import org.opencds.cqf.cql.engine.elm.executing.EqualEvaluator.equal
import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator.equivalent
import org.opencds.cqf.cql.engine.elm.executing.ExceptEvaluator.except
import org.opencds.cqf.cql.engine.elm.executing.ExistsEvaluator.exists
import org.opencds.cqf.cql.engine.elm.executing.ExpEvaluator.exp
import org.opencds.cqf.cql.engine.elm.executing.ExpandEvaluator.expand
import org.opencds.cqf.cql.engine.elm.executing.ExpandValueSetEvaluator.expand
import org.opencds.cqf.cql.engine.elm.executing.ExpressionDefEvaluator.internalEvaluate
import org.opencds.cqf.cql.engine.elm.executing.FilterEvaluator.filter
import org.opencds.cqf.cql.engine.elm.executing.FirstEvaluator.first
import org.opencds.cqf.cql.engine.elm.executing.FlattenEvaluator.flatten
import org.opencds.cqf.cql.engine.elm.executing.FloorEvaluator.floor
import org.opencds.cqf.cql.engine.elm.executing.ForEachEvaluator.forEach
import org.opencds.cqf.cql.engine.elm.executing.GeometricMeanEvaluator.geometricMean
import org.opencds.cqf.cql.engine.elm.executing.GreaterEvaluator.greater
import org.opencds.cqf.cql.engine.elm.executing.GreaterOrEqualEvaluator.greaterOrEqual
import org.opencds.cqf.cql.engine.elm.executing.HighBoundaryEvaluator.highBoundary
import org.opencds.cqf.cql.engine.elm.executing.ImpliesEvaluator.implies
import org.opencds.cqf.cql.engine.elm.executing.InCodeSystemEvaluator.inCodeSystem
import org.opencds.cqf.cql.engine.elm.executing.InValueSetEvaluator.inValueSet
import org.opencds.cqf.cql.engine.elm.executing.IndexOfEvaluator.indexOf
import org.opencds.cqf.cql.engine.elm.executing.IndexerEvaluator.indexer
import org.opencds.cqf.cql.engine.elm.executing.IntersectEvaluator.intersect
import org.opencds.cqf.cql.engine.elm.executing.IsEvaluator.internalEvaluate
import org.opencds.cqf.cql.engine.elm.executing.IsNullEvaluator.isNull
import org.opencds.cqf.cql.engine.elm.executing.LastEvaluator.last
import org.opencds.cqf.cql.engine.elm.executing.LastPositionOfEvaluator.lastPositionOf
import org.opencds.cqf.cql.engine.elm.executing.LengthEvaluator.internalEvaluate
import org.opencds.cqf.cql.engine.elm.executing.LessEvaluator.less
import org.opencds.cqf.cql.engine.elm.executing.LessOrEqualEvaluator.lessOrEqual
import org.opencds.cqf.cql.engine.elm.executing.LnEvaluator.ln
import org.opencds.cqf.cql.engine.elm.executing.LogEvaluator.log
import org.opencds.cqf.cql.engine.elm.executing.LowBoundaryEvaluator.lowBoundary
import org.opencds.cqf.cql.engine.elm.executing.LowerEvaluator.lower
import org.opencds.cqf.cql.engine.elm.executing.MatchesEvaluator.matches
import org.opencds.cqf.cql.engine.elm.executing.MaxEvaluator.max
import org.opencds.cqf.cql.engine.elm.executing.MedianEvaluator.median
import org.opencds.cqf.cql.engine.elm.executing.MeetsAfterEvaluator.meetsAfter
import org.opencds.cqf.cql.engine.elm.executing.MeetsBeforeEvaluator.meetsBefore
import org.opencds.cqf.cql.engine.elm.executing.MeetsEvaluator.meets
import org.opencds.cqf.cql.engine.elm.executing.MinEvaluator.min
import org.opencds.cqf.cql.engine.elm.executing.ModeEvaluator.mode
import org.opencds.cqf.cql.engine.elm.executing.ModuloEvaluator.modulo
import org.opencds.cqf.cql.engine.elm.executing.MultiplyEvaluator.multiply
import org.opencds.cqf.cql.engine.elm.executing.NotEqualEvaluator.notEqual
import org.opencds.cqf.cql.engine.elm.executing.NotEvaluator.not
import org.opencds.cqf.cql.engine.elm.executing.OrEvaluator.or
import org.opencds.cqf.cql.engine.elm.executing.OverlapsAfterEvaluator.overlapsAfter
import org.opencds.cqf.cql.engine.elm.executing.OverlapsBeforeEvaluator.overlapsBefore
import org.opencds.cqf.cql.engine.elm.executing.OverlapsEvaluator.overlaps
import org.opencds.cqf.cql.engine.elm.executing.PointFromEvaluator.pointFrom
import org.opencds.cqf.cql.engine.elm.executing.PopulationStdDevEvaluator.popStdDev
import org.opencds.cqf.cql.engine.elm.executing.PopulationVarianceEvaluator.popVariance
import org.opencds.cqf.cql.engine.elm.executing.PositionOfEvaluator.positionOf
import org.opencds.cqf.cql.engine.elm.executing.PowerEvaluator.power
import org.opencds.cqf.cql.engine.elm.executing.PrecisionEvaluator.precision
import org.opencds.cqf.cql.engine.elm.executing.PredecessorEvaluator.predecessor
import org.opencds.cqf.cql.engine.elm.executing.ProductEvaluator.product
import org.opencds.cqf.cql.engine.elm.executing.ProperContainsEvaluator.properContains
import org.opencds.cqf.cql.engine.elm.executing.ProperIncludedInEvaluator.properlyIncludedIn
import org.opencds.cqf.cql.engine.elm.executing.ProperIncludesEvaluator.properlyIncludes
import org.opencds.cqf.cql.engine.elm.executing.QuantityEvaluator.internalEvaluate
import org.opencds.cqf.cql.engine.elm.executing.QueryLetRefEvaluator.internalEvaluate
import org.opencds.cqf.cql.engine.elm.executing.ReplaceMatchesEvaluator.replaceMatches
import org.opencds.cqf.cql.engine.elm.executing.RoundEvaluator.round
import org.opencds.cqf.cql.engine.elm.executing.SameAsEvaluator.sameAs
import org.opencds.cqf.cql.engine.elm.executing.SameOrAfterEvaluator.sameOrAfter
import org.opencds.cqf.cql.engine.elm.executing.SameOrBeforeEvaluator.sameOrBefore
import org.opencds.cqf.cql.engine.elm.executing.SingletonFromEvaluator.singletonFrom
import org.opencds.cqf.cql.engine.elm.executing.SizeEvaluator.size
import org.opencds.cqf.cql.engine.elm.executing.SliceEvaluator.slice
import org.opencds.cqf.cql.engine.elm.executing.SplitEvaluator.split
import org.opencds.cqf.cql.engine.elm.executing.SplitOnMatchesEvaluator.splitOnMatches
import org.opencds.cqf.cql.engine.elm.executing.StartEvaluator.start
import org.opencds.cqf.cql.engine.elm.executing.StartsEvaluator.starts
import org.opencds.cqf.cql.engine.elm.executing.StartsWithEvaluator.startsWith
import org.opencds.cqf.cql.engine.elm.executing.StdDevEvaluator.stdDev
import org.opencds.cqf.cql.engine.elm.executing.SubstringEvaluator.substring
import org.opencds.cqf.cql.engine.elm.executing.SubtractEvaluator.subtract
import org.opencds.cqf.cql.engine.elm.executing.SuccessorEvaluator.successor
import org.opencds.cqf.cql.engine.elm.executing.SumEvaluator.sum
import org.opencds.cqf.cql.engine.elm.executing.TimeEvaluator.time
import org.opencds.cqf.cql.engine.elm.executing.TimeFromEvaluator.timeFrom
import org.opencds.cqf.cql.engine.elm.executing.TimezoneFromEvaluator.internalEvaluate
import org.opencds.cqf.cql.engine.elm.executing.TimezoneOffsetFromEvaluator.timezoneOffsetFrom
import org.opencds.cqf.cql.engine.elm.executing.ToBooleanEvaluator.toBoolean
import org.opencds.cqf.cql.engine.elm.executing.ToCharsEvaluator.toChars
import org.opencds.cqf.cql.engine.elm.executing.ToConceptEvaluator.toConcept
import org.opencds.cqf.cql.engine.elm.executing.ToDateEvaluator.toDate
import org.opencds.cqf.cql.engine.elm.executing.ToDateTimeEvaluator.toDateTime
import org.opencds.cqf.cql.engine.elm.executing.ToDecimalEvaluator.toDecimal
import org.opencds.cqf.cql.engine.elm.executing.ToIntegerEvaluator.toInteger
import org.opencds.cqf.cql.engine.elm.executing.ToListEvaluator.toList
import org.opencds.cqf.cql.engine.elm.executing.ToLongEvaluator.toLong
import org.opencds.cqf.cql.engine.elm.executing.ToQuantityEvaluator.toQuantity
import org.opencds.cqf.cql.engine.elm.executing.ToRatioEvaluator.toRatio
import org.opencds.cqf.cql.engine.elm.executing.ToStringEvaluator.toString
import org.opencds.cqf.cql.engine.elm.executing.ToTimeEvaluator.toTime
import org.opencds.cqf.cql.engine.elm.executing.TodayEvaluator.today
import org.opencds.cqf.cql.engine.elm.executing.TruncateEvaluator.truncate
import org.opencds.cqf.cql.engine.elm.executing.TruncatedDivideEvaluator.div
import org.opencds.cqf.cql.engine.elm.executing.TupleEvaluator.internalEvaluate
import org.opencds.cqf.cql.engine.elm.executing.UnionEvaluator.union
import org.opencds.cqf.cql.engine.elm.executing.UnionEvaluator.unionInterval
import org.opencds.cqf.cql.engine.elm.executing.UnionEvaluator.unionIterable
import org.opencds.cqf.cql.engine.elm.executing.UpperEvaluator.upper
import org.opencds.cqf.cql.engine.elm.executing.ValueSetRefEvaluator.internalEvaluate
import org.opencds.cqf.cql.engine.elm.executing.VarianceEvaluator.variance
import org.opencds.cqf.cql.engine.elm.executing.WidthEvaluator.width
import org.opencds.cqf.cql.engine.elm.executing.XorEvaluator.xor
import org.opencds.cqf.cql.engine.exception.CqlException
import org.opencds.cqf.cql.engine.exception.Severity
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.Precision
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.runtime.TemporalHelper

class EvaluationVisitor : BaseElmLibraryVisitor<Any?, State?>() {
    public override fun visitExpression(expression: Expression, state: State?): Any? {
        try {
            return super.visitExpression(expression, state)
        } catch (e: CqlException) {
            maybeExtendBacktrace(e, state, expression)
            throw e
        } catch (e: Exception) {
            val exception =
                CqlException(
                    e.message,
                    e,
                    fromNode(expression, state!!.getCurrentLibrary()),
                    Severity.ERROR,
                )
            maybeExtendBacktrace(exception, state, expression)
            throw exception
        }
    }

    private fun maybeExtendBacktrace(
        exception: CqlException,
        state: State?,
        expression: Expression?,
    ) {
        // If the top of the stack in state is call-like
        // ActivationFrame (that is an ActivationFrame for an
        // expression definition or a function definition), try to
        // extend the backtrace object of exception to include that
        // call.
        val frame = state!!.topActivationFrame
        if (frame.element is ExpressionDef) {
            exception.backtrace.maybeAddFrame(
                frame.element as ExpressionDef,
                frame,
                state.stack,
                state.getCurrentContext(),
                state.currentContextValue,
                expression,
            )
        }
    }

    public override fun visitExpressionDef(expressionDef: ExpressionDef, state: State?): Any? {
        return internalEvaluate(expressionDef, state, this)
    }

    public override fun visitExpressionRef(expressionRef: ExpressionRef, state: State?): Any? {
        return ExpressionRefEvaluator.internalEvaluate(expressionRef, state, this)
    }

    public override fun visitFunctionRef(elm: FunctionRef, state: State?): Any? {
        return FunctionRefEvaluator.internalEvaluate(elm, state, this)
    }

    public override fun visitAdd(add: Add, state: State?): Any? {
        val left = visitExpression(add.operand.get(0), state)
        val right = visitExpression(add.operand.get(1), state)

        return add(left, right)
    }

    public override fun visitAbs(abs: Abs, state: State?): Any? {
        val operand = visitExpression(abs.operand!!, state)
        return abs(operand)
    }

    public override fun visitAfter(after: After, state: State?): Any? {
        val left = visitExpression(after.operand.get(0), state)
        val right = visitExpression(after.operand.get(1), state)
        val precision = if (after.precision == null) null else after.precision!!.value()

        return after(left, right, precision, state)
    }

    public override fun visitAliasRef(aliasRef: AliasRef, state: State?): Any? {
        return AliasRefEvaluator.internalEvaluate(aliasRef.name, state)
    }

    public override fun visitAllTrue(allTrue: AllTrue, state: State?): Any? {
        val src = visitExpression(allTrue.source!!, state)
        return allTrue(src)
    }

    public override fun visitAnd(and: And, state: State?): Any? {
        val left = visitExpression(and.operand.get(0), state)
        val right = visitExpression(and.operand.get(1), state)

        return and(left, right)
    }

    public override fun visitAnyInCodeSystem(
        anyInCodeSystem: AnyInCodeSystem,
        state: State?,
    ): Any? {
        val codes = visitExpression(anyInCodeSystem.codes!!, state)
        var codeSystem: Any? = null
        if (anyInCodeSystem.codesystem != null) {
            codeSystem = CodeSystemRefEvaluator.toCodeSystem(anyInCodeSystem.codesystem, state)
        } else {
            codeSystem = visitExpression(anyInCodeSystem.codesystemExpression!!, state)
        }

        return internalEvaluate(codes, anyInCodeSystem.codesystem, codeSystem, state)
    }

    public override fun visitInCodeSystem(inCodeSystem: InCodeSystem, state: State?): Any? {
        val code = visitExpression(inCodeSystem.code!!, state)
        var cs: Any? = null
        if (inCodeSystem.codesystem != null) {
            cs = CodeSystemRefEvaluator.toCodeSystem(inCodeSystem.codesystem!!, state)
        } else if (inCodeSystem.codesystemExpression != null) {
            cs = visitExpression(inCodeSystem.codesystemExpression!!, state)
        }

        return inCodeSystem(code, cs, state)
    }

    public override fun visitAnyInValueSet(anyInValueSet: AnyInValueSet, state: State?): Any? {
        val codes = visitExpression(anyInValueSet.codes!!, state)
        var valueSet: Any? = null
        if (anyInValueSet.valueset != null) {
            valueSet = ValueSetRefEvaluator.toValueSet(state, anyInValueSet.valueset!!)
        } else {
            valueSet = visitExpression(anyInValueSet.valuesetExpression!!, state)
        }

        return internalEvaluate(codes, anyInValueSet.valueset, valueSet, state)
    }

    public override fun visitInValueSet(inValueSet: InValueSet, state: State?): Any? {
        val code = visitExpression(inValueSet.code!!, state)
        var vs: Any? = null
        if (inValueSet.valueset != null) {
            vs = ValueSetRefEvaluator.toValueSet(state, inValueSet.valueset!!)
        } else if (inValueSet.valuesetExpression != null) {
            vs = visitExpression(inValueSet.valuesetExpression!!, state)
        }
        return inValueSet(code, vs, state)
    }

    public override fun visitValueSetRef(elm: ValueSetRef, state: State?): Any? {
        return internalEvaluate(state, elm)
    }

    public override fun visitXor(elm: Xor, state: State?): Any? {
        val left = visitExpression(elm.operand.get(0), state)
        val right = visitExpression(elm.operand.get(1), state)
        return xor(left, right)
    }

    public override fun visitWidth(elm: Width, state: State?): Any? {
        val operand = visitExpression(elm.operand!!, state)
        return width(operand)
    }

    public override fun visitVariance(variance: Variance, state: State?): Any? {
        val source = visitExpression(variance.source!!, state)
        return variance(source, state)
    }

    public override fun visitAvg(avg: Avg, state: State?): Any? {
        val src = visitExpression(avg.source!!, state)
        return avg(src, state)
    }

    public override fun visitDivide(elm: Divide, state: State?): Any? {
        val left = visitExpression(elm.operand.get(0), state)
        val right = visitExpression(elm.operand.get(1), state)
        return divide(left, right, state)
    }

    public override fun visitUpper(elm: Upper, state: State?): Any? {
        val operand = visitExpression(elm.operand!!, state)
        return upper(operand)
    }

    public override fun visitUnion(elm: Union, state: State?): Any? {
        val left = elm.operand.get(0)
        val right = elm.operand.get(1)
        val leftResult = visitExpression(left, state)
        val rightResult = visitExpression(right, state)

        // Attempt to use resultTypes if present. This is needed because
        // null as List union null as List returns an empty list, but
        // null as Interval union null as Interval returns null.
        // Fixing solely in the engine requires type metadata for
        // the results to be available at runtime
        // (e.g. stored in a header, table, wrapper class, etc.)
        val leftResultType = left.resultType
        val rightResultType = right.resultType
        val elmResultType = elm.resultType
        if (
            leftResultType is ListType || rightResultType is ListType || elmResultType is ListType
        ) {
            return unionIterable(leftResult as Iterable<*>?, rightResult as Iterable<*>?, state)
        } else if (
            leftResultType is IntervalType ||
                rightResultType is IntervalType ||
                elmResultType is IntervalType
        ) {
            return unionInterval(leftResult as Interval?, rightResult as Interval?, state)
        } else {
            return union(leftResult, rightResult, state)
        }
    }

    public override fun visitGreater(elm: Greater, state: State?): Any? {
        val left = visitExpression(elm.operand.get(0), state)
        val right = visitExpression(elm.operand.get(1), state)

        return greater(left, right, state)
    }

    public override fun visitMeets(elm: Meets, state: State?): Any? {
        val left = visitExpression(elm.operand.get(0), state)
        val right = visitExpression(elm.operand.get(1), state)
        val precision = if (elm.precision == null) null else elm.precision!!.value()

        return meets(left, right, precision, state)
    }

    public override fun visitDistinct(elm: Distinct, state: State?): Any? {
        val value = visitExpression(elm.operand!!, state)
        return distinct(value as Iterable<*>?, state)
    }

    public override fun visitMeetsAfter(elm: MeetsAfter, state: State?): Any? {
        val left = visitExpression(elm.operand.get(0), state)
        val right = visitExpression(elm.operand.get(1), state)
        val precision = if (elm.precision == null) null else elm.precision!!.value()

        return meetsAfter(left, right, precision, state)
    }

    // SameAs
    public override fun visitMeetsBefore(elm: MeetsBefore, state: State?): Any? {
        val left = visitExpression(elm.operand.get(0), state)
        val right = visitExpression(elm.operand.get(1), state)
        val precision = if (elm.precision == null) null else elm.precision!!.value()

        return meetsBefore(left, right, precision, state)
    }

    public override fun visitSameAs(elm: SameAs, state: State?): Any? {
        val left = visitExpression(elm.operand.get(0), state)
        val right = visitExpression(elm.operand.get(1), state)
        val precision = if (elm.precision == null) null else elm.precision!!.value()

        return sameAs(left, right, precision, state)
    }

    public override fun visitSameOrAfter(elm: SameOrAfter, state: State?): Any? {
        val left = visitExpression(elm.operand.get(0), state)
        val right = visitExpression(elm.operand.get(1), state)
        val precision = if (elm.precision == null) null else elm.precision!!.value()

        return sameOrAfter(left, right, precision, state)
    }

    public override fun visitSameOrBefore(elm: SameOrBefore, state: State?): Any? {
        val left = visitExpression(elm.operand.get(0), state)
        val right = visitExpression(elm.operand.get(1), state)
        val precision = if (elm.precision == null) null else elm.precision!!.value()

        return sameOrBefore(left, right, precision, state)
    }

    public override fun visitGreaterOrEqual(elm: GreaterOrEqual, state: State?): Any? {
        val left = visitExpression(elm.operand.get(0), state)
        val right = visitExpression(elm.operand.get(1), state)

        return greaterOrEqual(left, right, state)
    }

    public override fun visitSingletonFrom(elm: SingletonFrom, state: State?): Any? {
        val operand = visitExpression(elm.operand!!, state)
        return singletonFrom(operand)
    }

    public override fun visitSize(elm: Size, state: State?): Any? {
        val argument = visitExpression(elm.operand!!, state)
        return size(argument)
    }

    public override fun visitSlice(elm: Slice, state: State?): Any? {
        val source = visitExpression(elm.source!!, state)
        val start = visitExpression(elm.startIndex!!, state) as Int?
        val end = if (elm.endIndex == null) null else visitExpression(elm.endIndex!!, state) as Int?

        return slice(source, start, end)
    }

    public override fun visitSplit(elm: Split, state: State?): Any? {
        val stringToSplit = visitExpression(elm.stringToSplit!!, state)
        val separator = visitExpression(elm.separator!!, state)

        return split(stringToSplit, separator)
    }

    public override fun visitSplitOnMatches(elm: SplitOnMatches, state: State?): Any? {
        val stringToSplit = visitExpression(elm.stringToSplit!!, state)
        val separator = visitExpression(elm.separatorPattern!!, state)

        return splitOnMatches(stringToSplit, separator)
    }

    public override fun visitStart(elm: Start, state: State?): Any? {
        val operand = visitExpression(elm.operand!!, state)
        return start(operand)
    }

    public override fun visitStarts(elm: Starts, state: State?): Any? {
        val left = visitExpression(elm.operand.get(0), state)
        val right = visitExpression(elm.operand.get(1), state)
        val precision = if (elm.precision == null) null else elm.precision!!.value()

        return starts(left, right, precision, state)
    }

    public override fun visitStartsWith(elm: StartsWith, state: State?): Any? {
        val argument = visitExpression(elm.operand.get(0), state)
        val prefix = visitExpression(elm.operand.get(1), state)

        return startsWith(argument, prefix)
    }

    public override fun visitStdDev(elm: StdDev, state: State?): Any? {
        val source = visitExpression(elm.source!!, state)
        return stdDev(source, state)
    }

    public override fun visitSubstring(elm: Substring, state: State?): Any? {
        val stringValue = visitExpression(elm.stringToSub!!, state)
        val startIndexValue = visitExpression(elm.startIndex!!, state)
        val lengthValue = if (elm.length == null) null else visitExpression(elm.length!!, state)

        return substring(stringValue, startIndexValue, lengthValue)
    }

    public override fun visitSubtract(elm: Subtract, state: State?): Any? {
        val left = visitExpression(elm.operand.get(0), state)
        val right = visitExpression(elm.operand.get(1), state)
        return subtract(left, right)
    }

    public override fun visitSuccessor(elm: Successor, state: State?): Any? {
        val value = visitExpression(elm.operand!!, state)
        return successor(value)
    }

    public override fun visitSum(elm: Sum, state: State?): Any? {
        val source = visitExpression(elm.source!!, state)
        return sum(source)
    }

    public override fun visitTime(elm: Time, state: State?): Any? {
        if (elm.hour == null) {
            return null
        }

        val hour = if (elm.hour == null) null else visitExpression(elm.hour!!, state) as Int?
        val minute = if (elm.minute == null) null else visitExpression(elm.minute!!, state) as Int?
        val second = if (elm.second == null) null else visitExpression(elm.second!!, state) as Int?
        val miliSecond =
            if (elm.millisecond == null) null else visitExpression(elm.millisecond!!, state) as Int?

        return time(hour, minute, second, miliSecond)
    }

    public override fun visitTimeFrom(elm: TimeFrom, state: State?): Any? {
        val operand = visitExpression(elm.operand!!, state)
        return timeFrom(operand)
    }

    public override fun visitTimeOfDay(elm: TimeOfDay, state: State?): Any? {
        return TimeOfDayEvaluator.internalEvaluate(state)
    }

    public override fun visitTimezoneFrom(elm: TimezoneFrom, state: State?): Any? {
        val operand = visitExpression(elm.operand!!, state)
        return internalEvaluate(operand)
    }

    public override fun visitTimezoneOffsetFrom(elm: TimezoneOffsetFrom, state: State?): Any? {
        val operand = visitExpression(elm.operand!!, state)
        return timezoneOffsetFrom(operand)
    }

    public override fun visitToBoolean(elm: ToBoolean, state: State?): Any? {
        val operand = visitExpression(elm.operand!!, state)
        return toBoolean(operand)
    }

    public override fun visitToConcept(elm: ToConcept, state: State?): Any? {
        val operand = visitExpression(elm.operand!!, state)
        return toConcept(operand)
    }

    public override fun visitToChars(elm: ToChars, state: State?): Any? {
        val operand = visitExpression(elm.operand!!, state)
        return toChars(operand)
    }

    public override fun visitToDate(elm: ToDate, state: State?): Any? {
        val operand = visitExpression(elm.operand!!, state)
        return toDate(operand)
    }

    public override fun visitToDateTime(elm: ToDateTime, state: State?): Any? {
        val operand = visitExpression(elm.operand!!, state)
        return toDateTime(operand, state)
    }

    public override fun visitToday(elm: Today, state: State?): Any? {
        return today(state)
    }

    public override fun visitToDecimal(elm: ToDecimal, state: State?): Any? {
        val operand = visitExpression(elm.operand!!, state)
        return toDecimal(operand)
    }

    public override fun visitToInteger(elm: ToInteger, state: State?): Any? {
        val operand = visitExpression(elm.operand!!, state)
        return toInteger(operand)
    }

    public override fun visitToList(elm: ToList, state: State?): Any? {
        val operand = visitExpression(elm.operand!!, state)
        return toList(operand)
    }

    public override fun visitToLong(elm: ToLong, state: State?): Any? {
        val operand = visitExpression(elm.operand!!, state)
        return toLong(operand)
    }

    public override fun visitToQuantity(elm: ToQuantity, state: State?): Any? {
        val operand = visitExpression(elm.operand!!, state)
        return toQuantity(operand, state)
    }

    public override fun visitToRatio(elm: ToRatio, state: State?): Any? {
        val operand = visitExpression(elm.operand!!, state)
        return toRatio(operand)
    }

    public override fun visitToString(elm: ToString, state: State?): Any? {
        val operand = visitExpression(elm.operand!!, state)
        return toString(operand)
    }

    public override fun visitToTime(elm: ToTime, state: State?): Any? {
        val operand = visitExpression(elm.operand!!, state)
        return toTime(operand)
    }

    public override fun visitTruncatedDivide(elm: TruncatedDivide, state: State?): Any? {
        val left = visitExpression(elm.operand.get(0), state)
        val right = visitExpression(elm.operand.get(1), state)
        return div(left, right, state)
    }

    public override fun visitMedian(elm: Median, state: State?): Any? {
        val source = visitExpression(elm.source!!, state)
        return median(source, state)
    }

    public override fun visitTruncate(elm: Truncate, state: State?): Any? {
        val operand = visitExpression(elm.operand!!, state)
        return truncate(operand)
    }

    public override fun visitTuple(elm: Tuple, state: State?): Any? {
        val ret = LinkedHashMap<String, Any?>()
        for (element in elm.element) {
            ret.put(element.name!!, visitExpression(element.value!!, state))
        }
        return internalEvaluate(ret, state)
    }

    public override fun visitAnyTrue(elm: AnyTrue, state: State?): Any? {
        val source = visitExpression(elm.source!!, state)
        return anyTrue(source)
    }

    public override fun visitAs(elm: As, state: State?): Any? {
        val operand = visitExpression(elm.operand!!, state)
        return AsEvaluator.internalEvaluate(operand, elm, elm.isStrict()!!, state)
    }

    public override fun visitBefore(elm: Before, state: State?): Any? {
        val left = visitExpression(elm.operand.get(0), state)
        val right = visitExpression(elm.operand.get(1), state)
        val precision = if (elm.precision == null) null else elm.precision!!.value()

        return before(left, right, precision, state)
    }

    public override fun visitCalculateAgeAt(elm: CalculateAgeAt, state: State?): Any? {
        val birthDate = visitExpression(elm.operand.get(0), state)
        val asOf = visitExpression(elm.operand.get(1), state)
        val precision = elm.precision!!.value()
        return calculateAgeAt(birthDate, asOf, precision)
    }

    public override fun visitCalculateAge(elm: CalculateAge, state: State?): Any? {
        val operand = visitExpression(elm.operand!!, state)
        val precision = elm.precision!!.value()
        return internalEvaluate(operand, precision, state)
    }

    public override fun visitCase(elm: Case, state: State?): Any? {
        if (elm.comparand == null) {
            for (caseItem in elm.caseItem) {
                val `when` = visitExpression(caseItem.`when`!!, state) as Boolean?

                if (`when` == null) {
                    continue
                }

                if (`when`) {
                    return visitExpression(caseItem.then!!, state)
                }
            }
            return visitElement(elm.`else`!!, state)
        } else {
            val comparand = visitExpression(elm.comparand!!, state)

            for (caseItem in elm.caseItem) {
                val `when` = visitExpression(caseItem.`when`!!, state)
                val check = equivalent(comparand, `when`, state)
                if (check == null) {
                    continue
                }

                if (check) {
                    return visitElement(caseItem.then!!, state)
                }
            }

            return visitElement(elm.`else`!!, state)
        }
    }

    public override fun visitCeiling(elm: Ceiling, state: State?): Any? {
        val operand = visitExpression(elm.operand!!, state)
        return ceiling(operand)
    }

    public override fun visitChildren(elm: Children, state: State?): Any? {
        val source = visitExpression(elm.source!!, state)
        return children(source)
    }

    public override fun visitCoalesce(elm: Coalesce, state: State?): Any? {
        val operands = mutableListOf<Any?>()
        for (operand in elm.operand) {
            operands.add(visitExpression(operand, state))
        }
        return coalesce(operands)
    }

    public override fun visitCode(elm: Code, state: State?): Any? {
        return CodeEvaluator.internalEvaluate(elm.system, elm.code, elm.display, state)
    }

    public override fun visitCodeRef(elm: CodeRef, state: State?): Any? {
        return toCode(elm, state)
    }

    public override fun visitConcept(elm: Concept, state: State?): Any? {
        val codes = ArrayList<org.opencds.cqf.cql.engine.runtime.Code?>()
        for (i in 0..<elm.code.size) {
            codes.add(
                visitExpression(elm.code.get(i), state) as org.opencds.cqf.cql.engine.runtime.Code?
            )
        }

        return internalEvaluate(codes, elm.display)
    }

    public override fun visitConceptRef(elm: ConceptRef, state: State?): Any? {
        return toConcept(elm, state)
    }

    public override fun visitCollapse(elm: Collapse, state: State?): Any? {
        val left = visitExpression(elm.operand.get(0), state)
        val right = visitExpression(elm.operand.get(1), state)

        val list = left as Iterable<Interval?>?
        val per = right as Quantity?

        return collapse(list, per, state)
    }

    public override fun visitCombine(elm: Combine, state: State?): Any? {
        val source = visitExpression(elm.source!!, state)
        val separator =
            if (elm.separator == null) "" else visitExpression(elm.separator!!, state) as String?

        return combine(source, separator)
    }

    public override fun visitConcatenate(elm: Concatenate, state: State?): Any? {
        val left = visitExpression(elm.operand.get(0), state)
        val right = visitExpression(elm.operand.get(1), state)

        return concatenate(left, right)
    }

    public override fun visitContains(elm: Contains, state: State?): Any? {
        val left = visitExpression(elm.operand.get(0), state)
        val right = visitExpression(elm.operand.get(1), state)
        val precision = if (elm.precision == null) null else elm.precision!!.value()
        return internalEvaluate(left, right, elm.operand.get(0), precision, state)
    }

    public override fun visitConvert(elm: Convert, state: State?): Any? {
        val operand = visitExpression(elm.operand!!, state)
        return internalEvaluate(operand, elm.toType, elm.toTypeSpecifier, state)
    }

    public override fun visitConvertQuantity(elm: ConvertQuantity, state: State?): Any? {
        val argument = visitExpression(elm.operand.get(0), state)
        val unit = visitExpression(elm.operand.get(1), state)
        return convertQuantity(argument, unit, state!!.environment.libraryManager!!.ucumService)
    }

    public override fun visitConvertsToBoolean(elm: ConvertsToBoolean, state: State?): Any? {
        val operand = visitExpression(elm.operand!!, state)
        return convertsToBoolean(operand)
    }

    public override fun visitConvertsToDate(elm: ConvertsToDate, state: State?): Any? {
        val operand = visitExpression(elm.operand!!, state)
        return convertsToDate(operand)
    }

    public override fun visitConvertsToDateTime(elm: ConvertsToDateTime, state: State?): Any? {
        val operand = visitExpression(elm.operand!!, state)
        return convertsToDateTime(operand, state!!.evaluationDateTime!!.zoneOffset)
    }

    public override fun visitConvertsToDecimal(elm: ConvertsToDecimal, state: State?): Any? {
        val operand = visitExpression(elm.operand!!, state)
        return convertsToDecimal(operand)
    }

    public override fun visitConvertsToInteger(elm: ConvertsToInteger, state: State?): Any? {
        val operand = visitExpression(elm.operand!!, state)
        return convertsToInteger(operand)
    }

    public override fun visitConvertsToLong(elm: ConvertsToLong, state: State?): Any? {
        val operand = visitExpression(elm.operand!!, state)
        return convertsToLong(operand)
    }

    public override fun visitConvertsToQuantity(elm: ConvertsToQuantity, state: State?): Any? {
        val operand = visitExpression(elm.operand!!, state)
        return convertsToQuantity(operand, state)
    }

    public override fun visitConvertsToString(elm: ConvertsToString, state: State?): Any? {
        val operand = visitExpression(elm.operand!!, state)
        return convertsToString(operand)
    }

    public override fun visitConvertsToTime(elm: ConvertsToTime, state: State?): Any? {
        val operand = visitExpression(elm.operand!!, state)
        return convertsToTime(operand)
    }

    public override fun visitCount(elm: Count, state: State?): Any? {
        val source = visitExpression(elm.source!!, state)
        return count(source)
    }

    public override fun visitDate(elm: Date, state: State?): Any? {
        val year = if (elm.year == null) null else visitExpression(elm.year!!, state) as Int?
        val month = if (elm.month == null) null else visitExpression(elm.month!!, state) as Int?
        val day = if (elm.day == null) null else visitExpression(elm.day!!, state) as Int?
        return internalEvaluate(year, month, day)
    }

    public override fun visitDateFrom(elm: DateFrom, state: State?): Any? {
        val operand = visitExpression(elm.operand!!, state)
        return dateFrom(operand)
    }

    public override fun visitDateTimeComponentFrom(
        elm: DateTimeComponentFrom,
        state: State?,
    ): Any? {
        val operand = visitExpression(elm.operand!!, state)
        val precision = elm.precision!!.value()
        return dateTimeComponentFrom(operand, precision)
    }

    public override fun visitDateTime(elm: DateTime, state: State?): Any? {
        val year = if (elm.year == null) null else visitExpression(elm.year!!, state) as Int?
        val month = if (elm.month == null) null else visitExpression(elm.month!!, state) as Int?
        val day = if (elm.day == null) null else visitExpression(elm.day!!, state) as Int?
        val hour = if (elm.hour == null) null else visitExpression(elm.hour!!, state) as Int?
        val minute = if (elm.minute == null) null else visitExpression(elm.minute!!, state) as Int?
        val second = if (elm.second == null) null else visitExpression(elm.second!!, state) as Int?
        val milliSecond =
            if (elm.millisecond == null) null else visitExpression(elm.millisecond!!, state) as Int?
        val timeZoneOffset =
            (if (elm.timezoneOffset == null)
                TemporalHelper.zoneToOffset(
                    state!!.evaluationDateTime!!.zoneOffset!!
                ) // Previously, we relied on null to trigger DateTime instantiation off the default
            // TimeZone
            // Now, we compute the Offset explicitly from the State evaluation time.
            else visitExpression(elm.timezoneOffset!!, state) as BigDecimal?)
        return internalEvaluate(year, month, day, hour, minute, second, milliSecond, timeZoneOffset)
    }

    /*
     * Deprecated, use Descendants
     *
     * CQL 1.5.3 corrected the spelling to Descendants
     *
     * @deprecated since 3.28.0
     */
    @Deprecated("")
    public override fun visitDescendents(elm: Descendents, state: State?): Any? {
        val source = visitExpression(elm.source!!, state)
        return descendents(source)
    }

    public override fun visitDescendants(elm: Descendants, state: State?): Any? {
        val source = visitExpression(elm.source!!, state)
        return descendents(source)
    }

    public override fun visitDifferenceBetween(elm: DifferenceBetween, state: State?): Any? {
        val left = visitExpression(elm.operand.get(0), state)
        val right = visitExpression(elm.operand.get(1), state)

        val precision = elm.precision!!.value()
        return difference(left, right, Precision.fromString(precision))
    }

    public override fun visitDurationBetween(elm: DurationBetween, state: State?): Any? {
        val left = visitExpression(elm.operand.get(0), state)
        val right = visitExpression(elm.operand.get(1), state)

        val precision = elm.precision!!.value()
        return duration(left, right, Precision.fromString(precision))
    }

    public override fun visitEnd(elm: End, state: State?): Any? {
        val operand = visitExpression(elm.operand!!, state)
        return end(operand)
    }

    public override fun visitEnds(elm: Ends, state: State?): Any? {
        val left = visitExpression(elm.operand.get(0), state)
        val right = visitExpression(elm.operand.get(1), state)

        val precision = if (elm.precision == null) null else elm.precision!!.value()
        return ends(left, right, precision, state)
    }

    public override fun visitEndsWith(elm: EndsWith, state: State?): Any? {
        val argument = visitExpression(elm.operand.get(0), state) as String?
        val suffix = visitExpression(elm.operand.get(1), state) as String?
        return endsWith(argument, suffix)
    }

    public override fun visitEqual(elm: Equal, state: State?): Any? {
        val left = visitExpression(elm.operand.get(0), state)
        val right = visitExpression(elm.operand.get(1), state)

        return equal(left, right, state)
    }

    public override fun visitEquivalent(elm: Equivalent, state: State?): Any? {
        val left = visitExpression(elm.operand.get(0), state)
        val right = visitExpression(elm.operand.get(1), state)

        return equivalent(left, right, state)
    }

    public override fun visitExcept(elm: Except, state: State?): Any? {
        val left = visitExpression(elm.operand.get(0), state)
        val right = visitExpression(elm.operand.get(1), state)
        return except(left, right, state)
    }

    public override fun visitExists(elm: Exists, state: State?): Any? {
        val operand = visitExpression(elm.operand!!, state)
        return exists(operand)
    }

    public override fun visitExpand(elm: Expand, state: State?): Any? {
        val listOrInterval = visitExpression(elm.operand.get(0), state)
        val per = visitExpression(elm.operand.get(1), state) as Quantity?
        return expand(listOrInterval, per, state)
    }

    public override fun visitExpandValueSet(elm: ExpandValueSet, state: State?): Any? {
        val operand = visitExpression(elm.operand!!, state)
        return expand(operand, state)
    }

    public override fun visitExp(elm: Exp, state: State?): Any? {
        val operand = visitExpression(elm.operand!!, state)
        return exp(operand)
    }

    public override fun visitFilter(elm: Filter, state: State?): Any? {
        val source = visitExpression(elm.source!!, state)
        val condition = visitExpression(elm.condition!!, state)
        return filter(elm, source, condition, state)
    }

    public override fun visitFirst(elm: First, state: State?): Any? {
        val source = visitExpression(elm.source!!, state)
        return first(source)
    }

    public override fun visitFlatten(elm: Flatten, state: State?): Any? {
        val operand = visitExpression(elm.operand!!, state)
        return flatten(operand)
    }

    public override fun visitFloor(elm: Floor, state: State?): Any? {
        val operand = visitExpression(elm.operand!!, state)
        return floor(operand)
    }

    public override fun visitForEach(elm: ForEach, state: State?): Any? {
        val source = visitExpression(elm.source!!, state)
        val element = visitExpression(elm.element!!, state)
        return forEach(source, element, state)
    }

    public override fun visitGeometricMean(elm: GeometricMean, state: State?): Any? {
        val source = visitExpression(elm.source!!, state) as Iterable<*>?
        return geometricMean(source, state)
    }

    public override fun visitHighBoundary(elm: HighBoundary, state: State?): Any? {
        val input = visitExpression(elm.operand.get(0), state)
        val precision = visitExpression(elm.operand.get(1), state)
        return highBoundary(input, precision)
    }

    public override fun visitIdentifierRef(elm: IdentifierRef, state: State?): Any? {
        return IdentifierRefEvaluator.internalEvaluate(elm.name, state)
    }

    public override fun visitIf(elm: If, state: State?): Any? {
        return IfEvaluator.internalEvaluate(elm, state, this)
    }

    public override fun visitImplies(elm: Implies, state: State?): Any? {
        val left = visitExpression(elm.operand.get(0), state) as Boolean?
        val right = visitExpression(elm.operand.get(1), state) as Boolean?
        return implies(left, right)
    }

    public override fun visitIncludedIn(elm: IncludedIn, state: State?): Any? {
        val left = visitExpression(elm.operand.get(0), state)
        val right = visitExpression(elm.operand.get(1), state)
        val precision = if (elm.precision != null) elm.precision!!.value() else null
        return IncludedInEvaluator.internalEvaluate(left, right, precision, state)
    }

    public override fun visitIncludes(elm: Includes, state: State?): Any? {
        val left = visitExpression(elm.operand.get(0), state)
        val right = visitExpression(elm.operand.get(1), state)
        val precision = if (elm.precision != null) elm.precision!!.value() else null
        return IncludesEvaluator.internalEvaluate(left, right, precision, state)
    }

    public override fun visitIndexOf(elm: IndexOf, state: State?): Any? {
        val source = visitExpression(elm.source!!, state)
        val element = visitExpression(elm.element!!, state)
        return indexOf(source, element, state)
    }

    public override fun visitIndexer(elm: Indexer, state: State?): Any? {
        val left = visitExpression(elm.operand.get(0), state)
        val right = visitExpression(elm.operand.get(1), state)
        return indexer(left, right)
    }

    public override fun visitIn(elm: In, state: State?): Any? {
        val left = visitExpression(elm.operand.get(0), state)
        val right = visitExpression(elm.operand.get(1), state)
        val precision = if (elm.precision != null) elm.precision!!.value() else null
        return InEvaluator.internalEvaluate(left, right, precision, state)
    }

    public override fun visitInstance(elm: Instance, state: State?): Any? {
        return InstanceEvaluator.internalEvaluate(elm, state, this)
    }

    public override fun visitIntersect(elm: Intersect, state: State?): Any? {
        val left = visitExpression(elm.operand.get(0), state)
        val right = visitExpression(elm.operand.get(1), state)
        return intersect(left, right, state)
    }

    public override fun visitInterval(elm: org.hl7.elm.r1.Interval, state: State?): Any? {
        return IntervalEvaluator.internalEvaluate(elm, state, this)
    }

    public override fun visitIs(elm: Is, state: State?): Any? {
        val operand = visitExpression(elm.operand!!, state)
        return internalEvaluate(elm, operand, state)
    }

    public override fun visitIsFalse(elm: IsFalse, state: State?): Any? {
        val operand = visitExpression(elm.operand!!, state) as Boolean?
        return IsFalseEvaluator.isFalse(operand)
    }

    public override fun visitIsNull(elm: IsNull, state: State?): Any? {
        val operand = visitExpression(elm.operand!!, state)
        return isNull(operand)
    }

    public override fun visitIsTrue(elm: IsTrue, state: State?): Any? {
        val operand = visitExpression(elm.operand!!, state) as Boolean?
        return IsTrueEvaluator.isTrue(operand)
    }

    public override fun visitLast(elm: Last, state: State?): Any? {
        val source = visitExpression(elm.source!!, state)
        return last(source)
    }

    public override fun visitLastPositionOf(elm: LastPositionOf, state: State?): Any? {
        val string = visitExpression(elm.string!!, state)
        val pattern = visitExpression(elm.pattern!!, state)
        return lastPositionOf(string, pattern)
    }

    public override fun visitLength(elm: Length, state: State?): Any? {
        val operand = visitExpression(elm.operand!!, state)
        return internalEvaluate(operand, elm, state)
    }

    public override fun visitLess(elm: Less, state: State?): Any? {
        val left = visitExpression(elm.operand.get(0), state)
        val right = visitExpression(elm.operand.get(1), state)
        return less(left, right, state)
    }

    public override fun visitLessOrEqual(elm: LessOrEqual, state: State?): Any? {
        val left = visitExpression(elm.operand.get(0), state)
        val right = visitExpression(elm.operand.get(1), state)

        return lessOrEqual(left, right, state)
    }

    public override fun visitLiteral(literal: Literal, state: State?): Any? {
        return LiteralEvaluator.internalEvaluate(literal.valueType!!, literal.value!!, state)
    }

    public override fun visitList(elm: List, state: State?): Any? {
        return ListEvaluator.internalEvaluate(elm, state, this)
    }

    public override fun visitLn(elm: Ln, state: State?): Any? {
        val operand = visitExpression(elm.operand!!, state)
        return ln(operand)
    }

    public override fun visitLog(elm: Log, state: State?): Any? {
        val left = visitExpression(elm.operand.get(0), state)
        val right = visitExpression(elm.operand.get(1), state)
        return log(left, right)
    }

    public override fun visitLowBoundary(elm: LowBoundary, state: State?): Any? {
        val input = visitExpression(elm.operand.get(0), state)
        val precision = visitExpression(elm.operand.get(1), state)
        return lowBoundary(input, precision)
    }

    public override fun visitLower(elm: Lower, state: State?): Any? {
        val operand = visitExpression(elm.operand!!, state)
        return lower(operand)
    }

    public override fun visitMatches(elm: Matches, state: State?): Any? {
        val argument = visitExpression(elm.operand.get(0), state) as String?
        val pattern = visitExpression(elm.operand.get(1), state) as String?
        return matches(argument, pattern)
    }

    public override fun visitMax(elm: Max, state: State?): Any? {
        val source = visitExpression(elm.source!!, state)
        return max(source, state)
    }

    public override fun visitMaxValue(elm: MaxValue, state: State?): Any? {
        return MaxValueEvaluator.internalEvaluate(elm.valueType!!, state)
    }

    public override fun visitMessage(elm: Message, state: State?): Any? {
        return MessageEvaluator.internalEvaluate(elm, state, this)
    }

    public override fun visitMin(elm: Min, state: State?): Any? {
        val source = visitExpression(elm.source!!, state)
        return min(source, state)
    }

    public override fun visitMinValue(elm: MinValue, state: State?): Any? {
        return MinValueEvaluator.internalEvaluate(elm.valueType!!, state)
    }

    public override fun visitMode(elm: Mode, state: State?): Any? {
        val source = visitExpression(elm.source!!, state)
        return mode(source, state)
    }

    public override fun visitModulo(elm: Modulo, state: State?): Any? {
        val left = visitExpression(elm.operand.get(0), state)
        val right = visitExpression(elm.operand.get(1), state)
        return modulo(left, right)
    }

    public override fun visitMultiply(elm: Multiply, state: State?): Any? {
        val left = visitExpression(elm.operand.get(0), state)
        val right = visitExpression(elm.operand.get(1), state)

        return multiply(left, right)
    }

    public override fun visitNegate(elm: Negate, state: State?): Any? {
        return NegateEvaluator.internalEvaluate(elm.operand!!, state, this)
    }

    public override fun visitNotEqual(elm: NotEqual, state: State?): Any? {
        val left = visitExpression(elm.operand.get(0), state)
        val right = visitExpression(elm.operand.get(1), state)
        return notEqual(left, right, state)
    }

    public override fun visitNot(elm: Not, state: State?): Any? {
        val operand = visitExpression(elm.operand!!, state)
        return not(operand)
    }

    public override fun visitNow(elm: Now, state: State?): Any? {
        return NowEvaluator.internalEvaluate(state)
    }

    public override fun visitNull(elm: Null, state: State?): Any? {
        return NullEvaluator.internalEvaluate(state)
    }

    public override fun visitOperandRef(elm: OperandRef, state: State?): Any? {
        return OperandRefEvaluator.internalEvaluate(elm, state, this)
    }

    public override fun visitOr(elm: Or, state: State?): Any? {
        val left = visitExpression(elm.operand.get(0), state)
        val right = visitExpression(elm.operand.get(1), state)
        return or(left, right)
    }

    public override fun visitOverlapsAfter(elm: OverlapsAfter, state: State?): Any? {
        val left = visitExpression(elm.operand.get(0), state)
        val right = visitExpression(elm.operand.get(1), state)
        val precision = if (elm.precision == null) null else elm.precision!!.value()
        return overlapsAfter(left, right, precision, state)
    }

    public override fun visitOverlapsBefore(elm: OverlapsBefore, state: State?): Any? {
        val left = visitExpression(elm.operand.get(0), state)
        val right = visitExpression(elm.operand.get(1), state)
        val precision = if (elm.precision == null) null else elm.precision!!.value()
        return overlapsBefore(left, right, precision, state)
    }

    public override fun visitOverlaps(elm: Overlaps, state: State?): Any? {
        val left = visitExpression(elm.operand.get(0), state)
        val right = visitExpression(elm.operand.get(1), state)
        val precision = if (elm.precision == null) null else elm.precision!!.value()
        return overlaps(left, right, precision, state)
    }

    public override fun visitParameterRef(elm: ParameterRef, state: State?): Any? {
        return ParameterRefEvaluator.internalEvaluate(elm, state, this)
    }

    public override fun visitPointFrom(elm: PointFrom, state: State?): Any? {
        val operand = visitExpression(elm.operand!!, state)
        return pointFrom(operand, state)
    }

    public override fun visitPopulationStdDev(elm: PopulationStdDev, state: State?): Any? {
        val source = visitExpression(elm.source!!, state)
        return popStdDev(source, state)
    }

    public override fun visitPopulationVariance(elm: PopulationVariance, state: State?): Any? {
        val source = visitExpression(elm.source!!, state)
        return popVariance(source, state)
    }

    public override fun visitPositionOf(elm: PositionOf, state: State?): Any? {
        val pattern = visitExpression(elm.pattern!!, state)
        val string = visitExpression(elm.string!!, state)
        return positionOf(pattern, string)
    }

    public override fun visitPower(elm: Power, state: State?): Any? {
        val left = visitExpression(elm.operand.get(0), state)
        val right = visitExpression(elm.operand.get(1), state)
        return power(left, right)
    }

    public override fun visitPrecision(elm: org.hl7.elm.r1.Precision, state: State?): Any? {
        val argument = visitExpression(elm.operand!!, state)
        return precision(argument)
    }

    public override fun visitPredecessor(elm: Predecessor, state: State?): Any? {
        val argument = visitExpression(elm.operand!!, state)
        return predecessor(argument)
    }

    public override fun visitProduct(elm: Product, state: State?): Any? {
        val source = visitExpression(elm.source!!, state)
        return product(source)
    }

    public override fun visitProperContains(elm: ProperContains, state: State?): Any? {
        val left = visitExpression(elm.operand.get(0), state)
        val right = visitExpression(elm.operand.get(1), state)
        val precision = if (elm.precision != null) elm.precision!!.value() else null
        return properContains(left, right, precision, state)
    }

    public override fun visitProperIncludedIn(elm: ProperIncludedIn, state: State?): Any? {
        val left = visitExpression(elm.operand.get(0), state)
        val right = visitExpression(elm.operand.get(1), state)
        val precision = if (elm.precision != null) elm.precision!!.value() else null
        return properlyIncludedIn(left, right, precision, state)
    }

    public override fun visitProperIncludes(elm: ProperIncludes, state: State?): Any? {
        val left = visitExpression(elm.operand.get(0), state)
        val right = visitExpression(elm.operand.get(1), state)
        val precision = if (elm.precision != null) elm.precision!!.value() else null
        return properlyIncludes(left, right, precision, state)
    }

    public override fun visitProperIn(elm: ProperIn, state: State?): Any? {
        val left = visitExpression(elm.operand.get(0), state)
        val right = visitExpression(elm.operand.get(1), state)
        val precision = if (elm.precision != null) elm.precision!!.value() else null
        return ProperInEvaluator.internalEvaluate(left, right, precision, state)
    }

    public override fun visitProperty(elm: Property, state: State?): Any? {
        return PropertyEvaluator.internalEvaluate(elm, state, this)
    }

    public override fun visitQuantity(elm: org.hl7.elm.r1.Quantity, state: State?): Any? {
        return internalEvaluate(elm, state)
    }

    public override fun visitRound(elm: Round, state: State?): Any? {
        val operand = visitExpression(elm.operand!!, state)
        val precision = if (elm.precision == null) null else visitExpression(elm.precision!!, state)
        return round(operand, precision)
    }

    public override fun visitRetrieve(elm: Retrieve, state: State?): Any? {
        return RetrieveEvaluator.internalEvaluate(elm, state, this)
    }

    public override fun visitReplaceMatches(elm: ReplaceMatches, state: State?): Any? {
        val argument = visitExpression(elm.operand.get(0), state) as String?
        val pattern = visitExpression(elm.operand.get(1), state) as String?
        val substitution = visitExpression(elm.operand.get(2), state) as String?
        return replaceMatches(argument, pattern, substitution)
    }

    public override fun visitRepeat(elm: Repeat, state: State?): Any? {
        val source = visitExpression(elm.source!!, state)
        val element = visitExpression(elm.element!!, state)
        val scope = elm.scope
        return RepeatEvaluator.internalEvaluate(source, element, scope, state)
    }

    public override fun visitRatio(elm: Ratio, state: State?): Any? {
        return RatioEvaluator.internalEvaluate(elm, state, this)
    }

    public override fun visitQueryLetRef(elm: QueryLetRef, state: State?): Any? {
        return internalEvaluate(elm, state)
    }

    public override fun visitQuery(elm: Query, state: State?): Any? {
        return QueryEvaluator.internalEvaluate(elm, state, this)
    }

    override fun defaultResult(elm: Element, context: State?): Any? {
        return null
    }
}
