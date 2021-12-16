package org.cqframework.cql.cql2elm;

import org.hl7.cql_annotations.r1.Annotation;
import org.hl7.elm.r1.Library;

import javax.xml.bind.*;
import java.io.*;

public class ElmJsonLibraryReader {
    private ElmJsonLibraryReader() {
    }

    private static JAXBContext jaxbContext;

    public static JAXBContext getJaxbContext() {
        if (jaxbContext == null) {
            try {
                jaxbContext = JAXBContext.newInstance(Library.class, Annotation.class);
            } catch (JAXBException e) {
                e.printStackTrace();
                throw new RuntimeException("Error creating JAXBContext - " + e.getMessage());
            }
        }
        return jaxbContext;
    }

    public static Library read(Object object) throws IOException, JAXBException {
        Unmarshaller unmarshaller = getJaxbContext().createUnmarshaller();
        unmarshaller.setProperty("eclipselink.media-type", "application/json");
        unmarshaller.setEventHandler(new ValidationEventHandler() {
            @Override
            public boolean handleEvent(ValidationEvent event) {
                return true;
            }
        });

        Library library = unmarshaller.unmarshal(LibraryReaderUtil.toSource(object), Library.class).getValue();
        return library;
    }


}
