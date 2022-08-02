package org.cqframework.cql.cql2elm;

import org.cqframework.cql.cql2elm.qdm.QdmModelInfoProvider;
import org.cqframework.cql.cql2elm.quick.FhirModelInfoProvider;
import org.cqframework.cql.cql2elm.quick.QICoreModelInfoProvider;
import org.cqframework.cql.cql2elm.quick.QuickFhirModelInfoProvider;
import org.cqframework.cql.cql2elm.quick.QuickModelInfoProvider;
import org.hl7.elm_modelinfo.r1.*;
import org.hl7.elm_modelinfo.r1.serializing.ModelInfoReaderFactory;
import org.testng.annotations.Test;
import java.io.IOException;

public class JacksonModelInfoLoadingTests {
    @Test
    public void testSystem() throws IOException {
        new org.hl7.elm_modelinfo.r1.serializing.jackson.XmlModelInfoReader().read(SystemModelInfoProvider.class.getResourceAsStream("/org/hl7/elm/r1/system-modelinfo.xml"));
    }

    @Test
    public void testUSCore310() throws IOException {
        new org.hl7.elm_modelinfo.r1.serializing.jackson.XmlModelInfoReader().read(QuickModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/uscore-modelinfo-3.1.0.xml"));
    }

    @Test
    public void testUSCore311() throws IOException {
        new org.hl7.elm_modelinfo.r1.serializing.jackson.XmlModelInfoReader().read(QuickModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/uscore-modelinfo-3.1.1.xml"));
    }

    @Test
    public void testQuickFhir301() throws IOException {
        new org.hl7.elm_modelinfo.r1.serializing.jackson.XmlModelInfoReader().read(QuickFhirModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/quickfhir-modelinfo-3.0.1.xml"));
    }

    @Test
    public void testQuick330() throws IOException {
        new org.hl7.elm_modelinfo.r1.serializing.jackson.XmlModelInfoReader().read(QuickModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/quick-modelinfo-3.3.0.xml"));
    }

    @Test
    public void testQuick300() throws IOException {
        new org.hl7.elm_modelinfo.r1.serializing.jackson.XmlModelInfoReader().read(QuickModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/quick-modelinfo-3.0.0.xml"));
    }

    @Test
    public void testQuick() throws IOException {
        new org.hl7.elm_modelinfo.r1.serializing.jackson.XmlModelInfoReader().read(QuickModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/quick-modelinfo.xml"));
    }

    @Test
    public void testQICore400() throws IOException {
        new org.hl7.elm_modelinfo.r1.serializing.jackson.XmlModelInfoReader().read(QICoreModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/qicore-modelinfo-4.0.0.xml"));
    }

    @Test
    public void testQICore410() throws IOException {
        new org.hl7.elm_modelinfo.r1.serializing.jackson.XmlModelInfoReader().read(QICoreModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/qicore-modelinfo-4.1.0.xml"));
    }

    @Test
    public void testQICore411() throws IOException {
        new org.hl7.elm_modelinfo.r1.serializing.jackson.XmlModelInfoReader().read(QICoreModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/qicore-modelinfo-4.1.1.xml"));
    }

    @Test
    public void testQDM() throws IOException {
        new org.hl7.elm_modelinfo.r1.serializing.jackson.XmlModelInfoReader().read(QdmModelInfoProvider.class.getResourceAsStream("/gov/healthit/qdm/qdm-modelinfo.xml"));
    }

    @Test
    public void testQDM420() throws IOException {
        new org.hl7.elm_modelinfo.r1.serializing.jackson.XmlModelInfoReader().read(QdmModelInfoProvider.class.getResourceAsStream("/gov/healthit/qdm/qdm-modelinfo-4.2.xml"));
    }

    @Test
    public void testQDM430() throws IOException {
        new org.hl7.elm_modelinfo.r1.serializing.jackson.XmlModelInfoReader().read(QdmModelInfoProvider.class.getResourceAsStream("/gov/healthit/qdm/qdm-modelinfo-4.3.xml"));
    }

    @Test
    public void testQDM500() throws IOException {
        new org.hl7.elm_modelinfo.r1.serializing.jackson.XmlModelInfoReader().read(QdmModelInfoProvider.class.getResourceAsStream("/gov/healthit/qdm/qdm-modelinfo-5.0.xml"));
    }

    @Test
    public void testQDM501() throws IOException {
        new org.hl7.elm_modelinfo.r1.serializing.jackson.XmlModelInfoReader().read(QdmModelInfoProvider.class.getResourceAsStream("/gov/healthit/qdm/qdm-modelinfo-5.0.1.xml"));
    }

    @Test
    public void testQDM502() throws IOException {
        new org.hl7.elm_modelinfo.r1.serializing.jackson.XmlModelInfoReader().read(QdmModelInfoProvider.class.getResourceAsStream("/gov/healthit/qdm/qdm-modelinfo-5.0.2.xml"));
    }

    @Test
    public void testQDM530() throws IOException {
        new org.hl7.elm_modelinfo.r1.serializing.jackson.XmlModelInfoReader().read(QdmModelInfoProvider.class.getResourceAsStream("/gov/healthit/qdm/qdm-modelinfo-5.3.xml"));
    }

    @Test
    public void testQDM540() throws IOException {
        new org.hl7.elm_modelinfo.r1.serializing.jackson.XmlModelInfoReader().read(QdmModelInfoProvider.class.getResourceAsStream("/gov/healthit/qdm/qdm-modelinfo-5.4.xml"));
    }

    @Test
    public void testQDM550() throws IOException {
        new org.hl7.elm_modelinfo.r1.serializing.jackson.XmlModelInfoReader().read(QdmModelInfoProvider.class.getResourceAsStream("/gov/healthit/qdm/qdm-modelinfo-5.5.xml"));
    }

    @Test
    public void testQDM560() throws IOException {
        new org.hl7.elm_modelinfo.r1.serializing.jackson.XmlModelInfoReader().read(QdmModelInfoProvider.class.getResourceAsStream("/gov/healthit/qdm/qdm-modelinfo-5.6.xml"));
    }

    @Test
    public void testFhirModelInfo102() throws IOException {
        new org.hl7.elm_modelinfo.r1.serializing.jackson.XmlModelInfoReader().read(FhirModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-1.0.2.xml"));
    }

    @Test
    public void testFhirModelInfo140() throws IOException {
        new org.hl7.elm_modelinfo.r1.serializing.jackson.XmlModelInfoReader().read(FhirModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-1.4.xml"));
    }

    @Test
    public void testFhirModelInfo160() throws IOException {
        new org.hl7.elm_modelinfo.r1.serializing.jackson.XmlModelInfoReader().read(FhirModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-1.6.xml"));
    }

    @Test
    public void testFhirModelInfo180() throws IOException {
        new org.hl7.elm_modelinfo.r1.serializing.jackson.XmlModelInfoReader().read(FhirModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-1.8.xml"));
    }

    @Test
    public void testFhirModelInfo300() throws IOException {
        new org.hl7.elm_modelinfo.r1.serializing.jackson.XmlModelInfoReader().read(FhirModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-3.0.0.xml"));
    }

    @Test
    public void testFhirModelInfo301() throws IOException {
        new org.hl7.elm_modelinfo.r1.serializing.jackson.XmlModelInfoReader().read(FhirModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-3.0.1.xml"));
    }

    @Test
    public void testFhirModelInfo320() throws IOException {
        new org.hl7.elm_modelinfo.r1.serializing.jackson.XmlModelInfoReader().read(FhirModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-3.2.0.xml"));
    }

    @Test
    public void testFhirModelInfo400() throws IOException {
        new org.hl7.elm_modelinfo.r1.serializing.jackson.XmlModelInfoReader().read(FhirModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-4.0.0.xml"));
    }
    
    @Test
    public void testFhirModelInfo401() throws IOException {
        new org.hl7.elm_modelinfo.r1.serializing.jackson.XmlModelInfoReader().read(FhirModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-4.0.1.xml"));
    }
}
