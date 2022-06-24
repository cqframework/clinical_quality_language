package org.cqframework.cql.elm.serializing;

import org.hl7.elm.r1.Library;

import java.io.*;

public interface ElmLibraryWriter {

    public void write(Library library, Writer writer) throws IOException;

    public String writeAsString(Library library);
}
