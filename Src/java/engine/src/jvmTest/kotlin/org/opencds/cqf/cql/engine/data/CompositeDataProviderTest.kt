package org.opencds.cqf.cql.engine.data

import java.lang.AutoCloseable
import java.util.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.opencds.cqf.cql.engine.model.ModelResolver
import org.opencds.cqf.cql.engine.retrieve.RetrieveProvider

internal class CompositeDataProviderTest {
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
