package org.opencds.cqf.cql.engine.elm.visiting;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.exception.UndefinedResult;
import org.opencds.cqf.cql.engine.execution.State;
import org.opencds.cqf.cql.engine.runtime.BaseTemporal;
import org.opencds.cqf.cql.engine.runtime.Interval;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
except(left Interval<T>, right Interval<T>) Interval<T>
The except operator for intervals returns the set difference of two intervals.
  More precisely, this operator returns the portion of the first interval that does not overlap with the second.
  Note that to avoid returning an improper interval, if the second argument is properly contained within the first and
    does not start or end it, this operator returns null.
If either argument is null, the result is null.

except(left List<T>, right List<T>) List<T>
The except operator returns the set difference of two lists.
    More precisely, the operator returns a list with the elements that appear in the first operand
    that do not appear in the second operand.

This operator uses equality semantics to determine whether two elements are the same for the purposes of computing the difference.

The operator is defined with set semantics, meaning that each element will appear in the result at most once,
    and that there is no expectation that the order of the inputs will be preserved in the results.

If either argument is null, the result is null.
*/

public class ExceptEvaluator
{
    public static Object except(Object left, Object right, State state)
    {
        if (left == null )
        {
            return null;
        }

        if(!(left instanceof Iterable) && right == null){
            return null;
        }

        if (left instanceof Interval) {
            Object leftStart = ((Interval)left).getStart();
            Object leftEnd = ((Interval)left).getEnd();
            Object rightStart = ((Interval)right).getStart();
            Object rightEnd = ((Interval)right).getEnd();

            if (leftStart == null || leftEnd == null
                    || rightStart == null || rightEnd == null)
            {
                return null;
            }

            // Return null when:
            // left and right are equal
            // right properly includes left
            // left properly includes right and right doesn't start or end left
            String precision = null;
            if (leftStart instanceof BaseTemporal && rightStart instanceof BaseTemporal)
            {
                precision = BaseTemporal.getHighestPrecision((BaseTemporal) leftStart, (BaseTemporal) leftEnd, (BaseTemporal) rightStart, (BaseTemporal) rightEnd);
            }

            Boolean leftEqualRight = EqualEvaluator.equal(left, right, state);
            Boolean rightProperlyIncludesLeft = ProperIncludesEvaluator.properlyIncludes(right, left, precision, state);
            Boolean leftProperlyIncludesRight = ProperIncludesEvaluator.properlyIncludes(left, right, precision, state);
            Boolean rightStartsLeft = StartsEvaluator.starts(right, left, precision, state);
            Boolean rightEndsLeft = EndsEvaluator.ends(right, left, precision, state);
            Boolean isUndefined = AnyTrueEvaluator.anyTrue(
                    Arrays.asList(
                            leftEqualRight,
                            rightProperlyIncludesLeft,
                            AndEvaluator.and(
                                    leftProperlyIncludesRight,
                                    AndEvaluator.and(
                                            NotEvaluator.not(rightStartsLeft),
                                            NotEvaluator.not(rightEndsLeft)
                                    )
                            )
                    )
            );

            if (isUndefined != null && isUndefined)
            {
                return null;
            }

            if (GreaterEvaluator.greater(rightStart, leftEnd, state))
            {
                return left;
            }

            else if (AndEvaluator.and(LessEvaluator.less(leftStart, rightStart, state), GreaterEvaluator.greater(leftEnd, rightEnd, state)))
            {
                return null;
            }

            // left interval starts before right interval
            if (AndEvaluator.and(LessEvaluator.less(leftStart, rightStart, state), LessOrEqualEvaluator.lessOrEqual(leftEnd, rightEnd, state)))
            {
                Object min = LessEvaluator.less(PredecessorEvaluator.predecessor(rightStart), leftEnd, state) ? PredecessorEvaluator.predecessor(rightStart) : leftEnd;
                return new Interval(leftStart, true, min, true);
            }

            // right interval starts before left interval
            else if (AndEvaluator.and(GreaterEvaluator.greater(leftEnd, rightEnd, state), GreaterOrEqualEvaluator.greaterOrEqual(leftStart, rightStart, state)))
            {
                Object max = GreaterEvaluator.greater(SuccessorEvaluator.successor(rightEnd), leftStart, state) ? SuccessorEvaluator.successor(rightEnd) : leftStart;
                return new Interval(max, true, leftEnd, true);
            }

            throw new UndefinedResult(String.format("The following interval values led to an undefined Except result: leftStart: %s, leftEnd: %s, rightStart: %s, rightEnd: %s", leftStart.toString(), leftEnd.toString(), rightStart.toString(), rightEnd.toString()));
        }

        else if (left instanceof Iterable)
        {
            Iterable<?> leftArr = (Iterable<?>)left;
            Iterable<?> rightArr = (Iterable<?>)right;

            List<Object> result = new ArrayList<>();
            Boolean in;
            for (Object leftItem : leftArr)
            {
                in = InEvaluator.in(leftItem, rightArr, null, state);
                if (in != null && !in)
                {
                    result.add(leftItem);
                }
            }

            return DistinctEvaluator.distinct(result, state);
        }

        throw new InvalidOperatorArgument(
                "Except(Interval<T>, Interval<T>) or Except(List<T>, List<T>)",
                String.format("Except(%s, %s)", left.getClass().getName(), right.getClass().getName())
        );
    }

}
