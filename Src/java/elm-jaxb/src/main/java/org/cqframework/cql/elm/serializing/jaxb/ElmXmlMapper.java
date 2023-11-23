package org.cqframework.cql.elm.serializing.jaxb;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;

public class ElmXmlMapper {

    private static JAXBContext jaxbContext;

    public static JAXBContext getJaxbContext() {
        if (jaxbContext == null) {
            try {
                jaxbContext = JAXBContext.newInstance(org.hl7.elm.r1.ObjectFactory.class, org.hl7.cql_annotations.r1.ObjectFactory.class);
            } catch (JAXBException e) {
                throw new RuntimeException("Error creating JAXBContext - " + e.getMessage());
            }
        }
        return jaxbContext;
    }
}
