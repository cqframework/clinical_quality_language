package org.opencds.cqf.cql.engine.execution;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.math.BigDecimal;

import org.opencds.cqf.cql.engine.debug.Location;
import org.opencds.cqf.cql.engine.debug.SourceLocator;
import org.opencds.cqf.cql.engine.exception.InvalidInterval;
import org.opencds.cqf.cql.engine.runtime.Interval;
import org.opencds.cqf.cql.engine.runtime.Quantity;
import org.opencds.cqf.cql.engine.runtime.Tuple;
import org.testng.annotations.Test;

public class RuntimeTests {
    @Test
    public void testQuantityToString() {
        Quantity q = new Quantity().withValue(null).withUnit(null);
        assertThat(q.toString(), is("null 'null'"));

        q = new Quantity();
        assertThat(q.toString(), is("0.0 '1'"));

        q = new Quantity().withValue(new BigDecimal("1.0")).withUnit("g");
        assertThat(q.toString(), is("1.0 'g'"));

        q = new Quantity().withValue(new BigDecimal("0.05")).withUnit("mg");
        assertThat(q.toString(), is("0.05 'mg'"));
    }

    @Test(expectedExceptions=InvalidInterval.class)
    public void testIntervalOfQuantityWithDifferentUOM() {
        Quantity s= new Quantity().withValue(new BigDecimal(10)).withUnit("mg/mL");
        Quantity e = new Quantity().withValue(new BigDecimal(10)).withUnit("kg/m3");
        new Interval( s, true, e, true );
    }

    @Test
    public void testTupleToString() {
        Tuple t = new Tuple();
        assertThat(t.toString(), is("Tuple { : }"));

        t = new Tuple();
        t.getElements().put("id", 1);
        t.getElements().put("value", new Quantity().withValue(new BigDecimal("1.0")).withUnit("g"));
        assertThat(t.toString(), is("Tuple {\n\t\"id\": 1\n\t\"value\": 1.0 'g'\n}"));
    }

    @Test
    public void testSourceLocation() {
        SourceLocator sourceLocator = new SourceLocator(
                "http://cql.hl7.org/Library/Example",
                "Example",
                "1.0.0",
                "1",
                "RetrieveEvaluator",
                Location.fromLocator("1:1-89:80")
        );

        assertThat(sourceLocator.toString(), is("Example.1:1-89:80(1)"));

        sourceLocator = new SourceLocator(
                null,
                null,
                null,
                null,
                null,
                null
        );

        assertThat(sourceLocator.toString(), is("?.?(?)"));
    }
}
