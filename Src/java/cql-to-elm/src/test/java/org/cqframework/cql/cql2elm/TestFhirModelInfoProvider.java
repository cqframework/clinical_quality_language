package org.cqframework.cql.cql2elm;

import static kotlinx.io.CoreKt.buffered;
import static kotlinx.io.JvmCoreKt.asSource;

import org.hl7.cql.model.ModelIdentifier;
import org.hl7.cql.model.ModelInfoProvider;
import org.hl7.elm_modelinfo.r1.ModelInfo;
import org.hl7.elm_modelinfo.r1.serializing.ModelInfoReaderFactory;

/**
 * Created by Bryn on 12/11/2016.
 */
public class TestFhirModelInfoProvider implements ModelInfoProvider {
    private Class clazz;

    public TestFhirModelInfoProvider(Class clazz) {
        this.clazz = clazz;
    }

    public ModelInfo load(ModelIdentifier modelIdentifier) {
        if (modelIdentifier.getId().equals("FHIR")) {
            var source = buffered(asSource(clazz.getResourceAsStream("fhir-modelinfo-1.8.xml")));
            return ModelInfoReaderFactory.INSTANCE.getReader("application/xml").read(source);
        }

        return null;
    }
}
