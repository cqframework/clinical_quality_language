package org.opencds.cqf.cql.engine.execution;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

import org.hl7.elm.r1.VersionedIdentifier;
import org.junit.jupiter.api.Test;
import org.opencds.cqf.cql.engine.elm.executing.AfterEvaluator;
import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator;
import org.opencds.cqf.cql.engine.exception.CqlException;
import org.opencds.cqf.cql.engine.runtime.*;

@SuppressWarnings("removal")
class CqlDateTimeOperatorsTest extends CqlTestBase {

    private static final VersionedIdentifier library = new VersionedIdentifier().withId("CqlDateTimeOperatorsTest");

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.AfterEvaluator#evaluate(Context)}
     */
    @Test
    void after() {
        assertThrows(CqlException.class, () -> AfterEvaluator.after(12, "This is an error", null, engine.getState()));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.DurationBetweenEvaluator#evaluate(Context)}
     */
    @Test
    void duration() {

        var value = engine.expression(library, "DateTimeDurationBetweenYear").value();
        assertThat(value, is(5));

        value = engine.expression(library, "DateTimeDurationBetweenUncertainInterval")
                .value();
        assertEquals(17, ((Interval) value).getStart());
        assertEquals(44, ((Interval) value).getEnd());

        value = engine.expression(library, "DateTimeDurationBetweenUncertainInterval2")
                .value();
        assertEquals(5, ((Interval) value).getStart());
        assertEquals(16, ((Interval) value).getEnd());
        //        assertThat(((Uncertainty)result).getUncertaintyInterval(), is(new Interval(5, true, 17, true)));

        value = engine.expression(library, "DateTimeDurationBetweenUncertainAdd")
                .value();
        assertEquals(34, ((Interval) value).getStart());
        assertEquals(88, ((Interval) value).getEnd());

        value = engine.expression(library, "DateTimeDurationBetweenUncertainSubtract")
                .value();
        assertEquals(12, ((Interval) value).getStart());
        assertEquals(28, ((Interval) value).getEnd());

        value = engine.expression(library, "DateTimeDurationBetweenUncertainMultiply")
                .value();
        assertEquals(289, ((Interval) value).getStart());
        assertEquals(1936, ((Interval) value).getEnd());

        value = engine.expression(library, "DurationInDaysA").value();
        assertThat(value, is(1));

        value = engine.expression(library, "DurationInDaysAA").value();
        assertThat(value, is(1));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.NowEvaluator#evaluate(Context)}
     */
    @Test
    void now() {
        DateTime evaluationDateTime = new DateTime(getBigDecimalZoneOffset(), 2016, 6, 10, 5, 5, 4, 999);
        var value = engine.expression(
                        library, "Issue34A", evaluationDateTime.getDateTime().toZonedDateTime())
                .value();
        assertTrue(EquivalentEvaluator.equivalent(value, evaluationDateTime));
        assertEquals(
                ((DateTime) value).getDateTime().getOffset(),
                evaluationDateTime.getDateTime().getOffset());
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.TimeOfDayEvaluator#evaluate(Context)}
     */
    @Test
    void timeOfDay() {
        // TODO: uncomment once Time(x,x,x,x,x) format is fixed
        // Context context = new Context(library);
        // var value = engine.expression(library, "TimeOfDayTest").value();
        // assertThat(((Time)result).getPartial().getValue(0), is(10));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.TodayEvaluator#evaluate(Context)}
     */
    @Test
    void today() {
        //        context = new Context(library, new DateTime(TemporalHelper.getDefaultOffset(), 2016, 6, 10, 5, 5, 4,
        // 999));
        //        value = engine.expression(library, "Issue34B").value();
        //        Assertions.assertTrue(EquivalentEvaluator.equivalent(value, new DateTime(null, 2016, 6, 10)));
        //        Assertions.assertTrue(((DateTime)
        // result).getDateTime().getOffset().equals(TemporalHelper.getDefaultZoneOffset()));
    }
}
