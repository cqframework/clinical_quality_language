package org.opencds.cqf.cql.engine.execution

import org.cqframework.cql.cql2elm.tracking.Trackable.resultType
import org.cqframework.cql.elm.visiting.BaseElmLibraryVisitor
import org.hl7.cql.model.IntervalType
import org.hl7.cql.model.ListType
import org.hl7.elm.r1.*
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
import org.opencds.cqf.cql.engine.elm.executing.UnionEvaluator.union
import org.opencds.cqf.cql.engine.elm.executing.UnionEvaluator.unionInterval
import org.opencds.cqf.cql.engine.elm.executing.UnionEvaluator.unionIterable
import org.opencds.cqf.cql.engine.elm.executing.UpperEvaluator.upper
import org.opencds.cqf.cql.engine.elm.executing.ValueSetRefEvaluator.internalEvaluate
import org.opencds.cqf.cql.engine.elm.executing.VarianceEvaluator.variance
import org.opencds.cqf.cql.engine.elm.executing.WidthEvaluator.width
import org.opencds.cqf.cql.engine.elm.executing.XorEvaluator.xor
import org.opencds.cqf.cql.engine.exception.Backtrace
import org.opencds.cqf.cql.engine.exception.CqlException
import org.opencds.cqf.cql.engine.exception.Severity
import org.opencds.cqf.cql.engine.runtime.Boolean
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.List
import org.opencds.cqf.cql.engine.runtime.Precision
import org.opencds.cqf.cql.engine.runtime.String
import org.opencds.cqf.cql.engine.runtime.TemporalHelper
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.toCqlDecimal
import org.opencds.cqf.cql.engine.runtime.toCqlList

class EvaluationVisitor : BaseElmLibraryVisitor<Value?, State?>() {
    override fun visitExpression(elm: Expression, context: State?): Value? {
        context?.markElementAsVisitedForCoverageReport(elm)

        // Capture the non-null state for detailed tracing to allow smart-cast
        val detailedState =
            if (
                context != null &&
                    context.engineOptions.contains(CqlEngine.Options.EnableDetailedTracing) &&
                    !context.isFilteredExpression(elm)
            )
                context
            else null

        if (detailedState != null) {
            detailedState.pushSubExpressionFrame(elm)
        }

        try {
            val value = super.visitExpression(elm, context)
            context?.checkType(elm, value)

            if (detailedState != null) {
                detailedState.storeSubExpressionResult(value)
            }
            return value
        } catch (e: CqlException) {
            maybeExtendBacktrace(e, context!!, elm)
            throw e
        } catch (e: Exception) {
            val exception =
                CqlException(
                    e.message,
                    e,
                    fromNode(elm, context!!.getCurrentLibrary()),
                    Severity.ERROR,
                )
            maybeExtendBacktrace(exception, context, elm)
            throw exception
        } finally {
            if (detailedState != null) {
                detailedState.popSubExpressionFrame()
            }
        }
    }

    /** Builds a backtrace for the exception if it does not already have one. */
    private fun maybeExtendBacktrace(
        exception: CqlException,
        context: State,
        expression: Expression,
    ) {
        if (exception.backtrace == null) {
            exception.backtrace =
                Backtrace.fromActivationFrames(context.stack, expression, context.contextValues)
        }
    }

    override fun visitExpressionDef(elm: ExpressionDef, context: State?): Value? {
        return internalEvaluate(elm, context, this)
    }

    override fun visitExpressionRef(elm: ExpressionRef, context: State?): Value? {
        return ExpressionRefEvaluator.internalEvaluate(elm, context, this)
    }

    override fun visitFunctionRef(elm: FunctionRef, context: State?): Value? {
        return FunctionRefEvaluator.internalEvaluate(elm, context, this)
    }

    override fun visitAdd(elm: Add, context: State?): Value? {
        val left = visitExpression(elm.operand[0], context)
        val right = visitExpression(elm.operand[1], context)

        return add(left, right, context)
    }

    override fun visitAbs(elm: Abs, context: State?): Value? {
        val operand = visitExpression(elm.operand!!, context)
        return abs(operand)
    }

    override fun visitAfter(elm: After, context: State?): Value? {
        val left = visitExpression(elm.operand[0], context)
        val right = visitExpression(elm.operand[1], context)
        val precision = if (elm.precision == null) null else elm.precision!!.value()

        return after(left, right, precision, context)
    }

    override fun visitAliasRef(elm: AliasRef, context: State?): Value? {
        return AliasRefEvaluator.internalEvaluate(elm.name, context)
    }

    override fun visitAllTrue(elm: AllTrue, context: State?): Value {
        val src = visitExpression(elm.source!!, context)
        return allTrue(src)
    }

    override fun visitAnd(elm: And, context: State?): Value? {
        val left = visitExpression(elm.operand[0], context)
        val right = visitExpression(elm.operand[1], context)

        return and(left, right)
    }

    override fun visitAnyInCodeSystem(elm: AnyInCodeSystem, context: State?): Value? {
        val codes = visitExpression(elm.codes!!, context)
        val codeSystem =
            if (elm.codesystem != null) {
                CodeSystemRefEvaluator.toCodeSystem(elm.codesystem, context)
            } else {
                visitExpression(elm.codesystemExpression!!, context)
            }

        return internalEvaluate(codes, elm.codesystem, codeSystem, context)
    }

    override fun visitInCodeSystem(elm: InCodeSystem, context: State?): Value? {
        val code = visitExpression(elm.code!!, context)
        var cs: Value? = null
        if (elm.codesystem != null) {
            cs = CodeSystemRefEvaluator.toCodeSystem(elm.codesystem!!, context)
        } else if (elm.codesystemExpression != null) {
            cs = visitExpression(elm.codesystemExpression!!, context)
        }

        return inCodeSystem(code, cs, context)
    }

    override fun visitAnyInValueSet(elm: AnyInValueSet, context: State?): Value? {
        val codes = visitExpression(elm.codes!!, context)
        val valueSet =
            if (elm.valueset != null) {
                ValueSetRefEvaluator.toValueSet(context, elm.valueset!!)
            } else {
                visitExpression(elm.valuesetExpression!!, context)
            }

        return internalEvaluate(codes, elm.valueset, valueSet, context)
    }

    override fun visitInValueSet(elm: InValueSet, context: State?): Value? {
        val code = visitExpression(elm.code!!, context)
        var vs: Value? = null
        if (elm.valueset != null) {
            vs = ValueSetRefEvaluator.toValueSet(context, elm.valueset!!)
        } else if (elm.valuesetExpression != null) {
            vs = visitExpression(elm.valuesetExpression!!, context)
        }
        return inValueSet(code, vs, context)
    }

    override fun visitValueSetRef(elm: ValueSetRef, context: State?): Value {
        return internalEvaluate(context, elm)
    }

    override fun visitXor(elm: Xor, context: State?): Value? {
        val left = visitExpression(elm.operand[0], context)
        val right = visitExpression(elm.operand[1], context)
        return xor(left, right)
    }

    override fun visitWidth(elm: Width, context: State?): Value? {
        val operand = visitExpression(elm.operand!!, context)
        return width(operand, context)
    }

    override fun visitVariance(elm: Variance, context: State?): Value? {
        val source = visitExpression(elm.source!!, context)
        return variance(source, context)
    }

    override fun visitAvg(elm: Avg, context: State?): Value? {
        val src = visitExpression(elm.source!!, context)
        return avg(src, context)
    }

    override fun visitDivide(elm: Divide, context: State?): Value? {
        val left = visitExpression(elm.operand[0], context)
        val right = visitExpression(elm.operand[1], context)
        return divide(left, right, context)
    }

    override fun visitUpper(elm: Upper, context: State?): Value? {
        val operand = visitExpression(elm.operand!!, context)
        return upper(operand)
    }

    override fun visitUnion(elm: Union, context: State?): Value? {
        val left = elm.operand[0]
        val right = elm.operand[1]
        val leftResult = visitExpression(left, context)
        val rightResult = visitExpression(right, context)

        // Attempt to use resultTypes if present. This is needed because
        // null as List union null as List returns an empty list, but
        // null as Interval union null as Interval returns null.
        // Fixing solely in the engine requires type metadata for
        // the results to be available at runtime
        // (e.g. stored in a header, table, wrapper class, etc.)
        val leftResultType = left.resultType
        val rightResultType = right.resultType
        val elmResultType = elm.resultType
        return if (
            leftResultType is ListType || rightResultType is ListType || elmResultType is ListType
        ) {
            unionIterable(leftResult as List?, rightResult as List?, context)
        } else if (
            leftResultType is IntervalType ||
                rightResultType is IntervalType ||
                elmResultType is IntervalType
        ) {
            unionInterval(leftResult as Interval?, rightResult as Interval?, context)
        } else {
            union(leftResult, rightResult, context)
        }
    }

    override fun visitGreater(elm: Greater, context: State?): Value? {
        val left = visitExpression(elm.operand[0], context)
        val right = visitExpression(elm.operand[1], context)

        return greater(left, right, context)
    }

    override fun visitMeets(elm: Meets, context: State?): Value? {
        val left = visitExpression(elm.operand[0], context)
        val right = visitExpression(elm.operand[1], context)
        val precision = if (elm.precision == null) null else elm.precision!!.value()

        return meets(left, right, precision, context)
    }

    override fun visitDistinct(elm: Distinct, context: State?): Value? {
        val value = visitExpression(elm.operand!!, context)
        return distinct(value, context)
    }

    override fun visitMeetsAfter(elm: MeetsAfter, context: State?): Value? {
        val left = visitExpression(elm.operand[0], context)
        val right = visitExpression(elm.operand[1], context)
        val precision = if (elm.precision == null) null else elm.precision!!.value()

        return meetsAfter(left, right, precision, context)
    }

    // SameAs
    override fun visitMeetsBefore(elm: MeetsBefore, context: State?): Value? {
        val left = visitExpression(elm.operand[0], context)
        val right = visitExpression(elm.operand[1], context)
        val precision = if (elm.precision == null) null else elm.precision!!.value()

        return meetsBefore(left, right, precision, context)
    }

    override fun visitSameAs(elm: SameAs, context: State?): Value? {
        val left = visitExpression(elm.operand[0], context)
        val right = visitExpression(elm.operand[1], context)
        val precision = if (elm.precision == null) null else elm.precision!!.value()

        return sameAs(left, right, precision, context)
    }

    override fun visitSameOrAfter(elm: SameOrAfter, context: State?): Value? {
        val left = visitExpression(elm.operand[0], context)
        val right = visitExpression(elm.operand[1], context)
        val precision = if (elm.precision == null) null else elm.precision!!.value()

        return sameOrAfter(left, right, precision, context)
    }

    override fun visitSameOrBefore(elm: SameOrBefore, context: State?): Value? {
        val left = visitExpression(elm.operand[0], context)
        val right = visitExpression(elm.operand[1], context)
        val precision = if (elm.precision == null) null else elm.precision!!.value()

        return sameOrBefore(left, right, precision, context)
    }

    override fun visitGreaterOrEqual(elm: GreaterOrEqual, context: State?): Value? {
        val left = visitExpression(elm.operand[0], context)
        val right = visitExpression(elm.operand[1], context)

        return greaterOrEqual(left, right, context)
    }

    override fun visitSingletonFrom(elm: SingletonFrom, context: State?): Value? {
        val operand = visitExpression(elm.operand!!, context)
        return singletonFrom(operand)
    }

    override fun visitSize(elm: Size, context: State?): Value? {
        val argument = visitExpression(elm.operand!!, context)
        return size(argument, context)
    }

    override fun visitSlice(elm: Slice, context: State?): Value? {
        val source = visitExpression(elm.source!!, context)
        val start = visitExpression(elm.startIndex!!, context)
        val end = if (elm.endIndex == null) null else visitExpression(elm.endIndex!!, context)

        return slice(source, start, end)
    }

    override fun visitSplit(elm: Split, context: State?): Value? {
        val stringToSplit = visitExpression(elm.stringToSplit!!, context)
        val separator = visitExpression(elm.separator!!, context)

        return split(stringToSplit, separator)
    }

    override fun visitSplitOnMatches(elm: SplitOnMatches, context: State?): Value? {
        val stringToSplit = visitExpression(elm.stringToSplit!!, context)
        val separator = visitExpression(elm.separatorPattern!!, context)

        return splitOnMatches(stringToSplit, separator)
    }

    override fun visitStart(elm: Start, context: State?): Value? {
        val operand = visitExpression(elm.operand!!, context)
        return start(operand)
    }

    override fun visitStarts(elm: Starts, context: State?): Value? {
        val left = visitExpression(elm.operand[0], context)
        val right = visitExpression(elm.operand[1], context)
        val precision = if (elm.precision == null) null else elm.precision!!.value()

        return starts(left, right, precision, context)
    }

    override fun visitStartsWith(elm: StartsWith, context: State?): Value? {
        val argument = visitExpression(elm.operand[0], context)
        val prefix = visitExpression(elm.operand[1], context)

        return startsWith(argument, prefix)
    }

    override fun visitStdDev(elm: StdDev, context: State?): Value? {
        val source = visitExpression(elm.source!!, context)
        return stdDev(source, context)
    }

    override fun visitSubstring(elm: Substring, context: State?): Value? {
        val stringValue = visitExpression(elm.stringToSub!!, context)
        val startIndexValue = visitExpression(elm.startIndex!!, context)
        val lengthValue = if (elm.length == null) null else visitExpression(elm.length!!, context)

        return substring(stringValue, startIndexValue, lengthValue)
    }

    override fun visitSubtract(elm: Subtract, context: State?): Value? {
        val left = visitExpression(elm.operand[0], context)
        val right = visitExpression(elm.operand[1], context)
        return subtract(left, right, context)
    }

    override fun visitSuccessor(elm: Successor, context: State?): Value? {
        val value = visitExpression(elm.operand!!, context)
        return successor(value)
    }

    override fun visitSum(elm: Sum, context: State?): Value? {
        val source = visitExpression(elm.source!!, context)
        return sum(source, context)
    }

    override fun visitTime(elm: Time, context: State?): Value? {
        if (elm.hour == null) {
            return null
        }

        val hour = visitExpression(elm.hour!!, context)
        val minute = if (elm.minute == null) null else visitExpression(elm.minute!!, context)
        val second = if (elm.second == null) null else visitExpression(elm.second!!, context)
        val millisecond =
            if (elm.millisecond == null) null else visitExpression(elm.millisecond!!, context)

        return time(hour, minute, second, millisecond)
    }

    override fun visitTimeFrom(elm: TimeFrom, context: State?): Value? {
        val operand = visitExpression(elm.operand!!, context)
        return timeFrom(operand)
    }

    override fun visitTimeOfDay(elm: TimeOfDay, context: State?): Value? {
        return TimeOfDayEvaluator.internalEvaluate(context)
    }

    override fun visitTimezoneFrom(elm: TimezoneFrom, context: State?): Value? {
        val operand = visitExpression(elm.operand!!, context)
        return internalEvaluate(operand)
    }

    override fun visitTimezoneOffsetFrom(elm: TimezoneOffsetFrom, context: State?): Value? {
        val operand = visitExpression(elm.operand!!, context)
        return timezoneOffsetFrom(operand)
    }

    override fun visitToBoolean(elm: ToBoolean, context: State?): Value? {
        val operand = visitExpression(elm.operand!!, context)
        return toBoolean(operand)
    }

    override fun visitToConcept(elm: ToConcept, context: State?): Value? {
        val operand = visitExpression(elm.operand!!, context)
        return toConcept(operand)
    }

    override fun visitToChars(elm: ToChars, context: State?): Value? {
        val operand = visitExpression(elm.operand!!, context)
        return toChars(operand)
    }

    override fun visitToDate(elm: ToDate, context: State?): Value? {
        val operand = visitExpression(elm.operand!!, context)
        return toDate(operand)
    }

    override fun visitToDateTime(elm: ToDateTime, context: State?): Value? {
        val operand = visitExpression(elm.operand!!, context)
        return toDateTime(operand, context)
    }

    override fun visitToday(elm: Today, context: State?): Value? {
        return today(context)
    }

    override fun visitToDecimal(elm: ToDecimal, context: State?): Value? {
        val operand = visitExpression(elm.operand!!, context)
        return toDecimal(operand)
    }

    override fun visitToInteger(elm: ToInteger, context: State?): Value? {
        val operand = visitExpression(elm.operand!!, context)
        return toInteger(operand)
    }

    override fun visitToList(elm: ToList, context: State?): Value {
        val operand = visitExpression(elm.operand!!, context)
        return toList(operand)
    }

    override fun visitToLong(elm: ToLong, context: State?): Value? {
        val operand = visitExpression(elm.operand!!, context)
        return toLong(operand)
    }

    override fun visitToQuantity(elm: ToQuantity, context: State?): Value? {
        val operand = visitExpression(elm.operand!!, context)
        return toQuantity(operand, context)
    }

    override fun visitToRatio(elm: ToRatio, context: State?): Value? {
        val operand = visitExpression(elm.operand!!, context)
        return toRatio(operand)
    }

    override fun visitToString(elm: ToString, context: State?): Value? {
        val operand = visitExpression(elm.operand!!, context)
        return toString(operand)
    }

    override fun visitToTime(elm: ToTime, context: State?): Value? {
        val operand = visitExpression(elm.operand!!, context)
        return toTime(operand)
    }

    override fun visitTruncatedDivide(elm: TruncatedDivide, context: State?): Value? {
        val left = visitExpression(elm.operand[0], context)
        val right = visitExpression(elm.operand[1], context)
        return div(left, right, context)
    }

    override fun visitMedian(elm: Median, context: State?): Value? {
        val source = visitExpression(elm.source!!, context)
        return median(source, context)
    }

    override fun visitTruncate(elm: Truncate, context: State?): Value? {
        val operand = visitExpression(elm.operand!!, context)
        return truncate(operand)
    }

    override fun visitTuple(elm: Tuple, context: State?): Value {
        val ret = mutableMapOf<kotlin.String, Value?>()
        for (element in elm.element) {
            ret[element.name!!] = visitExpression(element.value!!, context)
        }
        return TupleEvaluator.internalEvaluate(ret)
    }

    override fun visitAnyTrue(elm: AnyTrue, context: State?): Value {
        val source = visitExpression(elm.source!!, context)
        return anyTrue(source)
    }

    override fun visitAs(elm: As, context: State?): Value? {
        val operand = visitExpression(elm.operand!!, context)
        return AsEvaluator.internalEvaluate(operand, elm, elm.isStrict()!!, context)
    }

    override fun visitBefore(elm: Before, context: State?): Value? {
        val left = visitExpression(elm.operand[0], context)
        val right = visitExpression(elm.operand[1], context)
        val precision = if (elm.precision == null) null else elm.precision!!.value()

        return before(left, right, precision, context)
    }

    override fun visitCalculateAgeAt(elm: CalculateAgeAt, context: State?): Value? {
        val birthDate = visitExpression(elm.operand[0], context)
        val asOf = visitExpression(elm.operand[1], context)
        val precision = elm.precision!!.value()
        return calculateAgeAt(birthDate, asOf, precision)
    }

    override fun visitCalculateAge(elm: CalculateAge, context: State?): Value? {
        val operand = visitExpression(elm.operand!!, context)
        val precision = elm.precision!!.value()
        return internalEvaluate(operand, precision, context)
    }

    override fun visitCase(elm: Case, context: State?): Value? {
        if (elm.comparand == null) {
            for (caseItem in elm.caseItem) {
                val `when` = visitExpression(caseItem.`when`!!, context) ?: continue

                if ((`when` as Boolean).value) {
                    return visitExpression(caseItem.then!!, context)
                }
            }
            return visitElement(elm.`else`!!, context)
        } else {
            val comparand = visitExpression(elm.comparand!!, context)

            for (caseItem in elm.caseItem) {
                val `when` = visitExpression(caseItem.`when`!!, context)
                val check = equivalent(comparand, `when`, context)

                if (check.value) {
                    return visitElement(caseItem.then!!, context)
                }
            }

            return visitElement(elm.`else`!!, context)
        }
    }

    override fun visitCeiling(elm: Ceiling, context: State?): Value? {
        val operand = visitExpression(elm.operand!!, context)
        return ceiling(operand)
    }

    override fun visitChildren(elm: Children, context: State?): Value? {
        val source = visitExpression(elm.source!!, context)
        return children(source)
    }

    override fun visitCoalesce(elm: Coalesce, context: State?): Value? {
        val operands = mutableListOf<Value?>()
        for (operand in elm.operand) {
            operands.add(visitExpression(operand, context))
        }
        return coalesce(operands)
    }

    override fun visitCode(elm: Code, context: State?): Value {
        return CodeEvaluator.internalEvaluate(elm.system, elm.code, elm.display, context)
    }

    override fun visitCodeRef(elm: CodeRef, context: State?): Value {
        return toCode(elm, context)
    }

    override fun visitConcept(elm: Concept, context: State?): Value {
        val codes = mutableListOf<Value?>()
        for (i in 0..<elm.code.size) {
            codes.add(visitExpression(elm.code[i], context))
        }

        return internalEvaluate(codes.toCqlList(), elm.display)
    }

    override fun visitConceptRef(elm: ConceptRef, context: State?): Value {
        return toConcept(elm, context)
    }

    override fun visitCollapse(elm: Collapse, context: State?): Value? {
        val left = visitExpression(elm.operand[0], context)
        val right = visitExpression(elm.operand[1], context)

        return collapse(left, right, context)
    }

    override fun visitCombine(elm: Combine, context: State?): Value? {
        val source = visitExpression(elm.source!!, context)
        val separator =
            if (elm.separator == null) String.EMPTY_STRING
            else visitExpression(elm.separator!!, context)

        return combine(source, separator)
    }

    override fun visitConcatenate(elm: Concatenate, context: State?): Value? {
        val left = visitExpression(elm.operand[0], context)
        val right = visitExpression(elm.operand[1], context)

        return concatenate(left, right)
    }

    override fun visitContains(elm: Contains, context: State?): Value? {
        val left = visitExpression(elm.operand[0], context)
        val right = visitExpression(elm.operand[1], context)
        val precision = if (elm.precision == null) null else elm.precision!!.value()
        return internalEvaluate(left, right, elm.operand[0], precision, context)
    }

    override fun visitConvert(elm: Convert, context: State?): Value? {
        val operand = visitExpression(elm.operand!!, context)
        return internalEvaluate(operand, elm.toType, elm.toTypeSpecifier, context)
    }

    override fun visitConvertQuantity(elm: ConvertQuantity, context: State?): Value? {
        val argument = visitExpression(elm.operand[0], context)
        val unit = visitExpression(elm.operand[1], context)
        return convertQuantity(argument, unit, context!!.environment.libraryManager!!.ucumService)
    }

    override fun visitConvertsToBoolean(elm: ConvertsToBoolean, context: State?): Value? {
        val operand = visitExpression(elm.operand!!, context)
        return convertsToBoolean(operand)
    }

    override fun visitConvertsToDate(elm: ConvertsToDate, context: State?): Value? {
        val operand = visitExpression(elm.operand!!, context)
        return convertsToDate(operand)
    }

    override fun visitConvertsToDateTime(elm: ConvertsToDateTime, context: State?): Value? {
        val operand = visitExpression(elm.operand!!, context)
        return convertsToDateTime(operand, context!!.evaluationDateTime!!.zoneOffset)
    }

    override fun visitConvertsToDecimal(elm: ConvertsToDecimal, context: State?): Value? {
        val operand = visitExpression(elm.operand!!, context)
        return convertsToDecimal(operand)
    }

    override fun visitConvertsToInteger(elm: ConvertsToInteger, context: State?): Value? {
        val operand = visitExpression(elm.operand!!, context)
        return convertsToInteger(operand)
    }

    override fun visitConvertsToLong(elm: ConvertsToLong, context: State?): Value? {
        val operand = visitExpression(elm.operand!!, context)
        return convertsToLong(operand)
    }

    override fun visitConvertsToQuantity(elm: ConvertsToQuantity, context: State?): Value? {
        val operand = visitExpression(elm.operand!!, context)
        return convertsToQuantity(operand, context)
    }

    override fun visitConvertsToString(elm: ConvertsToString, context: State?): Value? {
        val operand = visitExpression(elm.operand!!, context)
        return convertsToString(operand)
    }

    override fun visitConvertsToTime(elm: ConvertsToTime, context: State?): Value? {
        val operand = visitExpression(elm.operand!!, context)
        return convertsToTime(operand)
    }

    override fun visitCount(elm: Count, context: State?): Value {
        val source = visitExpression(elm.source!!, context)
        return count(source)
    }

    override fun visitDate(elm: Date, context: State?): Value? {
        val year = if (elm.year == null) null else visitExpression(elm.year!!, context)
        val month = if (elm.month == null) null else visitExpression(elm.month!!, context)
        val day = if (elm.day == null) null else visitExpression(elm.day!!, context)
        return internalEvaluate(year, month, day)
    }

    override fun visitDateFrom(elm: DateFrom, context: State?): Value? {
        val operand = visitExpression(elm.operand!!, context)
        return dateFrom(operand)
    }

    override fun visitDateTimeComponentFrom(elm: DateTimeComponentFrom, context: State?): Value? {
        val operand = visitExpression(elm.operand!!, context)
        val precision = elm.precision!!.value()
        return dateTimeComponentFrom(operand, precision)
    }

    override fun visitDateTime(elm: DateTime, context: State?): Value? {
        val year = if (elm.year == null) null else visitExpression(elm.year!!, context)
        val month = if (elm.month == null) null else visitExpression(elm.month!!, context)
        val day = if (elm.day == null) null else visitExpression(elm.day!!, context)
        val hour = if (elm.hour == null) null else visitExpression(elm.hour!!, context)
        val minute = if (elm.minute == null) null else visitExpression(elm.minute!!, context)
        val second = if (elm.second == null) null else visitExpression(elm.second!!, context)
        val milliSecond =
            if (elm.millisecond == null) null else visitExpression(elm.millisecond!!, context)
        val timeZoneOffset =
            (if (elm.timezoneOffset == null)
                TemporalHelper.zoneToOffset(context!!.evaluationDateTime!!.zoneOffset)
                    .toCqlDecimal() // Previously, we relied on null to trigger DateTime
            // instantiation off the default
            // TimeZone
            // Now, we compute the Offset explicitly from the State evaluation time.
            else visitExpression(elm.timezoneOffset!!, context))
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
    override fun visitDescendents(elm: Descendents, context: State?): Value? {
        val source = visitExpression(elm.source!!, context)
        return descendents(source)
    }

    override fun visitDescendants(elm: Descendants, context: State?): Value? {
        val source = visitExpression(elm.source!!, context)
        return descendents(source)
    }

    override fun visitDifferenceBetween(elm: DifferenceBetween, context: State?): Value? {
        val left = visitExpression(elm.operand[0], context)
        val right = visitExpression(elm.operand[1], context)

        val precision = elm.precision!!.value()
        return difference(left, right, Precision.fromString(precision))
    }

    override fun visitDurationBetween(elm: DurationBetween, context: State?): Value? {
        val left = visitExpression(elm.operand[0], context)
        val right = visitExpression(elm.operand[1], context)

        val precision = elm.precision!!.value()
        return duration(left, right, Precision.fromString(precision))
    }

    override fun visitEnd(elm: End, context: State?): Value? {
        val operand = visitExpression(elm.operand!!, context)
        return end(operand)
    }

    override fun visitEnds(elm: Ends, context: State?): Value? {
        val left = visitExpression(elm.operand[0], context)
        val right = visitExpression(elm.operand[1], context)

        val precision = if (elm.precision == null) null else elm.precision!!.value()
        return ends(left, right, precision, context)
    }

    override fun visitEndsWith(elm: EndsWith, context: State?): Value? {
        val argument = visitExpression(elm.operand[0], context)
        val suffix = visitExpression(elm.operand[1], context)
        return endsWith(argument, suffix)
    }

    override fun visitEqual(elm: Equal, context: State?): Value? {
        val left = visitExpression(elm.operand[0], context)
        val right = visitExpression(elm.operand[1], context)

        return equal(left, right, context)
    }

    override fun visitEquivalent(elm: Equivalent, context: State?): Value {
        val left = visitExpression(elm.operand[0], context)
        val right = visitExpression(elm.operand[1], context)

        return equivalent(left, right, context)
    }

    override fun visitExcept(elm: Except, context: State?): Value? {
        val left = visitExpression(elm.operand[0], context)
        val right = visitExpression(elm.operand[1], context)
        return except(left, right, context)
    }

    override fun visitExists(elm: Exists, context: State?): Value {
        val operand = visitExpression(elm.operand!!, context)
        return exists(operand)
    }

    override fun visitExpand(elm: Expand, context: State?): Value? {
        val listOrInterval = visitExpression(elm.operand[0], context)
        val per = visitExpression(elm.operand[1], context)
        return expand(listOrInterval, per, context)
    }

    override fun visitExpandValueSet(elm: ExpandValueSet, context: State?): Value? {
        val operand = visitExpression(elm.operand!!, context)
        return expand(operand, context)
    }

    override fun visitExp(elm: Exp, context: State?): Value? {
        val operand = visitExpression(elm.operand!!, context)
        return exp(operand)
    }

    override fun visitFilter(elm: Filter, context: State?): Value? {
        val source = visitExpression(elm.source!!, context)
        val condition = visitExpression(elm.condition!!, context)
        return filter(elm, source, condition, context)
    }

    override fun visitFirst(elm: First, context: State?): Value? {
        val source = visitExpression(elm.source!!, context)
        return first(source)
    }

    override fun visitFlatten(elm: Flatten, context: State?): Value? {
        val operand = visitExpression(elm.operand!!, context)
        return flatten(operand)
    }

    override fun visitFloor(elm: Floor, context: State?): Value? {
        val operand = visitExpression(elm.operand!!, context)
        return floor(operand)
    }

    override fun visitForEach(elm: ForEach, context: State?): Value? {
        val source = visitExpression(elm.source!!, context)
        val element = visitExpression(elm.element!!, context)
        return forEach(source, element)
    }

    override fun visitGeometricMean(elm: GeometricMean, context: State?): Value? {
        val source = visitExpression(elm.source!!, context)
        return geometricMean(source, context)
    }

    override fun visitHighBoundary(elm: HighBoundary, context: State?): Value? {
        val input = visitExpression(elm.operand[0], context)
        val precision = visitExpression(elm.operand[1], context)
        return highBoundary(input, precision)
    }

    override fun visitIdentifierRef(elm: IdentifierRef, context: State?): Value? {
        return IdentifierRefEvaluator.internalEvaluate(elm.name, context)
    }

    override fun visitIf(elm: If, context: State?): Value? {
        return IfEvaluator.internalEvaluate(elm, context, this)
    }

    override fun visitImplies(elm: Implies, context: State?): Value? {
        val left = visitExpression(elm.operand[0], context)
        val right = visitExpression(elm.operand[1], context)
        return implies(left, right)
    }

    override fun visitIncludedIn(elm: IncludedIn, context: State?): Value? {
        val left = visitExpression(elm.operand[0], context)
        val right = visitExpression(elm.operand[1], context)
        val precision = if (elm.precision != null) elm.precision!!.value() else null
        return IncludedInEvaluator.internalEvaluate(left, right, precision, context)
    }

    override fun visitIncludes(elm: Includes, context: State?): Value? {
        val left = visitExpression(elm.operand[0], context)
        val right = visitExpression(elm.operand[1], context)
        val precision = if (elm.precision != null) elm.precision!!.value() else null
        return IncludesEvaluator.internalEvaluate(left, right, precision, context)
    }

    override fun visitIndexOf(elm: IndexOf, context: State?): Value? {
        val source = visitExpression(elm.source!!, context)
        val element = visitExpression(elm.element!!, context)
        return indexOf(source, element, context)
    }

    override fun visitIndexer(elm: Indexer, context: State?): Value? {
        val left = visitExpression(elm.operand[0], context)
        val right = visitExpression(elm.operand[1], context)
        return indexer(left, right)
    }

    override fun visitIn(elm: In, context: State?): Value? {
        val left = visitExpression(elm.operand[0], context)
        val right = visitExpression(elm.operand[1], context)
        val precision = if (elm.precision != null) elm.precision!!.value() else null
        return InEvaluator.internalEvaluate(left, right, precision, context)
    }

    override fun visitInstance(elm: Instance, context: State?): Value? {
        return InstanceEvaluator.internalEvaluate(elm, context, this)
    }

    override fun visitIntersect(elm: Intersect, context: State?): Value? {
        val left = visitExpression(elm.operand[0], context)
        val right = visitExpression(elm.operand[1], context)
        return intersect(left, right, context)
    }

    override fun visitInterval(elm: org.hl7.elm.r1.Interval, context: State?): Value? {
        return IntervalEvaluator.internalEvaluate(elm, context, this)
    }

    override fun visitIs(elm: Is, context: State?): Value? {
        val operand = visitExpression(elm.operand!!, context)
        return internalEvaluate(elm, operand, context)
    }

    override fun visitIsFalse(elm: IsFalse, context: State?): Value {
        val operand = visitExpression(elm.operand!!, context)
        return IsFalseEvaluator.isFalse(operand)
    }

    override fun visitIsNull(elm: IsNull, context: State?): Value {
        val operand = visitExpression(elm.operand!!, context)
        return isNull(operand)
    }

    override fun visitIsTrue(elm: IsTrue, context: State?): Value {
        val operand = visitExpression(elm.operand!!, context)
        return IsTrueEvaluator.isTrue(operand)
    }

    override fun visitLast(elm: Last, context: State?): Value? {
        val source = visitExpression(elm.source!!, context)
        return last(source)
    }

    override fun visitLastPositionOf(elm: LastPositionOf, context: State?): Value? {
        val string = visitExpression(elm.string!!, context)
        val pattern = visitExpression(elm.pattern!!, context)
        return lastPositionOf(string, pattern)
    }

    override fun visitLength(elm: Length, context: State?): Value? {
        val operand = visitExpression(elm.operand!!, context)
        return internalEvaluate(operand, elm, context)
    }

    override fun visitLess(elm: Less, context: State?): Value? {
        val left = visitExpression(elm.operand[0], context)
        val right = visitExpression(elm.operand[1], context)
        return less(left, right, context)
    }

    override fun visitLessOrEqual(elm: LessOrEqual, context: State?): Value? {
        val left = visitExpression(elm.operand[0], context)
        val right = visitExpression(elm.operand[1], context)

        return lessOrEqual(left, right, context)
    }

    override fun visitLiteral(elm: Literal, context: State?): Value? {
        return LiteralEvaluator.internalEvaluate(elm.valueType!!, elm.value!!, context)
    }

    override fun visitList(elm: org.hl7.elm.r1.List, context: State?): Value {
        return ListEvaluator.internalEvaluate(elm, context, this)
    }

    override fun visitLn(elm: Ln, context: State?): Value? {
        val operand = visitExpression(elm.operand!!, context)
        return ln(operand)
    }

    override fun visitLog(elm: Log, context: State?): Value? {
        val left = visitExpression(elm.operand[0], context)
        val right = visitExpression(elm.operand[1], context)
        return log(left, right)
    }

    override fun visitLowBoundary(elm: LowBoundary, context: State?): Value? {
        val input = visitExpression(elm.operand[0], context)
        val precision = visitExpression(elm.operand[1], context)
        return lowBoundary(input, precision)
    }

    override fun visitLower(elm: Lower, context: State?): Value? {
        val operand = visitExpression(elm.operand!!, context)
        return lower(operand)
    }

    override fun visitMatches(elm: Matches, context: State?): Value? {
        val argument = visitExpression(elm.operand[0], context)
        val pattern = visitExpression(elm.operand[1], context)
        return matches(argument, pattern)
    }

    override fun visitMax(elm: Max, context: State?): Value? {
        val source = visitExpression(elm.source!!, context)
        return max(source, context)
    }

    override fun visitMaxValue(elm: MaxValue, context: State?): Value? {
        return MaxValueEvaluator.internalEvaluate(elm.valueType!!, context)
    }

    override fun visitMessage(elm: Message, context: State?): Value? {
        return MessageEvaluator.internalEvaluate(elm, context, this)
    }

    override fun visitMin(elm: Min, context: State?): Value? {
        val source = visitExpression(elm.source!!, context)
        return min(source, context)
    }

    override fun visitMinValue(elm: MinValue, context: State?): Value? {
        return MinValueEvaluator.internalEvaluate(elm.valueType!!, context)
    }

    override fun visitMode(elm: Mode, context: State?): Value? {
        val source = visitExpression(elm.source!!, context)
        return mode(source, context)
    }

    override fun visitModulo(elm: Modulo, context: State?): Value? {
        val left = visitExpression(elm.operand[0], context)
        val right = visitExpression(elm.operand[1], context)
        return modulo(left, right)
    }

    override fun visitMultiply(elm: Multiply, context: State?): Value? {
        val left = visitExpression(elm.operand[0], context)
        val right = visitExpression(elm.operand[1], context)

        return multiply(left, right, context)
    }

    override fun visitNegate(elm: Negate, context: State?): Value? {
        return NegateEvaluator.internalEvaluate(elm.operand!!, context, this)
    }

    override fun visitNotEqual(elm: NotEqual, context: State?): Value? {
        val left = visitExpression(elm.operand[0], context)
        val right = visitExpression(elm.operand[1], context)
        return notEqual(left, right, context)
    }

    override fun visitNot(elm: Not, context: State?): Value? {
        val operand = visitExpression(elm.operand!!, context)
        return not(operand)
    }

    override fun visitNow(elm: Now, context: State?): Value? {
        return NowEvaluator.internalEvaluate(context)
    }

    override fun visitNull(elm: Null, context: State?): Value? {
        return NullEvaluator.internalEvaluate(context)
    }

    override fun visitOperandRef(elm: OperandRef, context: State?): Value? {
        return OperandRefEvaluator.internalEvaluate(elm, context, this)
    }

    override fun visitOr(elm: Or, context: State?): Value? {
        val left = visitExpression(elm.operand[0], context)
        val right = visitExpression(elm.operand[1], context)
        return or(left, right)
    }

    override fun visitOverlapsAfter(elm: OverlapsAfter, context: State?): Value? {
        val left = visitExpression(elm.operand[0], context)
        val right = visitExpression(elm.operand[1], context)
        val precision = if (elm.precision == null) null else elm.precision!!.value()
        return overlapsAfter(left, right, precision, context)
    }

    override fun visitOverlapsBefore(elm: OverlapsBefore, context: State?): Value? {
        val left = visitExpression(elm.operand[0], context)
        val right = visitExpression(elm.operand[1], context)
        val precision = if (elm.precision == null) null else elm.precision!!.value()
        return overlapsBefore(left, right, precision, context)
    }

    override fun visitOverlaps(elm: Overlaps, context: State?): Value? {
        val left = visitExpression(elm.operand[0], context)
        val right = visitExpression(elm.operand[1], context)
        val precision = if (elm.precision == null) null else elm.precision!!.value()
        return overlaps(left, right, precision, context)
    }

    override fun visitParameterRef(elm: ParameterRef, context: State?): Value? {
        return ParameterRefEvaluator.internalEvaluate(elm, context, this)
    }

    override fun visitPointFrom(elm: PointFrom, context: State?): Value? {
        val operand = visitExpression(elm.operand!!, context)
        return pointFrom(operand, context)
    }

    override fun visitPopulationStdDev(elm: PopulationStdDev, context: State?): Value? {
        val source = visitExpression(elm.source!!, context)
        return popStdDev(source, context)
    }

    override fun visitPopulationVariance(elm: PopulationVariance, context: State?): Value? {
        val source = visitExpression(elm.source!!, context)
        return popVariance(source, context)
    }

    override fun visitPositionOf(elm: PositionOf, context: State?): Value? {
        val pattern = visitExpression(elm.pattern!!, context)
        val string = visitExpression(elm.string!!, context)
        return positionOf(pattern, string)
    }

    override fun visitPower(elm: Power, context: State?): Value? {
        val left = visitExpression(elm.operand[0], context)
        val right = visitExpression(elm.operand[1], context)
        return power(left, right)
    }

    override fun visitPrecision(elm: org.hl7.elm.r1.Precision, context: State?): Value? {
        val argument = visitExpression(elm.operand!!, context)
        return precision(argument)
    }

    override fun visitPredecessor(elm: Predecessor, context: State?): Value? {
        val argument = visitExpression(elm.operand!!, context)
        return predecessor(argument)
    }

    override fun visitProduct(elm: Product, context: State?): Value? {
        val source = visitExpression(elm.source!!, context)
        return product(source, context)
    }

    override fun visitProperContains(elm: ProperContains, context: State?): Value? {
        val left = visitExpression(elm.operand[0], context)
        val right = visitExpression(elm.operand[1], context)
        val precision = if (elm.precision != null) elm.precision!!.value() else null
        return properContains(left, right, precision, context)
    }

    override fun visitProperIncludedIn(elm: ProperIncludedIn, context: State?): Value? {
        val left = visitExpression(elm.operand[0], context)
        val right = visitExpression(elm.operand[1], context)
        val precision = if (elm.precision != null) elm.precision!!.value() else null
        return properlyIncludedIn(left, right, precision, context)
    }

    override fun visitProperIncludes(elm: ProperIncludes, context: State?): Value? {
        val left = visitExpression(elm.operand[0], context)
        val right = visitExpression(elm.operand[1], context)
        val precision = if (elm.precision != null) elm.precision!!.value() else null
        return properlyIncludes(left, right, precision, context)
    }

    override fun visitProperIn(elm: ProperIn, context: State?): Value? {
        val left = visitExpression(elm.operand[0], context)
        val right = visitExpression(elm.operand[1], context)
        val precision = if (elm.precision != null) elm.precision!!.value() else null
        return ProperInEvaluator.internalEvaluate(left, right, precision, context)
    }

    override fun visitProperty(elm: Property, context: State?): Value? {
        return PropertyEvaluator.internalEvaluate(elm, context, this)
    }

    override fun visitQuantity(elm: org.hl7.elm.r1.Quantity, context: State?): Value {
        return internalEvaluate(elm, context)
    }

    override fun visitRound(elm: Round, context: State?): Value? {
        val operand = visitExpression(elm.operand!!, context)
        val precision =
            if (elm.precision == null) null else visitExpression(elm.precision!!, context)
        return round(operand, precision)
    }

    override fun visitRetrieve(elm: Retrieve, context: State?): Value? {
        return RetrieveEvaluator.internalEvaluate(elm, context, this)
    }

    override fun visitReplaceMatches(elm: ReplaceMatches, context: State?): Value? {
        val argument = visitExpression(elm.operand[0], context)
        val pattern = visitExpression(elm.operand[1], context)
        val substitution = visitExpression(elm.operand[2], context)
        return replaceMatches(argument, pattern, substitution)
    }

    override fun visitRepeat(elm: Repeat, context: State?): Value {
        val source = visitExpression(elm.source!!, context)
        val element = visitExpression(elm.element!!, context)
        val scope = elm.scope
        return RepeatEvaluator.internalEvaluate(source, element, scope, context)
    }

    override fun visitRatio(elm: Ratio, context: State?): Value {
        return RatioEvaluator.internalEvaluate(elm, context, this)
    }

    override fun visitQueryLetRef(elm: QueryLetRef, context: State?): Value? {
        return internalEvaluate(elm, context)
    }

    override fun visitQuery(elm: Query, context: State?): Value? {
        return QueryEvaluator.internalEvaluate(elm, context, this)
    }

    override fun defaultResult(elm: Element, context: State?): Value? {
        return null
    }
}
