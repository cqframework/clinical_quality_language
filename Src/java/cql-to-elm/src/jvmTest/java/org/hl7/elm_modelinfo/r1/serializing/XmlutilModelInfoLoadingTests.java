package org.hl7.elm_modelinfo.r1.serializing;

import static kotlinx.io.CoreKt.buffered;
import static kotlinx.io.JvmCoreKt.asSource;

import org.cqframework.cql.cql2elm.qdm.QdmModelInfoProvider;
import org.cqframework.cql.cql2elm.quick.FhirModelInfoProvider;
import org.cqframework.cql.cql2elm.quick.QICoreModelInfoProvider;
import org.cqframework.cql.cql2elm.quick.QuickFhirModelInfoProvider;
import org.cqframework.cql.cql2elm.quick.QuickModelInfoProvider;
import org.hl7.cql.model.SystemModelInfoProvider;
import org.hl7.elm_modelinfo.r1.ModelInfo;
import org.junit.jupiter.api.Test;

class XmlutilModelInfoLoadingTests {
    private ModelInfo read(Class<?> clazz, String resource) {
        var stream = clazz.getResourceAsStream(resource);
        return XmlModelInfoReader.read(buffered(asSource(stream)));
    }

    @Test
    void system() {
        read(SystemModelInfoProvider.class, "/org/hl7/elm/r1/system-modelinfo.xml");
    }

    @Test
    void uSCore310() {
        read(QuickModelInfoProvider.class, "/org/hl7/fhir/uscore-modelinfo-3.1.0.xml");
    }

    @Test
    void uSCore311() {
        read(QuickModelInfoProvider.class, "/org/hl7/fhir/uscore-modelinfo-3.1.1.xml");
    }

    @Test
    void quickFhir301() {
        read(QuickFhirModelInfoProvider.class, "/org/hl7/fhir/quickfhir-modelinfo-3.0.1.xml");
    }

    @Test
    void quick330() {
        read(QuickModelInfoProvider.class, "/org/hl7/fhir/quick-modelinfo-3.3.0.xml");
    }

    @Test
    void quick300() {
        read(QuickModelInfoProvider.class, "/org/hl7/fhir/quick-modelinfo-3.0.0.xml");
    }

    @Test
    void quick() {
        read(QuickModelInfoProvider.class, "/org/hl7/fhir/quick-modelinfo.xml");
    }

    @Test
    void qICore400() {
        read(QICoreModelInfoProvider.class, "/org/hl7/fhir/qicore-modelinfo-4.0.0.xml");
    }

    @Test
    void qICore410() {
        read(QICoreModelInfoProvider.class, "/org/hl7/fhir/qicore-modelinfo-4.1.0.xml");
    }

    @Test
    void qICore411() {
        read(QICoreModelInfoProvider.class, "/org/hl7/fhir/qicore-modelinfo-4.1.1.xml");
    }

    @Test
    void qdm() {
        read(QdmModelInfoProvider.class, "/gov/healthit/qdm/qdm-modelinfo.xml");
    }

    @Test
    void qdm420() {
        read(QdmModelInfoProvider.class, "/gov/healthit/qdm/qdm-modelinfo-4.2.xml");
    }

    @Test
    void qdm430() {
        read(QdmModelInfoProvider.class, "/gov/healthit/qdm/qdm-modelinfo-4.3.xml");
    }

    @Test
    void qdm500() {
        read(QdmModelInfoProvider.class, "/gov/healthit/qdm/qdm-modelinfo-5.0.xml");
    }

    @Test
    void qdm501() {
        read(QdmModelInfoProvider.class, "/gov/healthit/qdm/qdm-modelinfo-5.0.1.xml");
    }

    @Test
    void qdm502() {
        read(QdmModelInfoProvider.class, "/gov/healthit/qdm/qdm-modelinfo-5.0.2.xml");
    }

    @Test
    void qdm530() {
        read(QdmModelInfoProvider.class, "/gov/healthit/qdm/qdm-modelinfo-5.3.xml");
    }

    @Test
    void qdm540() {
        read(QdmModelInfoProvider.class, "/gov/healthit/qdm/qdm-modelinfo-5.4.xml");
    }

    @Test
    void qdm550() {
        read(QdmModelInfoProvider.class, "/gov/healthit/qdm/qdm-modelinfo-5.5.xml");
    }

    @Test
    void qdm560() {
        read(QdmModelInfoProvider.class, "/gov/healthit/qdm/qdm-modelinfo-5.6.xml");
    }

    @Test
    void fhirModelInfo102() {
        read(FhirModelInfoProvider.class, "/org/hl7/fhir/fhir-modelinfo-1.0.2.xml");
    }

    @Test
    void fhirModelInfo140() {
        read(FhirModelInfoProvider.class, "/org/hl7/fhir/fhir-modelinfo-1.4.xml");
    }

    @Test
    void fhirModelInfo160() {
        read(FhirModelInfoProvider.class, "/org/hl7/fhir/fhir-modelinfo-1.6.xml");
    }

    @Test
    void fhirModelInfo180() {
        read(FhirModelInfoProvider.class, "/org/hl7/fhir/fhir-modelinfo-1.8.xml");
    }

    @Test
    void fhirModelInfo300() {
        read(FhirModelInfoProvider.class, "/org/hl7/fhir/fhir-modelinfo-3.0.0.xml");
    }

    @Test
    void fhirModelInfo301() {
        read(FhirModelInfoProvider.class, "/org/hl7/fhir/fhir-modelinfo-3.0.1.xml");
    }

    @Test
    void fhirModelInfo320() {
        read(FhirModelInfoProvider.class, "/org/hl7/fhir/fhir-modelinfo-3.2.0.xml");
    }

    @Test
    void fhirModelInfo400() {
        read(FhirModelInfoProvider.class, "/org/hl7/fhir/fhir-modelinfo-4.0.0.xml");
    }

    @Test
    void fhirModelInfo401() {
        read(FhirModelInfoProvider.class, "/org/hl7/fhir/fhir-modelinfo-4.0.1.xml");
    }
}
