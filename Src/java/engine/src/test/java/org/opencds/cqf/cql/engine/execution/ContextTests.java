package org.opencds.cqf.cql.engine.execution;

import org.cqframework.cql.elm.execution.Library;
import org.cqframework.cql.elm.execution.VersionedIdentifier;
import org.opencds.cqf.cql.engine.data.DataProvider;
import org.opencds.cqf.cql.engine.exception.CqlException;
import org.opencds.cqf.cql.engine.runtime.Code;
import org.opencds.cqf.cql.engine.runtime.Interval;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class ContextTests {

    Context context;

    @BeforeMethod
    public void initialize() {
        this.context = new Context(new Library().withIdentifier(new VersionedIdentifier().withId("Test")));
    }


    @Test(expectedExceptions = CqlException.class)
    public void resolveMissingDataProviderByModelUri() {
        this.context.resolveDataProviderByModelUri("test.com");
    }

    @Test
    public void resolveDataProviderByModelUri() {
        // This is an empty dummy data provider used only for this test
        DataProvider dataProvider = new DataProvider(){

            @Override
            @SuppressWarnings("deprecation")
            public String getPackageName() {
                return "test.package.name";
            }

            @Override
            @SuppressWarnings("deprecation")
            public void setPackageName(String packageName) {
            }

            @Override
            public Object resolvePath(Object target, String path) {
                return null;
            }

            @Override
            public Object getContextPath(String contextType, String targetType) {
                return null;
            }

            @Override
            public Class<?> resolveType(String typeName) {
                return null;
            }

            @Override
            public Class<?> resolveType(Object value) {
                return null;
            }

            @Override
            public Boolean is(Object value, Class<?> type) {
                return null;
            }

            @Override
            public Object as(Object value, Class<?> type, boolean isStrict) {
                return null;
            }

            @Override
            public Object createInstance(String typeName) {
                return null;
            }

            @Override
            public void setValue(Object target, String path, Object value) {
            }

            @Override
            public Boolean objectEqual(Object left, Object right) {
                return null;
            }

            @Override
            public Boolean objectEquivalent(Object left, Object right) {
                return null;
            }

            @Override
            public Iterable<Object> retrieve(String context, String contextPath, Object contextValue, String dataType,
                    String templateId, String codePath, Iterable<Code> codes, String valueSet, String datePath,
                    String dateLowPath, String dateHighPath, Interval dateRange) {
                        return null;
            }
        };

        this.context.registerDataProvider("test.com", dataProvider);


        DataProvider resolvedDataProvider = this.context.resolveDataProviderByModelUri("test.com");
        assertEquals(dataProvider, resolvedDataProvider);
    }
}
