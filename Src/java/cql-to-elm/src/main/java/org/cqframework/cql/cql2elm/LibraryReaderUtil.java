package org.cqframework.cql.cql2elm;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;


public class LibraryReaderUtil {

    /**
     * Creates {@link Source} from various JSON representation.
     */
    public static Source toSource(Object json) throws IOException {
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
