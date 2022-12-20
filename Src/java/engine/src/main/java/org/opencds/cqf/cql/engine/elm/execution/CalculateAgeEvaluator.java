package org.opencds.cqf.cql.engine.elm.execution;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.Context;
import org.opencds.cqf.cql.engine.runtime.Date;
import org.opencds.cqf.cql.engine.runtime.DateTime;

// for Uncertainty
/*
CalculateAgeInYears(birthDate Date) Integer
CalculateAgeInYears(birthDate DateTime) Integer
CalculateAgeInMonths(birthDate Date) Integer
CalculateAgeInMonths(birthDate DateTime) Integer
CalculateAgeInWeeks(birthDate Date) Integer
CalculateAgeInWeeks(birthDate DateTime) Integer
CalculateAgeInDays(birthDate Date) Integer
CalculateAgeInDays(birthDate DateTime) Integer
CalculateAgeInHours(birthDate DateTime) Integer
CalculateAgeInMinutes(birthDate DateTime) Integer
CalculateAgeInSeconds(birthDate DateTime) Integer

The CalculateAge operators calculate the age of a person born on the given birth date/time
    as of today/now in the precision named in the operator.

If the birthdate is null, the result is null.

The CalculateAge operators are defined in terms of a date/time duration calculation.
    This means that if the given birthDate is not specified to the level of precision corresponding
        to the operator being invoked, the result will be an uncertainty over the range of possible values,
        potentially causing some comparisons to return null.
*/

public class CalculateAgeEvaluator extends org.cqframework.cql.elm.execution.CalculateAge {

    public static Object calculateAge(Object operand, String precision, Object today) {
        if (operand == null) {
            return null;
        }

        if (operand instanceof Date || operand instanceof DateTime)
        {
            return CalculateAgeAtEvaluator.calculateAgeAt(operand, today, precision);
        }

        throw new InvalidOperatorArgument(
                "CalculateAgeInYears(Date), CalculateAgeInYears(DateTime), CalculateAgeInMonths(Date), CalculateAgeInMonths(DateTime), CalculateAgeInWeeks(Date), CalculateAgeInWeeks(DateTime), CalculateAgeInDays(Date), CalculateAgeInDays(DateTime), CalculateAgeInHours(Date), CalculateAgeInHours(DateTime), CalculateAgeInMinutes(Date), CalculateAgeInMinutes(DateTime), CalculateAgeInSeconds(Date), CalculateAgeInSeconds(DateTime)",
                String.format("CalculateAgeIn%ss(%s)", precision, operand.getClass().getName())
        );
    }

    @Override
    protected Object internalEvaluate(Context context) {
        Object operand = getOperand().evaluate(context);
        String precision = getPrecision().value();

        Object today =
                operand instanceof Date
                        ? DateFromEvaluator.dateFrom(context.getEvaluationDateTime())
                        : context.getEvaluationDateTime();

        return calculateAge(operand, precision, today);
    }
}