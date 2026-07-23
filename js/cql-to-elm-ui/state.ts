import React from "react";
import {
  TCqlEngineOutput,
  TElmContentType,
  TLibrarySource,
  TMountedDir,
  TOutput,
} from "@/shared";
import { TCqlToAstOutput } from "@/cql/cql-to-ast";
import exampleFhirBundle from "@/example-fhir-bundle.json";

export const initialState = {
  common: {
    selectedTab: "cql-engine",
    cql: `library Playground

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1'

valueset "Vital Sign": 'http://example.org/fhir/ValueSet/vital-sign'

codesystem "LOINC": 'http://loinc.org'
code "Blood pressure panel": '85354-9' from "LOINC"

context Patient

define "Vital Signs":
  [Observation: category in "Vital Sign"]

define "Vital Sign Category In Value Set": (singleton from "Vital Signs").category in "Vital Sign"

// define "Blood Pressure Observations": [Observation: "Blood pressure panel"]
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
    highlightActiveStatement: false,
    showLog: false,
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
      elm: null as
        | null
        | (TOutput & {
            type: "elm";
          }),
      isBusy: true,
      prettyPrintJson: false,
    },
    "cql-engine": {
      engineOptions: ["EnableExpressionCaching"],
      data: JSON.stringify(exampleFhirBundle, null, 2),
      result: null as
        | null
        | (TCqlEngineOutput & {
            type: "expressionResults" | "evaluationException";
          }),
      isBusy: true,
      showNullsInClassInstances: false,
      showTypeHints: true,
    },
  },
};

export type TState = typeof initialState;

export type TSetState = React.Dispatch<React.SetStateAction<TState>>;
