package org.hl7.elm_modelinfo.r1.serializing.xmlutil;

import java.io.IOException;
import org.cqframework.cql.cql2elm.qdm.QdmModelInfoProvider;
import org.cqframework.cql.cql2elm.quick.FhirModelInfoProvider;
import org.cqframework.cql.cql2elm.quick.QICoreModelInfoProvider;
import org.cqframework.cql.cql2elm.quick.QuickFhirModelInfoProvider;
import org.cqframework.cql.cql2elm.quick.QuickModelInfoProvider;
import org.hl7.cql.model.SystemModelInfoProvider;
import org.junit.jupiter.api.Test;

class XmlutilModelInfoLoadingTests {
    @Test
    void system() throws IOException {
        new XmlModelInfoReader()
                .read(SystemModelInfoProvider.class.getResourceAsStream("/org/hl7/elm/r1/system-modelinfo.xml"));
    }

    @Test
    void uSCore310() throws IOException {
        new XmlModelInfoReader()
                .read(QuickModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/uscore-modelinfo-3.1.0.xml"));
    }

    @Test
    void uSCore311() throws IOException {
        new XmlModelInfoReader()
                .read(QuickModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/uscore-modelinfo-3.1.1.xml"));
    }

    @Test
    void quickFhir301() throws IOException {
        new XmlModelInfoReader()
                .read(QuickFhirModelInfoProvider.class.getResourceAsStream(
                        "/org/hl7/fhir/quickfhir-modelinfo-3.0.1.xml"));
    }

    @Test
    void quick330() throws IOException {
        new XmlModelInfoReader()
                .read(QuickModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/quick-modelinfo-3.3.0.xml"));
    }

    @Test
    void quick300() throws IOException {
        new XmlModelInfoReader()
                .read(QuickModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/quick-modelinfo-3.0.0.xml"));
    }

    @Test
    void quick() throws IOException {
        new XmlModelInfoReader()
                .read(QuickModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/quick-modelinfo.xml"));
    }

    @Test
    void qICore400() throws IOException {
        new XmlModelInfoReader()
                .read(QICoreModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/qicore-modelinfo-4.0.0.xml"));
    }

    @Test
    void qICore410() throws IOException {
        new XmlModelInfoReader()
                .read(QICoreModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/qicore-modelinfo-4.1.0.xml"));
    }

    @Test
    void qICore411() throws IOException {
        new XmlModelInfoReader()
                .read(QICoreModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/qicore-modelinfo-4.1.1.xml"));
    }

    @Test
    void qdm() throws IOException {
        new XmlModelInfoReader()
                .read(QdmModelInfoProvider.class.getResourceAsStream("/gov/healthit/qdm/qdm-modelinfo.xml"));
    }

    @Test
    void qdm420() throws IOException {
        new XmlModelInfoReader()
                .read(QdmModelInfoProvider.class.getResourceAsStream("/gov/healthit/qdm/qdm-modelinfo-4.2.xml"));
    }

    @Test
    void qdm430() throws IOException {
        new XmlModelInfoReader()
                .read(QdmModelInfoProvider.class.getResourceAsStream("/gov/healthit/qdm/qdm-modelinfo-4.3.xml"));
    }

    @Test
    void qdm500() throws IOException {
        new XmlModelInfoReader()
                .read(QdmModelInfoProvider.class.getResourceAsStream("/gov/healthit/qdm/qdm-modelinfo-5.0.xml"));
    }

    @Test
    void qdm501() throws IOException {
        new XmlModelInfoReader()
                .read(QdmModelInfoProvider.class.getResourceAsStream("/gov/healthit/qdm/qdm-modelinfo-5.0.1.xml"));
    }

    @Test
    void qdm502() throws IOException {
        new XmlModelInfoReader()
                .read(QdmModelInfoProvider.class.getResourceAsStream("/gov/healthit/qdm/qdm-modelinfo-5.0.2.xml"));
    }

    @Test
    void qdm530() throws IOException {
        new XmlModelInfoReader()
                .read(QdmModelInfoProvider.class.getResourceAsStream("/gov/healthit/qdm/qdm-modelinfo-5.3.xml"));
    }

    @Test
    void qdm540() throws IOException {
        new XmlModelInfoReader()
                .read(QdmModelInfoProvider.class.getResourceAsStream("/gov/healthit/qdm/qdm-modelinfo-5.4.xml"));
    }

    @Test
    void qdm550() throws IOException {
        new XmlModelInfoReader()
                .read(QdmModelInfoProvider.class.getResourceAsStream("/gov/healthit/qdm/qdm-modelinfo-5.5.xml"));
    }

    @Test
    void qdm560() throws IOException {
        new XmlModelInfoReader()
                .read(QdmModelInfoProvider.class.getResourceAsStream("/gov/healthit/qdm/qdm-modelinfo-5.6.xml"));
    }

    @Test
    void fhirModelInfo102() throws IOException {
        new XmlModelInfoReader()
                .read(FhirModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-1.0.2.xml"));
    }

    @Test
    void fhirModelInfo140() throws IOException {
        new XmlModelInfoReader()
                .read(FhirModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-1.4.xml"));
    }

    @Test
    void fhirModelInfo160() throws IOException {
        new XmlModelInfoReader()
                .read(FhirModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-1.6.xml"));
    }

    @Test
    void fhirModelInfo180() throws IOException {
        new XmlModelInfoReader()
                .read(FhirModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-1.8.xml"));
    }

    @Test
    void fhirModelInfo300() throws IOException {
        new XmlModelInfoReader()
                .read(FhirModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-3.0.0.xml"));
    }

    @Test
    void fhirModelInfo301() throws IOException {
        new XmlModelInfoReader()
                .read(FhirModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-3.0.1.xml"));
    }

    @Test
    void fhirModelInfo320() throws IOException {
        new XmlModelInfoReader()
                .read(FhirModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-3.2.0.xml"));
    }

    @Test
    void fhirModelInfo400() throws IOException {
        new XmlModelInfoReader()
                .read(FhirModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-4.0.0.xml"));
    }

    @Test
    void fhirModelInfo401() throws IOException {
        new XmlModelInfoReader()
                .read(FhirModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-4.0.1.xml"));
    }
}
