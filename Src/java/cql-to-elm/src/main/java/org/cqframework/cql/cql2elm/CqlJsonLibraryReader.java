package org.cqframework.cql.cql2elm;

import org.hl7.cql_annotations.r1.Annotation;
import org.hl7.elm.r1.Library;

import javax.xml.bind.*;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class CqlJsonLibraryReader {
    private CqlJsonLibraryReader() {
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
        Library library = unmarshaller.unmarshal(toSource(object), Library.class).getValue();

        return library;
    }

    /**
     * Creates {@link Source} from various JSON representation.
     */
    private static Source toSource(Object json) throws IOException {
        if (json == null)
            throw new CqlTranslatorException("no JSON is given");

        if (json instanceof String) {
            try {
                json = new URI((String)json);
            } catch (URISyntaxException e) {
                json = new File((String)json);
            }
        }

        if (json instanceof File) {
            return new StreamSource((File)json);
        }

        if (json instanceof URI) {
            json = ((URI)json).toURL();
        }

        if (json instanceof URL) {
            return new StreamSource(((URL)json).toExternalForm());
        }

        if (json instanceof InputStream) {
            return new StreamSource((InputStream)json);
        }

        if (json instanceof Reader) {
            return new StreamSource((Reader)json);
        }

        if (json instanceof Source) {
            return (Source)json;
        }

        throw new CqlTranslatorException(String.format("Could not determine access path for input of type %s.", json.getClass()));
    }
}
