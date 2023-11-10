package org.cqframework.cql.elm.serializing.jaxb;

import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.PropertyException;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.ValidationEvent;
import jakarta.xml.bind.ValidationEventHandler;
import org.cqframework.cql.elm.serializing.ElmLibraryReader;
import org.hl7.elm.r1.Library;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.net.URL;

public class ElmJsonLibraryReader implements ElmLibraryReader {

    public ElmJsonLibraryReader() {
    }

    private Unmarshaller getUnmarshaller() {
        Unmarshaller unmarshaller = null;
        try {
            unmarshaller = ElmJsonMapper.getJaxbContext().createUnmarshaller();
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
        try {
            unmarshaller.setProperty("eclipselink.media-type", "application/json");
        } catch (PropertyException e) {
            throw new RuntimeException(e);
        }
        try {
            unmarshaller.setEventHandler(new ValidationEventHandler() {
                @Override
                public boolean handleEvent(ValidationEvent event) {
                    return true;
                }
            });
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
        return unmarshaller;
    }

    private Library read(Object source) throws IOException {
        Library library = null;
        try {
            library = getUnmarshaller().unmarshal(LibraryReaderUtil.toSource(source), Library.class).getValue();
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
        return library;
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
}
