import React, { Fragment } from "react";

export type TElmContentType = "json" | "xml";

export type TLibrarySource = "local" | "remote";

export type TMountedDir = {
  handle: FileSystemDirectoryHandle;
  files: {
    handle: FileSystemFileHandle;
  }[];
};

export const compilerOptions = [
  {
    value: "EnableDateRangeOptimization",
    label: "Enable date range optimization",
  },
  {
    value: "EnableAnnotations",
    label: "Enable annotations",
  },
  {
    value: "EnableLocators",
    label: "Enable locators",
  },
  {
    value: "EnableResultTypes",
    label: "Enable result types",
  },
  {
    value: "EnableDetailedErrors",
    label: "Enable detailed errors",
  },
  {
    value: "DisableListTraversal",
    label: "Disable list traversal",
  },
  {
    value: "DisableListDemotion",
    label: "Disable list demotion",
  },
  {
    value: "DisableListPromotion",
    label: "Disable list promotion",
  },
  {
    value: "EnableIntervalDemotion",
    label: "Enable interval demotion",
  },
  {
    value: "EnableIntervalPromotion",
    label: "Enable interval promotion",
  },
  {
    value: "DisableMethodInvocation",
    label: "Disable method invocation",
  },
  {
    value: "RequireFromKeyword",
    label: (
      <Fragment>
        Require{" "}
        <span
          style={{
            fontFamily: "var(--monospace-font-family)",
            fontSize: "90%",
            color: "#d73a49",
          }}
        >
          from
        </span>{" "}
        keyword for queries
      </Fragment>
    ),
  },
  {
    value: "DisableDefaultModelInfoLoad",
    label: "Disable default model info load",
  },
];

export const signatureLevels = ["None", "Differing", "Overloads", "All"];

export type TCqlToElmArgs = {
  cql: string;
  useWasm: boolean;
  compilerOptions: string[];
  signatureLevel: string;
  outputContentType: TElmContentType;
  librarySource: TLibrarySource;
  baseUrl: string;
  mountedDir: TMountedDir | null;
  useWorker: boolean;
};

export type TOutput =
  | {
      type: "log";
      log: string;
    }
  | {
      type: "elm";
      contentType: TElmContentType;
      elm: string;
    };

export const initialState = {
  common: {
    selectedTab: "cql-to-elm",
    cql: `library Test version '0.1.0'

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
    mountedDir: null as TMountedDir | null,
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
        compilerOptions: [
          "EnableLocators",
          "DisableListDemotion",
          "DisableListPromotion",
        ],
        signatureLevel: "Overloads",
        outputContentType: "json",
        librarySource: "remote",
        baseUrl:
          "https://raw.githubusercontent.com/cqframework/cqf-exercises/refs/heads/master/input/cql/",
        useWorker: true,
      } as Omit<TCqlToElmArgs, "cql" | "mountedDir">,
      elm: {
        contentType: "json" as TElmContentType,
        content: "",
      },
      isBusy: true,
    },
  },
};

export type TState = typeof initialState;

export type TSetState = React.Dispatch<React.SetStateAction<TState>>;
