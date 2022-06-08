package org.cqframework.cql.cql2elm;

import org.hl7.elm_modelinfo.r1.*;
import org.testng.annotations.Test;
import java.io.IOException;

public class JacksonModelInfoLoadingTests {
    @Test
    public void testSystem() throws IOException {
        ModelInfoXmlReader.readValue(SystemModelInfoProvider.class.getResourceAsStream("/org/hl7/elm/r1/system-modelinfo.xml"), ModelInfo.class);
    }

    @Test
    public void testUSCore310() throws IOException {
        ModelInfoXmlReader.readValue(QuickModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/uscore-modelinfo-3.1.0.xml"), ModelInfo.class);
    }

    @Test
    public void testUSCore311() throws IOException {
        ModelInfoXmlReader.readValue(QuickModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/uscore-modelinfo-3.1.1.xml"), ModelInfo.class);
    }

    @Test
    public void testQuickFhir301() throws IOException {
        ModelInfoXmlReader.readValue(QuickFhirModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/quickfhir-modelinfo-3.0.1.xml"), ModelInfo.class);
    }

    @Test
    public void testQuick330() throws IOException {
        ModelInfoXmlReader.readValue(QuickModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/quick-modelinfo-3.3.0.xml"), ModelInfo.class);
    }

    @Test
    public void testQuick300() throws IOException {
        ModelInfoXmlReader.readValue(QuickModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/quick-modelinfo-3.0.0.xml"), ModelInfo.class);
    }

    @Test
    public void testQuick() throws IOException {
        ModelInfoXmlReader.readValue(QuickModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/quick-modelinfo.xml"), ModelInfo.class);
    }

    @Test
    public void testQICore400() throws IOException {
        ModelInfoXmlReader.readValue(QICoreModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/qicore-modelinfo-4.0.0.xml"), ModelInfo.class);
    }

    @Test
    public void testQICore410() throws IOException {
        ModelInfoXmlReader.readValue(QICoreModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/qicore-modelinfo-4.1.0.xml"), ModelInfo.class);
    }

    @Test
    public void testQICore411() throws IOException {
        ModelInfoXmlReader.readValue(QICoreModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/qicore-modelinfo-4.1.1.xml"), ModelInfo.class);
    }

    @Test
    public void testQDM() throws IOException {
        ModelInfoXmlReader.readValue(QdmModelInfoProvider.class.getResourceAsStream("/gov/healthit/qdm/qdm-modelinfo.xml"), ModelInfo.class);
    }

    @Test
    public void testQDM420() throws IOException {
        ModelInfoXmlReader.readValue(QdmModelInfoProvider.class.getResourceAsStream("/gov/healthit/qdm/qdm-modelinfo-4.2.xml"), ModelInfo.class);
    }

    @Test
    public void testQDM430() throws IOException {
        ModelInfoXmlReader.readValue(QdmModelInfoProvider.class.getResourceAsStream("/gov/healthit/qdm/qdm-modelinfo-4.3.xml"), ModelInfo.class);
    }

    @Test
    public void testQDM500() throws IOException {
        ModelInfoXmlReader.readValue(QdmModelInfoProvider.class.getResourceAsStream("/gov/healthit/qdm/qdm-modelinfo-5.0.xml"), ModelInfo.class);
    }

    @Test
    public void testQDM501() throws IOException {
        ModelInfoXmlReader.readValue(QdmModelInfoProvider.class.getResourceAsStream("/gov/healthit/qdm/qdm-modelinfo-5.0.1.xml"), ModelInfo.class);
    }

    @Test
    public void testQDM502() throws IOException {
        ModelInfoXmlReader.readValue(QdmModelInfoProvider.class.getResourceAsStream("/gov/healthit/qdm/qdm-modelinfo-5.0.2.xml"), ModelInfo.class);
    }

    @Test
    public void testQDM530() throws IOException {
        ModelInfoXmlReader.readValue(QdmModelInfoProvider.class.getResourceAsStream("/gov/healthit/qdm/qdm-modelinfo-5.3.xml"), ModelInfo.class);
    }

    @Test
    public void testQDM540() throws IOException {
        ModelInfoXmlReader.readValue(QdmModelInfoProvider.class.getResourceAsStream("/gov/healthit/qdm/qdm-modelinfo-5.4.xml"), ModelInfo.class);
    }

    @Test
    public void testQDM550() throws IOException {
        ModelInfoXmlReader.readValue(QdmModelInfoProvider.class.getResourceAsStream("/gov/healthit/qdm/qdm-modelinfo-5.5.xml"), ModelInfo.class);
    }

    @Test
    public void testQDM560() throws IOException {
        ModelInfoXmlReader.readValue(QdmModelInfoProvider.class.getResourceAsStream("/gov/healthit/qdm/qdm-modelinfo-5.6.xml"), ModelInfo.class);
    }

    @Test
    public void testFhirModelInfo102() throws IOException {
        ModelInfoXmlReader.readValue(FhirModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-1.0.2.xml"),ModelInfo.class);
    }

    @Test
    public void testFhirModelInfo140() throws IOException {
        ModelInfoXmlReader.readValue(FhirModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-1.4.xml"),ModelInfo.class);
    }

    @Test
    public void testFhirModelInfo160() throws IOException {
        ModelInfoXmlReader.readValue(FhirModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-1.6.xml"),ModelInfo.class);
    }

    @Test
    public void testFhirModelInfo180() throws IOException {
        ModelInfoXmlReader.readValue(FhirModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-1.8.xml"),ModelInfo.class);
    }

    @Test
    public void testFhirModelInfo300() throws IOException {
        ModelInfoXmlReader.readValue(FhirModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-3.0.0.xml"),ModelInfo.class);
    }

    @Test
    public void testFhirModelInfo301() throws IOException {
        ModelInfoXmlReader.readValue(FhirModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-3.0.1.xml"),ModelInfo.class);
    }

    @Test
    public void testFhirModelInfo320() throws IOException {
        ModelInfoXmlReader.readValue(FhirModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-3.2.0.xml"),ModelInfo.class);
    }

    @Test
    public void testFhirModelInfo400() throws IOException {
        ModelInfoXmlReader.readValue(FhirModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-4.0.0.xml"), ModelInfo.class);
    }
    
    @Test
    public void testFhirModelInfo401() throws IOException {
        ModelInfoXmlReader.readValue(FhirModelInfoProvider.class.getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-4.0.1.xml"), ModelInfo.class);
    }
}
