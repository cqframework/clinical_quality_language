package org.opencds.cqf.cql.engine.serializing.jaxb;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.hl7.cql_annotations.r1.CqlToElmBase;
import org.hl7.elm.r1.ObjectFactory;

public class JsonCqlMapper {

    private static JAXBContext jaxbContext;

    public static JAXBContext getJaxbContext() {
        if (jaxbContext == null) {
            try {
                jaxbContext = JAXBContext.newInstance(ObjectFactory.class, CqlToElmBase.class);
            } catch (JAXBException e) {
                e.printStackTrace();
                throw new RuntimeException("Error creating JAXBContext - " + e.getMessage());
            }
        }
        return jaxbContext;
    }
}
