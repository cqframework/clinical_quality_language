import "./require-polyfill.mjs";
import { NamespaceManager } from "@cqframework/cql/cql";
import {
  DefaultModelInfoProvider,
  DefaultLibrarySourceProvider,
  stringAsPath,
  ModelManager,
  LibraryManager,
  CqlTranslator,
} from "@cqframework/cql/cql-to-elm";

const namespaceManager = new NamespaceManager();
const modelManager = new ModelManager(namespaceManager);
modelManager.modelInfoLoader.registerModelInfoProvider(
  new DefaultModelInfoProvider(stringAsPath("./models")),
);
const libraryManager = new LibraryManager(modelManager);
libraryManager.librarySourceLoader.registerProvider(
  new DefaultLibrarySourceProvider(stringAsPath("./libraries")),
);

const cql = `
library Test version '0.1.0'

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1'

valueset "Encounter Inpatient": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.666.5.307'

parameter "Measurement Period" Interval<DateTime>

context Patient

define "Inpatient Encounter":
  [Encounter: "Encounter Inpatient"] EncounterInpatient
    where EncounterInpatient.status = 'finished'
      and EncounterInpatient.period ends during day of "Measurement Period"

`;

const translator = CqlTranslator.fromText(cql, libraryManager);

console.log(translator.toXml());
