package org.hl7.elm_modelinfo.r1.serializing.jaxb;

import org.hl7.elm_modelinfo.r1.ModelInfo;
import org.hl7.elm_modelinfo.r1.serializing.ModelInfoReader;

import jakarta.xml.bind.JAXB;
import java.io.*;
import java.net.URI;
import java.net.URL;

public class XmlModelInfoReader implements ModelInfoReader {

    public ModelInfo read(File src) throws IOException {
        return JAXB.unmarshal(src, ModelInfo.class);
    }

    public ModelInfo read(Reader src) throws IOException {
        return JAXB.unmarshal(src, ModelInfo.class);
    }

    public ModelInfo read(InputStream src) throws IOException {
        return JAXB.unmarshal(src, ModelInfo.class);
    }

    public ModelInfo read(URL url) throws IOException {
        return JAXB.unmarshal(url, ModelInfo.class);
    }

    public ModelInfo read(URI uri) throws IOException {
        return JAXB.unmarshal(uri, ModelInfo.class);
    }

    public ModelInfo read(String string) throws IOException {
        return JAXB.unmarshal(string, ModelInfo.class);
    }
}
