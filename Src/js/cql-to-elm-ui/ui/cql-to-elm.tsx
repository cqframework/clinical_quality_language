import {
  compilerOptions,
  signatureLevels,
  TCqlToElmArgs,
  TLibrarySource,
  TOutput,
  TSetState,
  TState,
} from "@/shared";
import { Fragment, useEffect, useRef } from "react";
import { createStatefulCompiler } from "@/compiler/compiler";
import { json } from "@codemirror/lang-json";
import { xml } from "@codemirror/lang-xml";
import { Editor } from "@/ui/editor/editor";
import { Label } from "@/ui/label";
import { Caption } from "@/ui/caption";
import { Check } from "@/ui/icons/check";

export function CqlToElmResult({
  state,
  setState,
}: {
  state: TState;
  setState: TSetState;
}) {
  const currentRunIdRef = useRef(0);

  const statefulCompilerRef = useRef<ReturnType<
    typeof createStatefulCompiler
  > | null>(null);
  const workerPromiseRef = useRef<Promise<Worker> | null>(null);

  const createWorkerPromise = async () => {
    const worker = new Worker(new URL("@/compiler/worker", import.meta.url));

    await new Promise<void>((resolve) => {
      worker.onmessage = (event) => {
        if (event.data.type === "ready") {
          resolve();
        }
      };
    });

    worker.onmessage = (event) => {
      const { type, data } = event.data;

      if (type === "output") {
        if (data.runId === currentRunIdRef.current) {
          const output = data.output as TOutput;

          setState((prevState) => {
            if (output.type === "log") {
              return {
                ...prevState,
                common: {
                  ...prevState.common,
                  log: [...prevState.common.log, output.log],
                },
              };
            }
            return {
              ...prevState,
              common: {
                ...prevState.common,
                log: [
                  ...prevState.common.log,
                  `INFO Compilation finished in ${(Date.now() - data.startTime) / 1000}s.`,
                ],
              },
              tabs: {
                ...prevState.tabs,
                "cql-to-elm": {
                  ...prevState.tabs["cql-to-elm"],
                  elm: {
                    contentType: output.contentType,
                    content: output.elm,
                  },
                  isBusy: false,
                },
              },
            };
          });
        }
      }
    };

    return worker;
  };

  useEffect(() => {
    // Reset all caches by recreating stateful compiler and worker.
    // This also runs on initial load.

    statefulCompilerRef.current = createStatefulCompiler(false);

    const workerPromise = createWorkerPromise();
    if (workerPromiseRef.current) {
      workerPromiseRef.current.then((worker) => worker.terminate());
    }
    workerPromiseRef.current = workerPromise;

    setState((prevState) => ({
      ...prevState,
      common: {
        ...prevState.common,
        log: [...prevState.common.log, "INFO Caches reset."],
      },
    }));
  }, [
    state.tabs["cql-to-elm"].cqlToElmArgs.librarySource,
    state.common.mountedDir,
    state.tabs["cql-to-elm"].cqlToElmArgs.baseUrl,
    state.tabs["cql-to-elm"].cqlToElmArgs.compilerOptions,
    state.tabs["cql-to-elm"].cqlToElmArgs.signatureLevel,
  ]);

  useEffect(() => {
    (async () => {
      setState((prevState) => ({
        ...prevState,
        common: {
          ...prevState.common,
          log: [...prevState.common.log, "INFO Starting compilation..."],
        },
        tabs: {
          ...prevState.tabs,
          "cql-to-elm": {
            ...prevState.tabs["cql-to-elm"],
            isBusy: true,
          },
        },
      }));

      const runId = ++currentRunIdRef.current;
      const startTime = Date.now();

      const cqlToElmArgs: TCqlToElmArgs = {
        cql: state.common.cql,
        mountedDir: state.common.mountedDir,
        ...state.tabs["cql-to-elm"].cqlToElmArgs,
      };

      if (state.tabs["cql-to-elm"].cqlToElmArgs.useWorker) {
        const worker = await workerPromiseRef.current!;

        worker.postMessage({
          type: "compileCql",
          data: {
            args: cqlToElmArgs,
            runId: runId,
            startTime: startTime,
          },
        });
      } else {
        const { compileCql } = statefulCompilerRef.current!;

        compileCql(cqlToElmArgs, (output) => {
          if (runId === currentRunIdRef.current) {
            setState((prevState) => {
              if (output.type === "log") {
                return {
                  ...prevState,
                  common: {
                    ...prevState.common,
                    log: [...prevState.common.log, output.log],
                  },
                };
              }
              return {
                ...prevState,
                common: {
                  ...prevState.common,
                  log: [
                    ...prevState.common.log,
                    `INFO Compilation finished in ${
                      (Date.now() - startTime) / 1000
                    }s.`,
                  ],
                },
                tabs: {
                  ...prevState.tabs,
                  "cql-to-elm": {
                    ...prevState.tabs["cql-to-elm"],
                    elm: {
                      contentType: output.contentType,
                      content: output.elm,
                    },
                    isBusy: false,
                  },
                },
              };
            });
          }
        });
      }
    })();
  }, [
    state.common.cql,
    state.common.mountedDir,
    state.tabs["cql-to-elm"].cqlToElmArgs,
  ]);

  return (
    <div
      style={{
        display: "grid",
        width: "100%",
        height: "100%",
        minHeight: 0,
        position: "relative",
      }}
    >
      <Editor
        value={state.tabs["cql-to-elm"].elm.content}
        onChange={() => {}}
        editable={false}
        lineNumbers={true}
        extensions={[
          state.tabs["cql-to-elm"].elm.contentType === "json" ? json() : xml(),
        ]}
      />
      <div
        style={{
          position: "absolute",
          top: 5,
          right: 15,
          opacity: state.tabs["cql-to-elm"].isBusy ? 0 : 1,
          transition: "opacity 0.2s",
        }}
      >
        <Check style={{ width: 20 }} />
      </div>
    </div>
  );
}

export function CqlToElmSettings({
  state,
  setState,
}: {
  state: TState;
  setState: TSetState;
}) {
  return (
    <Fragment>
      <div>
        <Label>Library sources</Label>
        <div
          style={{
            display: "grid",
            gap: 10,
          }}
        >
          {[
            {
              value: "local" as TLibrarySource,
              label: "Mounted directory",
              content: (
                <Fragment>
                  <Caption>
                    Included libraries are loaded from files in the local
                    directory (library ID + .cql).
                  </Caption>
                </Fragment>
              ),
            },
            {
              value: "remote" as TLibrarySource,
              label: "Remote URL",
              content: (
                <Fragment>
                  <input
                    placeholder={
                      "E.g. https://raw.githubusercontent.com/cqframework/cqf-exercises/refs/heads/master/input/cql/"
                    }
                    value={state.tabs["cql-to-elm"].cqlToElmArgs.baseUrl}
                    onChange={(event) => {
                      const nextBaseUrl = event.target.value;
                      setState((prevState) => ({
                        ...prevState,
                        tabs: {
                          ...prevState.tabs,
                          "cql-to-elm": {
                            ...prevState.tabs["cql-to-elm"],
                            cqlToElmArgs: {
                              ...prevState.tabs["cql-to-elm"].cqlToElmArgs,
                              baseUrl: nextBaseUrl,
                            },
                          },
                        },
                      }));
                    }}
                    style={{
                      width: "100%",
                      padding: "6px 12px",
                      border: "var(--border)",
                      borderRadius: "var(--border-radius)",
                      fontFamily: "inherit",
                    }}
                  />
                  <Caption>
                    Included libraries are fetched from this base URL + library
                    ID + .cql.
                  </Caption>
                </Fragment>
              ),
            },
          ].map((librarySourceOption, librarySourceOptionIndex) => (
            <div key={librarySourceOptionIndex}>
              <label style={{ display: "flex", gap: 5, margin: "0 0 5px 0" }}>
                <input
                  type={"radio"}
                  style={{
                    margin: 0,
                  }}
                  checked={
                    state.tabs["cql-to-elm"].cqlToElmArgs.librarySource ===
                    librarySourceOption.value
                  }
                  onChange={() => {
                    setState((prevState) => ({
                      ...prevState,
                      tabs: {
                        ...prevState.tabs,
                        "cql-to-elm": {
                          ...prevState.tabs["cql-to-elm"],
                          cqlToElmArgs: {
                            ...prevState.tabs["cql-to-elm"].cqlToElmArgs,
                            librarySource: librarySourceOption.value,
                          },
                        },
                      },
                    }));
                  }}
                />
                {librarySourceOption.label}
              </label>
              {librarySourceOption.content}
            </div>
          ))}
        </div>
      </div>

      <div>
        <Label>Compiler options</Label>

        <div
          style={{
            display: "grid",
            gap: 3,
            margin: "0 0 12px 0",
          }}
        >
          {compilerOptions.map((compilerOption, compilerOptionIndex) => (
            <label
              key={compilerOptionIndex}
              style={{ display: "flex", gap: 5 }}
            >
              <input
                type={"checkbox"}
                style={{
                  margin: 0,
                }}
                checked={state.tabs[
                  "cql-to-elm"
                ].cqlToElmArgs.compilerOptions.includes(compilerOption.value)}
                onChange={(event) => {
                  const nextChecked = event.target.checked;
                  setState((prevState) => ({
                    ...prevState,
                    tabs: {
                      ...prevState.tabs,
                      "cql-to-elm": {
                        ...prevState.tabs["cql-to-elm"],
                        cqlToElmArgs: {
                          ...prevState.tabs["cql-to-elm"].cqlToElmArgs,
                          compilerOptions: nextChecked
                            ? [
                                ...prevState.tabs["cql-to-elm"].cqlToElmArgs
                                  .compilerOptions,
                                compilerOption.value,
                              ]
                            : prevState.tabs[
                                "cql-to-elm"
                              ].cqlToElmArgs.compilerOptions.filter(
                                (_) => _ !== compilerOption.value,
                              ),
                        },
                      },
                    },
                  }));
                }}
              />
              <div>{compilerOption.label}</div>
            </label>
          ))}
        </div>

        <div>
          <div style={{ margin: "0 0 5px 0" }}>Signature level</div>
          <select
            value={state.tabs["cql-to-elm"].cqlToElmArgs.signatureLevel}
            onChange={(event) => {
              const nextSignatureLevel = event.target.value;
              setState((prevState) => ({
                ...prevState,
                tabs: {
                  ...prevState.tabs,
                  "cql-to-elm": {
                    ...prevState.tabs["cql-to-elm"],
                    cqlToElmArgs: {
                      ...prevState.tabs["cql-to-elm"].cqlToElmArgs,
                      signatureLevel: nextSignatureLevel,
                    },
                  },
                },
              }));
            }}
            style={{
              width: "100%",
              padding: "6px 12px 6px 8px",
              border: "var(--border)",
              borderRadius: "var(--border-radius)",
              fontFamily: "inherit",
            }}
          >
            {signatureLevels.map((signatureLevel, signatureLevelIndex) => (
              <option key={signatureLevelIndex} value={signatureLevel}>
                {signatureLevel}
              </option>
            ))}
          </select>
        </div>
      </div>

      <div>
        <Label>Output content type</Label>
        <div
          style={{
            display: "flex",
            gap: 10,
          }}
        >
          {(["json", "xml"] as const).map(
            (outputContentType, outputContentTypeIndex) => (
              <label
                key={outputContentTypeIndex}
                style={{ display: "flex", gap: 5 }}
              >
                <input
                  type={"radio"}
                  style={{
                    margin: 0,
                  }}
                  checked={
                    state.tabs["cql-to-elm"].cqlToElmArgs.outputContentType ===
                    outputContentType
                  }
                  onChange={() => {
                    setState((prevState) => ({
                      ...prevState,
                      tabs: {
                        ...prevState.tabs,
                        "cql-to-elm": {
                          ...prevState.tabs["cql-to-elm"],
                          cqlToElmArgs: {
                            ...prevState.tabs["cql-to-elm"].cqlToElmArgs,
                            outputContentType: outputContentType,
                          },
                        },
                      },
                    }));
                  }}
                />
                {outputContentType.toUpperCase()}
              </label>
            ),
          )}
        </div>
      </div>
      <div>
        <Label>Compiler run configuration</Label>
        <div style={{ margin: "0 0 10px 0" }}>
          <label style={{ display: "flex", gap: 5 }}>
            <input
              type={"checkbox"}
              style={{
                margin: 0,
              }}
              checked={state.tabs["cql-to-elm"].cqlToElmArgs.useWorker}
              onChange={(event) => {
                const nextUseWorker = event.target.checked;
                setState((prevState) => ({
                  ...prevState,
                  tabs: {
                    ...prevState.tabs,
                    "cql-to-elm": {
                      ...prevState.tabs["cql-to-elm"],
                      cqlToElmArgs: {
                        ...prevState.tabs["cql-to-elm"].cqlToElmArgs,
                        useWorker: nextUseWorker,
                      },
                    },
                  },
                }));
              }}
            />
            Use web worker
          </label>
          <Caption>
            Run compilation in a persistent web worker. This avoids blocking the
            UI thread and allows for synchronous HTTP requests, but requires the
            XML parser and serializer polyfills.
          </Caption>
        </div>
        <div>
          <label style={{ display: "flex", gap: 5 }}>
            <input
              type={"checkbox"}
              style={{
                margin: 0,
              }}
              checked={state.tabs["cql-to-elm"].cqlToElmArgs.useWasm}
              onChange={(event) => {
                const nextUseWasm = event.target.checked;
                setState((prevState) => ({
                  ...prevState,
                  tabs: {
                    ...prevState.tabs,
                    "cql-to-elm": {
                      ...prevState.tabs["cql-to-elm"],
                      cqlToElmArgs: {
                        ...prevState.tabs["cql-to-elm"].cqlToElmArgs,
                        useWasm: nextUseWasm,
                      },
                    },
                  },
                }));
              }}
            />
            Use WASM
          </label>
          <Caption>Use the WASM build of the CQL compiler.</Caption>
        </div>
      </div>
    </Fragment>
  );
}
