package org.opencds.cqf.cql.engine.fhir.parser

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlinx.io.asSource
import kotlinx.io.buffered
import org.cqframework.cql.cql2elm.ModelManager
import org.cqframework.cql.shared.QName
import org.opencds.cqf.cql.engine.fhir.fhirModelNamespaceUri
import org.opencds.cqf.cql.engine.runtime.ClassInstance
import org.opencds.cqf.cql.engine.runtime.List

class ParserTest {
    /**
     * Tests various parsing edge cases using the JSON from
     * https://hl7.org/fhir/R4/json-edge-cases.json
     */
    @Test
    fun parserTest() {
        val fhirJson =
            ParserTest::class
                .java
                .getResourceAsStream("json-edge-cases.json")!!
                .asSource()
                .buffered()

        val modelManager = ModelManager()
        val model = modelManager.resolveModel("FHIR", "4.0.1")

        val patient = fhirResourceJsonToCqlValue(fhirJson, model)
        assertEquals(QName(fhirModelNamespaceUri, "Patient"), patient.type)

        // Type narrowing for instances of FHIR.Resource
        val containedResources = patient.elements["contained"]
        assertIs<List>(containedResources)
        val containedBinary = containedResources.elementAt(0)
        assertIs<ClassInstance>(containedBinary)
        assertEquals(QName(fhirModelNamespaceUri, "Binary"), containedBinary.type)

        // Named FHIR.code elements
        val gender = patient.elements["gender"]
        assertIs<ClassInstance>(gender)
        assertEquals(QName(fhirModelNamespaceUri, "AdministrativeGender"), gender.type)

        // Extensions on primitive types
        val active = patient.elements["active"]
        assertIs<ClassInstance>(active)
        assertEquals(QName(fhirModelNamespaceUri, "boolean"), active.type)
        val activeExtensions = active.elements["extension"]
        assertIs<List>(activeExtensions)
        val activeExtension = activeExtensions.elementAt(0)
        assertIs<ClassInstance>(activeExtension)
        assertEquals(QName(fhirModelNamespaceUri, "Extension"), activeExtension.type)

        // Choice types
        val multipleBirth = patient.elements["multipleBirth"]
        assertIs<ClassInstance>(multipleBirth)
        assertEquals(QName(fhirModelNamespaceUri, "integer"), multipleBirth.type)
    }
}
