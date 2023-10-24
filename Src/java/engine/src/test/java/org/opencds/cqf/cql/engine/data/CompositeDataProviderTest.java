package org.opencds.cqf.cql.engine.data;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.opencds.cqf.cql.engine.model.ModelResolver;
import org.opencds.cqf.cql.engine.retrieve.RetrieveProvider;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Date;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class CompositeDataProviderTest {
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
    void testResolveIdString() {
        final String object = "object";
        final String id = "text";

        when(mockModelResolver.resolveId(object)).thenReturn(id);

        final CompositeDataProvider compositeDataProvider = new CompositeDataProvider(mockModelResolver, mockRetrieveProvider);

        assertEquals(id, compositeDataProvider.resolveId(object));
        verify(mockModelResolver, times(1)).resolveId(object);
    }

    @Test
    void testResolveIdIntLong() {
        final long object = 1L;
        final String id = "oneL";

        when(mockModelResolver.resolveId(object)).thenReturn(id);

        final CompositeDataProvider compositeDataProvider = new CompositeDataProvider(mockModelResolver, mockRetrieveProvider);

        assertEquals(id, compositeDataProvider.resolveId(object));
        verify(mockModelResolver, times(1)).resolveId(object);
    }

    @Test
    void testResolveIdDate() {
        final Date object = new Date();
        final String id = "now";

        when(mockModelResolver.resolveId(object)).thenReturn(id);

        final CompositeDataProvider compositeDataProvider = new CompositeDataProvider(mockModelResolver, mockRetrieveProvider);

        assertEquals(id, compositeDataProvider.resolveId(object));
        verify(mockModelResolver, times(1)).resolveId(object);
    }
}