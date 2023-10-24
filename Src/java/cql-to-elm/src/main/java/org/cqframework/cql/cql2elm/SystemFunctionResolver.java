package org.cqframework.cql.cql2elm;


import org.cqframework.cql.cql2elm.model.Conversion;
import org.cqframework.cql.cql2elm.model.Invocation;
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

    public Invocation resolveSystemFunction(FunctionRef fun) {
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

    private UnaryExpressionInvocation resolveCalculateAge(Expression e, DateTimePrecision p) {
        CalculateAge operator = of.createCalculateAge()
                .withPrecision(p)
                .withOperand(e);

        UnaryExpressionInvocation invocation = new UnaryExpressionInvocation(operator);
        builder.resolveInvocation("System", "CalculateAge", invocation);
        return invocation;
    }

    private BinaryExpressionInvocation resolveCalculateAgeAt(List<Expression> e, DateTimePrecision p) {
        CalculateAgeAt operator = of.createCalculateAgeAt()
                .withPrecision(p)
                .withOperand(e);

        BinaryExpressionInvocation invocation = new BinaryExpressionInvocation(operator);
        builder.resolveInvocation("System", "CalculateAgeAt", invocation);
        return invocation;
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

    private RoundInvocation resolveRound(FunctionRef fun) {
        if (fun.getOperand().isEmpty() || fun.getOperand().size() > 2) {
            throw new IllegalArgumentException("Could not resolve call to system operator Round.  Expected 1 or 2 arguments.");
        }
        final Round round = of.createRound().withOperand(fun.getOperand().get(0));
        if (fun.getOperand().size() == 2) {
            round.setPrecision(fun.getOperand().get(1));
        }
        RoundInvocation invocation = new RoundInvocation(round);
        builder.resolveInvocation("System", "Round", new RoundInvocation(round));
        return invocation;
    }

    // DateTime Function Support

    private DateTimeInvocation resolveDateTime(FunctionRef fun) {
        final DateTime dt = of.createDateTime();
        DateTimeInvocation.setDateTimeFieldsFromOperands(dt, fun.getOperand());
        DateTimeInvocation invocation = new DateTimeInvocation(dt);
        builder.resolveInvocation("System", "DateTime", invocation);
        return invocation;
    }

    private DateInvocation resolveDate(FunctionRef fun) {
        final Date d = of.createDate();
        DateInvocation.setDateFieldsFromOperands(d, fun.getOperand());
        DateInvocation invocation = new DateInvocation(d);
        builder.resolveInvocation("System", "Date", invocation);
        return invocation;
    }

    private TimeInvocation resolveTime(FunctionRef fun) {
        final Time t = of.createTime();
        TimeInvocation.setTimeFieldsFromOperands(t, fun.getOperand());
        TimeInvocation invocation = new TimeInvocation(t);
        builder.resolveInvocation("System", "Time", invocation);
        return invocation;
    }

    private ZeroOperandExpressionInvocation resolveNow(FunctionRef fun) {
        checkNumberOfOperands(fun, 0);
        final Now now = of.createNow();
        ZeroOperandExpressionInvocation invocation = new ZeroOperandExpressionInvocation(now);
        builder.resolveInvocation("System", "Now", invocation);
        return invocation;
    }

    private ZeroOperandExpressionInvocation resolveToday(FunctionRef fun) {
        checkNumberOfOperands(fun, 0);
        final Today today = of.createToday();
        ZeroOperandExpressionInvocation invocation = new ZeroOperandExpressionInvocation(today);
        builder.resolveInvocation("System", "Today", invocation);
        return invocation;
    }

    private ZeroOperandExpressionInvocation resolveTimeOfDay(FunctionRef fun) {
        checkNumberOfOperands(fun, 0);
        final TimeOfDay timeOfDay = of.createTimeOfDay();
        ZeroOperandExpressionInvocation invocation = new ZeroOperandExpressionInvocation(timeOfDay);
        builder.resolveInvocation("System", "TimeOfDay", invocation);
        return invocation;
    }

    // List Function Support

    private IndexOfInvocation resolveIndexOf(FunctionRef fun) {
        checkNumberOfOperands(fun, 2);
        final IndexOf indexOf = of.createIndexOf();
        indexOf.setSource(fun.getOperand().get(0));
        indexOf.setElement(fun.getOperand().get(1));
        IndexOfInvocation invocation = new IndexOfInvocation(indexOf);
        builder.resolveInvocation("System", "IndexOf", invocation);
        return invocation;
    }

    private FirstInvocation resolveFirst(FunctionRef fun) {
        checkNumberOfOperands(fun, 1);
        final First first = of.createFirst();
        first.setSource(fun.getOperand().get(0));
        FirstInvocation invocation = new FirstInvocation(first);
        builder.resolveInvocation("System", "First", invocation);
        return invocation;
    }

    private LastInvocation resolveLast(FunctionRef fun) {
        checkNumberOfOperands(fun, 1);
        final Last last = of.createLast();
        last.setSource(fun.getOperand().get(0));
        LastInvocation invocation = new LastInvocation(last);
        builder.resolveInvocation("System", "Last", invocation);
        return invocation;
    }

    private SkipInvocation resolveSkip(FunctionRef fun) {
        checkNumberOfOperands(fun, 2);
        Slice slice = of.createSlice();
        slice.setSource(fun.getOperand().get(0));
        slice.setStartIndex(fun.getOperand().get(1));
        slice.setEndIndex(builder.buildNull(fun.getOperand().get(1).getResultType()));
        SkipInvocation invocation = new SkipInvocation(slice);
        builder.resolveInvocation("System", "Skip", invocation);
        return invocation;
    }

    private TakeInvocation resolveTake(FunctionRef fun) {
        checkNumberOfOperands(fun, 2);
        Slice slice = of.createSlice();
        slice.setSource(fun.getOperand().get(0));
        slice.setStartIndex(builder.createLiteral(0));
        Coalesce coalesce = of.createCoalesce().withOperand(fun.getOperand().get(1), builder.createLiteral(0));
        NaryExpressionInvocation naryInvocation = new NaryExpressionInvocation(coalesce);
        builder.resolveInvocation("System", "Coalesce", naryInvocation);
        slice.setEndIndex(coalesce);
        TakeInvocation invocation = new TakeInvocation(slice);
        builder.resolveInvocation("System", "Take", invocation);
        return invocation;
    }

    private TailInvocation resolveTail(FunctionRef fun) {
        checkNumberOfOperands(fun, 1);
        Slice slice = of.createSlice();
        slice.setSource(fun.getOperand().get(0));
        slice.setStartIndex(builder.createLiteral(1));
        slice.setEndIndex(builder.buildNull(builder.resolveTypeName("System", "Integer")));
        TailInvocation invocation = new TailInvocation(slice);
        builder.resolveInvocation("System", "Tail", invocation);
        return invocation;
    }

    // String Function Support

    private CombineInvocation resolveCombine(FunctionRef fun) {
        if (fun.getOperand().isEmpty() || fun.getOperand().size() > 2) {
            throw new IllegalArgumentException("Could not resolve call to system operator Combine.  Expected 1 or 2 arguments.");
        }
        final Combine combine = of.createCombine().withSource(fun.getOperand().get(0));
        if (fun.getOperand().size() == 2) {
            combine.setSeparator(fun.getOperand().get(1));
        }
        CombineInvocation invocation = new CombineInvocation(combine);
        builder.resolveInvocation("System", "Combine", invocation);
        return invocation;
    }

    private SplitInvocation resolveSplit(FunctionRef fun) {
        checkNumberOfOperands(fun, 2);
        final Split split = of.createSplit()
                .withStringToSplit(fun.getOperand().get(0))
                .withSeparator(fun.getOperand().get(1));
        SplitInvocation invocation = new SplitInvocation(split);
        builder.resolveInvocation("System", "Split", invocation);
        return invocation;
    }

    private SplitOnMatchesInvocation resolveSplitOnMatches(FunctionRef fun) {
        checkNumberOfOperands(fun, 2);
        final SplitOnMatches splitOnMatches = of.createSplitOnMatches()
                .withStringToSplit(fun.getOperand().get(0))
                .withSeparatorPattern(fun.getOperand().get(1));
        SplitOnMatchesInvocation invocation = new SplitOnMatchesInvocation(splitOnMatches);
        builder.resolveInvocation("System", "SplitOnMatches", invocation);
        return invocation;
    }

    private PositionOfInvocation resolvePositionOf(FunctionRef fun) {
        checkNumberOfOperands(fun, 2);
        final PositionOf pos = of.createPositionOf()
                .withPattern(fun.getOperand().get(0))
                .withString(fun.getOperand().get(1));
        PositionOfInvocation invocation = new PositionOfInvocation(pos);
        builder.resolveInvocation("System", "PositionOf", invocation);
        return invocation;
    }

    private LastPositionOfInvocation resolveLastPositionOf(FunctionRef fun) {
        checkNumberOfOperands(fun, 2);
        final LastPositionOf pos = of.createLastPositionOf()
                .withPattern(fun.getOperand().get(0))
                .withString(fun.getOperand().get(1));
        LastPositionOfInvocation invocation = new LastPositionOfInvocation(pos);
        builder.resolveInvocation("System", "LastPositionOf", invocation);
        return invocation;
    }

    private SubstringInvocation resolveSubstring(FunctionRef fun) {
        if (fun.getOperand().size() < 2 || fun.getOperand().size() > 3) {
            throw new IllegalArgumentException("Could not resolve call to system operator Substring.  Expected 2 or 3 arguments.");
        }
        final Substring substring = of.createSubstring()
                .withStringToSub(fun.getOperand().get(0))
                .withStartIndex(fun.getOperand().get(1));
        if (fun.getOperand().size() == 3) {
            substring.setLength(fun.getOperand().get(2));
        }
        SubstringInvocation invocation = new SubstringInvocation(substring);
        builder.resolveInvocation("System", "Substring", invocation);
        return invocation;
    }

    // Error Functions
    private MessageInvocation resolveMessage(FunctionRef fun) {
        if (fun.getOperand().size() != 5) {
            throw new IllegalArgumentException("Could not resolve call to system operator Message. Expected 5 arguments.");
        }

        Message message = of.createMessage()
                .withSource(fun.getOperand().get(0))
                .withCondition(fun.getOperand().get(1))
                .withCode(fun.getOperand().get(2))
                .withSeverity(fun.getOperand().get(3))
                .withMessage(fun.getOperand().get(4));

        MessageInvocation invocation = new MessageInvocation(message);
        builder.resolveInvocation("System", "Message", invocation);
        return invocation;
    }

    // Type Functions

    private ConvertInvocation resolveConvert(FunctionRef fun) {
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
        ConvertInvocation invocation = new ConvertInvocation(convert);
        builder.resolveInvocation("System", fun.getName(), invocation);
        return invocation;
    }

    // General Function Support

    private UnaryExpressionInvocation resolveUnary(FunctionRef fun) {
        UnaryExpression operator = null;
        try {
            Class<?> clazz = Class.forName("org.hl7.elm.r1." + fun.getName());
            if (UnaryExpression.class.isAssignableFrom(clazz)) {
                operator = (UnaryExpression) clazz.getConstructor().newInstance();
                checkNumberOfOperands(fun, 1);
                operator.setOperand(fun.getOperand().get(0));
                UnaryExpressionInvocation invocation = new UnaryExpressionInvocation(operator);
                builder.resolveInvocation("System", fun.getName(), invocation);
                return invocation;
            }
        } catch (Exception e) {
            // Do nothing but fall through
        }
        return null;
    }

    private BinaryExpressionInvocation resolveBinary(FunctionRef fun) {
        BinaryExpression operator = null;
        try {
            Class<?> clazz = Class.forName("org.hl7.elm.r1." + fun.getName());
            if (BinaryExpression.class.isAssignableFrom(clazz)) {
                operator = (BinaryExpression) clazz.getConstructor().newInstance();
                checkNumberOfOperands(fun, 2);
                operator.getOperand().addAll(fun.getOperand());
                BinaryExpressionInvocation invocation = new BinaryExpressionInvocation(operator);
                builder.resolveInvocation("System", fun.getName(), invocation);
                return invocation;
            }
        } catch (Exception e) {
            // Do nothing but fall through
        }
        return null;
    }

    private TernaryExpressionInvocation resolveTernary(FunctionRef fun) {
        TernaryExpression operator = null;
        try {
            Class<?> clazz = Class.forName("org.hl7.elm.r1." + fun.getName());
            if (TernaryExpression.class.isAssignableFrom(clazz)) {
                operator = (TernaryExpression)clazz.getConstructor().newInstance();
                checkNumberOfOperands(fun, 3);
                operator.getOperand().addAll(fun.getOperand());
                TernaryExpressionInvocation invocation = new TernaryExpressionInvocation(operator);
                builder.resolveInvocation("System", fun.getName(), invocation);
                return invocation;
            }
        }
        catch (Exception e) {
            // Do nothing but fall through
        }
        return null;
    }

    private NaryExpressionInvocation resolveNary(FunctionRef fun) {
        NaryExpression operator = null;
        try {
            Class<?> clazz = Class.forName("org.hl7.elm.r1." + fun.getName());
            if (NaryExpression.class.isAssignableFrom(clazz)) {
                operator = (NaryExpression) clazz.getConstructor().newInstance();
                operator.getOperand().addAll(fun.getOperand());
                NaryExpressionInvocation invocation = new NaryExpressionInvocation(operator);
                builder.resolveInvocation("System", fun.getName(), invocation);
                return invocation;
            }
        } catch (Exception e) {
            // Do nothing but fall through
        }
        return null;
    }

    private AggregateExpressionInvocation resolveAggregate(FunctionRef fun) {
        AggregateExpression operator = null;
        try {
            Class<?> clazz = Class.forName("org.hl7.elm.r1." + fun.getName());
            if (AggregateExpression.class.isAssignableFrom(clazz)) {
                operator = (AggregateExpression) clazz.getConstructor().newInstance();
                checkNumberOfOperands(fun, 1);
                operator.setSource(fun.getOperand().get(0));
                AggregateExpressionInvocation invocation = new AggregateExpressionInvocation(operator);
                builder.resolveInvocation("System", fun.getName(), invocation);
                return invocation;
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
