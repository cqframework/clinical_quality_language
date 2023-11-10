package org.cqframework.cql.elm.serializing.jaxb;

import org.cqframework.cql.elm.serializing.ElmLibraryWriter;
import org.hl7.elm.r1.Library;
import org.hl7.elm.r1.ObjectFactory;

import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.PropertyException;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public class ElmJsonLibraryWriter implements ElmLibraryWriter {
    @Override
    public void write(Library library, Writer writer) throws IOException {
        Marshaller marshaller = null;
        try {
            marshaller = ElmJsonMapper.getJaxbContext().createMarshaller();
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
        try {
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty("eclipselink.media-type", "application/json");
        } catch (PropertyException e) {
            throw new RuntimeException(e);
        }

        try {
            marshaller.marshal(new ObjectFactory().createLibrary(library), writer);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String writeAsString(Library library) {

        StringWriter writer = new StringWriter();
        try {
            write(library, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return writer.getBuffer().toString();
    }
}
