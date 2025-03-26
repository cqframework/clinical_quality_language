package org.cqframework.cql.cql2elm.model;

import static kotlinx.io.CoreKt.buffered;
import static kotlinx.io.JvmCoreKt.asSource;

import java.io.InputStream;
import org.hl7.cql.model.ModelIdentifier;
import org.hl7.cql.model.ModelInfoProvider;
import org.hl7.elm_modelinfo.r1.ModelInfo;
import org.hl7.elm_modelinfo.r1.serializing.XmlModelInfoReader;

public class GentestModelInfoProvider implements ModelInfoProvider {
    @Override
    public ModelInfo load(ModelIdentifier modelIdentifier) {
        if (modelIdentifier.getId().equals("GENTEST")) {
            InputStream is = GentestModelInfoProvider.class.getResourceAsStream(
                    "/org/cqframework/cql/cql2elm/ModelTests/test-modelinfowithgenerics-happy.xml");
            return XmlModelInfoReader.read(buffered(asSource(is)));
        }

        return null;
    }
}
