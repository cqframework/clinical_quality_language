package org.cqframework.cql.cql2elm.model;

import static kotlinx.io.CoreKt.buffered;
import static kotlinx.io.JvmCoreKt.asSource;
import static org.hl7.elm_modelinfo.r1.serializing.XmlModelInfoReaderKt.parseModelInfoXml;

import java.io.InputStream;
import org.hl7.cql.model.ModelIdentifier;
import org.hl7.cql.model.ModelInfoProvider;
import org.hl7.elm_modelinfo.r1.ModelInfo;

public class GentestModelInfoProviderSad1 implements ModelInfoProvider {
    @Override
    public ModelInfo load(ModelIdentifier modelIdentifier) {
        if (modelIdentifier.equals("GENTEST")) {
            InputStream is = GentestModelInfoProviderSad1.class.getResourceAsStream(
                    "/org/cqframework/cql/cql2elm/ModelTests/test-modelinfowithgenerics-sad1.xml");
            return parseModelInfoXml(buffered(asSource(is)));
        }

        return null;
    }
}
