// @ts-expect-error No type definitions available for @lhncbc/ucum-lhc
import * as ucum from "@lhncbc/ucum-lhc";
import * as cqlToElmJs from "../../../java/build/js/packages/cql-all-cql-to-elm";
import * as cqlToElmWasmJs from "../../../java/build/js/packages/cql-all-cql-to-elm-wasm-js";
import { compilerOptions, TCompileCqlArgs, TOutput } from "@/shared";
import { supportedModels } from "@/compiler/supported-models";
import { fetchSync, readFile } from "@/compiler/utils";

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
        if (sync) {
          onOutput({
            type: "log",
            log: `INFO Fetching model with id=${id}, system=${system}, version=${version} synchronously from url=${supportedModel.url}...`,
          });
          const xml = fetchSync(supportedModel.url);
          if (xml === null) {
            onOutput({
              type: "log",
              log: `WARN Couldn't fetch model from url=${supportedModel.url}.`,
            });
          } else {
            onOutput({
              type: "log",
              log: `INFO Fetched model with id=${id}, system=${system}, version=${version} successfully.`,
            });
          }
          fetchedModels.push({
            id: supportedModel.id,
            system: supportedModel.system,
            version: supportedModel.version,
            xml,
          });
          return xml;
        } else {
          onOutput({
            type: "log",
            log: `INFO Fetching model with id=${id}, system=${system}, version=${version} asynchronously from url=${supportedModel.url}...`,
          });
          (async () => {
            const response = await fetch(supportedModel.url);
            const xml = response.ok ? await response.text() : null;
            if (xml === null) {
              onOutput({
                type: "log",
                log: `WARN Couldn't fetch model from url=${supportedModel.url}.`,
              });
            } else {
              onOutput({
                type: "log",
                log: `INFO Fetched model with id=${id}, system=${system}, version=${version} successfully.`,
              });
            }
            fetchedModels.push({
              id: supportedModel.id,
              system: supportedModel.system,
              version: supportedModel.version,
              xml,
            });
            onOutput({
              type: "log",
              log: "INFO Rerunning compilation...",
            });
            compileCql(args, onOutput);
          })();
          throw "INFO Model is being fetched asynchronously. Will rerun when fetch completes.";
        }
      }
      onOutput({
        type: "log",
        log: `WARN Model with id=${id}, system=${system}, version=${version} is not in the list of supported models.`,
      });
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
      if (args.librarySource === "local") {
        if (args.mountedDir) {
          const dirHandle = args.mountedDir.handle;
          const fileName = `${id}.cql`;
          const file = args.mountedDir.files.find(
            (_) => _.handle.name === fileName,
          );
          if (file) {
            onOutput({
              type: "log",
              log: `INFO Reading library with id=${id}, system=${system}, version=${version} asynchronously from local file=${dirHandle.name}/${fileName}...`,
            });
            (async () => {
              const cql = await readFile(file.handle);
              if (cql === null) {
                onOutput({
                  type: "log",
                  log: `WARN Couldn't read library from local file=${dirHandle.name}/${fileName}.`,
                });
              } else {
                onOutput({
                  type: "log",
                  log: `INFO Read library with id=${id}, system=${system}, version=${version} successfully.`,
                });
              }
              fetchedLibraries.push({
                id,
                system,
                version,
                cql,
              });
              onOutput({
                type: "log",
                log: "INFO Rerunning compilation...",
              });
              compileCql(args, onOutput);
            })();
            throw "INFO Library is being read asynchronously from local file system. Will rerun when reading completes.";
          }

          onOutput({
            type: "log",
            log: `WARN Library with id=${id}, system=${system}, version=${version} not found in mounted directory=${dirHandle.name}.`,
          });
          return null;
        }
        onOutput({
          type: "log",
          log: `WARN Library with id=${id}, system=${system}, version=${version} cannot be read from local file system because no directory is mounted.`,
        });
        return null;
      }

      const url = `${args.baseUrl}${id}.cql`;

      if (sync) {
        onOutput({
          type: "log",
          log: `INFO Fetching library with id=${id}, system=${system}, version=${version} synchronously from url=${url}...`,
        });
        const cql = fetchSync(url);
        if (cql === null) {
          onOutput({
            type: "log",
            log: `WARN Couldn't fetch library from url=${url}.`,
          });
        } else {
          onOutput({
            type: "log",
            log: `INFO Fetched library with id=${id}, system=${system}, version=${version} successfully.`,
          });
        }
        fetchedLibraries.push({
          id,
          system,
          version,
          cql,
        });
        return cql;
      } else {
        onOutput({
          type: "log",
          log: `INFO Fetching library with id=${id}, system=${system}, version=${version} asynchronously from url=${url}...`,
        });
        (async () => {
          const response = await fetch(url);
          const cql = response.ok ? await response.text() : null;
          if (cql === null) {
            onOutput({
              type: "log",
              log: `WARN Couldn't fetch library from url=${url}.`,
            });
          } else {
            onOutput({
              type: "log",
              log: `INFO Fetched library with id=${id}, system=${system}, version=${version} successfully.`,
            });
          }
          fetchedLibraries.push({
            id,
            system,
            version,
            cql,
          });
          onOutput({
            type: "log",
            log: "INFO Rerunning compilation...",
          });
          compileCql(args, onOutput);
        })();
        throw "INFO Library is being fetched asynchronously. Will rerun when fetch completes.";
      }
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

    for (const compilerOption of compilerOptions) {
      if (args.compilerOptions.includes(compilerOption.value)) {
        // @ts-expect-error TypeScript error
        libraryManagerJs.addCompilerOption(compilerOption.value);
        // @ts-expect-error TypeScript error
        cqlToElmWasmJs.libraryManagerAddCompilerOption(
          libraryManagerWasmJs,
          compilerOption.value,
        );
      } else {
        // @ts-expect-error TypeScript error
        libraryManagerJs.removeCompilerOption(compilerOption.value);
        // @ts-expect-error TypeScript error
        cqlToElmWasmJs.libraryManagerRemoveCompilerOption(
          libraryManagerWasmJs,
          compilerOption.value,
        );
      }
    }

    // @ts-expect-error TypeScript error
    libraryManagerJs.setSignatureLevel(args.signatureLevel);
    // @ts-expect-error TypeScript error
    cqlToElmWasmJs.libraryManagerSetSignatureLevel(
      libraryManagerWasmJs,
      args.signatureLevel,
    );

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
                elm: prettyPrintJsonIfPossible(
                  // @ts-expect-error TypeScript error
                  cqlToElmWasmJs.cqlTranslatorToJson(cqlTranslator),
                ),
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
            log: "INFO Busy...",
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
              elm: prettyPrintJsonIfPossible(cqlTranslator.toJson()),
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

function prettyPrintJsonIfPossible(json: string): string {
  try {
    return JSON.stringify(JSON.parse(json), null, 2);
  } catch (e) {
    console.error(e);
  }
  return json;
}
