package org.opencds.cqf.cql.engine.execution;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.cqframework.cql.elm.visiting.ElmBaseLibraryVisitor;
import org.hl7.cql.model.IntervalType;
import org.hl7.cql.model.ListType;
import org.hl7.elm.r1.*;
import org.opencds.cqf.cql.engine.elm.executing.*;
import org.opencds.cqf.cql.engine.runtime.TemporalHelper;

public class EvaluationVisitor extends ElmBaseLibraryVisitor<Object, State> {

    @Override
    public Object visitExpressionDef(ExpressionDef expressionDef, State state) {
        return ExpressionDefEvaluator.internalEvaluate(expressionDef, state, this);
    }

    @Override
    public Object visitExpressionRef(ExpressionRef expressionRef, State state) {
        return ExpressionRefEvaluator.internalEvaluate(expressionRef, state, this);
    }

    @Override
    public Object visitFunctionRef(FunctionRef elm, State state) {
        return FunctionRefEvaluator.internalEvaluate(elm, state, this);
    }

    @Override
    public Object visitAdd(Add add, State state) {
        Object left = visitExpression(add.getOperand().get(0), state);
        Object right = visitExpression(add.getOperand().get(1), state);

        return AddEvaluator.add(left, right);
    }

    @Override
    public Object visitAbs(Abs abs, State state) {
        Object operand = visitExpression(abs.getOperand(), state);
        return AbsEvaluator.abs(operand);
    }

    @Override
    public Object visitAfter(After after, State state) {
        Object left = visitExpression(after.getOperand().get(0), state);
        Object right = visitExpression(after.getOperand().get(1), state);
        String precision = after.getPrecision() == null ? null : after.getPrecision().value();

        return AfterEvaluator.after(left, right, precision, state);
    }

    @Override
    public Object visitAliasRef(AliasRef aliasRef, State state) {
        return AliasRefEvaluator.internalEvaluate(aliasRef.getName(), state);
    }

    @Override
    public Object visitAllTrue(AllTrue allTrue, State state) {
        Object src = visitExpression(allTrue.getSource(), state);
        return AllTrueEvaluator.allTrue(src);
    }


    @Override
    public Object visitAnd(And and, State state) {
        Object left = visitExpression(and.getOperand().get(0), state);
        Object right = visitExpression(and.getOperand().get(1), state);

        return AndEvaluator.and(left, right);
    }

    @Override
    public Object visitAnyInCodeSystem(AnyInCodeSystem anyInCodeSystem, State state) {
        Object codes = visitExpression(anyInCodeSystem.getCodes(), state);
        Object codeSystem = visitExpression(anyInCodeSystem.getCodesystemExpression(), state);
        return AnyInCodeSystemEvaluator.internalEvaluate(codes, anyInCodeSystem.getCodesystem(), codeSystem, state);
    }

    @Override
    public Object visitInCodeSystem(InCodeSystem inCodeSystem, State state) {
        Object code = visitExpression(inCodeSystem.getCode(), state);
        Object cs = null;
        if (inCodeSystem.getCodesystem() != null) {
            cs = CodeSystemRefEvaluator.toCodeSystem(inCodeSystem.getCodesystem(), state);
        } else if (inCodeSystem.getCodesystemExpression() != null) {
            cs = visitExpression(inCodeSystem.getCodesystemExpression(), state);
        }

        return InCodeSystemEvaluator.inCodeSystem(code, cs, state);
    }

    @Override
    public Object visitAnyInValueSet(AnyInValueSet anyInValueSet, State state) {
        Object codes = visitExpression(anyInValueSet.getCodes(), state);
        Object valueset = visitExpression(anyInValueSet.getValuesetExpression(), state);

        return AnyInValueSetEvaluator.internalEvaluate(codes, anyInValueSet.getValueset(), valueset, state);
    }

    @Override
    public Object visitInValueSet(InValueSet inValueSet, State state) {

        Object code = visitExpression(inValueSet.getCode(), state);
        Object vs = null;
        if (inValueSet.getValueset() != null) {
            vs = ValueSetRefEvaluator.toValueSet(state, inValueSet.getValueset());
        } else if (inValueSet.getValuesetExpression() != null) {
            vs = visitExpression(inValueSet.getValuesetExpression(), state);
        }
        return InValueSetEvaluator.inValueSet(code, vs, state);
    }

    @Override
    public Object visitValueSetRef(ValueSetRef elm, State state) {
        return ValueSetRefEvaluator.internalEvaluate(state, elm);
    }

    @Override
    public Object visitXor(Xor elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        Object right = visitExpression(elm.getOperand().get(1), state);
        return XorEvaluator.xor(left, right);
    }


    @Override
    public Object visitWidth(Width elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        return WidthEvaluator.width(operand);
    }

    @Override
    public Object visitVariance(Variance variance, State state) {
        Object source = visitExpression(variance.getSource(), state);
        return VarianceEvaluator.variance(source, state);
    }

    @Override
    public Object visitAvg(Avg avg, State state) {
        Object src = visitExpression(avg.getSource(), state);
        return AvgEvaluator.avg(src, state);
    }

    @Override
    public Object visitDivide(Divide elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        Object right = visitExpression(elm.getOperand().get(1), state);
        return DivideEvaluator.divide(left, right, state);
    }


    @Override
    public Object visitUpper(Upper elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        return UpperEvaluator.upper(operand);
    }

    @Override
    public Object visitUnion(Union elm, State state) {
        var left = elm.getOperand().get(0);
        var right = elm.getOperand().get(1);
        Object leftResult = visitExpression(left, state);
        Object rightResult = visitExpression(right, state);


        // This will attempt to use the declared result types from the ELM to determine which type of Union
        // to perform. If the types are not declared, it will fall back to checking the values of the result.
        if (left.getResultType() instanceof ListType || right.getResultType() instanceof ListType || elm.getResultType() instanceof ListType) {
            return UnionEvaluator.unionIterable((Iterable<?>)leftResult, (Iterable<?>)rightResult, state);
        }
        else if (left.getResultType() instanceof IntervalType || right.getResultType() instanceof IntervalType || elm.getResultType() instanceof IntervalType) {
            return UnionEvaluator.unionInterval((org.opencds.cqf.cql.engine.runtime.Interval)leftResult, (org.opencds.cqf.cql.engine.runtime.Interval)rightResult, state);
        }
        else {
            return UnionEvaluator.union(left, right, state);
        }
    }

    @Override
    public Object visitGreater(Greater elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        Object right = visitExpression(elm.getOperand().get(1), state);

        return GreaterEvaluator.greater(left, right, state);
    }

    @Override
    public Object visitMeets(Meets elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        Object right = visitExpression(elm.getOperand().get(1), state);
        String precision = elm.getPrecision() == null ? null : elm.getPrecision().value();

        return MeetsEvaluator.meets(left, right, precision, state);
    }

    @Override
    public Object visitDistinct(Distinct elm, State state) {
        Object value = visitExpression(elm.getOperand(), state);
        return DistinctEvaluator.distinct((Iterable<?>) value, state);
    }

    @Override
    public Object visitMeetsAfter(MeetsAfter elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        Object right = visitExpression(elm.getOperand().get(1), state);
        String precision = elm.getPrecision() == null ? null : elm.getPrecision().value();

        return MeetsAfterEvaluator.meetsAfter(left, right, precision, state);
    }

    //SameAs

    @Override
    public Object visitMeetsBefore(MeetsBefore elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        Object right = visitExpression(elm.getOperand().get(1), state);
        String precision = elm.getPrecision() == null ? null : elm.getPrecision().value();

        return MeetsBeforeEvaluator.meetsBefore(left, right, precision, state);
    }

    @Override
    public Object visitSameAs(SameAs elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        Object right = visitExpression(elm.getOperand().get(1), state);
        String precision = elm.getPrecision() == null ? null : elm.getPrecision().value();

        return SameAsEvaluator.sameAs(left, right, precision, state);
    }


    @Override
    public Object visitSameOrAfter(SameOrAfter elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        Object right = visitExpression(elm.getOperand().get(1), state);
        String precision = elm.getPrecision() == null ? null : elm.getPrecision().value();

        return SameOrAfterEvaluator.sameOrAfter(left, right, precision, state);
    }

    @Override
    public Object visitSameOrBefore(SameOrBefore elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        Object right = visitExpression(elm.getOperand().get(1), state);
        String precision = elm.getPrecision() == null ? null : elm.getPrecision().value();

        return SameOrBeforeEvaluator.sameOrBefore(left, right, precision, state);
    }

    @Override
    public Object visitGreaterOrEqual(GreaterOrEqual elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        Object right = visitExpression(elm.getOperand().get(1), state);

        return GreaterOrEqualEvaluator.greaterOrEqual(left, right, state);
    }

    @Override
    public Object visitSingletonFrom(SingletonFrom elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        return SingletonFromEvaluator.singletonFrom(operand);
    }

    @Override
    public Object visitSize(Size elm, State state) {
        Object argument = visitExpression(elm.getOperand(), state);
        return SizeEvaluator.size(argument);
    }

    @Override
    public Object visitSlice(Slice elm, State state) {
        Object source = visitExpression(elm.getSource(), state);
        Integer start = (Integer) visitExpression(elm.getStartIndex(), state);
        Integer end = elm.getEndIndex() == null ? null : (Integer) visitExpression(elm.getEndIndex(), state);

        return SliceEvaluator.slice(source, start, end);
    }

    @Override
    public Object visitSplit(Split elm, State state) {
        Object stringToSplit = visitExpression(elm.getStringToSplit(), state);
        Object separator = visitExpression(elm.getSeparator(), state);

        return SplitEvaluator.split(stringToSplit, separator);
    }

    @Override
    public Object visitSplitOnMatches(SplitOnMatches elm, State state) {
        Object stringToSplit = visitExpression(elm.getStringToSplit(), state);
        Object separator = visitExpression(elm.getSeparatorPattern(), state);

        return SplitOnMatchesEvaluator.splitOnMatches(stringToSplit, separator);
    }

    @Override
    public Object visitStart(Start elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        return StartEvaluator.start(operand);
    }

    @Override
    public Object visitStarts(Starts elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        Object right = visitExpression(elm.getOperand().get(1), state);
        String precision = elm.getPrecision() == null ? null : elm.getPrecision().value();

        return StartsEvaluator.starts(left, right, precision, state);
    }

    @Override
    public Object visitStartsWith(StartsWith elm, State state) {
        Object argument = visitExpression(elm.getOperand().get(0), state);
        Object prefix = visitExpression(elm.getOperand().get(1), state);

        return StartsWithEvaluator.startsWith(argument, prefix);
    }

    @Override
    public Object visitStdDev(StdDev elm, State state) {
        Object source = visitExpression(elm.getSource(), state);
        return StdDevEvaluator.stdDev(source, state);
    }

    @Override
    public Object visitSubstring(Substring elm, State state) {
        Object stringValue = visitExpression(elm.getStringToSub(), state);
        Object startIndexValue = visitExpression(elm.getStartIndex(), state);
        Object lengthValue = elm.getLength() == null ? null : visitExpression(elm.getLength(), state);

        return SubstringEvaluator.substring(stringValue, startIndexValue, lengthValue);
    }

    @Override
    public Object visitSubtract(Subtract elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        Object right = visitExpression(elm.getOperand().get(1), state);
        return SubtractEvaluator.subtract(left, right);
    }

    @Override
    public Object visitSuccessor(Successor elm, State state) {
        Object value = visitExpression(elm.getOperand(), state);
        return SuccessorEvaluator.successor(value);
    }

    @Override
    public Object visitSum(Sum elm, State state) {
        Object source = visitExpression(elm.getSource(), state);
        return SumEvaluator.sum(source);
    }

    @Override
    public Object visitTime(Time elm, State state) {
        if (elm.getHour() == null) {
            return null;
        }

        Integer hour = elm.getHour() == null ? null : (Integer) visitExpression(elm.getHour(), state);
        Integer minute = elm.getMinute() == null ? null : (Integer) visitExpression(elm.getMinute(), state);
        Integer second = elm.getSecond() == null ? null : (Integer) visitExpression(elm.getSecond(), state);
        Integer miliSecond = elm.getMillisecond() == null ? null : (Integer) visitExpression(elm.getMillisecond(), state);

        return TimeEvaluator.time(hour, minute, second, miliSecond);
    }

    @Override
    public Object visitTimeFrom(TimeFrom elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        return TimeFromEvaluator.timeFrom(operand);
    }

    @Override
    public Object visitTimeOfDay(TimeOfDay elm, State state) {
        return TimeOfDayEvaluator.internalEvaluate(state);
    }

    @Override
    public Object visitTimezoneFrom(TimezoneFrom elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        return TimezoneFromEvaluator.internalEvaluate(operand);
    }

    @Override
    public Object visitTimezoneOffsetFrom(TimezoneOffsetFrom elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        return TimezoneOffsetFromEvaluator.timezoneOffsetFrom(operand);
    }

    @Override
    public Object visitToBoolean(ToBoolean elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        return ToBooleanEvaluator.toBoolean(operand);
    }

    @Override
    public Object visitToConcept(ToConcept elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        return ToConceptEvaluator.toConcept(operand);
    }

    @Override
    public Object visitToDate(ToDate elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        return ToDateEvaluator.toDate(operand);
    }

    @Override
    public Object visitToDateTime(ToDateTime elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        return ToDateTimeEvaluator.ToDateTime(operand, state);
    }

    @Override
    public Object visitToday(Today elm, State state) {
        return TodayEvaluator.today(state);
    }

    @Override
    public Object visitToDecimal(ToDecimal elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        return ToDecimalEvaluator.toDecimal(operand);
    }

    @Override
    public Object visitToInteger(ToInteger elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        return ToIntegerEvaluator.toInteger(operand);
    }

    @Override
    public Object visitToList(ToList elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        return ToListEvaluator.toList(operand);
    }

    @Override
    public Object visitToLong(ToLong elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        return ToLongEvaluator.toLong(operand);
    }

    @Override
    public Object visitToQuantity(ToQuantity elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        return ToQuantityEvaluator.toQuantity(operand, state);
    }

    @Override
    public Object visitToRatio(ToRatio elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        return ToRatioEvaluator.toRatio(operand);
    }

    @Override
    public Object visitToString(ToString elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        return ToStringEvaluator.toString(operand);
    }

    @Override
    public Object visitToTime(ToTime elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        return ToTimeEvaluator.toTime(operand);
    }

    @Override
    public Object visitTruncatedDivide(TruncatedDivide elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        Object right = visitExpression(elm.getOperand().get(1), state);
        return TruncatedDivideEvaluator.div(left, right, state);
    }

    @Override
    public Object visitMedian(Median elm, State state) {
        Object source = visitExpression(elm.getSource(), state);
        return MedianEvaluator.median(source, state);
    }

    @Override
    public Object visitTruncate(Truncate elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        return TruncateEvaluator.truncate(operand);
    }

    @Override
    public Object visitTuple(Tuple elm, State state) {
        LinkedHashMap<String, Object> ret = new LinkedHashMap<>();
        for (TupleElement element : elm.getElement()) {
            ret.put(element.getName(), visitExpression(element.getValue(), state));
        }
        return TupleEvaluator.internalEvaluate(ret, state);
    }


    @Override
    public Object visitAnyTrue(AnyTrue elm, State state) {
        Object source = visitExpression(elm.getSource(), state);
        return AnyTrueEvaluator.anyTrue(source);
    }

    @Override
    public Object visitAs(As elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        return AsEvaluator.internalEvaluate(operand, elm, elm.isStrict(), state);
    }

    @Override
    public Object visitBefore(Before elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        Object right = visitExpression(elm.getOperand().get(1), state);
        String precision = elm.getPrecision() == null ? null : elm.getPrecision().value();

        return BeforeEvaluator.before(left, right, precision, state);
    }

    @Override
    public Object visitCalculateAgeAt(CalculateAgeAt elm, State state) {
        Object birthDate = visitExpression(elm.getOperand().get(0), state);
        Object asOf = visitExpression(elm.getOperand().get(1), state);
        String precision = elm.getPrecision().value();
        return CalculateAgeAtEvaluator.calculateAgeAt(birthDate, asOf, precision);
    }

    @Override
    public Object visitCalculateAge(CalculateAge elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        String precision = elm.getPrecision().value();
        return CalculateAgeEvaluator.internalEvaluate(operand, precision, state);
    }

    @Override
    public Object visitCase(Case elm, State state) {
        if (elm.getComparand() == null) {
            for (CaseItem caseItem : elm.getCaseItem()) {
                Boolean when = (Boolean) visitExpression(caseItem.getWhen(), state);

                if (when == null) {
                    continue;
                }

                if (when) {
                    return visitExpression(caseItem.getThen(), state);
                }
            }
            return visitElement(elm.getElse(), state);

        } else {
            Object comparand = visitExpression(elm.getComparand(), state);

            for (CaseItem caseItem : elm.getCaseItem()) {
                Object when = visitExpression(caseItem.getWhen(), state);
                Boolean check = EquivalentEvaluator.equivalent(comparand, when, state);
                if (check == null) {
                    continue;
                }

                if (check) {
                    return visitElement(caseItem.getThen(), state);
                }
            }

            return visitElement(elm.getElse(), state);
        }
    }

    @Override
    public Object visitCeiling(Ceiling elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        return CeilingEvaluator.ceiling(operand);
    }

    @Override
    public Object visitChildren(Children elm, State state) {
        Object source = visitExpression(elm.getSource(), state);
        return ChildrenEvaluator.children(source);
    }

    @Override
    public Object visitCoalesce(Coalesce elm, State state) {
        List<Object> operands = new ArrayList<>();
        for (Expression operand : elm.getOperand()) {
            operands.add(visitExpression(operand, state));
        }
        return CoalesceEvaluator.coalesce(operands);
    }

    @Override
    public Object visitCode(Code elm, State state) {
        return CodeEvaluator.internalEvaluate(elm.getSystem(), elm.getCode(), elm.getDisplay(), state);
    }

    @Override
    public Object visitCodeRef(CodeRef elm, State state) {
        return CodeRefEvaluator.toCode(elm, state);
    }

    @Override
    public Object visitConcept(Concept elm, State state) {
        ArrayList<org.opencds.cqf.cql.engine.runtime.Code> codes = new ArrayList<>();
        for (int i = 0; i < elm.getCode().size(); ++i) {
            codes.add((org.opencds.cqf.cql.engine.runtime.Code) visitExpression(elm.getCode().get(i), state));
        }

        return ConceptEvaluator.internalEvaluate(codes, elm.getDisplay());
    }

    @Override
    public Object visitConceptRef(ConceptRef elm, State state) {
        return ConceptRefEvaluator.toConcept(elm, state);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object visitCollapse(Collapse elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        Object right = visitExpression(elm.getOperand().get(1), state);

        Iterable<org.opencds.cqf.cql.engine.runtime.Interval> list = (Iterable<org.opencds.cqf.cql.engine.runtime.Interval>) left;
        org.opencds.cqf.cql.engine.runtime.Quantity per = (org.opencds.cqf.cql.engine.runtime.Quantity) right;

        return CollapseEvaluator.collapse(list, per, state);
    }

    @Override
    public Object visitCombine(Combine elm, State state) {
        Object source = visitExpression(elm.getSource(), state);
        String separator = elm.getSeparator() == null ? "" : (String) visitExpression(elm.getSeparator(), state);

        return CombineEvaluator.combine(source, separator);
    }

    @Override
    public Object visitConcatenate(Concatenate elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        Object right = visitExpression(elm.getOperand().get(1), state);

        return ConcatenateEvaluator.concatenate(left, right);
    }

    @Override
    public Object visitContains(Contains elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        Object right = visitExpression(elm.getOperand().get(1), state);
        String precision = elm.getPrecision() == null ? null : elm.getPrecision().value();
        return ContainsEvaluator.internalEvaluate(left, right, elm.getOperand().get(0), precision, state);
    }

    @Override
    public Object visitConvert(Convert elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        return ConvertEvaluator.internalEvaluate(operand, elm.getToType(), elm.getToTypeSpecifier(), state);
    }

    @Override
    public Object visitConvertQuantity(ConvertQuantity elm, State state) {
        Object argument = visitExpression(elm.getOperand().get(0), state);
        Object unit = visitExpression(elm.getOperand().get(1), state);
        return ConvertQuantityEvaluator.convertQuantity(argument, unit, state.getEnvironment().getLibraryManager().getUcumService());
    }

    @Override
    public Object visitConvertsToBoolean(ConvertsToBoolean elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        return ConvertsToBooleanEvaluator.convertsToBoolean(operand);
    }

    @Override
    public Object visitConvertsToDate(ConvertsToDate elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        return ConvertsToDateEvaluator.convertsToDate(operand);
    }

    @Override
    public Object visitConvertsToDateTime(ConvertsToDateTime elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        return ConvertsToDateTimeEvaluator.convertsToDateTime(operand, state.getEvaluationDateTime().getZoneOffset());
    }

    @Override
    public Object visitConvertsToDecimal(ConvertsToDecimal elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        return ConvertsToDecimalEvaluator.convertsToDecimal(operand);
    }

    @Override
    public Object visitConvertsToInteger(ConvertsToInteger elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        return ConvertsToIntegerEvaluator.convertsToInteger(operand);
    }

    @Override
    public Object visitConvertsToLong(ConvertsToLong elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        return ConvertsToLongEvaluator.convertsToLong(operand);
    }

    @Override
    public Object visitConvertsToQuantity(ConvertsToQuantity elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        return ConvertsToQuantityEvaluator.convertsToQuantity(operand, state);
    }

    @Override
    public Object visitConvertsToString(ConvertsToString elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        return ConvertsToStringEvaluator.convertsToString(operand);
    }

    @Override
    public Object visitConvertsToTime(ConvertsToTime elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        return ConvertsToTimeEvaluator.convertsToTime(operand);
    }

    @Override
    public Object visitCount(Count elm, State state) {
        Object source = visitExpression(elm.getSource(), state);
        return CountEvaluator.count(source);
    }

    @Override
    public Object visitDate(Date elm, State state) {
        Integer year = elm.getYear() == null ? null : (Integer) visitExpression(elm.getYear(), state);
        Integer month = elm.getMonth() == null ? null : (Integer) visitExpression(elm.getMonth(), state);
        Integer day = elm.getDay() == null ? null : (Integer) visitExpression(elm.getDay(), state);
        return DateEvaluator.internalEvaluate(year, month, day);
    }

    @Override
    public Object visitDateFrom(DateFrom elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        return DateFromEvaluator.dateFrom(operand);
    }

    @Override
    public Object visitDateTimeComponentFrom(DateTimeComponentFrom elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        String precision = elm.getPrecision().value();
        return DateTimeComponentFromEvaluator.dateTimeComponentFrom(operand, precision);
    }

    @Override
    public Object visitDateTime(DateTime elm, State state) {
        Integer year = elm.getYear() == null ? null : (Integer) visitExpression(elm.getYear(), state);
        Integer month = elm.getMonth() == null ? null : (Integer) visitExpression(elm.getMonth(), state);
        Integer day = elm.getDay() == null ? null : (Integer) visitExpression(elm.getDay(), state);
        Integer hour = elm.getHour() == null ? null : (Integer) visitExpression(elm.getHour(), state);
        Integer minute = elm.getMinute() == null ? null : (Integer) visitExpression(elm.getMinute(), state);
        Integer second = elm.getSecond() == null ? null : (Integer) visitExpression(elm.getSecond(), state);
        Integer milliSecond = elm.getMillisecond() == null ? null : (Integer) visitExpression(elm.getMillisecond(), state);
        BigDecimal timeZoneOffset = elm.getTimezoneOffset() == null
                ? TemporalHelper.zoneToOffset(state.getEvaluationDateTime().getZoneOffset())
                // Previously, we relied on null to trigger DateTime instantiation off the default TimeZone
                // Now, we compute the Offset explicitly from the State evaluation time.
                : (BigDecimal) visitExpression(elm.getTimezoneOffset(), state);
        return DateTimeEvaluator.internalEvaluate(year, month, day, hour, minute, second, milliSecond, timeZoneOffset);
    }

    @Override
    public Object visitDescendents(Descendents elm, State state) {
        Object source = visitExpression(elm.getSource(), state);
        return DescendentsEvaluator.descendents(source);
    }

    @Override
    public Object visitDifferenceBetween(DifferenceBetween elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        Object right = visitExpression(elm.getOperand().get(1), state);

        String precision = elm.getPrecision().value();
        return DifferenceBetweenEvaluator.difference(left, right, org.opencds.cqf.cql.engine.runtime.Precision.fromString(precision));
    }

    @Override
    public Object visitDurationBetween(DurationBetween elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        Object right = visitExpression(elm.getOperand().get(1), state);

        String precision = elm.getPrecision().value();
        return DurationBetweenEvaluator.duration(left, right, org.opencds.cqf.cql.engine.runtime.Precision.fromString(precision));
    }

    @Override
    public Object visitEnd(End elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        return EndEvaluator.end(operand);
    }

    @Override
    public Object visitEnds(Ends elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        Object right = visitExpression(elm.getOperand().get(1), state);

        String precision = elm.getPrecision() == null ? null : elm.getPrecision().value();
        return EndsEvaluator.ends(left, right, precision, state);
    }

    @Override
    public Object visitEndsWith(EndsWith elm, State state) {
        String argument = (String) visitExpression(elm.getOperand().get(0), state);
        String suffix = (String) visitExpression(elm.getOperand().get(1), state);
        return EndsWithEvaluator.endsWith(argument, suffix);
    }

    @Override
    public Object visitEqual(Equal elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        Object right = visitExpression(elm.getOperand().get(1), state);

        return EqualEvaluator.equal(left, right, state);
    }

    @Override
    public Object visitEquivalent(Equivalent elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        Object right = visitExpression(elm.getOperand().get(1), state);

        return EquivalentEvaluator.equivalent(left, right, state);
    }

    @Override
    public Object visitExcept(Except elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        Object right = visitExpression(elm.getOperand().get(1), state);
        return ExceptEvaluator.except(left, right, state);
    }

    @Override
    public Object visitExists(Exists elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        return ExistsEvaluator.exists(operand);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object visitExpand(Expand elm, State state) {
        Iterable<org.opencds.cqf.cql.engine.runtime.Interval> list = (Iterable<org.opencds.cqf.cql.engine.runtime.Interval>) visitExpression(elm.getOperand().get(0), state);
        org.opencds.cqf.cql.engine.runtime.Quantity per = (org.opencds.cqf.cql.engine.runtime.Quantity) visitExpression(elm.getOperand().get(1), state);
        return ExpandEvaluator.expand(list, per, state);
    }

    @Override
    public Object visitExpandValueSet(ExpandValueSet elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        return ExpandValueSetEvaluator.expand(operand, state);
    }

    @Override
    public Object visitExp(Exp elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        return ExpEvaluator.exp(operand);
    }

    @Override
    public Object visitFilter(Filter elm, State state) {
        Object source = visitExpression(elm.getSource(), state);
        Object condition = visitExpression(elm.getCondition(), state);
        return FilterEvaluator.filter(elm, source, condition, state);
    }

    @Override
    public Object visitFirst(First elm, State state) {
        Object source = visitExpression(elm.getSource(), state);
        return FirstEvaluator.first(source);
    }

    @Override
    public Object visitFlatten(Flatten elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        return FlattenEvaluator.flatten(operand);
    }

    @Override
    public Object visitFloor(Floor elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        return FloorEvaluator.floor(operand);
    }

    @Override
    public Object visitForEach(ForEach elm, State state) {
        Object source = visitExpression(elm.getSource(), state);
        Object element = visitExpression(elm.getElement(), state);
        return ForEachEvaluator.forEach(source, element, state);
    }


    @Override
    public Object visitGeometricMean(GeometricMean elm, State state) {
        Iterable<?> source = (Iterable<?>) visitExpression(elm.getSource(), state);
        return GeometricMeanEvaluator.geometricMean(source, state);
    }


    @Override
    public Object visitHighBoundary(HighBoundary elm, State state) {
        Object input = visitExpression(elm.getOperand().get(0), state);
        Object precision = visitExpression(elm.getOperand().get(1), state);
        return HighBoundaryEvaluator.highBoundary(input, precision);
    }

    @Override
    public Object visitIdentifierRef(IdentifierRef elm, State state) {
        return IdentifierRefEvaluator.internalEvaluate(elm.getName(), state);
    }

    @Override
    public Object visitIf(If elm, State state) {
        return IfEvaluator.internalEvaluate(elm, state, this);
    }

    @Override
    public Object visitImplies(Implies elm, State state) {
        Boolean left = (Boolean) visitExpression(elm.getOperand().get(0), state);
        Boolean right = (Boolean) visitExpression(elm.getOperand().get(1), state);
        return ImpliesEvaluator.implies(left, right);
    }

    @Override
    public Object visitIncludedIn(IncludedIn elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        Object right = visitExpression(elm.getOperand().get(1), state);
        String precision = elm.getPrecision() != null ? elm.getPrecision().value() : null;
        return IncludedInEvaluator.internalEvaluate(left, right, precision, state);
    }

    @Override
    public Object visitIncludes(Includes elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        Object right = visitExpression(elm.getOperand().get(1), state);
        String precision = elm.getPrecision() != null ? elm.getPrecision().value() : null;
        return IncludesEvaluator.internalEvaluate(left, right, precision, state);
    }

    @Override
    public Object visitIndexOf(IndexOf elm, State state) {
        Object source = visitExpression(elm.getSource(), state);
        Object element = visitExpression(elm.getElement(), state);
        return IndexOfEvaluator.indexOf(source, element, state);
    }

    @Override
    public Object visitIndexer(Indexer elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        Object right = visitExpression(elm.getOperand().get(1), state);
        return IndexerEvaluator.indexer(left, right);
    }

    @Override
    public Object visitIn(In elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        Object right = visitExpression(elm.getOperand().get(1), state);
        String precision = elm.getPrecision() != null ? elm.getPrecision().value() : null;
        return InEvaluator.internalEvaluate(left, right, precision, state);
    }

    @Override
    public Object visitInstance(Instance elm, State state) {
        return InstanceEvaluator.internalEvaluate(elm, state, this);
    }


    @Override
    public Object visitIntersect(Intersect elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        Object right = visitExpression(elm.getOperand().get(1), state);
        return IntersectEvaluator.intersect(left, right, state);
    }

    @Override
    public Object visitInterval(Interval elm, State state) {
        return IntervalEvaluator.internalEvaluate(elm, state, this);
    }

    @Override
    public Object visitIs(Is elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        return IsEvaluator.internalEvaluate(elm, operand, state);
    }

    @Override
    public Object visitIsFalse(IsFalse elm, State state) {
        Boolean operand = (Boolean) visitExpression(elm.getOperand(), state);
        return IsFalseEvaluator.isFalse(operand);
    }

    @Override
    public Object visitIsNull(IsNull elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        return IsNullEvaluator.isNull(operand);
    }

    @Override
    public Object visitIsTrue(IsTrue elm, State state) {
        Boolean operand = (Boolean) visitExpression(elm.getOperand(), state);
        return IsTrueEvaluator.isTrue(operand);
    }

    @Override
    public Object visitLast(Last elm, State state) {
        Object source = visitExpression(elm.getSource(), state);
        return LastEvaluator.last(source);
    }

    @Override
    public Object visitLastPositionOf(LastPositionOf elm, State state) {
        Object string = visitExpression(elm.getString(), state);
        Object pattern = visitExpression(elm.getPattern(), state);
        return LastPositionOfEvaluator.lastPositionOf(string, pattern);
    }

    @Override
    public Object visitLength(Length elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        return LengthEvaluator.internalEvaluate(operand, elm, state);
    }

    @Override
    public Object visitLess(Less elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        Object right = visitExpression(elm.getOperand().get(1), state);
        return LessEvaluator.less(left, right, state);
    }

    @Override
    public Object visitLessOrEqual(LessOrEqual elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        Object right = visitExpression(elm.getOperand().get(1), state);

        return LessOrEqualEvaluator.lessOrEqual(left, right, state);
    }

    @Override
    public Object visitLiteral(Literal literal, State state) {
        return LiteralEvaluator.internalEvaluate(literal.getValueType(), literal.getValue(), state);
    }

    @Override
    public Object visitList(org.hl7.elm.r1.List elm, State state) {
        return ListEvaluator.internalEvaluate(elm, state, this);
    }

    @Override
    public Object visitLn(Ln elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        return LnEvaluator.ln(operand);
    }

    @Override
    public Object visitLog(Log elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        Object right = visitExpression(elm.getOperand().get(1), state);
        return LogEvaluator.log(left, right);
    }

    @Override
    public Object visitLowBoundary(LowBoundary elm, State state) {
        Object input = visitExpression(elm.getOperand().get(0), state);
        Object precision = visitExpression(elm.getOperand().get(1), state);
        return LowBoundaryEvaluator.lowBoundary(input, precision);
    }

    @Override
    public Object visitLower(Lower elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        return LowerEvaluator.lower(operand);
    }

    @Override
    public Object visitMatches(Matches elm, State state) {
        String argument = (String) visitExpression(elm.getOperand().get(0), state);
        String pattern = (String) visitExpression(elm.getOperand().get(1), state);
        return MatchesEvaluator.matches(argument, pattern);
    }

    @Override
    public Object visitMax(Max elm, State state) {
        Object source = visitExpression(elm.getSource(), state);
        return MaxEvaluator.max(source, state);
    }

    @Override
    public Object visitMaxValue(MaxValue elm, State state) {
        return MaxValueEvaluator.internalEvaluate(elm.getValueType(), state);
    }

    @Override
    public Object visitMessage(Message elm, State state) {
        return MessageEvaluator.internalEvaluate(elm, state, this);
    }

    @Override
    public Object visitMin(Min elm, State state) {
        Object source = visitExpression(elm.getSource(), state);
        return MinEvaluator.min(source, state);
    }

    @Override
    public Object visitMinValue(MinValue elm, State state) {
        return MinValueEvaluator.internalEvaluate(elm.getValueType(), state);
    }

    @Override
    public Object visitMode(Mode elm, State state) {
        Object source = visitExpression(elm.getSource(), state);
        return ModeEvaluator.mode(source, state);
    }

    @Override
    public Object visitModulo(Modulo elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        Object right = visitExpression(elm.getOperand().get(1), state);
        return ModuloEvaluator.modulo(left, right);
    }

    @Override
    public Object visitMultiply(Multiply elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        Object right = visitExpression(elm.getOperand().get(1), state);

        return MultiplyEvaluator.multiply(left, right);
    }

    @Override
    public Object visitNegate(Negate elm, State state) {
        return NegateEvaluator.internalEvaluate(elm.getOperand(), state, this);
    }

    @Override
    public Object visitNotEqual(NotEqual elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        Object right = visitExpression(elm.getOperand().get(1), state);
        return NotEqualEvaluator.notEqual(left, right, state);
    }

    @Override
    public Object visitNot(Not elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        return NotEvaluator.not(operand);
    }

    @Override
    public Object visitNow(Now elm, State state) {
        return NowEvaluator.internalEvaluate(state);
    }

    @Override
    public Object visitNull(Null elm, State state) {
        return NullEvaluator.internalEvaluate(state);
    }

    @Override
    public Object visitOperandRef(OperandRef elm, State state) {
        return OperandRefEvaluator.internalEvaluate(elm, state, this);
    }

    @Override
    public Object visitOr(Or elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        Object right = visitExpression(elm.getOperand().get(1), state);
        return OrEvaluator.or(left, right);
    }

    @Override
    public Object visitOverlapsAfter(OverlapsAfter elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        Object right = visitExpression(elm.getOperand().get(1), state);
        String precision = elm.getPrecision() == null ? null : elm.getPrecision().value();
        return OverlapsAfterEvaluator.overlapsAfter(left, right, precision, state);
    }

    @Override
    public Object visitOverlapsBefore(OverlapsBefore elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        Object right = visitExpression(elm.getOperand().get(1), state);
        String precision = elm.getPrecision() == null ? null : elm.getPrecision().value();
        return OverlapsBeforeEvaluator.overlapsBefore(left, right, precision, state);
    }

    @Override
    public Object visitOverlaps(Overlaps elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        Object right = visitExpression(elm.getOperand().get(1), state);
        String precision = elm.getPrecision() == null ? null : elm.getPrecision().value();
        return OverlapsEvaluator.overlaps(left, right, precision, state);
    }

    @Override
    public Object visitParameterRef(ParameterRef elm, State state) {
        return ParameterRefEvaluator.internalEvaluate(elm, state, this);
    }

    @Override
    public Object visitPointFrom(PointFrom elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        return PointFromEvaluator.pointFrom(operand, state);
    }

    @Override
    public Object visitPopulationStdDev(PopulationStdDev elm, State state) {
        Object source = visitExpression(elm.getSource(), state);
        return PopulationStdDevEvaluator.popStdDev(source, state);
    }

    @Override
    public Object visitPopulationVariance(PopulationVariance elm, State state) {
        Object source = visitExpression(elm.getSource(), state);
        return PopulationVarianceEvaluator.popVariance(source, state);
    }

    @Override
    public Object visitPositionOf(PositionOf elm, State state) {
        Object pattern = visitExpression(elm.getPattern(), state);
        Object string = visitExpression(elm.getString(), state);
        return PositionOfEvaluator.positionOf(pattern, string);
    }

    @Override
    public Object visitPower(Power elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        Object right = visitExpression(elm.getOperand().get(1), state);
        return PowerEvaluator.power(left, right);
    }

    @Override
    public Object visitPrecision(Precision elm, State state) {
        Object argument = visitExpression(elm.getOperand(), state);
        return PrecisionEvaluator.precision(argument);
    }

    @Override
    public Object visitPredecessor(Predecessor elm, State state) {
        Object argument = visitExpression(elm.getOperand(), state);
        return PredecessorEvaluator.predecessor(argument);
    }

    @Override
    public Object visitProduct(Product elm, State state) {
        Object source = visitExpression(elm.getSource(), state);
        return ProductEvaluator.product(source);
    }

    @Override
    public Object visitProperContains(ProperContains elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        Object right = visitExpression(elm.getOperand().get(1), state);
        String precision = elm.getPrecision() != null ? elm.getPrecision().value() : null;
        return ProperContainsEvaluator.properContains(left, right, precision, state);
    }

    @Override
    public Object visitProperIncludedIn(ProperIncludedIn elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        Object right = visitExpression(elm.getOperand().get(1), state);
        String precision = elm.getPrecision() != null ? elm.getPrecision().value() : null;
        return ProperIncludedInEvaluator.properlyIncludedIn(left, right, precision, state);
    }

    @Override
    public Object visitProperIncludes(ProperIncludes elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        Object right = visitExpression(elm.getOperand().get(1), state);
        String precision = elm.getPrecision() != null ? elm.getPrecision().value() : null;
        return ProperIncludesEvaluator.properlyIncludes(left, right, precision, state);
    }

    @Override
    public Object visitProperIn(ProperIn elm, State state) {
        Object left = visitExpression(elm.getOperand().get(0), state);
        Object right = visitExpression(elm.getOperand().get(1), state);
        String precision = elm.getPrecision() != null ? elm.getPrecision().value() : null;
        return ProperInEvaluator.internalEvaluate(left, right, precision, state);
    }

    @Override
    public Object visitProperty(Property elm, State state) {
        return PropertyEvaluator.internalEvaluate(elm, state, this);
    }

    @Override

    public Object visitQuantity(Quantity elm, State state) {
        return QuantityEvaluator.internalEvaluate(elm, state);
    }

    @Override
    public Object visitRound(Round elm, State state) {
        Object operand = visitExpression(elm.getOperand(), state);
        Object precision = elm.getPrecision() == null ? null : visitExpression(elm.getPrecision(), state);
        return RoundEvaluator.round(operand, precision);
    }

    @Override
    public Object visitRetrieve(Retrieve elm, State state) {
        return RetrieveEvaluator.internalEvaluate(elm, state, this);
    }

    @Override
    public Object visitReplaceMatches(ReplaceMatches elm, State state) {
        String argument = (String) visitExpression(elm.getOperand().get(0), state);
        String pattern = (String) visitExpression(elm.getOperand().get(1), state);
        String substitution = (String) visitExpression(elm.getOperand().get(2), state);
        return ReplaceMatchesEvaluator.replaceMatches(argument, pattern, substitution);
    }

    @Override
    public Object visitRepeat(Repeat elm, State state) {
        Object source = visitExpression(elm.getSource(), state);
        Object element = visitExpression(elm.getElement(), state);
        String scope = elm.getScope();
        return RepeatEvaluator.internalEvaluate(source, element, scope, state);
    }

    @Override
    public Object visitRatio(Ratio elm, State state) {
        return RatioEvaluator.internalEvaluate(elm, state, this);
    }

    @Override
    public Object visitQueryLetRef(QueryLetRef elm, State state) {
        return QueryLetRefEvaluator.internalEvaluate(elm, state);
    }

    @Override
    public Object visitQuery(Query elm, State state) {
        return QueryEvaluator.internalEvaluate(elm, state, this) ;
    }
}
