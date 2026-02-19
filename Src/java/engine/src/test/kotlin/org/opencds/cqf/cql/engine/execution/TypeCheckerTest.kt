package org.opencds.cqf.cql.engine.execution

import java.math.BigDecimal
import java.time.ZoneOffset
import javax.xml.namespace.QName
import kotlin.test.Test
import kotlin.test.assertEquals
import org.hl7.elm.r1.ChoiceTypeSpecifier
import org.hl7.elm.r1.IntervalTypeSpecifier
import org.hl7.elm.r1.ListTypeSpecifier
import org.hl7.elm.r1.NamedTypeSpecifier
import org.hl7.elm.r1.TupleElementDefinition
import org.hl7.elm.r1.TupleTypeSpecifier
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.runtime.CodeSystem
import org.opencds.cqf.cql.engine.runtime.Concept
import org.opencds.cqf.cql.engine.runtime.Date
import org.opencds.cqf.cql.engine.runtime.DateTime
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.runtime.Ratio
import org.opencds.cqf.cql.engine.runtime.Time
import org.opencds.cqf.cql.engine.runtime.Tuple
import org.opencds.cqf.cql.engine.runtime.ValueSet

@Suppress("LongMethod")
class TypeCheckerTest {
    @Test
    fun checkResultTypeNameMatch() {
        assertEquals(
            TypeChecker.TypeCheckResult.MATCH,
            TypeChecker.checkType(QName("urn:hl7-org:elm-types:r1", "Any"), 10),
        )
        assertEquals(
            TypeChecker.TypeCheckResult.MATCH,
            TypeChecker.checkType(QName("urn:hl7-org:elm-types:r1", "Boolean"), true),
        )
        assertEquals(
            TypeChecker.TypeCheckResult.MATCH,
            TypeChecker.checkType(QName("urn:hl7-org:elm-types:r1", "Integer"), 10),
        )
        assertEquals(
            TypeChecker.TypeCheckResult.MATCH,
            TypeChecker.checkType(QName("urn:hl7-org:elm-types:r1", "Long"), 10L),
        )
        assertEquals(
            TypeChecker.TypeCheckResult.MATCH,
            TypeChecker.checkType(QName("urn:hl7-org:elm-types:r1", "Decimal"), BigDecimal("10.5")),
        )
        assertEquals(
            TypeChecker.TypeCheckResult.MATCH,
            TypeChecker.checkType(QName("urn:hl7-org:elm-types:r1", "Quantity"), Quantity()),
        )
        assertEquals(
            TypeChecker.TypeCheckResult.MATCH,
            TypeChecker.checkType(QName("urn:hl7-org:elm-types:r1", "String"), "test"),
        )
        assertEquals(
            TypeChecker.TypeCheckResult.MATCH,
            TypeChecker.checkType(QName("urn:hl7-org:elm-types:r1", "Date"), Date(2026)),
        )
        assertEquals(
            TypeChecker.TypeCheckResult.MATCH,
            TypeChecker.checkType(
                QName("urn:hl7-org:elm-types:r1", "DateTime"),
                DateTime("2026-01-01", ZoneOffset.UTC),
            ),
        )
        assertEquals(
            TypeChecker.TypeCheckResult.MATCH,
            TypeChecker.checkType(QName("urn:hl7-org:elm-types:r1", "Time"), Time("10:00:00")),
        )
        assertEquals(
            TypeChecker.TypeCheckResult.MATCH,
            TypeChecker.checkType(QName("urn:hl7-org:elm-types:r1", "Ratio"), Ratio()),
        )
        assertEquals(
            TypeChecker.TypeCheckResult.MATCH,
            TypeChecker.checkType(QName("urn:hl7-org:elm-types:r1", "Concept"), Concept()),
        )
        assertEquals(
            TypeChecker.TypeCheckResult.MATCH,
            TypeChecker.checkType(QName("urn:hl7-org:elm-types:r1", "Code"), Code()),
        )
        assertEquals(
            TypeChecker.TypeCheckResult.MATCH,
            TypeChecker.checkType(QName("urn:hl7-org:elm-types:r1", "CodeSystem"), CodeSystem()),
        )
        assertEquals(
            TypeChecker.TypeCheckResult.MATCH,
            TypeChecker.checkType(QName("urn:hl7-org:elm-types:r1", "ValueSet"), ValueSet()),
        )
    }

    @Test
    fun checkResultTypeNameMismatch() {
        assertEquals(
            TypeChecker.TypeCheckResult.MISMATCH,
            TypeChecker.checkType(QName("urn:hl7-org:elm-types:r1", "Integer"), "not an integer"),
        )
    }

    @Test
    fun checkResultTypeSpecifier() {
        val integerTypeSpecifier =
            NamedTypeSpecifier().withName(QName("urn:hl7-org:elm-types:r1", "Integer"))
        val stringTypeSpecifier =
            NamedTypeSpecifier().withName(QName("urn:hl7-org:elm-types:r1", "String"))

        assertEquals(
            TypeChecker.TypeCheckResult.MATCH,
            TypeChecker.checkType(integerTypeSpecifier, 10),
        )
        assertEquals(
            TypeChecker.TypeCheckResult.MATCH,
            TypeChecker.checkType(
                ListTypeSpecifier().withElementType(integerTypeSpecifier),
                listOf(10, 20, 30),
            ),
        )
        assertEquals(
            TypeChecker.TypeCheckResult.MATCH,
            TypeChecker.checkType(
                IntervalTypeSpecifier().withPointType(integerTypeSpecifier),
                Interval(1, true, 10, true),
            ),
        )
        assertEquals(
            TypeChecker.TypeCheckResult.MATCH,
            TypeChecker.checkType(
                TupleTypeSpecifier()
                    .withElement(
                        listOf(
                            TupleElementDefinition()
                                .withName("field1")
                                .withElementType(integerTypeSpecifier)
                        )
                    ),
                Tuple().withElements(mutableMapOf("field1" to 10)),
            ),
        )
        assertEquals(
            TypeChecker.TypeCheckResult.MATCH,
            TypeChecker.checkType(
                ChoiceTypeSpecifier().withChoice(listOf(integerTypeSpecifier, stringTypeSpecifier)),
                "test",
            ),
        )
    }

    @Test
    fun checkResultTypeSpecifierMismatch() {
        val integerTypeSpecifier =
            NamedTypeSpecifier().withName(QName("urn:hl7-org:elm-types:r1", "Integer"))
        val stringTypeSpecifier =
            NamedTypeSpecifier().withName(QName("urn:hl7-org:elm-types:r1", "String"))

        assertEquals(
            TypeChecker.TypeCheckResult.MISMATCH,
            TypeChecker.checkType(
                ListTypeSpecifier().withElementType(integerTypeSpecifier),
                listOf("test"),
            ),
        )
        assertEquals(
            TypeChecker.TypeCheckResult.MISMATCH,
            TypeChecker.checkType(
                IntervalTypeSpecifier().withPointType(integerTypeSpecifier),
                Interval(Time("10:00:00"), true, Time("11:00:00"), true),
            ),
        )
        assertEquals(
            TypeChecker.TypeCheckResult.MISMATCH,
            TypeChecker.checkType(
                TupleTypeSpecifier()
                    .withElement(
                        listOf(
                            TupleElementDefinition()
                                .withName("field1")
                                .withElementType(integerTypeSpecifier)
                        )
                    ),
                Tuple().withElements(mutableMapOf("field2" to 10)),
            ),
        )
        assertEquals(
            TypeChecker.TypeCheckResult.MISMATCH,
            TypeChecker.checkType(
                ChoiceTypeSpecifier().withChoice(listOf(integerTypeSpecifier, stringTypeSpecifier)),
                Quantity(),
            ),
        )
    }
}
