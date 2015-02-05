package org.cqframework.cql.tools.xsd2modelinfo;

import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.cqframework.cql.elm.tracking.DataType;
import org.hl7.elm_modelinfo.r1.ModelInfo;
import org.hl7.elm_modelinfo.r1.ObjectFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Generates a ModelInfo.xml for the input xsd.
 */
public class Main {
    public static void main(String[] args) throws IOException, JAXBException {
        String inputFilePath = args[0];
        File f = new File(inputFilePath);
        InputStream is = new FileInputStream(f);
        XmlSchemaCollection schemaCol = new XmlSchemaCollection();
        schemaCol.setBaseUri(f.getParent());
        XmlSchema schema = schemaCol.read(new StreamSource(is));
        ModelInfo modelInfo = ModelImporter.fromXsd(schema, "QUICK");

        JAXBContext jc = JAXBContext.newInstance(ModelInfo.class);
        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        File outputFile = new File(f.getParent(), String.format("%s-modelinfo.xml", modelInfo.getTargetQualifier().getLocalPart()));
        OutputStream os = new FileOutputStream(outputFile, false);
        try {
            OutputStreamWriter writer = new OutputStreamWriter(os);
            marshaller.marshal(new ObjectFactory().createModelInfo(modelInfo), writer);
        }
        finally {
            os.close();
        }
    }
}
