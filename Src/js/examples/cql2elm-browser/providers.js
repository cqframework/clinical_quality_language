import {
  createModelInfoProvider,
  createLibrarySourceProvider,
  stringAsSource,
} from "https://esm.sh/@cqframework/cql@4.0.0-beta.1/cql-to-elm";
import { fetchSync } from "./utils.js";

const supportedModels = [
  {
    id: "System",
    system: null,
    version: null,
    url: "https://raw.githubusercontent.com/cqframework/clinical_quality_language/refs/heads/main/Src/java/cql/src/commonMain/resources/org/hl7/elm/r1/system-modelinfo.xml",
  },
  {
    id: "FHIR",
    system: null,
    version: "4.0.1",
    url: "https://raw.githubusercontent.com/cqframework/clinical_quality_language/refs/heads/main/Src/java/quick/src/main/resources/org/hl7/fhir/fhir-modelinfo-4.0.1.xml",
  },
];

export const modelInfoProvider = createModelInfoProvider(
  (id, system, version) => {
    console.log(
      `Requested model info with id=${id}, system=${system}, version=${version}`,
    );
    for (const model of supportedModels) {
      if (
        model.id === id &&
        model.system === system &&
        model.version === version
      ) {
        const xml = fetchSync(model.url);
        return stringAsSource(xml);
      }
    }
    return null;
  },
);

const supportedLibraries = [
  {
    id: "FHIRHelpers",
    system: null,
    version: "4.0.1",
    url: "https://raw.githubusercontent.com/cqframework/clinical_quality_language/refs/heads/main/Src/java/quick/src/main/resources/org/hl7/fhir/FHIRHelpers-4.0.1.cql",
  },
];

export const librarySourceProvider = createLibrarySourceProvider(
  (id, system, version) => {
    console.log(
      `Requested library source with id=${id}, system=${system}, version=${version}`,
    );
    for (const library of supportedLibraries) {
      if (
        library.id === id &&
        library.system === system &&
        library.version === version
      ) {
        const cql = fetchSync(library.url);
        return stringAsSource(cql);
      }
    }
    return null;
  },
);
