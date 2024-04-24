package org.opencds.cqf.cql.engine.data;

import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.Month;
import org.junit.jupiter.api.Test;
import org.opencds.cqf.cql.engine.runtime.Date;

class SystemDataProviderTest {

    @Test
    void resolveMissingPropertyReturnsNull() {
        SystemDataProvider provider = new SystemDataProvider();

        Date date = new Date(2019, Month.JANUARY.getValue(), 1);

        var value = provider.resolvePath(date, "notapath");
        assertNull(value);
    }

    @Test
    void resolveIdAlwaysReturnsNull() {
        final SystemDataProvider provider = new SystemDataProvider();

        assertNull(provider.resolveId("someObject"));
        assertNull(provider.resolveId(new java.util.Date()));
        assertNull(provider.resolveId(1));
    }
}
