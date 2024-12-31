package org.cqframework.cql.cql2elm.model;

import static kotlinx.io.CoreKt.buffered;
import static kotlinx.io.JvmCoreKt.asSource;

import java.io.InputStream;
import org.hl7.cql.model.ModelIdentifier;
import org.hl7.cql.model.ModelInfoProvider;
import org.hl7.elm_modelinfo.r1.ModelInfo;
import org.hl7.elm_modelinfo.r1.serializing.ModelInfoReaderFactory;

public class GentestModelInfoProviderSad1 implements ModelInfoProvider {
    @Override
    public ModelInfo load(ModelIdentifier modelIdentifier) {
        if (modelIdentifier.equals("GENTEST")) {
            InputStream is = GentestModelInfoProviderSad1.class.getResourceAsStream(
                    "/org/cqframework/cql/cql2elm/ModelTests/test-modelinfowithgenerics-sad1.xml");
            return ModelInfoReaderFactory.INSTANCE.getReader("application/xml").read(buffered(asSource(is)));
        }

        return null;
    }
}
