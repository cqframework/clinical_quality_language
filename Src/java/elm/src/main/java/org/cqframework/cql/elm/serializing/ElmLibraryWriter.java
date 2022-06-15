package org.cqframework.cql.elm.serializing;

import org.hl7.elm.r1.Library;

import java.io.*;
import java.net.URI;
import java.net.URL;

public interface ElmLibraryWriter {

    public void writeValue(Library library, Writer writer) throws IOException;

    public String writeValueAsString(Library library);
}
