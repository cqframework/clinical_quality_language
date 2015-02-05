package org.cqframework.cql.cql2elm;

import org.apache.ws.commons.schema.*;
import org.apache.ws.commons.schema.resolver.URIResolver;
import org.apache.ws.commons.schema.walker.XmlSchemaAttrInfo;
import org.apache.ws.commons.schema.walker.XmlSchemaTypeInfo;
import org.apache.ws.commons.schema.walker.XmlSchemaVisitor;
import org.apache.ws.commons.schema.walker.XmlSchemaWalker;
import org.cqframework.cql.cql2elm.model.ModelImporter;
import org.cqframework.cql.elm.tracking.DataType;
import org.testng.annotations.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.TypeInfoProvider;
import javax.xml.validation.ValidatorHandler;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;

public class TestTypes {

    public class TestResolver implements URIResolver {

        @Override
        public InputSource resolveEntity(String targetNamespace, String schemaLocation, String baseUri) {
            return null;
        }
    }

    private List<TestTypes> testTypeList;

//    @Test
//    public void TestModelImporter() {
//        InputStream is = null;
//        try {
//            is = new FileInputStream("C:\\Users\\Bryn\\Documents\\Src\\SS\\CQL\\Src\\java\\quick\\schema\\fhir-single.xsd");
//            XmlSchemaCollection schemaCol = new XmlSchemaCollection();
//            schemaCol.setBaseUri("C:\\Users\\Bryn\\Documents\\Src\\SS\\CQL\\Src\\java\\quick\\schema");
//            XmlSchema schema = schemaCol.read(new StreamSource(is));
//            Map<String, DataType> typeCatalog = new HashMap<String, DataType>();
//            Collection<DataType> dataTypes = ModelImporter.fromXsd(schema, typeCatalog);
//            dataTypes.size();
//        }
//        catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//    }

//    @Test(enabled = false)
//    public void TypeTest() {
//        InputStream is = null;
//        try {
//            is = new FileInputStream("C:\\Users\\Bryn\\Documents\\Src\\SS\\CQL\\Src\\java\\quick\\schema\\fhir-single.xsd");
//            XmlSchemaCollection schemaCol = new XmlSchemaCollection();
//            schemaCol.setBaseUri("C:\\Users\\Bryn\\Documents\\Src\\SS\\CQL\\Src\\java\\quick\\schema");
//            schemaCol.setSchemaResolver(new TestResolver());
//            XmlSchema schema = schemaCol.read(new StreamSource(is));
//            Map<QName, XmlSchemaType> schemaTypes = schema.getSchemaTypes();
//
//            List<String> typeNames = new ArrayList<String>();
//
//            for (XmlSchemaType schemaType : schemaTypes.values()) {
//                if (schemaType instanceof XmlSchemaComplexType) {
//                    XmlSchemaComplexType complexType = (XmlSchemaComplexType)schemaType;
//                    typeNames.add(complexType.getName());
//                    complexType.getBaseSchemaTypeName();
//
//                    XmlSchemaParticle particle = complexType.getContentTypeParticle();
//                    if (particle instanceof XmlSchemaElement) {
//                        XmlSchemaElement element = (XmlSchemaElement)particle;
//                        element.getName();
//                        element.getSchemaType();
//                    }
//                    else if (particle instanceof XmlSchemaSequence) {
//                        XmlSchemaSequence sequence = (XmlSchemaSequence)particle;
//                        for (XmlSchemaSequenceMember member : sequence.getItems()) {
//                            if (member instanceof XmlSchemaElement) {
//                                XmlSchemaElement element = (XmlSchemaElement)member;
//                                element.getName();
//                                element.getSchemaType();
//                            }
//                        }
//                    }
//                    else if (particle instanceof XmlSchemaChoice) {
//                        XmlSchemaChoice choice = (XmlSchemaChoice)particle;
//                        for (XmlSchemaChoiceMember member : choice.getItems()) {
//                            if (member instanceof XmlSchemaElement) {
//                                XmlSchemaElement element = (XmlSchemaElement)member;
//                                element.getName();
//                                element.getSchemaType();
//                            }
//                        }
//                    }
//                }
//            }
//
//            typeNames.size();
//        }
//        catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//    }
}
