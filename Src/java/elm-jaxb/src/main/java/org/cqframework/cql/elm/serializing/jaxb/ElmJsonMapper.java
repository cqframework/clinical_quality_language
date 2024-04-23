package org.cqframework.cql.elm.serializing.jaxb;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import java.util.HashMap;
import org.hl7.cql_annotations.r1.CqlToElmBase;
import org.hl7.elm.r1.Library;

public class ElmJsonMapper {

    private static JAXBContext jaxbContext;
    private static HashMap<String, String> properties = new HashMap<>();

    static {
        properties.put(JAXBContext.JAXB_CONTEXT_FACTORY, "org.eclipse.persistence.jaxb.JAXBContextFactory");
    }

    public static JAXBContext getJaxbContext() {
        if (jaxbContext == null) {
            try {
                jaxbContext = JAXBContext.newInstance(new Class<?>[] {Library.class, CqlToElmBase.class}, properties);
            } catch (JAXBException e) {
                throw new RuntimeException("Error creating JAXBContext - " + e.getMessage());
            }
        }
        return jaxbContext;
    }
}
