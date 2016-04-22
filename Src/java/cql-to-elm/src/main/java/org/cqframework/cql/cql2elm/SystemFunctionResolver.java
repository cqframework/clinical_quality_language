package org.cqframework.cql.cql2elm;


import org.cqframework.cql.cql2elm.model.invocation.CombineInvocation;
import org.cqframework.cql.cql2elm.model.invocation.DateTimeInvocation;
import org.cqframework.cql.cql2elm.model.invocation.FirstInvocation;
import org.cqframework.cql.cql2elm.model.invocation.IndexOfInvocation;
import org.cqframework.cql.cql2elm.model.invocation.LastInvocation;
import org.cqframework.cql.cql2elm.model.invocation.NaryExpressionInvocation;
import org.cqframework.cql.cql2elm.model.invocation.PositionOfInvocation;
import org.cqframework.cql.cql2elm.model.invocation.RoundInvocation;
import org.cqframework.cql.cql2elm.model.invocation.SplitInvocation;
import org.cqframework.cql.cql2elm.model.invocation.SubstringInvocation;
import org.cqframework.cql.cql2elm.model.invocation.ZeroOperandExpressionInvocation;
import org.hl7.elm.r1.AggregateExpression;
import org.hl7.elm.r1.BinaryExpression;
import org.hl7.elm.r1.CalculateAge;
import org.hl7.elm.r1.CalculateAgeAt;
import org.hl7.elm.r1.Combine;
import org.hl7.elm.r1.DateTime;
import org.hl7.elm.r1.DateTimePrecision;
import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.First;
import org.hl7.elm.r1.FunctionRef;
import org.hl7.elm.r1.IndexOf;
import org.hl7.elm.r1.Last;
import org.hl7.elm.r1.NaryExpression;
import org.hl7.elm.r1.Now;
import org.hl7.elm.r1.ObjectFactory;
import org.hl7.elm.r1.PositionOf;
import org.hl7.elm.r1.Property;
import org.hl7.elm.r1.Round;
import org.hl7.elm.r1.Split;
import org.hl7.elm.r1.Substring;
import org.hl7.elm.r1.Today;
import org.hl7.elm.r1.UnaryExpression;

import java.util.ArrayList;
import java.util.List;

public class SystemFunctionResolver {
    private final ObjectFactory of = new ObjectFactory();
    private final Cql2ElmVisitor visitor;

    public SystemFunctionResolver(Cql2ElmVisitor visitor) {
        this.visitor = visitor;
    }

    public Expression resolveSystemFunction(FunctionRef fun) {
        if (fun.getLibraryName() == null) {
            switch (fun.getName()) {
                // Aggregate Functions
                case "AllTrue":
                case "AnyTrue":
                case "Avg":
                case "Count":
                case "Max":
                case "Median":
                case "Min":
                case "Mode":
                case "PopulationStdDev":
                case "PopulationVariance":
                case "StdDev":
                case "Sum":
                case "Variance": {
                    return resolveAggregate(fun);
                }

                // Arithmetic Functions
                case "Abs":
                case "Ceiling":
                case "Floor":
                case "Exp":
                case "Ln":
                case "Truncate": {
                    return resolveUnary(fun);
                }

                case "Log": {
                    return resolveBinary(fun);
                }

                case "Round": {
                    return resolveRound(fun);
                }

                // Clinical Functions
                case "AgeInYears":
                case "AgeInMonths":
                case "AgeInDays":
                case "AgeInHours":
                case "AgeInMinutes":
                case "AgeInSeconds":
                case "AgeInMilliseconds": {
                    checkNumberOfOperands(fun, 0);
                    return resolveCalculateAge(getPatientBirthDateProperty(), resolveAgeRelatedFunctionPrecision(fun));
                }

                case "AgeInYearsAt":
                case "AgeInMonthsAt":
                case "AgeInDaysAt":
                case "AgeInHoursAt":
                case "AgeInMinutesAt":
                case "AgeInSecondsAt":
                case "AgeInMillisecondsAt": {
                    List<Expression> ops = new ArrayList<>();
                    ops.add(getPatientBirthDateProperty());
                    ops.addAll(fun.getOperand());
                    return resolveCalculateAgeAt(ops, resolveAgeRelatedFunctionPrecision(fun));
                }

                case "CalculateAgeInMonths":
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

                case "Now": {
                    return resolveNow(fun);
                }

                case "Today": {
                    return resolveToday(fun);
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

                // Nullological Functions
                case "Coalesce": {
                    return resolveNary(fun);
                }

                // Overloaded Functions
                case "Length": {
                    return resolveUnary(fun);
                }

                // String Functions
                case "Combine": {
                    return resolveCombine(fun);
                }

                case "Split": {
                    return resolveSplit(fun);
                }

                case "Upper":
                case "Lower": {
                    return resolveUnary(fun);
                }

                case "PositionOf": {
                    return resolvePositionOf(fun);
                }

                case "Substring": {
                    return resolveSubstring(fun);
                }
            }
        }

        return null;
    }

    // Age-Related Function Support

    private CalculateAge resolveCalculateAge(Expression e, DateTimePrecision p) {
        CalculateAge operator = of.createCalculateAge()
                .withPrecision(p)
                .withOperand(e);

        visitor.resolveUnaryCall("System", "CalculateAge", operator);
        return operator;
    }

    private CalculateAgeAt resolveCalculateAgeAt(List<Expression> e, DateTimePrecision p) {
        CalculateAgeAt operator = of.createCalculateAgeAt()
                .withPrecision(p)
                .withOperand(e);

        visitor.resolveBinaryCall("System", "CalculateAgeAt", operator);
        return operator;
    }

    private static DateTimePrecision resolveAgeRelatedFunctionPrecision(FunctionRef fun) {
        String name = fun.getName();
        if (name.contains("Years")) {
            return DateTimePrecision.YEAR;
        } else if (name.contains("Months")) {
            return DateTimePrecision.MONTH;
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

    private Property getPatientBirthDateProperty() {
        Expression source = visitor.resolveIdentifier("Patient");
        Property property = of.createProperty().withSource(source).withPath(visitor.getModel().getModelInfo().getPatientBirthDatePropertyName());
        property.setResultType(visitor.resolvePath(source.getResultType(), property.getPath()));
        return property;
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
        visitor.resolveCall("System", "Round", new RoundInvocation(round));
        return round;
    }

    // DateTime Function Support

    private DateTime resolveDateTime(FunctionRef fun) {
        final DateTime dt = of.createDateTime();
        DateTimeInvocation.setDateTimeFieldsFromOperands(dt, fun.getOperand());
        visitor.resolveCall("System", "DateTime", new DateTimeInvocation(dt));
        return dt;
    }

    private Now resolveNow(FunctionRef fun) {
        checkNumberOfOperands(fun, 0);
        final Now now = of.createNow();
        visitor.resolveCall("System", "Now", new ZeroOperandExpressionInvocation(now));
        return now;
    }

    private Today resolveToday(FunctionRef fun) {
        checkNumberOfOperands(fun, 0);
        final Today today = of.createToday();
        visitor.resolveCall("System", "Today", new ZeroOperandExpressionInvocation(today));
        return today;
    }

    // List Function Support

    private IndexOf resolveIndexOf(FunctionRef fun) {
        checkNumberOfOperands(fun, 2);
        final IndexOf indexOf = of.createIndexOf();
        indexOf.setSource(fun.getOperand().get(0));
        indexOf.setElement(fun.getOperand().get(1));
        visitor.resolveCall("System", "IndexOf", new IndexOfInvocation(indexOf));
        return indexOf;
    }

    private First resolveFirst(FunctionRef fun) {
        checkNumberOfOperands(fun, 1);
        final First first = of.createFirst();
        first.setSource(fun.getOperand().get(0));
        visitor.resolveCall("System", "First", new FirstInvocation(first));
        return first;
    }

    private Last resolveLast(FunctionRef fun) {
        checkNumberOfOperands(fun, 1);
        final Last last = of.createLast();
        last.setSource(fun.getOperand().get(0));
        visitor.resolveCall("System", "Last", new LastInvocation(last));
        return last;
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
        visitor.resolveCall("System", "Combine", new CombineInvocation(combine));
        return combine;
    }

    private Split resolveSplit(FunctionRef fun) {
        checkNumberOfOperands(fun, 2);
        final Split split = of.createSplit()
                .withStringToSplit(fun.getOperand().get(0))
                .withSeparator(fun.getOperand().get(1));
        visitor.resolveCall("System", "Split", new SplitInvocation(split));
        return split;
    }

    private PositionOf resolvePositionOf(FunctionRef fun) {
        checkNumberOfOperands(fun, 2);
        final PositionOf pos = of.createPositionOf()
                .withPattern(fun.getOperand().get(0))
                .withString(fun.getOperand().get(1));
        visitor.resolveCall("System", "PositionOf", new PositionOfInvocation(pos));
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
        visitor.resolveCall("System", "Substring", new SubstringInvocation(substring));
        return substring;
    }

    // General Function Support

    private UnaryExpression resolveUnary(FunctionRef fun) {
        UnaryExpression operator = null;
        try {
            Class clazz = Class.forName("org.hl7.elm.r1." + fun.getName());
            if (UnaryExpression.class.isAssignableFrom(clazz)) {
                operator = (UnaryExpression) clazz.newInstance();
                checkNumberOfOperands(fun, 1);
                operator.setOperand(fun.getOperand().get(0));
                visitor.resolveUnaryCall("System", fun.getName(), operator);
                return operator;
            }
        } catch (Exception e) {
            // Do nothing but fall through
        }
        return operator;
    }

    private BinaryExpression resolveBinary(FunctionRef fun) {
        BinaryExpression operator = null;
        try {
            Class clazz = Class.forName("org.hl7.elm.r1." + fun.getName());
            if (BinaryExpression.class.isAssignableFrom(clazz)) {
                operator = (BinaryExpression) clazz.newInstance();
                checkNumberOfOperands(fun, 2);
                operator.getOperand().addAll(fun.getOperand());
                visitor.resolveBinaryCall("System", fun.getName(), operator);
                return operator;
            }
        } catch (Exception e) {
            // Do nothing but fall through
        }
        return operator;
    }

    private NaryExpression resolveNary(FunctionRef fun) {
        NaryExpression operator = null;
        try {
            Class clazz = Class.forName("org.hl7.elm.r1." + fun.getName());
            if (NaryExpression.class.isAssignableFrom(clazz)) {
                operator = (NaryExpression) clazz.newInstance();
                operator.getOperand().addAll(fun.getOperand());
                visitor.resolveCall("System", fun.getName(), new NaryExpressionInvocation(operator));
                return operator;
            }
        } catch (Exception e) {
            // Do nothing but fall through
        }
        return operator;
    }

    private AggregateExpression resolveAggregate(FunctionRef fun) {
        AggregateExpression operator = null;
        try {
            Class clazz = Class.forName("org.hl7.elm.r1." + fun.getName());
            if (AggregateExpression.class.isAssignableFrom(clazz)) {
                operator = (AggregateExpression) clazz.newInstance();
                checkNumberOfOperands(fun, 1);
                operator.setSource(fun.getOperand().get(0));
                visitor.resolveAggregateCall("System", fun.getName(), operator);
                return operator;
            }
        } catch (Exception e) {
            // Do nothing but fall through
        }
        return operator;
    }

    private void checkNumberOfOperands(FunctionRef fun, int expectedOperands) {
        if (fun.getOperand().size() != expectedOperands) {
            throw new IllegalArgumentException(String.format("Could not resolve call to system operator %s.  Expected %d arguments.",
                    fun.getName(), expectedOperands));
        }
    }
}
