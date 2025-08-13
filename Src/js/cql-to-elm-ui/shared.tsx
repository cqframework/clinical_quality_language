import React, { Fragment } from "react";

export type TElmContentType = "json" | "xml";

export type TLibrarySource = "local" | "remote";

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
            color: "#aa0d91",
            fontWeight: 700,
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

export type TCompileCqlArgs = {
  cql: string;
  useWasm: boolean;
  compilerOptions: string[];
  signatureLevel: string;
  outputContentType: TElmContentType;
  librarySource: TLibrarySource;
  baseUrl: string;
  mountedDir: {
    handle: FileSystemDirectoryHandle;
    files: {
      handle: FileSystemFileHandle;
    }[];
  } | null;
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
