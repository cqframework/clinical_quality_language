package org.opencds.cqf.cql.engine.elm.execution;

import java.math.BigDecimal;

import org.fhir.ucum.Decimal;
import org.fhir.ucum.UcumService;
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.Context;
import org.opencds.cqf.cql.engine.runtime.Quantity;

/*
    convert <quantity> to <unit>
    ConvertQuantity(argument Quantity, unit String)

    The ConvertQuantity operator converts a Quantity to an equivalent Quantity with the given unit. If the unit of the
        input quantity can be converted to the target unit, the result is an equivalent Quantity with the target unit.
        Otherwise, the result is null.

    Note that implementations are not required to support quantity conversion. Implementations that do support unit
        conversion shall do so according to the conversion specified by UCUM. Implementations that do not support unit
        conversion shall throw an error if an unsupported unit conversion is requested with this operation.

    If either argument is null, the result is null.

    define ConvertQuantity: ConvertQuantity(5 'mg', 'g')
    define ConvertSyntax: convert 5 'mg' to 'g'

*/

public class ConvertQuantityEvaluator extends org.cqframework.cql.elm.execution.ConvertQuantity {

    public static Object convertQuantity(Object argument, Object unit, UcumService ucumService) {
        if (argument == null || unit == null) {
            return null;
        }

        if (argument instanceof Quantity) {
            if (ucumService == null) {
                return null;
            }
            try {
                Decimal result = ucumService.convert(new Decimal(String.valueOf(((Quantity) argument).getValue())), ((Quantity) argument).getUnit(), (String) unit);
                return new Quantity().withValue(new BigDecimal(result.asDecimal())).withUnit((String) unit);
            } catch (Exception e) {
                return null;
            }
        }

        throw new InvalidOperatorArgument(
                "ConvertQuantity(Quantity, String)",
                String.format("ConvertQuantity(%s, %s)", argument.getClass().getName(), unit.getClass().getName())
        );
    }

    @Override
    protected Object internalEvaluate(Context context) {
        Object argument = getOperand().get(0).evaluate(context);
        Object unit = getOperand().get(1).evaluate(context);
        return convertQuantity(argument, unit, context.getUcumService());
    }

}
