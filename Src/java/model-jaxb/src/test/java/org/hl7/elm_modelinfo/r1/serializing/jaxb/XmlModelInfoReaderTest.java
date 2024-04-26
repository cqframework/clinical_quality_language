package org.hl7.elm_modelinfo.r1.serializing.jaxb;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import org.hl7.elm_modelinfo.r1.ModelInfo;
import org.junit.jupiter.api.Test;

class XmlModelInfoReaderTest {

    @Test
    void read() throws IOException {
        var reader = new XmlModelInfoReader();
        ModelInfo mi = reader.read(
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><ns4:modelInfo xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:ns4=\"urn:hl7-org:elm-modelinfo:r1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"></ns4:modelInfo>");
        assertNotNull(mi);
    }
}
