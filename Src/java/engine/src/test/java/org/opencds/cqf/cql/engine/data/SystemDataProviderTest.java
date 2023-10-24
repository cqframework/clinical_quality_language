package org.opencds.cqf.cql.engine.data;

import static org.testng.Assert.assertNull;

import org.opencds.cqf.cql.engine.runtime.Date;
import org.testng.annotations.Test;

import java.time.Month;

public class SystemDataProviderTest {

    @Test
    public void resolveMissingPropertyReturnsNull() {
        SystemDataProvider provider = new SystemDataProvider();

        Date date = new Date(2019, Month.JANUARY.getValue(), 1);

        Object result = provider.resolvePath(date, "notapath");
        assertNull(result);
    }

    @Test
    public void resolveIdAlwaysReturnsNull() {
        final SystemDataProvider provider = new SystemDataProvider();

        assertNull(provider.resolveId("someObject"));
        assertNull(provider.resolveId(new java.util.Date()));
        assertNull(provider.resolveId(1));
    }
}
