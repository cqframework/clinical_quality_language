export const supportedModels = [
  {
    id: "System",
    system: null,
    version: null,
    url: "https://raw.githubusercontent.com/cqframework/clinical_quality_language/refs/heads/master/Src/java/model/src/main/resources/org/hl7/elm/r1/system-modelinfo.xml",
  },
  {
    id: "System",
    system: null,
    version: "1.0.0",
    url: "https://raw.githubusercontent.com/cqframework/clinical_quality_language/refs/heads/master/Src/java/model/src/main/resources/org/hl7/elm/r1/system-modelinfo.xml",
  },
  {
    id: "FHIR",
    system: null,
    version: "4.0.1",
    url: "https://raw.githubusercontent.com/cqframework/clinical_quality_language/refs/heads/master/Src/java/quick/src/main/resources/org/hl7/fhir/fhir-modelinfo-4.0.1.xml",
  },
  {
    id: "FHIR",
    system: null,
    version: null,
    url: "https://raw.githubusercontent.com/cqframework/clinical_quality_language/refs/heads/master/Src/java/quick/src/main/resources/org/hl7/fhir/fhir-modelinfo-4.0.1.xml",
  },
] as {
  id: string;
  system: string | null;
  version: string | null;
  url: string;
}[];
