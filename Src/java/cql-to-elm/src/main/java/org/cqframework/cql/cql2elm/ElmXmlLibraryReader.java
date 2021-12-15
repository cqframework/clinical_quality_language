package org.cqframework.cql.cql2elm;

import org.hl7.elm.r1.Library;
import org.hl7.cql_annotations.r1.*;
import org.hl7.elm.r1.ObjectFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class ElmXmlLibraryReader {

    private static JAXBContext context;
    private static Unmarshaller unmarshaller;

    private ElmXmlLibraryReader() {
    }

    // Performance enhancement additions ~ start
    public static synchronized Unmarshaller getUnmarshaller() throws JAXBException {
        if (context == null)
        {
            context = JAXBContext.newInstance(ObjectFactory.class, Annotation.class);
        }

        if (unmarshaller == null) {
            unmarshaller = context.createUnmarshaller();
        }

        return unmarshaller;
    }

    public static Library read(Unmarshaller u, File file) throws IOException, JAXBException {
        return read(u, LibraryReaderUtil.toSource(file));
    }

    public static Library read(Unmarshaller u, URL url) throws IOException, JAXBException {
        return read(u, LibraryReaderUtil.toSource(url));
    }

    public static Library read(Unmarshaller u, URI uri) throws IOException, JAXBException {
        return read(u, LibraryReaderUtil.toSource(uri));
    }

    public static Library read(Unmarshaller u, String string) throws IOException, JAXBException {
        return read(u, LibraryReaderUtil.toSource(string));
    }

    public static Library read(Unmarshaller u, InputStream inputStream) throws IOException, JAXBException {
        return read(u, LibraryReaderUtil.toSource(inputStream));
    }

    public static Library read(Unmarshaller u, Reader reader) throws IOException, JAXBException {
        return read(u, LibraryReaderUtil.toSource(reader));
    }

    @SuppressWarnings("unchecked")
    public static synchronized Library read(Unmarshaller u, Source source) throws JAXBException {
        Object result = u.unmarshal(source);
        return ((JAXBElement<Library>)result).getValue();
    }
    // Performance enhancement additions ~ end

    public static Library read(File file) throws IOException, JAXBException {
        return read(LibraryReaderUtil.toSource(file));
    }

    public static Library read(URL url) throws IOException, JAXBException {
        return read(LibraryReaderUtil.toSource(url));
    }

    public static Library read(URI uri) throws IOException, JAXBException {
        return read(LibraryReaderUtil.toSource(uri));
    }

    public static Library read(String string) throws IOException, JAXBException {
        return read(LibraryReaderUtil.toSource(string));
    }

    public static Library read(InputStream inputStream) throws IOException, JAXBException {
        return read(LibraryReaderUtil.toSource(inputStream));
    }

    public static Library read(Reader reader) throws IOException, JAXBException {
        return read(LibraryReaderUtil.toSource(reader));
    }

    public static synchronized Library read(Source source) throws JAXBException {
        return read(getUnmarshaller(), source);
    }

}
