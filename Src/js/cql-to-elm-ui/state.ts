import React from "react";
import {
  playgroundLibraryName,
  TCqlEngineOutput,
  TElmContentType,
  TLibrarySource,
  TMountedDir,
} from "@/shared";
import { TCqlToAstOutput } from "@/cql/cql-to-ast";

export const initialState = {
  common: {
    selectedTab: "cql-to-elm",
    cql: `library ${playgroundLibraryName}

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1'

valueset "Encounter Inpatient": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.666.5.307'

parameter "Measurement Period" Interval<DateTime>

context Patient

define "Inpatient Encounter":
  [Encounter: "Encounter Inpatient"] EncounterInpatient
    where EncounterInpatient.status = 'finished'
      and EncounterInpatient.period ends during day of "Measurement Period"
`,
    cursorPos: 0,
    cqlToAstOutput: null as null | TCqlToAstOutput, // used to highlight the current statement in the CQL editor
    mountedDir: null as TMountedDir | null,
    compilerOptions: [
      "EnableLocators",
      "DisableListDemotion",
      "DisableListPromotion",
    ],
    signatureLevel: "Overloads",
    librarySource: "remote" as TLibrarySource,
    baseUrl:
      "https://raw.githubusercontent.com/cqframework/cqf-exercises/refs/heads/master/input/cql/",
    log: [] as string[],
  },

  tabs: {
    "cql-to-parse-tree": {},
    "cql-to-ast": {
      showJson: false,
    },
    "cql-to-elm": {
      cqlToElmArgs: {
        useWasm: false,
        useWorker: true,
        outputContentType: "json" as TElmContentType,
      },
      elm: {
        contentType: "json" as TElmContentType,
        content: "",
      },
      isBusy: true,
      prettyPrintJson: false,
    },
    "cql-engine": {
      engineOptions: ["EnableExpressionCaching"],
      result: null as
        | null
        | (TCqlEngineOutput & {
            type: "expressionResults" | "evaluationException";
          }),
      isBusy: true,
    },
  },
};

export type TState = typeof initialState;

export type TSetState = React.Dispatch<React.SetStateAction<TState>>;
