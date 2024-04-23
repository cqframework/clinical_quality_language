package org.cqframework.cql.elm.serializing.jaxb;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import java.util.HashMap;
import org.cqframework.cql.elm.IdObjectFactory;

public class ElmXmlMapper {

    private static JAXBContext jaxbContext;
    private static HashMap<String, String> properties = new HashMap<>();

    static {
        properties.put(JAXBContext.JAXB_CONTEXT_FACTORY, "org.eclipse.persistence.jaxb.JAXBContextFactory");
    }

    public static JAXBContext getJaxbContext() {
        if (jaxbContext == null) {
            try {
                jaxbContext = JAXBContext.newInstance(
                        new Class<?>[] {IdObjectFactory.class, org.hl7.cql_annotations.r1.ObjectFactory.class},
                        properties);
            } catch (JAXBException e) {
                throw new RuntimeException("Error creating JAXBContext - " + e.getMessage());
            }
        }
        return jaxbContext;
    }
}
