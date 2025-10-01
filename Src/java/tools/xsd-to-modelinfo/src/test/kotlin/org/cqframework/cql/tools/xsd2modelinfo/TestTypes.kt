package org.cqframework.cql.tools.xsd2modelinfo

import org.apache.ws.commons.schema.XmlSchemaCollection
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.InputStream
import javax.xml.transform.stream.StreamSource

internal class TestTypes {
    @Test
    fun modelImporter() {
        var `is`: InputStream?
        try {
            val f = File(TestTypes::class.java.getResource("fhir-single.xsd")!!.file)
            `is` = FileInputStream(f)
            val schemaCol = XmlSchemaCollection()
            schemaCol.setBaseUri(f.path)
            val schema = schemaCol.read(StreamSource(`is`))
            val modelInfo = ModelImporter.fromXsd(
                schema,
                ModelImporterOptions()
                    .withModel("QUICK")
                    .withElementRedeclarationPolicy(
                        ModelImporterOptions.ElementRedeclarationPolicy.RENAME_INVALID_REDECLARATIONS
                    ),
                null
            )

            assertThat(modelInfo.name, Matchers.`is`<String?>("QUICK"))
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }
}
