package org.opencds.cqf.cql.engine.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.opencds.cqf.cql.engine.data.CompositeDataProvider;
import org.opencds.cqf.cql.engine.retrieve.RetrieveProvider;

// TODO: Extend testing to cover more of the CachedModelResolver
class CachingModelResolverDecoratorTest {
    @Mock
    private ModelResolver mockModelResolver;

    @Mock
    private RetrieveProvider mockRetrieveProvider;

    private AutoCloseable mocks;

    @BeforeEach
    void before() {
        mocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void after() throws Exception {
        mocks.close();
    }

    @Test
    @SuppressWarnings("deprecation")
    void context_path_resolved_only_once() {
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
    @SuppressWarnings({"unchecked", "deprecation"})
    void type_resolved_only_once() {
        var m = mock(ModelResolver.class);
        when(m.getPackageName()).thenReturn("test.package");
        when(m.resolveType(isA(Integer.class))).thenReturn((Class) Integer.class);
        when(m.resolveType(isA(Class.class))).thenThrow(new RuntimeException("Can't get a class of a class"));

        var cache = new CachingModelResolverDecorator(m);
        cache.resolveType(5);
        var result = cache.resolveType(5);

        assertEquals(Integer.class, result);
        verify(m, times(1)).resolveType(5);
    }

    @Test
    void resolveIdString() {
        final String object = "object";
        final String id = "text";

        when(mockModelResolver.resolveId(object)).thenReturn(id);

        final CompositeDataProvider compositeDataProvider =
                new CompositeDataProvider(mockModelResolver, mockRetrieveProvider);

        assertEquals(id, compositeDataProvider.resolveId(object));
        verify(mockModelResolver, times(1)).resolveId(object);
    }

    @Test
    void resolveIdIntLong() {
        final long object = 1L;
        final String id = "oneL";

        when(mockModelResolver.resolveId(object)).thenReturn(id);

        final CompositeDataProvider compositeDataProvider =
                new CompositeDataProvider(mockModelResolver, mockRetrieveProvider);

        assertEquals(id, compositeDataProvider.resolveId(object));
        verify(mockModelResolver, times(1)).resolveId(object);
    }

    @Test
    void resolveIdDate() {
        final Date object = new Date();
        final String id = "now";

        when(mockModelResolver.resolveId(object)).thenReturn(id);

        final CompositeDataProvider compositeDataProvider =
                new CompositeDataProvider(mockModelResolver, mockRetrieveProvider);

        assertEquals(id, compositeDataProvider.resolveId(object));
        verify(mockModelResolver, times(1)).resolveId(object);
    }
}
