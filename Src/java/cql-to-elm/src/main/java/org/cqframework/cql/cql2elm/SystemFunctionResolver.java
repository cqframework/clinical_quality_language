package org.cqframework.cql.cql2elm;


import org.cqframework.cql.cql2elm.model.Conversion;
import org.cqframework.cql.cql2elm.model.PropertyResolution;
import org.cqframework.cql.cql2elm.model.SystemModel;
import org.cqframework.cql.cql2elm.model.invocation.*;
import org.hl7.elm.r1.*;

import java.util.ArrayList;
import java.util.List;

public class SystemFunctionResolver {
    private final ObjectFactory of = new ObjectFactory();
    private final LibraryBuilder builder;

    public SystemFunctionResolver(LibraryBuilder builder) {
        this.builder = builder;
    }

    public Expression resolveSystemFunction(FunctionRef fun) {
        if (fun.getLibraryName() == null || "System".equals(fun.getLibraryName())) {
            switch (fun.getName()) {
                // Aggregate Functions
                case "AllTrue":
                case "AnyTrue":
                case "Avg":
                case "Count":
                case "GeometricMean":
                case "Max":
                case "Median":
                case "Min":
                case "Mode":
                case "PopulationStdDev":
                case "PopulationVariance":
                case "Product":
                case "StdDev":
                case "Sum":
                case "Variance": {
                    return resolveAggregate(fun);
                }

                // Arithmetic Functions
                case "Abs":
                case "Ceiling":
                case "Exp":
                case "Floor":
                case "Ln":
                case "Negate":
                case "Precision":
                case "Predecessor":
                case "Successor":
                case "Truncate": {
                    return resolveUnary(fun);
                }

                case "HighBoundary":
                case "Log":
                case "LowBoundary":
                case "Modulo":
                case "Power":
                case "TruncatedDivide": {
                    return resolveBinary(fun);
                }

                case "Round": {
                    return resolveRound(fun);
                }

                // Clinical Functions
                case "AgeInYears":
                case "AgeInMonths": {
                    checkNumberOfOperands(fun, 0);
                    return resolveCalculateAge(
                            builder.enforceCompatible(getPatientBirthDateProperty(), builder.resolveTypeName("System", "Date")),
                            resolveAgeRelatedFunctionPrecision(fun));
                }

                case "AgeInWeeks":
                case "AgeInDays":
                case "AgeInHours":
                case "AgeInMinutes":
                case "AgeInSeconds":
                case "AgeInMilliseconds": {
                    checkNumberOfOperands(fun, 0);
                    return resolveCalculateAge(
                            builder.ensureCompatible(getPatientBirthDateProperty(), builder.resolveTypeName("System", "DateTime")),
                            resolveAgeRelatedFunctionPrecision(fun));
                }

                case "AgeInYearsAt":
                case "AgeInMonthsAt":
                case "AgeInWeeksAt":
                case "AgeInDaysAt": {
                    checkNumberOfOperands(fun, 1);
                    List<Expression> ops = new ArrayList<>();
                    Expression op = fun.getOperand().get(0);
                    // If the op is not a Date or DateTime, attempt to get it to convert it to a Date or DateTime
                    // If the op can be converted to both a Date and a DateTime, throw an ambiguous error
                    if (!(op.getResultType().isSubTypeOf(builder.resolveTypeName("System", "Date"))
                        || op.getResultType().isSubTypeOf(builder.resolveTypeName("System", "DateTime")))) {
                        Conversion dateConversion = builder.findConversion(op.getResultType(), builder.resolveTypeName("System", "Date"), true, false);
                        Conversion dateTimeConversion = builder.findConversion(op.getResultType(), builder.resolveTypeName("System", "DateTime"), true, false);
                        if (dateConversion != null && dateTimeConversion != null) {
                            if (dateConversion.getScore() == dateTimeConversion.getScore()) {
                                // ERROR
                                throw new IllegalArgumentException(String.format("Ambiguous implicit conversion from %s to %s or %s.",
                                        op.getResultType().toString(), dateConversion.getToType().toString(), dateTimeConversion.getToType().toString()));
                            }
                            else if (dateConversion.getScore() < dateTimeConversion.getScore()) {
                                op = builder.convertExpression(op, dateConversion);
                            }
                            else {
                                op = builder.convertExpression(op, dateTimeConversion);
                            }
                        }
                        else if (dateConversion != null) {
                            op = builder.convertExpression(op, dateConversion);
                        }
                        else if (dateTimeConversion != null) {
                            op = builder.convertExpression(op, dateTimeConversion);
                        }
                        else {
                            // ERROR
                            throw new IllegalArgumentException(String.format("Could not resolve call to operator %s with argument of type %s.",
                                    fun.getName(), op.getResultType().toString()));
                        }
                    }
                    ops.add(builder.enforceCompatible(getPatientBirthDateProperty(), op.getResultType()));
                    ops.add(op);
                    return resolveCalculateAgeAt(ops, resolveAgeRelatedFunctionPrecision(fun));
                }

                case "AgeInHoursAt":
                case "AgeInMinutesAt":
                case "AgeInSecondsAt":
                case "AgeInMillisecondsAt": {
                    List<Expression> ops = new ArrayList<>();
                    ops.add(getPatientBirthDateProperty());
                    ops.addAll(fun.getOperand());
                    return resolveCalculateAgeAt(ops, resolveAgeRelatedFunctionPrecision(fun));
                }

                case "CalculateAgeInYears":
                case "CalculateAgeInMonths":
                case "CalculateAgeInWeeks":
                case "CalculateAgeInDays":
                case "CalculateAgeInHours":
                case "CalculateAgeInMinutes":
                case "CalculateAgeInSeconds":
                case "CalculateAgeInMilliseconds": {
                    checkNumberOfOperands(fun, 1);
                    return resolveCalculateAge(fun.getOperand().get(0), resolveAgeRelatedFunctionPrecision(fun));
                }

                case "CalculateAgeInYearsAt":
                case "CalculateAgeInMonthsAt":
                case "CalculateAgeInWeeksAt":
                case "CalculateAgeInDaysAt":
                case "CalculateAgeInHoursAt":
                case "CalculateAgeInMinutesAt":
                case "CalculateAgeInSecondsAt":
                case "CalculateAgeInMillisecondsAt": {
                    return resolveCalculateAgeAt(fun.getOperand(), resolveAgeRelatedFunctionPrecision(fun));
                }

                // DateTime Functions
                case "DateTime": {
                    return resolveDateTime(fun);
                }

                case "Date": {
                    return resolveDate(fun);
                }

                case "Time": {
                    return resolveTime(fun);
                }

                case "Now":
                case "now": {
                    return resolveNow(fun);
                }

                case "Today":
                case "today": {
                    return resolveToday(fun);
                }

                case "TimeOfDay":
                case "timeOfDay": {
                    return resolveTimeOfDay(fun);
                }

                // List Functions
                case "IndexOf": {
                    return resolveIndexOf(fun);
                }

                case "First": {
                    return resolveFirst(fun);
                }

                case "Last": {
                    return resolveLast(fun);
                }

                case "Skip": {
                    return resolveSkip(fun);
                }

                case "Take": {
                    return resolveTake(fun);
                }

                case "Tail": {
                    return resolveTail(fun);
                }

                case "Contains":
                case "Except":
                case "Expand":
                case "In":
                case "Includes":
                case "IncludedIn":
                case "Intersect":
                case "ProperIncludes":
                case "ProperIncludedIn":
                case "Union": {
                    return resolveBinary(fun);
                }

                case "Distinct":
                case "Exists":
                case "Flatten":
                case "Collapse":
                case "SingletonFrom":
                case "ExpandValueSet": {
                    return resolveUnary(fun);
                }

                // Nullological Functions
                case "Coalesce": {
                    return resolveNary(fun);
                }

                case "IsNull":
                case "IsTrue":
                case "IsFalse": {
                    return resolveUnary(fun);
                }

                // Overloaded Functions
                case "Length":
                case "Width":
                case "Size": {
                    return resolveUnary(fun);
                }

                // String Functions
                case "Indexer":
                case "StartsWith":
                case "EndsWith":
                case "Matches": {
                    return resolveBinary(fun);
                }

                case "ReplaceMatches": {
                    return resolveTernary(fun);
                }

                case "Concatenate": {
                    return resolveNary(fun);
                }

                case "Combine": {
                    return resolveCombine(fun);
                }

                case "Split": {
                    return resolveSplit(fun);
                }

                case "SplitOnMatches": {
                    return resolveSplitOnMatches(fun);
                }

                case "Upper":
                case "Lower": {
                    return resolveUnary(fun);
                }

                case "PositionOf": {
                    return resolvePositionOf(fun);
                }

                case "LastPositionOf": {
                    return resolveLastPositionOf(fun);
                }

                case "Substring": {
                    return resolveSubstring(fun);
                }

                // Logical Functions
                case "Not": {
                    return resolveUnary(fun);
                }

                case "And":
                case "Or":
                case "Xor":
                case "Implies": {
                    return resolveBinary(fun);
                }

                // Type Functions
                case "ConvertsToString":
                case "ConvertsToBoolean":
                case "ConvertsToInteger":
                case "ConvertsToLong":
                case "ConvertsToDecimal":
                case "ConvertsToDateTime":
                case "ConvertsToDate":
                case "ConvertsToTime":
                case "ConvertsToQuantity":
                case "ConvertsToRatio":
                case "ToString":
                case "ToBoolean":
                case "ToInteger":
                case "ToLong":
                case "ToDecimal":
                case "ToDateTime":
                case "ToDate":
                case "ToTime":
                case "ToQuantity":
                case "ToRatio":
                case "ToConcept": {
                    return resolveUnary(fun);
                }

                // Quantity Conversion
                case "CanConvertQuantity":
                case "ConvertQuantity": {
                    return resolveBinary(fun);
                }

                // Comparison Functions
                case "Equal":
                case "NotEqual":
                case "Greater":
                case "GreaterOrEqual":
                case "Less":
                case "LessOrEqual":
                case "Equivalent": {
                    return resolveBinary(fun);
                }

                // Error Functions
                case "Message":
                    return resolveMessage(fun);
            }
        }

        return null;
    }

    // Age-Related Function Support

    private CalculateAge resolveCalculateAge(Expression e, DateTimePrecision p) {
        CalculateAge operator = of.createCalculateAge()
                .withPrecision(p)
                .withOperand(e);

        builder.resolveUnaryCall("System", "CalculateAge", operator);
        return operator;
    }

    private CalculateAgeAt resolveCalculateAgeAt(List<Expression> e, DateTimePrecision p) {
        CalculateAgeAt operator = of.createCalculateAgeAt()
                .withPrecision(p)
                .withOperand(e);

        builder.resolveBinaryCall("System", "CalculateAgeAt", operator);
        return operator;
    }

    private static DateTimePrecision resolveAgeRelatedFunctionPrecision(FunctionRef fun) {
        String name = fun.getName();
        if (name.contains("Years")) {
            return DateTimePrecision.YEAR;
        } else if (name.contains("Months")) {
            return DateTimePrecision.MONTH;
        } else if (name.contains("Weeks")) {
            return DateTimePrecision.WEEK;
        } else if (name.contains("Days")) {
            return DateTimePrecision.DAY;
        } else if (name.contains("Hours")) {
            return DateTimePrecision.HOUR;
        } else if (name.contains("Minutes")) {
            return DateTimePrecision.MINUTE;
        } else if (name.contains("Second")) {
            return DateTimePrecision.SECOND;
        } else if (name.contains("Milliseconds")) {
            return DateTimePrecision.MILLISECOND;
        }

        throw new IllegalArgumentException(String.format("Unknown precision '%s'.", name));
    }

    private Expression getPatientBirthDateProperty() {
        Expression source = builder.resolveIdentifier("Patient", true);
        String birthDateProperty = builder.getDefaultModel().getModelInfo().getPatientBirthDatePropertyName();
        // If the property has a qualifier, resolve it as a path (without model mapping)
        if (birthDateProperty.indexOf('.') >= 1) {
            Property property = of.createProperty().withSource(source).withPath(birthDateProperty);
            property.setResultType(builder.resolvePath(source.getResultType(), property.getPath()));
            return property;
        }
        else {
            PropertyResolution resolution = builder.resolveProperty(source.getResultType(), birthDateProperty);
            Expression result = builder.buildProperty(source, resolution.getName(), resolution.isSearch(), resolution.getType());
            result = builder.applyTargetMap(result, resolution.getTargetMap());
            return result;
        }
    }

    // Arithmetic Function Support

    private Round resolveRound(FunctionRef fun) {
        if (fun.getOperand().isEmpty() || fun.getOperand().size() > 2) {
            throw new IllegalArgumentException("Could not resolve call to system operator Round.  Expected 1 or 2 arguments.");
        }
        final Round round = of.createRound().withOperand(fun.getOperand().get(0));
        if (fun.getOperand().size() == 2) {
            round.setPrecision(fun.getOperand().get(1));
        }
        builder.resolveCall("System", "Round", new RoundInvocation(round));
        return round;
    }

    // DateTime Function Support

    private DateTime resolveDateTime(FunctionRef fun) {
        final DateTime dt = of.createDateTime();
        DateTimeInvocation.setDateTimeFieldsFromOperands(dt, fun.getOperand());
        builder.resolveCall("System", "DateTime", new DateTimeInvocation(dt));
        return dt;
    }

    private Date resolveDate(FunctionRef fun) {
        final Date d = of.createDate();
        DateInvocation.setDateFieldsFromOperands(d, fun.getOperand());
        builder.resolveCall("System", "Date", new DateInvocation(d));
        return d;
    }

    private Time resolveTime(FunctionRef fun) {
        final Time t = of.createTime();
        TimeInvocation.setTimeFieldsFromOperands(t, fun.getOperand());
        builder.resolveCall("System", "Time", new TimeInvocation(t));
        return t;
    }

    private Now resolveNow(FunctionRef fun) {
        checkNumberOfOperands(fun, 0);
        final Now now = of.createNow();
        builder.resolveCall("System", "Now", new ZeroOperandExpressionInvocation(now));
        return now;
    }

    private Today resolveToday(FunctionRef fun) {
        checkNumberOfOperands(fun, 0);
        final Today today = of.createToday();
        builder.resolveCall("System", "Today", new ZeroOperandExpressionInvocation(today));
        return today;
    }

    private TimeOfDay resolveTimeOfDay(FunctionRef fun) {
        checkNumberOfOperands(fun, 0);
        final TimeOfDay timeOfDay = of.createTimeOfDay();
        builder.resolveCall("System", "TimeOfDay", new ZeroOperandExpressionInvocation(timeOfDay));
        return timeOfDay;
    }

    // List Function Support

    private IndexOf resolveIndexOf(FunctionRef fun) {
        checkNumberOfOperands(fun, 2);
        final IndexOf indexOf = of.createIndexOf();
        indexOf.setSource(fun.getOperand().get(0));
        indexOf.setElement(fun.getOperand().get(1));
        builder.resolveCall("System", "IndexOf", new IndexOfInvocation(indexOf));
        return indexOf;
    }

    private First resolveFirst(FunctionRef fun) {
        checkNumberOfOperands(fun, 1);
        final First first = of.createFirst();
        first.setSource(fun.getOperand().get(0));
        builder.resolveCall("System", "First", new FirstInvocation(first));
        return first;
    }

    private Last resolveLast(FunctionRef fun) {
        checkNumberOfOperands(fun, 1);
        final Last last = of.createLast();
        last.setSource(fun.getOperand().get(0));
        builder.resolveCall("System", "Last", new LastInvocation(last));
        return last;
    }

    private Slice resolveSkip(FunctionRef fun) {
        checkNumberOfOperands(fun, 2);
        Slice slice = of.createSlice();
        slice.setSource(fun.getOperand().get(0));
        slice.setStartIndex(fun.getOperand().get(1));
        slice.setEndIndex(builder.buildNull(fun.getOperand().get(1).getResultType()));
        builder.resolveCall("System", "Skip", new SkipInvocation(slice));
        return slice;
    }

    private Slice resolveTake(FunctionRef fun) {
        checkNumberOfOperands(fun, 2);
        Slice slice = of.createSlice();
        slice.setSource(fun.getOperand().get(0));
        slice.setStartIndex(builder.createLiteral(0));
        Coalesce coalesce = of.createCoalesce().withOperand(fun.getOperand().get(1), builder.createLiteral(0));
        builder.resolveCall("System", "Coalesce", new NaryExpressionInvocation(coalesce));
        slice.setEndIndex(coalesce);
        builder.resolveCall("System", "Take", new TakeInvocation(slice));
        return slice;
    }

    private Slice resolveTail(FunctionRef fun) {
        checkNumberOfOperands(fun, 1);
        Slice slice = of.createSlice();
        slice.setSource(fun.getOperand().get(0));
        slice.setStartIndex(builder.createLiteral(1));
        slice.setEndIndex(builder.buildNull(builder.resolveTypeName("System", "Integer")));
        builder.resolveCall("System", "Tail", new TailInvocation(slice));
        return slice;
    }

    // String Function Support

    private Combine resolveCombine(FunctionRef fun) {
        if (fun.getOperand().isEmpty() || fun.getOperand().size() > 2) {
            throw new IllegalArgumentException("Could not resolve call to system operator Combine.  Expected 1 or 2 arguments.");
        }
        final Combine combine = of.createCombine().withSource(fun.getOperand().get(0));
        if (fun.getOperand().size() == 2) {
            combine.setSeparator(fun.getOperand().get(1));
        }
        builder.resolveCall("System", "Combine", new CombineInvocation(combine));
        return combine;
    }

    private Split resolveSplit(FunctionRef fun) {
        checkNumberOfOperands(fun, 2);
        final Split split = of.createSplit()
                .withStringToSplit(fun.getOperand().get(0))
                .withSeparator(fun.getOperand().get(1));
        builder.resolveCall("System", "Split", new SplitInvocation(split));
        return split;
    }

    private SplitOnMatches resolveSplitOnMatches(FunctionRef fun) {
        checkNumberOfOperands(fun, 2);
        final SplitOnMatches splitOnMatches = of.createSplitOnMatches()
                .withStringToSplit(fun.getOperand().get(0))
                .withSeparatorPattern(fun.getOperand().get(1));
        builder.resolveCall("System", "SplitOnMatches", new SplitOnMatchesInvocation(splitOnMatches));
        return splitOnMatches;
    }

    private PositionOf resolvePositionOf(FunctionRef fun) {
        checkNumberOfOperands(fun, 2);
        final PositionOf pos = of.createPositionOf()
                .withPattern(fun.getOperand().get(0))
                .withString(fun.getOperand().get(1));
        builder.resolveCall("System", "PositionOf", new PositionOfInvocation(pos));
        return pos;
    }

    private LastPositionOf resolveLastPositionOf(FunctionRef fun) {
        checkNumberOfOperands(fun, 2);
        final LastPositionOf pos = of.createLastPositionOf()
                .withPattern(fun.getOperand().get(0))
                .withString(fun.getOperand().get(1));
        builder.resolveCall("System", "LastPositionOf", new LastPositionOfInvocation(pos));
        return pos;
    }

    private Substring resolveSubstring(FunctionRef fun) {
        if (fun.getOperand().size() < 2 || fun.getOperand().size() > 3) {
            throw new IllegalArgumentException("Could not resolve call to system operator Substring.  Expected 2 or 3 arguments.");
        }
        final Substring substring = of.createSubstring()
                .withStringToSub(fun.getOperand().get(0))
                .withStartIndex(fun.getOperand().get(1));
        if (fun.getOperand().size() == 3) {
            substring.setLength(fun.getOperand().get(2));
        }
        builder.resolveCall("System", "Substring", new SubstringInvocation(substring));
        return substring;
    }

    // Error Functions
    private Message resolveMessage(FunctionRef fun) {
        if (fun.getOperand().size() != 5) {
            throw new IllegalArgumentException("Could not resolve call to system operator Message. Expected 5 arguments.");
        }

        Message message = of.createMessage()
                .withSource(fun.getOperand().get(0))
                .withCondition(fun.getOperand().get(1))
                .withCode(fun.getOperand().get(2))
                .withSeverity(fun.getOperand().get(3))
                .withMessage(fun.getOperand().get(4));

        builder.resolveCall("System", "Message", new MessageInvocation(message));
        return message;
    }

    // Type Functions

    private Convert resolveConvert(FunctionRef fun) {
        checkNumberOfOperands(fun, 1);
        final Convert convert = of.createConvert().withOperand(fun.getOperand().get(0));
        final SystemModel sm = builder.getSystemModel();
        switch (fun.getName()) {
            case "ToString":
                convert.setToType(builder.dataTypeToQName(sm.getString()));
                break;
            case "ToBoolean":
                convert.setToType(builder.dataTypeToQName(sm.getBoolean()));
                break;
            case "ToInteger":
                convert.setToType(builder.dataTypeToQName(sm.getInteger()));
                break;
            case "ToLong":
                convert.setToType(builder.dataTypeToQName(sm.getLong()));
                break;
            case "ToDecimal":
                convert.setToType(builder.dataTypeToQName(sm.getDecimal()));
                break;
            case "ToQuantity":
                convert.setToType(builder.dataTypeToQName(sm.getQuantity()));
                break;
            case "ToRatio":
                convert.setToType(builder.dataTypeToQName(sm.getRatio()));
                break;
            case "ToDate":
                convert.setToType(builder.dataTypeToQName(sm.getDate()));
                break;
            case "ToDateTime":
                convert.setToType(builder.dataTypeToQName(sm.getDateTime()));
                break;
            case "ToTime":
                convert.setToType(builder.dataTypeToQName(sm.getTime()));
                break;
            case "ToConcept":
                convert.setToType(builder.dataTypeToQName(sm.getConcept()));
                break;
            default:
                throw new IllegalArgumentException(
                        String.format("Could not resolve call to system operator %s. Unknown conversion type.", fun.getName()));
        }
        builder.resolveCall("System", fun.getName(), new ConvertInvocation(convert));
        return convert;
    }

    // General Function Support

    private UnaryExpression resolveUnary(FunctionRef fun) {
        UnaryExpression operator = null;
        try {
            Class<?> clazz = Class.forName("org.hl7.elm.r1." + fun.getName());
            if (UnaryExpression.class.isAssignableFrom(clazz)) {
                operator = (UnaryExpression) clazz.getConstructor().newInstance();
                checkNumberOfOperands(fun, 1);
                operator.setOperand(fun.getOperand().get(0));
                builder.resolveUnaryCall("System", fun.getName(), operator);
                return operator;
            }
        } catch (Exception e) {
            // Do nothing but fall through
        }
        return null;
    }

    private BinaryExpression resolveBinary(FunctionRef fun) {
        BinaryExpression operator = null;
        try {
            Class<?> clazz = Class.forName("org.hl7.elm.r1." + fun.getName());
            if (BinaryExpression.class.isAssignableFrom(clazz)) {
                operator = (BinaryExpression) clazz.getConstructor().newInstance();
                checkNumberOfOperands(fun, 2);
                operator.getOperand().addAll(fun.getOperand());
                builder.resolveBinaryCall("System", fun.getName(), operator);
                return operator;
            }
        } catch (Exception e) {
            // Do nothing but fall through
        }
        return null;
    }

    private TernaryExpression resolveTernary(FunctionRef fun) {
        TernaryExpression operator = null;
        try {
            Class<?> clazz = Class.forName("org.hl7.elm.r1." + fun.getName());
            if (TernaryExpression.class.isAssignableFrom(clazz)) {
                operator = (TernaryExpression)clazz.getConstructor().newInstance();
                checkNumberOfOperands(fun, 3);
                operator.getOperand().addAll(fun.getOperand());
                builder.resolveTernaryCall("System", fun.getName(), operator);
                return operator;
            }
        }
        catch (Exception e) {
            // Do nothing but fall through
        }
        return null;
    }

    private NaryExpression resolveNary(FunctionRef fun) {
        NaryExpression operator = null;
        try {
            Class<?> clazz = Class.forName("org.hl7.elm.r1." + fun.getName());
            if (NaryExpression.class.isAssignableFrom(clazz)) {
                operator = (NaryExpression) clazz.getConstructor().newInstance();
                operator.getOperand().addAll(fun.getOperand());
                builder.resolveCall("System", fun.getName(), new NaryExpressionInvocation(operator));
                return operator;
            }
        } catch (Exception e) {
            // Do nothing but fall through
        }
        return null;
    }

    private AggregateExpression resolveAggregate(FunctionRef fun) {
        AggregateExpression operator = null;
        try {
            Class<?> clazz = Class.forName("org.hl7.elm.r1." + fun.getName());
            if (AggregateExpression.class.isAssignableFrom(clazz)) {
                operator = (AggregateExpression) clazz.getConstructor().newInstance();
                checkNumberOfOperands(fun, 1);
                operator.setSource(fun.getOperand().get(0));
                builder.resolveAggregateCall("System", fun.getName(), operator);
                return operator;
            }
        } catch (Exception e) {
            // Do nothing but fall through
        }
        return null;
    }

    private void checkNumberOfOperands(FunctionRef fun, int expectedOperands) {
        if (fun.getOperand().size() != expectedOperands) {
            throw new IllegalArgumentException(String.format("Could not resolve call to system operator %s.  Expected %d arguments.",
                    fun.getName(), expectedOperands));
        }
    }
}
