import React, { Fragment } from "react";
import * as cqlJs from "cql-js";
import * as cqlWasmJs from "cql-wasm-js";

export type Nullable<T> = cqlJs.Nullable<T> | cqlWasmJs.Nullable<T>;

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

export function unsupportedOperation(): never {
  throw new Error("Unsupported operation");
}

export const engineOptions = [
  {
    value: "EnableExpressionCaching",
    label: "Enable expression caching",
  },
  {
    value: "EnableValidation",
    label: "Enable validation",
  },
  {
    value: "EnableHedisCompatibilityMode",
    label: "Enable HEDIS compatibility mode",
  },
  {
    value: "EnableProfiling",
    label: "Enable profiling",
  },
  {
    value: "EnableTracing",
    label: "Enable tracing",
  },
  {
    value: "EnableCoverageCollection",
    label: "Enable coverage collection",
  },
  {
    value: "EnableTypeChecking",
    label: "Enable type checking",
  },
];

export type TCqlEngineArgs = {
  cql: string;
  compilerOptions: string[];
  signatureLevel: string;
  librarySource: TLibrarySource;
  baseUrl: string;
  mountedDir: TMountedDir | null;
  engineOptions: string[];
};

export type TCqlEngineOutput =
  | {
      type: "log";
      log: string;
    }
  | {
      type: "expressionResults";
      expressionResults: {
        expressionName: string;
        expressionResult: string;
      }[];
    }
  | {
      type: "evaluationException";
      message: string;
      stack: string;
    };

export const playgroundLibraryName = "Playground";
