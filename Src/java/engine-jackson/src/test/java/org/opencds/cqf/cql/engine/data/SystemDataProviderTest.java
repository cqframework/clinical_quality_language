package org.opencds.cqf.cql.engine.data;

import static org.testng.Assert.assertNull;

import org.opencds.cqf.cql.engine.runtime.Date;
import org.testng.annotations.Test;

public class SystemDataProviderTest {

    @Test
    public void resolveMissingPropertyReturnsNull() {
        SystemDataProvider provider = new SystemDataProvider();

        Date date = new Date(2019, 01, 01);

        Object result = provider.resolvePath(date, "notapath");
        assertNull(result);
    }
}
