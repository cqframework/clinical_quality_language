package org.hl7.elm_modelinfo.r1.serializing.xmlutil;

import org.cqframework.cql.cql2elm.qdm.QdmModelInfoProvider;
import org.cqframework.cql.cql2elm.quick.FhirModelInfoProvider;
import org.cqframework.cql.cql2elm.quick.QICoreModelInfoProvider;
import org.cqframework.cql.cql2elm.quick.QuickFhirModelInfoProvider;
import org.cqframework.cql.cql2elm.quick.QuickModelInfoProvider;
import org.hl7.cql.model.SystemModelInfoProvider;
import org.junit.jupiter.api.Test;

class XmlutilModelInfoLoadingTests {
    static XmlModelInfoReader reader = new XmlModelInfoReader();

    @Test
    void system() {
        reader.read(SystemModelInfoProvider.class.getResourceAsStream("/org/hl7/elm/r1/system-modelinfo.xml"));
    }

    @Test
    void uSCore310() {
        reader.read(QuickModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/uscore-modelinfo-3.1.0.xml"));
    }

    @Test
    void uSCore311() {
        reader.read(QuickModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/uscore-modelinfo-3.1.1.xml"));
    }

    @Test
    void quickFhir301() {
        reader.read(
                QuickFhirModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/quickfhir-modelinfo-3.0.1.xml"));
    }

    @Test
    void quick330() {
        reader.read(QuickModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/quick-modelinfo-3.3.0.xml"));
    }

    @Test
    void quick300() {
        reader.read(QuickModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/quick-modelinfo-3.0.0.xml"));
    }

    @Test
    void quick() {
        reader.read(QuickModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/quick-modelinfo.xml"));
    }

    @Test
    void qICore400() {
        reader.read(QICoreModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/qicore-modelinfo-4.0.0.xml"));
    }

    @Test
    void qICore410() {
        reader.read(QICoreModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/qicore-modelinfo-4.1.0.xml"));
    }

    @Test
    void qICore411() {
        reader.read(QICoreModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/qicore-modelinfo-4.1.1.xml"));
    }

    @Test
    void qdm() {
        reader.read(QdmModelInfoProvider.class.getResourceAsStream("/gov/healthit/qdm/qdm-modelinfo.xml"));
    }

    @Test
    void qdm420() {
        reader.read(QdmModelInfoProvider.class.getResourceAsStream("/gov/healthit/qdm/qdm-modelinfo-4.2.xml"));
    }

    @Test
    void qdm430() {
        reader.read(QdmModelInfoProvider.class.getResourceAsStream("/gov/healthit/qdm/qdm-modelinfo-4.3.xml"));
    }

    @Test
    void qdm500() {
        reader.read(QdmModelInfoProvider.class.getResourceAsStream("/gov/healthit/qdm/qdm-modelinfo-5.0.xml"));
    }

    @Test
    void qdm501() {
        reader.read(QdmModelInfoProvider.class.getResourceAsStream("/gov/healthit/qdm/qdm-modelinfo-5.0.1.xml"));
    }

    @Test
    void qdm502() {
        reader.read(QdmModelInfoProvider.class.getResourceAsStream("/gov/healthit/qdm/qdm-modelinfo-5.0.2.xml"));
    }

    @Test
    void qdm530() {
        reader.read(QdmModelInfoProvider.class.getResourceAsStream("/gov/healthit/qdm/qdm-modelinfo-5.3.xml"));
    }

    @Test
    void qdm540() {
        reader.read(QdmModelInfoProvider.class.getResourceAsStream("/gov/healthit/qdm/qdm-modelinfo-5.4.xml"));
    }

    @Test
    void qdm550() {
        reader.read(QdmModelInfoProvider.class.getResourceAsStream("/gov/healthit/qdm/qdm-modelinfo-5.5.xml"));
    }

    @Test
    void qdm560() {
        reader.read(QdmModelInfoProvider.class.getResourceAsStream("/gov/healthit/qdm/qdm-modelinfo-5.6.xml"));
    }

    @Test
    void fhirModelInfo102() {
        reader.read(FhirModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-1.0.2.xml"));
    }

    @Test
    void fhirModelInfo140() {
        reader.read(FhirModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-1.4.xml"));
    }

    @Test
    void fhirModelInfo160() {
        reader.read(FhirModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-1.6.xml"));
    }

    @Test
    void fhirModelInfo180() {
        reader.read(FhirModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-1.8.xml"));
    }

    @Test
    void fhirModelInfo300() {
        reader.read(FhirModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-3.0.0.xml"));
    }

    @Test
    void fhirModelInfo301() {
        reader.read(FhirModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-3.0.1.xml"));
    }

    @Test
    void fhirModelInfo320() {
        reader.read(FhirModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-3.2.0.xml"));
    }

    @Test
    void fhirModelInfo400() {
        reader.read(FhirModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-4.0.0.xml"));
    }

    @Test
    void fhirModelInfo401() {
        reader.read(FhirModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-4.0.1.xml"));
    }
}
