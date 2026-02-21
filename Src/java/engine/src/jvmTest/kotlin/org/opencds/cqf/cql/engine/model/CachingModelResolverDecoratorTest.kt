package org.opencds.cqf.cql.engine.model

import java.lang.AutoCloseable
import java.util.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.opencds.cqf.cql.engine.data.CompositeDataProvider
import org.opencds.cqf.cql.engine.retrieve.RetrieveProvider

// TODO: Extend testing to cover more of the CachedModelResolver
internal class CachingModelResolverDecoratorTest {
    @Mock private val mockModelResolver: ModelResolver? = null

    @Mock private val mockRetrieveProvider: RetrieveProvider? = null

    private var mocks: AutoCloseable? = null

    @BeforeEach
    fun before() {
        mocks = MockitoAnnotations.openMocks(this)
    }

    @AfterEach
    @Throws(Exception::class)
    fun after() {
        mocks!!.close()
    }

    @Test
    @Suppress("deprecation")
    fun context_path_resolved_only_once() {
        val m = Mockito.mock(ModelResolver::class.java)
        Mockito.`when`(m.packageName).thenReturn("test.package")
        Mockito.`when`(m.getContextPath("Patient", "Patient")).thenReturn("id")

        val cache = CachingModelResolverDecorator(m)
        cache.getContextPath("Patient", "Patient")
        val result = cache.getContextPath("Patient", "Patient")

        Assertions.assertEquals("id", result)
        Mockito.verify(m, Mockito.times(1)).getContextPath("Patient", "Patient")
    }

    @Test
    @Suppress("deprecation")
    fun type_resolved_only_once() {
        val m = Mockito.mock(ModelResolver::class.java)
        Mockito.`when`(m.packageName).thenReturn("test.package")
        Mockito.`when`(m.resolveType(ArgumentMatchers.isA(Int::class.java)))
            .thenReturn(Int::class.java)
        Mockito.`when`(m.resolveType(ArgumentMatchers.isA(Class::class.java)))
            .thenThrow(RuntimeException("Can't get a class of a class"))

        val cache = CachingModelResolverDecorator(m)
        cache.resolveType(5)
        val result = cache.resolveType(5)

        Assertions.assertEquals(Int::class.java, result)
        Mockito.verify(m, Mockito.times(1)).resolveType(5)
    }

    @Test
    fun resolveIdString() {
        val `object` = "object"
        val id = "text"

        Mockito.`when`(mockModelResolver!!.resolveId(`object`)).thenReturn(id)

        val compositeDataProvider = CompositeDataProvider(mockModelResolver, mockRetrieveProvider)

        Assertions.assertEquals(id, compositeDataProvider.resolveId(`object`))
        Mockito.verify(mockModelResolver, Mockito.times(1)).resolveId(`object`)
    }

    @Test
    fun resolveIdIntLong() {
        val `object` = 1L
        val id = "oneL"

        Mockito.`when`(mockModelResolver!!.resolveId(`object`)).thenReturn(id)

        val compositeDataProvider = CompositeDataProvider(mockModelResolver, mockRetrieveProvider)

        Assertions.assertEquals(id, compositeDataProvider.resolveId(`object`))
        Mockito.verify(mockModelResolver, Mockito.times(1)).resolveId(`object`)
    }

    @Test
    fun resolveIdDate() {
        val `object` = Date()
        val id = "now"

        Mockito.`when`(mockModelResolver!!.resolveId(`object`)).thenReturn(id)

        val compositeDataProvider = CompositeDataProvider(mockModelResolver, mockRetrieveProvider)

        Assertions.assertEquals(id, compositeDataProvider.resolveId(`object`))
        Mockito.verify(mockModelResolver, Mockito.times(1)).resolveId(`object`)
    }
}
