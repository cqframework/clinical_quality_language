package org.opencds.cqf.cql.engine.model;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.opencds.cqf.cql.engine.data.CompositeDataProvider;
import org.opencds.cqf.cql.engine.retrieve.RetrieveProvider;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.isA;

import java.util.Date;


// TODO: Extend testing to cover more of the CachedModelResolver
public class CachingModelResolverDecoratorTest {
    @Mock
    private ModelResolver mockModelResolver;
    @Mock
    private RetrieveProvider mockRetrieveProvider;

    private AutoCloseable mocks;

    @BeforeMethod
    void before() {
        mocks = MockitoAnnotations.openMocks(this);
    }

    @AfterMethod
    void after() throws Exception {
        mocks.close();
    }

    @Test
    public void context_path_resolved_only_once() {
        var m = mock(ModelResolver.class);
        when(m.getPackageName()).thenReturn("test.package");
        when(m.getContextPath("Patient", "Patient")).thenReturn("id");

        var cache = new CachingModelResolverDecorator(m);
        cache.getContextPath("Patient", "Patient");
        var result = cache.getContextPath("Patient", "Patient");

        assertEquals("id", result);
        verify(m, times(1)).getContextPath("Patient", "Patient");
    }

    @Test
    public void type_resolved_only_once() {
        var m = mock(ModelResolver.class);
        when(m.getPackageName()).thenReturn("test.package");
        when(m.resolveType(isA(Integer.class))).thenReturn((Class)Integer.class);
        when(m.resolveType(isA(Class.class))).thenThrow(new RuntimeException("Can't get a class of a class"));

        var cache = new CachingModelResolverDecorator(m);
        cache.resolveType(5);
        var result = cache.resolveType(5);

        assertEquals(Integer.class, result);
        verify(m, times(1)).resolveType(5);
    }

    @Test
    void testResolveIdString() {
        final String object = "object";
        final String id = "text";

        when(mockModelResolver.resolveId(object)).thenReturn(id);

        final CompositeDataProvider compositeDataProvider = new CompositeDataProvider(mockModelResolver, mockRetrieveProvider);

        Assert.assertEquals(id, compositeDataProvider.resolveId(object));
        verify(mockModelResolver, times(1)).resolveId(object);
    }

    @Test
    void testResolveIdIntLong() {
        final long object = 1L;
        final String id = "oneL";

        when(mockModelResolver.resolveId(object)).thenReturn(id);

        final CompositeDataProvider compositeDataProvider = new CompositeDataProvider(mockModelResolver, mockRetrieveProvider);

        Assert.assertEquals(id, compositeDataProvider.resolveId(object));
        verify(mockModelResolver, times(1)).resolveId(object);
    }

    @Test
    void testResolveIdDate() {
        final Date object = new Date();
        final String id = "now";

        when(mockModelResolver.resolveId(object)).thenReturn(id);

        final CompositeDataProvider compositeDataProvider = new CompositeDataProvider(mockModelResolver, mockRetrieveProvider);

        Assert.assertEquals(id, compositeDataProvider.resolveId(object));
        verify(mockModelResolver, times(1)).resolveId(object);
    }
}
