package org.cqframework.cql.elm.serializing.jaxb;

import org.cqframework.cql.elm.serializing.ElmLibraryReader;
import org.hl7.elm.r1.Library;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.net.URL;

public class ElmXmlLibraryReader implements ElmLibraryReader {

    private static Unmarshaller unmarshaller;

    public ElmXmlLibraryReader() {
    }

    public static synchronized Unmarshaller getUnmarshaller() throws JAXBException {
        if (unmarshaller == null) {
            unmarshaller = ElmXmlMapper.getJaxbContext().createUnmarshaller();
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

    public Library read(File file) throws IOException {
        return read(LibraryReaderUtil.toSource(file));
    }

    public Library read(URL url) throws IOException {
        return read(LibraryReaderUtil.toSource(url));
    }

    public Library read(URI uri) throws IOException {
        return read(LibraryReaderUtil.toSource(uri));
    }

    public Library read(String string) throws IOException {
        return read(LibraryReaderUtil.toSource(string));
    }

    public Library read(InputStream inputStream) throws IOException {
        return read(LibraryReaderUtil.toSource(inputStream));
    }

    public Library read(Reader reader) throws IOException {
        return read(LibraryReaderUtil.toSource(reader));
    }

    public synchronized Library read(Source source) {
        try {
            return read(getUnmarshaller(), source);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }
}
