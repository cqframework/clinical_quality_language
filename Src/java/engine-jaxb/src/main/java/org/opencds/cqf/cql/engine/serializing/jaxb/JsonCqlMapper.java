package org.opencds.cqf.cql.engine.serializing.jaxb;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.cqframework.cql.elm.execution.CqlToElmBase;
import org.opencds.cqf.cql.engine.elm.execution.ObjectFactoryEx;

public class JsonCqlMapper {

    private static JAXBContext jaxbContext;

    public static JAXBContext getJaxbContext() {
        if (jaxbContext == null) {
            try {
                jaxbContext = JAXBContext.newInstance(ObjectFactoryEx.class, CqlToElmBase.class);
            } catch (JAXBException e) {
                e.printStackTrace();
                throw new RuntimeException("Error creating JAXBContext - " + e.getMessage());
            }
        }
        return jaxbContext;
    }
}
