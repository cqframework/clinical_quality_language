package org.cqframework.cql.tools.xsd2modelinfo;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.*;
import javax.xml.transform.stream.StreamSource;
import org.apache.ws.commons.schema.*;
import org.hl7.elm_modelinfo.r1.ModelInfo;
import org.testng.annotations.Test;

public class TestTypes {

    @Test
    public void testModelImporter() {
        InputStream is = null;
        try {
            File f = new File(TestTypes.class.getResource("fhir-single.xsd").getFile());
            is = new FileInputStream(f);
            XmlSchemaCollection schemaCol = new XmlSchemaCollection();
            schemaCol.setBaseUri(f.getPath());
            XmlSchema schema = schemaCol.read(new StreamSource(is));
            ModelInfo modelInfo = ModelImporter.fromXsd(
                    schema,
                    new ModelImporterOptions()
                            .withModel("QUICK")
                            .withElementRedeclarationPolicy(
                                    ModelImporterOptions.ElementRedeclarationPolicy.RENAME_INVALID_REDECLARATIONS),
                    null);

            assertThat(modelInfo.getName(), is("QUICK"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
