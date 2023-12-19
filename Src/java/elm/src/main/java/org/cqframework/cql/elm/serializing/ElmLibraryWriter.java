package org.cqframework.cql.elm.serializing;

import java.io.*;
import org.hl7.elm.r1.Library;

public interface ElmLibraryWriter {

    public void write(Library library, Writer writer) throws IOException;

    public String writeAsString(Library library);
}
