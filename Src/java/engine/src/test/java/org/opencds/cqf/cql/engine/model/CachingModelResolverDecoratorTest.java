package org.opencds.cqf.cql.engine.model;

import org.testng.annotations.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;


// TODO: Extend testing to cover more of the CachedModelResolver
public class CachingModelResolverDecoratorTest {


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
}
