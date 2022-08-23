package org.cqframework.cql.elm.serializing.jaxb;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class LibraryReaderUtil {
    public static Source toSource(Object source) {
        if (source == null)
            throw new RuntimeException("no source is given");

        if (source instanceof String) {
            try {
                source = new URI((String)source);
            } catch (URISyntaxException e) {
                source = new File((String)source);
            }
        }

        if (source instanceof File) {
            return new StreamSource((File)source);
        }

        if (source instanceof URI) {
            try {
                source = ((URI)source).toURL();
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }

        if (source instanceof URL) {
            return new StreamSource(((URL)source).toExternalForm());
        }

        if (source instanceof InputStream) {
            return new StreamSource((InputStream)source);
        }

        if (source instanceof Reader) {
            return new StreamSource((Reader)source);
        }

        if (source instanceof Source) {
            return (Source)source;
        }

        throw new RuntimeException(String.format("Could not determine access path for input of type %s.", source.getClass()));
    }
}
