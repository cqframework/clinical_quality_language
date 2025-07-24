// @ts-expect-error No type definitions available for @lhncbc/ucum-lhc
import * as ucum from "@lhncbc/ucum-lhc";
import * as cqlToElmJs from "../../../java/build/js/packages/cql-all-cql-to-elm";
import * as cqlToElmWasmJs from "../../../java/build/js/packages/cql-all-cql-to-elm-wasm-js";
import { TCompileCqlArgs, TOutput } from "@/app/cql-compiler-playground-shared";
import { supportedModels } from "@/app/supported-models";

export function createStatefulCompiler() {
  const ucumUtils = ucum.UcumLhcUtils.getInstance();
  const validateUnit = (unit: string) => {
    const result = ucumUtils.validateUnitString(unit);

    if (result.status === "valid") {
      return null;
    } else {
      return result.msg[0];
    }
  };

  const fetchedModels: {
    id: string;
    system: string | null;
    version: string | null;
    xml: string | null;
  }[] = [];

  const fetchedLibraries: {
    id: string;
    system: string | null;
    version: string | null;
    cql: string | null;
  }[] = [];

  const modelCacheJs = cqlToElmJs.LibraryManager.createModelCache();
  const libraryCacheJs = cqlToElmJs.LibraryManager.createLibraryCache();

  // @ts-expect-error TypeScript error
  const modelCacheWasmJs = cqlToElmWasmJs.createModelCache();
  // @ts-expect-error TypeScript error
  const libraryCacheWasmJs = cqlToElmWasmJs.createLibraryCache();

  const compileCql = (
    args: TCompileCqlArgs,
    onOutput: (output: TOutput) => void,
  ) => {
    const getModelXml = (
      id: string,
      system: string | null,
      version: string | null,
    ) => {
      const fetchedModel = fetchedModels.find(
        (_) => _.id === id && _.system === system && _.version === version,
      );
      if (fetchedModel) {
        return fetchedModel.xml;
      }
      const supportedModel = supportedModels.find(
        (_) => _.id === id && _.system === system && _.version === version,
      );
      if (supportedModel) {
        (async () => {
          const response = await fetch(supportedModel.url);
          const xml = response.ok ? await response.text() : null;
          fetchedModels.push({
            id: supportedModel.id,
            system: supportedModel.system,
            version: supportedModel.version,
            xml,
          });
          // Compile again with the model fetched
          compileCql(args, onOutput);
        })();
        throw `Busy loading model: id=${id} system=${system} version=${version} from ${supportedModel.url}`;
      }
      return null;
    };

    const getLibraryCql = (
      id: string,
      system: string | null,
      version: string | null,
    ) => {
      const fetchedLibrary = fetchedLibraries.find(
        (_) => _.id === id && _.system === system && _.version === version,
      );
      if (fetchedLibrary) {
        return fetchedLibrary.cql;
      }
      const url = `${args.baseUrl}${id}.cql`;
      (async () => {
        const response = await fetch(url);
        const cql = response.ok ? await response.text() : null;
        fetchedLibraries.push({
          id,
          system,
          version,
          cql,
        });
        // Compile again with the library fetched
        compileCql(args, onOutput);
      })();
      throw `Busy loading library: id=${id} system=${system} version=${version} from ${url}`;
    };

    const libraryManagerJs = new cqlToElmJs.LibraryManager(
      getModelXml,
      getLibraryCql,
      validateUnit,
      modelCacheJs,
      libraryCacheJs,
    );

    // @ts-expect-error TypeScript error
    const libraryManagerWasmJs = cqlToElmWasmJs.createLibraryManager(
      getModelXml,
      getLibraryCql,
      validateUnit,
      modelCacheWasmJs,
      libraryCacheWasmJs,
    );

    if (args.enableAnnotations) {
      // @ts-expect-error TypeScript error
      libraryManagerJs.addCompilerOption("EnableAnnotations");
      // @ts-expect-error TypeScript error
      cqlToElmWasmJs.libraryManagerAddCompilerOption(
        libraryManagerWasmJs,
        "EnableAnnotations",
      );
    } else {
      // @ts-expect-error TypeScript error
      libraryManagerJs.removeCompilerOption("EnableAnnotations");
      // @ts-expect-error TypeScript error
      cqlToElmWasmJs.libraryManagerRemoveCompilerOption(
        libraryManagerWasmJs,
        "EnableAnnotations",
      );
    }

    if (args.enableLocators) {
      // @ts-expect-error TypeScript error
      libraryManagerJs.addCompilerOption("EnableLocators");
      // @ts-expect-error TypeScript error
      cqlToElmWasmJs.libraryManagerAddCompilerOption(
        libraryManagerWasmJs,
        "EnableLocators",
      );
    } else {
      // @ts-expect-error TypeScript error
      libraryManagerJs.removeCompilerOption("EnableLocators");
      // @ts-expect-error TypeScript error
      cqlToElmWasmJs.libraryManagerRemoveCompilerOption(
        libraryManagerWasmJs,
        "EnableLocators",
      );
    }

    const output = ((): TOutput => {
      if (args.useWasm) {
        try {
          // @ts-expect-error TypeScript error
          const cqlTranslator = cqlToElmWasmJs.createCqlTranslator(
            args.cql,
            libraryManagerWasmJs,
          );
          return args.outputContentType === "json"
            ? {
                type: "elm",
                contentType: "json",
                // @ts-expect-error TypeScript error
                elm: cqlToElmWasmJs.cqlTranslatorToJson(cqlTranslator),
              }
            : {
                type: "elm",
                contentType: "xml",
                // @ts-expect-error TypeScript error
                elm: cqlToElmWasmJs.cqlTranslatorToXml(cqlTranslator),
              };
          // eslint-disable-next-line @typescript-eslint/no-unused-vars
        } catch (e) {
          // If you try to use a JavaScript try-catch expression to catch Kotlin/Wasm exceptions, it looks like a generic WebAssembly.Exception without directly accessible messages and data. (https://kotlinlang.org/docs/wasm-js-interop.html#exception-handling)
          return {
            type: "log",
            log: "Busy...",
          };
        }
      }

      try {
        const cqlTranslator = new cqlToElmJs.CqlTranslator(
          args.cql,
          libraryManagerJs,
        );
        return args.outputContentType === "json"
          ? {
              type: "elm",
              contentType: "json",
              // @ts-expect-error TypeScript error
              elm: cqlTranslator.toJson(),
            }
          : {
              type: "elm",
              contentType: "xml",
              // @ts-expect-error TypeScript error
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
