package org.cqframework.cql.cql2elm;

import static kotlinx.io.CoreKt.buffered;
import static kotlinx.io.JvmCoreKt.asSource;

import org.hl7.cql.model.ModelIdentifier;
import org.hl7.cql.model.ModelInfoProvider;
import org.hl7.elm_modelinfo.r1.ModelInfo;
import org.hl7.elm_modelinfo.r1.serializing.XmlModelInfoReader;

public class TestModelInfoProvider implements ModelInfoProvider {
    public ModelInfo load(ModelIdentifier modelIdentifier) {
        if (modelIdentifier.getId().equals("Test")) {
            var stream = TestModelInfoProvider.class.getResourceAsStream("ModelTests/test-modelinfo.xml");
            return XmlModelInfoReader.read(buffered(asSource(stream)));
        }

        return null;
    }
}
