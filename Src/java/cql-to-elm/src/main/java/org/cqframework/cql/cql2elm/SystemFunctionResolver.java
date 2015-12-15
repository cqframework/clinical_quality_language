package org.cqframework.cql.cql2elm;


import org.hl7.elm.r1.AggregateExpression;
import org.hl7.elm.r1.CalculateAge;
import org.hl7.elm.r1.CalculateAgeAt;
import org.hl7.elm.r1.DateTimePrecision;
import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.FunctionRef;
import org.hl7.elm.r1.ObjectFactory;
import org.hl7.elm.r1.Property;

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
            }
        }

        return null;
    }

    // Aggregate Functions
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

    // Clinical Functions

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
        property.setResultType(visitor.resolveProperty(source.getResultType(), property.getPath()));
        return property;
    }

    private void checkNumberOfOperands(FunctionRef fun, int expectedOperands) {
        if (fun.getOperand().size() != expectedOperands) {
            throw new IllegalArgumentException(String.format("Could not resolve call to system operator %s.  Wrong number of arguments: %d.",
                    fun.getName(), expectedOperands));
        }
    }
}
