// @ts-expect-error No type definitions available for @lhncbc/ucum-lhc
import * as ucum from "@lhncbc/ucum-lhc";
import * as cqlToElmJs from "cql-js/kotlin/cql-to-elm.mjs";
import * as cqlWasmJs from "cql-wasm-js";
import {
  compilerOptions,
  Nullable,
  TCqlToElmArgs,
  TOutput,
  unsupportedOperation,
} from "@/shared";
import { getModelXml } from "@/cql/get-model-xml";
import { getLibraryCql } from "@/cql/get-library-cql";

export function createStatefulCompiler(sync: boolean) {
  const ucumUtils = ucum.UcumLhcUtils.getInstance();
  const validateUnit = (unit: string) => {
    const result = ucumUtils.validateUnitString(unit);

    if (result.status === "valid") {
      return null;
    } else {
      return result.msg[0];
    }
  };
  const ucumServiceJs = cqlToElmJs.createUcumService(
    unsupportedOperation,
    validateUnit,
    unsupportedOperation,
    unsupportedOperation,
  );
  const ucumServiceWasmJs = cqlWasmJs.createUcumService(
    unsupportedOperation,
    validateUnit,
    unsupportedOperation,
    unsupportedOperation,
  );

  const modelManagerJs = new cqlToElmJs.ModelManager();
  // @ts-expect-error TypeScript error
  const modelManagerWasmJs = cqlWasmJs.createModelManager();

  const libraryManagerJs = new cqlToElmJs.LibraryManager(
    modelManagerJs,
    cqlToElmJs.CqlCompilerOptions.defaultOptions(),
    null,
    ucumServiceJs,
  );
  // @ts-expect-error TypeScript error
  const libraryManagerWasmJs = cqlWasmJs.createLibraryManager(
    modelManagerWasmJs,
    ucumServiceWasmJs,
  );

  const compileCql = (
    args: TCqlToElmArgs,
    onOutput: (output: TOutput) => void,
  ) => {
    const modelInfoProviderJs = cqlToElmJs.createModelInfoProvider(
      (id: string, system: Nullable<string>, version: Nullable<string>) => {
        const xml = getModelXml(
          id,
          system,
          version,
          sync,
          (message) => {
            onOutput({
              type: "log",
              log: message,
            });
          },
          () => {
            compileCql(args, onOutput);
          },
        );
        if (xml === null) {
          return null;
        }
        return cqlToElmJs.stringAsSource(xml);
      },
    );
    const modelInfoProviderWasmJs = cqlWasmJs.createModelInfoProvider(
      (id, system, version) => {
        const xml = getModelXml(
          id,
          system,
          version,
          sync,
          (message) => {
            onOutput({
              type: "log",
              log: message,
            });
          },
          () => {
            compileCql(args, onOutput);
          },
        );
        if (xml === null) {
          return null;
        }
        return cqlWasmJs.stringAsSource(xml);
      },
    );

    const librarySourceProviderJs = cqlToElmJs.createLibrarySourceProvider(
      (id: string, system: Nullable<string>, version: Nullable<string>) => {
        const cql = getLibraryCql(
          id,
          system,
          version,
          args.cql,
          args.librarySource,
          args.baseUrl,
          args.mountedDir,
          sync,
          (message) => {
            onOutput({
              type: "log",
              log: message,
            });
          },
          () => {
            compileCql(args, onOutput);
          },
        );
        if (cql === null) {
          return null;
        }
        return cqlToElmJs.stringAsSource(cql);
      },
    );
    const librarySourceProviderWasmJs = cqlWasmJs.createLibrarySourceProvider(
      (id, system, version) => {
        const cql = getLibraryCql(
          id,
          system,
          version,
          args.cql,
          args.librarySource,
          args.baseUrl,
          args.mountedDir,
          sync,
          (message) => {
            onOutput({
              type: "log",
              log: message,
            });
          },
          () => {
            compileCql(args, onOutput);
          },
        );
        if (cql === null) {
          return null;
        }
        return cqlWasmJs.stringAsSource(cql);
      },
    );

    // @ts-expect-error TypeScript error
    modelManagerJs.modelInfoLoader.clearModelInfoProviders();
    // @ts-expect-error TypeScript error
    modelManagerJs.modelInfoLoader.registerModelInfoProvider(
      modelInfoProviderJs,
    );
    // @ts-expect-error TypeScript error
    libraryManagerJs.librarySourceLoader.clearProviders();
    // @ts-expect-error TypeScript error
    libraryManagerJs.librarySourceLoader.registerProvider(
      librarySourceProviderJs,
    );

    // @ts-expect-error TypeScript error
    cqlWasmJs.modelManagerClearModelInfoProviders(modelManagerWasmJs);
    // @ts-expect-error TypeScript error
    cqlWasmJs.modelManagerRegisterModelInfoProvider(
      modelManagerWasmJs,
      modelInfoProviderWasmJs,
    );
    // @ts-expect-error TypeScript error
    cqlWasmJs.libraryManagerClearLibrarySourceProviders(libraryManagerWasmJs);
    // @ts-expect-error TypeScript error
    cqlWasmJs.libraryManagerRegisterLibrarySourceProvider(
      libraryManagerWasmJs,
      librarySourceProviderWasmJs,
    );

    for (const compilerOption of compilerOptions) {
      if (args.compilerOptions.includes(compilerOption.value)) {
        libraryManagerJs.cqlCompilerOptions.options.asJsSetView().add(
          // @ts-expect-error TypeScript error
          cqlToElmJs.CqlCompilerOptions.Options.valueOf(compilerOption.value),
        );
        // @ts-expect-error TypeScript error
        cqlWasmJs.libraryManagerAddCompilerOption(
          libraryManagerWasmJs,
          compilerOption.value,
        );
      } else {
        libraryManagerJs.cqlCompilerOptions.options.asJsSetView().delete(
          // @ts-expect-error TypeScript error
          cqlToElmJs.CqlCompilerOptions.Options.valueOf(compilerOption.value),
        );
        // @ts-expect-error TypeScript error
        cqlWasmJs.libraryManagerRemoveCompilerOption(
          libraryManagerWasmJs,
          compilerOption.value,
        );
      }
    }

    libraryManagerJs.cqlCompilerOptions.signatureLevel =
      // @ts-expect-error TypeScript error
      cqlToElmJs.LibraryBuilder.SignatureLevel.valueOf(args.signatureLevel);
    // @ts-expect-error TypeScript error
    cqlWasmJs.libraryManagerSetSignatureLevel(
      libraryManagerWasmJs,
      args.signatureLevel,
    );

    const output = ((): TOutput => {
      if (args.useWasm) {
        try {
          // @ts-expect-error TypeScript error
          const cqlTranslator = cqlWasmJs.cqlTranslatorFromText(
            args.cql,
            libraryManagerWasmJs,
          );
          return args.outputContentType === "json"
            ? {
                type: "elm",
                contentType: "json",
                // @ts-expect-error TypeScript error
                elm: cqlWasmJs.cqlTranslatorToJson(cqlTranslator),
              }
            : {
                type: "elm",
                contentType: "xml",
                // @ts-expect-error TypeScript error
                elm: cqlWasmJs.cqlTranslatorToXml(cqlTranslator),
              };
          // eslint-disable-next-line @typescript-eslint/no-unused-vars
        } catch (e) {
          // If you try to use a JavaScript try-catch expression to catch Kotlin/Wasm exceptions, it looks like a generic WebAssembly.Exception without directly accessible messages and data. (https://kotlinlang.org/docs/wasm-js-interop.html#exception-handling)
          return {
            type: "log",
            log: "INFO Busy...",
          };
        }
      }

      try {
        const cqlTranslator = cqlToElmJs.CqlTranslator.fromText(
          args.cql,
          libraryManagerJs,
        );
        return args.outputContentType === "json"
          ? {
              type: "elm",
              contentType: "json",
              elm: cqlTranslator.toJson(),
            }
          : {
              type: "elm",
              contentType: "xml",
              elm: cqlTranslator.toXml(),
            };
      } catch (e) {
        return {
          type: "log",
          log: String(e),
        };
      }
    })();

    onOutput(output);
  };

  return {
    compileCql,
  };
}
