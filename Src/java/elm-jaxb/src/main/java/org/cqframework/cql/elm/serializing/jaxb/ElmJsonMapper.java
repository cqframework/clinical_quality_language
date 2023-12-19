package org.cqframework.cql.elm.serializing.jaxb;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import org.hl7.cql_annotations.r1.CqlToElmBase;
import org.hl7.elm.r1.Library;

public class ElmJsonMapper {

    private static JAXBContext jaxbContext;

    public static JAXBContext getJaxbContext() {
        if (jaxbContext == null) {
            try {
                jaxbContext = JAXBContext.newInstance(Library.class, CqlToElmBase.class);
            } catch (JAXBException e) {
                throw new RuntimeException("Error creating JAXBContext - " + e.getMessage());
            }
        }
        return jaxbContext;
    }
}
