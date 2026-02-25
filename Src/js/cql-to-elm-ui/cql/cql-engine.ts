// @ts-expect-error No type definitions available for @lhncbc/ucum-lhc
import * as ucum from "@lhncbc/ucum-lhc";
import * as cqlToElmJs from "cql-js/kotlin/cql-to-elm.mjs";
import * as engineJs from "cql-js/kotlin/engine.mjs";
import * as kotlinStdlibJs from "cql-js/kotlin/kotlin-kotlin-stdlib.mjs";
import {
  compilerOptions,
  Nullable,
  playgroundLibraryName,
  TCqlEngineArgs,
  TCqlEngineOutput,
  unsupportedOperation,
} from "@/shared";
import { getModelXml } from "@/cql/get-model-xml";
import { getLibraryCql } from "@/cql/get-library-cql";

export function createStatefulEngine() {
  const ucumUtils = ucum.UcumLhcUtils.getInstance();
  const validateUnit = (unit: string) => {
    const result = ucumUtils.validateUnitString(unit);

    if (result.status === "valid") {
      return null;
    } else {
      return result.msg[0];
    }
  };
  const ucumService = cqlToElmJs.createUcumService(
    unsupportedOperation,
    validateUnit,
    unsupportedOperation,
    unsupportedOperation,
  );

  const modelManager = new cqlToElmJs.ModelManager();

  const libraryManager = new cqlToElmJs.LibraryManager(
    modelManager,
    cqlToElmJs.CqlCompilerOptions.defaultOptions(),
    null,
    ucumService,
  );

  const runCql = (
    args: TCqlEngineArgs,
    onOutput: (output: TCqlEngineOutput) => void,
  ) => {
    const modelInfoProvider = cqlToElmJs.createModelInfoProvider(
      (id: string, system: Nullable<string>, version: Nullable<string>) => {
        const xml = getModelXml(
          id,
          system,
          version,
          true,
          (message) => {
            onOutput({
              type: "log",
              log: message,
            });
          },
          () => {
            runCql(args, onOutput);
          },
        );
        if (xml === null) {
          return null;
        }
        return cqlToElmJs.stringAsSource(xml);
      },
    );

    const librarySourceProvider = cqlToElmJs.createLibrarySourceProvider(
      (id: string, system: Nullable<string>, version: Nullable<string>) => {
        const cql = getLibraryCql(
          id,
          system,
          version,
          args.cql,
          args.librarySource,
          args.baseUrl,
          args.mountedDir,
          true,
          (message) => {
            onOutput({
              type: "log",
              log: message,
            });
          },
          () => {
            runCql(args, onOutput);
          },
        );
        if (cql === null) {
          return null;
        }
        return cqlToElmJs.stringAsSource(cql);
      },
    );

    // @ts-expect-error TypeScript error
    modelManager.modelInfoLoader.clearModelInfoProviders();
    // @ts-expect-error TypeScript error
    modelManager.modelInfoLoader.registerModelInfoProvider(modelInfoProvider);
    // @ts-expect-error TypeScript error
    libraryManager.librarySourceLoader.clearProviders();
    // @ts-expect-error TypeScript error
    libraryManager.librarySourceLoader.registerProvider(librarySourceProvider);

    // Ideally, only the playground library is evicted from the cache.
    libraryManager.compiledLibraries.asJsMapView().clear();

    for (const compilerOption of compilerOptions) {
      if (args.compilerOptions.includes(compilerOption.value)) {
        libraryManager.cqlCompilerOptions.options.asJsSetView().add(
          // @ts-expect-error TypeScript error
          cqlToElmJs.CqlCompilerOptions.Options.valueOf(compilerOption.value),
        );
      } else {
        libraryManager.cqlCompilerOptions.options.asJsSetView().delete(
          // @ts-expect-error TypeScript error
          cqlToElmJs.CqlCompilerOptions.Options.valueOf(compilerOption.value),
        );
      }
    }

    libraryManager.cqlCompilerOptions.signatureLevel =
      // @ts-expect-error TypeScript error
      cqlToElmJs.LibraryBuilder.SignatureLevel.valueOf(args.signatureLevel);

    const output = ((): TCqlEngineOutput => {
      try {
        // @ts-expect-error TypeScript error
        const environment = new engineJs.Environment(libraryManager);

        const engine = new engineJs.CqlEngine(
          environment,
          kotlinStdlibJs.KtMutableSet.fromJsSet(
            new Set(
              args.engineOptions.map((engineOption) => {
                return engineJs.CqlEngine.Options.valueOf(engineOption);
              }),
            ),
          ),
        );

        const evaluationParamsBuilder = new engineJs.EvaluationParams.Builder();
        const libraryParamsBuilder =
          new engineJs.EvaluationParams.LibraryParams.Builder();
        const libraryParams = libraryParamsBuilder.build();
        evaluationParamsBuilder.libraryByName(
          playgroundLibraryName,
          libraryParams,
        );
        const evaluationParams = evaluationParamsBuilder.build();

        const evaluationResults = engine.evaluate(evaluationParams);

        const expressionResults = [
          ...evaluationResults.onlyResultOrThrow.expressionResults
            .asJsReadonlyMapView()
            .entries(),
        ].map(([expressionName, expressionResult]) => ({
          expressionName,
          expressionResult: prettyPrintValue(expressionResult.value),
        }));

        return {
          type: "expressionResults",
          expressionResults: expressionResults,
        };
      } catch (e) {
        // console.error("Error during CQL evaluation:", e);
        if (e instanceof Error) {
          return {
            type: "evaluationException",
            message: e.message,
            stack: e.stack || "",
          };
        }
        return {
          type: "evaluationException",
          message: String(e),
          stack: "",
        };
      }
    })();

    onOutput(output);
  };

  return {
    runCql,
  };
}

// eslint-disable-next-line @typescript-eslint/no-explicit-any
function prettyPrintValue(value: Nullable<any>): string {
  if (typeof value === "string") {
    return `'${value}'`;
  }
  return String(value);
}
