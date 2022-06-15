package org.cqframework.cql.elm.serializing.jaxb;

import org.cqframework.cql.elm.serializing.ElmLibraryWriter;
import org.hl7.elm.r1.Library;
import org.hl7.elm.r1.ObjectFactory;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import java.io.*;

public class ElmXmlLibraryWriter implements ElmLibraryWriter {
    @Override
    public void writeValue(Library library, Writer writer) throws IOException {
        throw new RuntimeException("JAXB writer must use write to string");
    }

    @Override
    public String writeValueAsString(Library library) {
        Marshaller marshaller = null;
        try {
            marshaller = ElmXmlMapper.getJaxbContext().createMarshaller();
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
        try {
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        } catch (PropertyException e) {
            throw new RuntimeException(e);
        }

        StringWriter writer = new StringWriter();
        try {
            marshaller.marshal(new ObjectFactory().createLibrary(library), writer);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }

        // The marshaller is not encoding the form feed character (presumably because it's not valid in XML 1.0 at all (even encoded)).
        // Tried to get it to write 1.1 XML, but JAXB can't apparently? ()
        // So hacking it after the fact...
        // NOTE: Even after doing this and getting a valid XML 1.1 document with the form feed as a character reference, the JAXB unmarshaller still complains
        // So... basically, form feeds are not supported in ELM XML
        return writer.getBuffer().toString().replace("<xml version=\"1.0\"", "<xml version=\"1.1\"").replace("\f", "&#xc;");
    }
}
