package org.opencds.cqf.cql.engine.elm.executing

import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.execution.CqlTestBase
import org.opencds.cqf.cql.engine.runtime.Boolean
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.runtime.toCqlInteger
import org.opencds.cqf.cql.engine.runtime.toCqlString

/** [#1961](https://github.com/cqframework/clinical_quality_language/issues/1961) */
internal class CanConvertQuantityEvaluatorTest : CqlTestBase() {
    private val ucumService = libraryManager.ucumService

    private fun canConvert(value: kotlin.String, unit: kotlin.String, target: kotlin.String) =
        CanConvertQuantityEvaluator.canConvertQuantity(
            Quantity().withValue(BigDecimal(value)).withUnit(unit),
            target.toCqlString(),
            ucumService,
        )

    @Test
    fun convertibleUnits() {
        assertEquals(Boolean.TRUE, canConvert("5", "mg", "g"))
        assertEquals(Boolean.TRUE, canConvert("5", "mg", "mg"))
        // Calendar keyword units are normalized the way ConvertQuantity normalizes them
        assertEquals(Boolean.TRUE, canConvert("1", "week", "d"))
    }

    @Test
    fun unitsOfDifferentDimensions() {
        assertEquals(Boolean.FALSE, canConvert("5", "mg", "cm"))
        assertEquals(Boolean.FALSE, canConvert("5", "mg", "not-a-unit"))
    }

    @Test
    fun withoutUcumServiceIsFalse() {
        assertEquals(
            Boolean.FALSE,
            CanConvertQuantityEvaluator.canConvertQuantity(
                Quantity().withValue(BigDecimal("5")).withUnit("mg"),
                "g".toCqlString(),
                null,
            ),
        )
    }

    @Test
    fun nullArguments() {
        assertNull(
            CanConvertQuantityEvaluator.canConvertQuantity(null, "g".toCqlString(), ucumService)
        )
        assertNull(
            CanConvertQuantityEvaluator.canConvertQuantity(
                Quantity().withValue(BigDecimal("5")).withUnit("mg"),
                null,
                ucumService,
            )
        )
    }

    @Test
    fun wrongArgumentTypes() {
        assertFailsWith<InvalidOperatorArgument> {
            CanConvertQuantityEvaluator.canConvertQuantity(
                5.toCqlInteger(),
                "g".toCqlString(),
                ucumService,
            )
        }
    }
}
