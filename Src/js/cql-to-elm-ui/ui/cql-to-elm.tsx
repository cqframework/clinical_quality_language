import { TCqlToElmArgs, TOutput } from "@/shared";
import { TSetState, TState } from "@/state";
import { Fragment, useEffect, useRef } from "react";
import { createStatefulCompiler } from "@/cql/cql-to-elm";
import { json } from "@codemirror/lang-json";
import { xml } from "@codemirror/lang-xml";
import { Editor } from "@/ui/editor/editor";
import { Label } from "@/ui/label";
import { Caption } from "@/ui/caption";
import { Spinner } from "@/ui/spinner";

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
    const worker = new Worker(
      new URL("@/cql/cql-to-elm-worker", import.meta.url),
    );

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
    state.common.librarySource,
    state.common.mountedDir,
    state.common.baseUrl,
    state.common.compilerOptions,
    state.common.signatureLevel,
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
        librarySource: state.common.librarySource,
        baseUrl: state.common.baseUrl,
        compilerOptions: state.common.compilerOptions,
        signatureLevel: state.common.signatureLevel,
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
    state.common.librarySource,
    state.common.baseUrl,
    state.common.compilerOptions,
    state.common.signatureLevel,
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
        value={(() => {
          if (state.tabs["cql-to-elm"].elm.contentType === "json") {
            if (state.tabs["cql-to-elm"].prettyPrintJson) {
              return prettyPrintJsonIfPossible(
                state.tabs["cql-to-elm"].elm.content,
              );
            }
          }
          return state.tabs["cql-to-elm"].elm.content;
        })()}
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
          right: 5,
          opacity: state.tabs["cql-to-elm"].isBusy ? 1 : 0,
          transition: "opacity 0.2s",
          pointerEvents: "none",
        }}
      >
        <Spinner />
      </div>
    </div>
  );
}

function prettyPrintJsonIfPossible(json: string): string {
  try {
    return JSON.stringify(JSON.parse(json), null, 2);
  } catch (e) {
    console.error(e);
  }
  return json;
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

      {state.tabs["cql-to-elm"].cqlToElmArgs.outputContentType === "json" && (
        <label style={{ display: "flex", gap: 5 }}>
          <input
            type={"checkbox"}
            style={{
              margin: 0,
            }}
            checked={state.tabs["cql-to-elm"].prettyPrintJson}
            onChange={(event) => {
              const nextChecked = event.target.checked;
              setState((prevState) => ({
                ...prevState,
                tabs: {
                  ...prevState.tabs,
                  "cql-to-elm": {
                    ...prevState.tabs["cql-to-elm"],
                    prettyPrintJson: nextChecked,
                  },
                },
              }));
            }}
          />
          <div>Pretty print JSON output</div>
        </label>
      )}

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
