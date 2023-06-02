package org.opencds.cqf.cql.engine.elm.execution;

import java.math.BigDecimal;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.Context;
import org.opencds.cqf.cql.engine.runtime.Quantity;

/*

Product(argument List<Integer>) Integer
Product(argument List<Long>) Long
Product(argument List<Decimal>) Decimal
Product(argument List<Quantity>) Quantity

The Product operator returns the geometric product of the elements in source.

If the source contains no non-null elements, null is returned.

If the source is null, the result is null.

*/

public class ProductEvaluator extends org.cqframework.cql.elm.execution.Product {

    public static Object product(Object source) {
        if (source == null) {
            return null;
        }

        if (source instanceof Iterable) {
            Object result = null;
            for (Object element : (Iterable<?>) source) {
                if (element == null) return null;
                if (result == null) {
                    result = element;
                    continue;
                }
                if ((element instanceof Integer && result instanceof Integer)
                    || (element instanceof Long && result instanceof Long)
                    || (element instanceof BigDecimal && result instanceof BigDecimal)) {
                    result = MultiplyEvaluator.multiply(result, element);
                } else if (element instanceof Quantity && result instanceof Quantity) {
                    if (!((Quantity) element).getUnit().equals(((Quantity) result).getUnit())) {
                        // TODO: try to normalize units?
                        throw new IllegalArgumentException(
                                String.format(
                                        "Found different units during Quantity product evaluation: %s and %s",
                                        ((Quantity) element).getUnit(), ((Quantity) result).getUnit()
                                )
                        );
                    }
                    ((Quantity) result).setValue(
                            (BigDecimal) MultiplyEvaluator.multiply(
                                    ((Quantity) result).getValue(),
                                    ((Quantity) element).getValue()
                            )
                    );
                } else {
                    throw new InvalidOperatorArgument(
                        "Product(List<Integer>), Product(List<Long>), Product(List<Decimal>) or Product(List<Quantity>)",
                        String.format("Product(List<%s>)", element.getClass().getName())
                    );
                }
            }

            return result;
        }

        throw new InvalidOperatorArgument(
            "Product(List<Integer>), Product(List<Long>), Product(List<Decimal>) or Product(List<Quantity>)",
            String.format("Product(%s)", source.getClass().getName())
        );
    }

    @Override
    protected Object internalEvaluate(Context context) {
        Object source = getSource().evaluate(context);
        return product(source);
    }
}
