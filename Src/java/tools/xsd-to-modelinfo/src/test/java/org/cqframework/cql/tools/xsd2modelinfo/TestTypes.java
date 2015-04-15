package org.cqframework.cql.tools.xsd2modelinfo;

import org.apache.ws.commons.schema.*;
import org.cqframework.cql.elm.tracking.DataType;
import org.hl7.elm_modelinfo.r1.ModelInfo;
import org.testng.annotations.Test;

import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class TestTypes {

    @Test
    public void TestModelImporter() {
        InputStream is = null;
        try {
            File f = new File(TestTypes.class.getResource("fhir-single.xsd").getFile());
            is = new FileInputStream(f);
            XmlSchemaCollection schemaCol = new XmlSchemaCollection();
            schemaCol.setBaseUri(f.getPath());
            XmlSchema schema = schemaCol.read(new StreamSource(is));
            ModelInfo modelInfo = ModelImporter.fromXsd(schema, new ModelImporterOptions().withModel("QUICK"));

            assertThat(modelInfo.getName(), is("QUICK"));
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
