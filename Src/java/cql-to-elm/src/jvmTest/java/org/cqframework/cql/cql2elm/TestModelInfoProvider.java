package org.cqframework.cql.cql2elm;

import static kotlinx.io.CoreKt.buffered;
import static kotlinx.io.JvmCoreKt.asSource;
import static org.hl7.elm_modelinfo.r1.serializing.XmlModelInfoReaderKt.parseModelInfoXml;

import org.hl7.cql.model.ModelIdentifier;
import org.hl7.cql.model.ModelInfoProvider;
import org.hl7.elm_modelinfo.r1.ModelInfo;

public class TestModelInfoProvider implements ModelInfoProvider {
    public ModelInfo load(ModelIdentifier modelIdentifier) {
        if (modelIdentifier.getId().equals("Test")) {
            var stream = TestModelInfoProvider.class.getResourceAsStream("ModelTests/test-modelinfo.xml");
            return parseModelInfoXml(buffered(asSource(stream)));
        }

        return null;
    }
}
