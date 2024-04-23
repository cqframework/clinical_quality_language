package org.hl7.elm_modelinfo.r1.serializing.jaxb;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.hl7.elm_modelinfo.r1.ModelInfo;
import org.hl7.elm_modelinfo.r1.ObjectFactory;
import org.hl7.elm_modelinfo.r1.serializing.ModelInfoReader;

public class XmlModelInfoReader implements ModelInfoReader {

    private final Map<String, String> properties = new HashMap<>();
    private final Unmarshaller unmarshaller;

    public XmlModelInfoReader() {
        properties.put(JAXBContext.JAXB_CONTEXT_FACTORY, "org.eclipse.persistence.jaxb.JAXBContextFactory");

        try {
            this.unmarshaller = JAXBContext.newInstance(new Class<?>[] {ObjectFactory.class}, properties)
                    .createUnmarshaller();
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    private Unmarshaller getUnmarshaller() {
        return unmarshaller;
    }

    @SuppressWarnings("unchecked")
    private ModelInfo toModelInfo(Object obj) {
        return ((JAXBElement<ModelInfo>) obj).getValue();
    }

    public ModelInfo read(File src) throws IOException {
        try {
            return toModelInfo(getUnmarshaller().unmarshal(src));
        } catch (JAXBException e) {
            throw new IOException(e);
        }
    }

    public ModelInfo read(Reader src) throws IOException {
        try {
            return toModelInfo(getUnmarshaller().unmarshal(src));
        } catch (JAXBException e) {
            throw new IOException(e);
        }
    }

    public ModelInfo read(InputStream src) throws IOException {
        try {
            return toModelInfo(getUnmarshaller().unmarshal(src));
        } catch (JAXBException e) {
            throw new IOException(e);
        }
    }

    public ModelInfo read(URL url) throws IOException {
        try {
            return toModelInfo(getUnmarshaller().unmarshal(url));
        } catch (JAXBException e) {
            throw new IOException(e);
        }
    }

    public ModelInfo read(URI uri) throws IOException {
        try {
            return toModelInfo(getUnmarshaller().unmarshal(uri.toURL()));
        } catch (JAXBException e) {
            throw new IOException(e);
        }
    }

    public ModelInfo read(String string) throws IOException {
        try {
            return toModelInfo(getUnmarshaller().unmarshal(new ByteArrayInputStream(string.getBytes())));
        } catch (JAXBException e) {
            throw new IOException(e);
        }
    }
}
