package org.hl7.elm_modelinfo.r1.serializing;

import org.hl7.elm_modelinfo.r1.ModelInfo;

import java.io.*;
import java.net.URI;
import java.net.URL;

public interface ModelInfoReader {
    public ModelInfo read(File src) throws IOException;

    public ModelInfo read(Reader src) throws IOException;

    public ModelInfo read(InputStream src) throws IOException;

    public ModelInfo read(URL url) throws IOException;

    public ModelInfo read(URI uri) throws IOException;

    public ModelInfo read(String string) throws IOException;
}
