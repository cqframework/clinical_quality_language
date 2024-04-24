package org.opencds.cqf.cql.engine.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.util.Date;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.opencds.cqf.cql.engine.model.ModelResolver;
import org.opencds.cqf.cql.engine.retrieve.RetrieveProvider;

class CompositeDataProviderTest {
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
