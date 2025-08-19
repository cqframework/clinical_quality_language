"use client";

import { useState, useRef, useEffect, useLayoutEffect, Fragment } from "react";
import { json } from "@codemirror/lang-json";
import { xml } from "@codemirror/lang-xml";
import {
  compilerOptions,
  signatureLevels,
  TCompileCqlArgs,
  TLibrarySource,
  TOutput,
} from "@/shared";
import { createStatefulCompiler } from "@/compiler/compiler";
import { cqlLanguage } from "@/ui/editor/cql-language";
import { logLanguage } from "@/ui/editor/log-language";
import {
  customHighlightsEffectType,
  Editor,
  getCursorLineAndCol,
  getRangeFromLinesAndCols,
} from "@/ui/editor/editor";
import { ReactCodeMirrorRef } from "@uiw/react-codemirror";
import { EditorView } from "@codemirror/view";
import { findRangesForCqlPos } from "@/compiler/elm";
import { Label } from "@/ui/label";
import { Caption } from "@/ui/caption";
import { buttonStyle } from "@/ui/button";
import { supportedModels } from "@/compiler/supported-models";
import { readFile } from "@/compiler/utils";

const initialCompileCqlArgs: TCompileCqlArgs = {
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
  mountedDir: null,
  useWorker: true,
};

export function CqlCompilerPlayground() {
  const [state, setState] = useState({
    compileCqlArgs: initialCompileCqlArgs,

    isBusy: true,

    elm: {
      contentType: initialCompileCqlArgs.outputContentType,
      content: "",
    },

    log: [] as string[],
  });

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
                log: [...prevState.log, output.log],
              };
            }
            return {
              ...prevState,
              isBusy: false,
              elm: {
                contentType: output.contentType,
                content: output.elm,
              },
              log: [
                ...prevState.log,
                `INFO Compilation finished in ${(Date.now() - data.startTime) / 1000}s.`,
              ],
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
      isBusy: true,
      log: [...prevState.log, "INFO Caches reset."],
    }));
  }, [
    state.compileCqlArgs.librarySource,
    state.compileCqlArgs.mountedDir,
    state.compileCqlArgs.baseUrl,
    state.compileCqlArgs.compilerOptions,
    state.compileCqlArgs.signatureLevel,
  ]);

  useEffect(() => {
    (async () => {
      setState((prevState) => ({
        ...prevState,
        isBusy: true,
        log: [...prevState.log, "INFO Starting compilation..."],
      }));

      const runId = ++currentRunIdRef.current;
      const startTime = Date.now();

      if (state.compileCqlArgs.useWorker) {
        const worker = await workerPromiseRef.current!;

        worker.postMessage({
          type: "compileCql",
          data: {
            args: state.compileCqlArgs,
            runId: runId,
            startTime: startTime,
          },
        });
      } else {
        const { compileCql } = statefulCompilerRef.current!;

        compileCql(state.compileCqlArgs, (output) => {
          if (runId === currentRunIdRef.current) {
            setState((prevState) => {
              if (output.type === "log") {
                return {
                  ...prevState,
                  log: [...prevState.log, output.log],
                };
              }
              return {
                ...prevState,
                isBusy: false,
                elm: {
                  contentType: output.contentType,
                  content: output.elm,
                },
                log: [
                  ...prevState.log,
                  `INFO Compilation finished in ${
                    (Date.now() - startTime) / 1000
                  }s.`,
                ],
              };
            });
          }
        });
      }
    })();
  }, [state.compileCqlArgs]);

  const leftEditorRef = useRef<ReactCodeMirrorRef>(null);
  const rightEditorRef = useRef<ReactCodeMirrorRef>(null);
  const logEditorRef = useRef<ReactCodeMirrorRef>(null);

  useEffect(() => {
    try {
      const { line, col } = getCursorLineAndCol(
        leftEditorRef.current!.view!.state,
      );
      const ranges = findRangesForCqlPos(state.elm, line, col);
      leftEditorRef.current!.view!.dispatch({
        effects: [
          customHighlightsEffectType.of(
            ranges
              .slice(0, 1)
              .map((_) =>
                getRangeFromLinesAndCols(
                  leftEditorRef.current!.view!.state,
                  _.cql,
                ),
              ),
          ),
        ],
      });
      rightEditorRef.current!.view!.dispatch({
        effects: [
          customHighlightsEffectType.of(ranges.map((_) => _.elm)),
          ...(ranges.length
            ? [
                EditorView.scrollIntoView(ranges[ranges.length - 1].elm.start, {
                  y: "nearest",
                }),
              ]
            : []),
        ],
      });

      // eslint-disable-next-line @typescript-eslint/no-unused-vars
    } catch (e) {}
  }, [state.elm]);

  useLayoutEffect(() => {
    try {
      logEditorRef.current!.view!.dispatch({
        effects: EditorView.scrollIntoView(
          logEditorRef.current!.view!.state.doc.length,
          { y: "end" },
        ),
      });
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
    } catch (e) {}
  }, [state.log]);

  return (
    <div
      style={{
        position: "absolute",
        inset: 0,
        display: "grid",
        gridTemplateColumns: "1fr",
        gridTemplateRows: "auto 1fr",
        gridTemplateAreas: '"header" "body"',
        minHeight: 0,
      }}
    >
      <div
        style={{
          gridArea: "header",
        }}
      >
        <div
          style={{
            display: "flex",
            minHeight: 40,
            padding: "0 4px 0 0",
            gap: 10,
          }}
        >
          <h1
            style={{
              flex: "0 0 auto",
              padding: "0 15px",
              margin: 0,
              fontSize: 15,
              fontWeight: 400,
              color: "white",
              background: "var(--primary-color)",
              display: "grid",
              placeItems: "center start",
            }}
          >
            CQL Compiler in Kotlin/JS &mdash; Demo
          </h1>

          <button
            type={"button"}
            style={{
              ...buttonStyle,
              flex: "0 0 auto",
              padding: "4px 12px",
              alignSelf: "center",
            }}
            onClick={async () => {
              const dirHandle: FileSystemDirectoryHandle =
                await // eslint-disable-next-line @typescript-eslint/no-explicit-any
                (window as any).showDirectoryPicker({
                  mode: "read",
                });
              const dirScan: FileSystemHandle[] = await Array.fromAsync(
                // eslint-disable-next-line @typescript-eslint/no-explicit-any
                (dirHandle as any).values(),
              );
              const files = dirScan
                .filter(
                  (handle): handle is FileSystemFileHandle =>
                    handle.kind === "file",
                )
                .map((handle) => ({
                  handle: handle,
                }));

              setState((prevState) => ({
                ...prevState,
                compileCqlArgs: {
                  ...prevState.compileCqlArgs,
                  mountedDir: {
                    handle: dirHandle,
                    files: files,
                  },
                },
              }));
            }}
          >
            {state.compileCqlArgs.mountedDir ? (
              <Fragment>
                <b>{state.compileCqlArgs.mountedDir.handle.name}</b> folder
                selected
              </Fragment>
            ) : (
              "Mount directory..."
            )}
          </button>

          <select
            disabled={!state.compileCqlArgs.mountedDir}
            style={{
              flex: "0 0 auto",
              alignSelf: "center",
              width: 250,
              padding: "4px 12px 4px 8px",
              border: "var(--border)",
              borderRadius: "var(--border-radius)",
              whiteSpace: "nowrap",
              overflow: "hidden",
              textOverflow: "ellipsis",
            }}
            defaultValue={""}
            onChange={async (event) => {
              const fileName = event.target.value;

              if (state.compileCqlArgs.mountedDir) {
                const content = await readFile(
                  state.compileCqlArgs.mountedDir.files.find(
                    (_) => _.handle.name === fileName,
                  )!.handle,
                );
                if (content !== null) {
                  setState((prevState) => ({
                    ...prevState,
                    compileCqlArgs: {
                      ...prevState.compileCqlArgs,
                      cql: content,
                    },
                  }));
                }
              }
            }}
          >
            <option value={""}>Import CQL from file...</option>
            {state.compileCqlArgs.mountedDir &&
              state.compileCqlArgs.mountedDir.files.map((file, fileIndex) => (
                <option key={fileIndex} value={file.handle.name}>
                  {file.handle.name}
                </option>
              ))}
          </select>

          <div style={{ flex: "1 1 auto" }} />

          <a
            style={{
              flex: "0 0 auto",
              alignSelf: "center",
              width: 32,
              height: 32,
              background:
                "url(https://raw.githubusercontent.com/microsoft/vscode-icons/refs/heads/main/icons/light/github-inverted.svg) center/18px 18px no-repeat",
            }}
            href={
              "https://github.com/cqframework/clinical_quality_language/tree/feature-kotlin/Src/js/cql-to-elm-ui"
            }
          />
        </div>
      </div>

      <div
        style={{
          gridArea: "body",
          display: "grid",
          gridTemplateColumns: "4fr 1fr",
          gridTemplateRows: "1fr",
          gridTemplateAreas: '"main sidebar"',
          minHeight: 0,
        }}
      >
        <div
          style={{
            gridArea: "main",
            display: "grid",
            gridTemplateColumns: "1fr",
            gridTemplateRows: "4fr 1fr",
            gridTemplateAreas: '"editors" "log"',
            minHeight: 0,
            background: "white",
          }}
        >
          <div
            style={{
              gridArea: "editors",
              display: "grid",
              gridTemplateColumns: "1fr 1fr",
              gridTemplateRows: "1fr",
              gridTemplateAreas: '"left-editor right-editor"',
              minHeight: 0,
            }}
          >
            <div
              style={{
                gridArea: "left-editor",
                display: "grid",
                gridTemplateColumns: "1fr",
                gridTemplateRows: "auto 1fr",
                gridTemplateAreas: '"label" "textarea"',
                minHeight: 0,
                borderTop: "2px solid var(--primary-color)",
              }}
            >
              <Editor
                ref={leftEditorRef}
                gridArea={"textarea"}
                value={state.compileCqlArgs.cql}
                onChange={(nextCql) => {
                  setState((prevState) => ({
                    ...prevState,
                    compileCqlArgs: {
                      ...prevState.compileCqlArgs,
                      cql: nextCql,
                    },
                  }));
                }}
                editable={true}
                lineNumbers={true}
                extensions={[
                  cqlLanguage,
                  EditorView.updateListener.of((update) => {
                    if (!update.docChanged && update.selectionSet) {
                      const { line, col } = getCursorLineAndCol(
                        leftEditorRef.current!.view!.state,
                      );
                      const ranges = findRangesForCqlPos(state.elm, line, col);
                      leftEditorRef.current!.view!.dispatch({
                        effects: [
                          customHighlightsEffectType.of(
                            ranges
                              .slice(0, 1)
                              .map((_) =>
                                getRangeFromLinesAndCols(
                                  leftEditorRef.current!.view!.state,
                                  _.cql,
                                ),
                              ),
                          ),
                        ],
                      });
                      rightEditorRef.current!.view!.dispatch({
                        effects: [
                          customHighlightsEffectType.of(
                            ranges.map((_) => _.elm),
                          ),
                          ...(ranges.length
                            ? [
                                EditorView.scrollIntoView(
                                  ranges[ranges.length - 1].elm.start,
                                  {
                                    y: "nearest",
                                  },
                                ),
                              ]
                            : []),
                        ],
                      });
                    }
                  }),
                ]}
              />
            </div>

            <div
              style={{
                gridArea: "right-editor",
                display: "grid",
                gridTemplateColumns: "1fr",
                gridTemplateRows: "auto 1fr",
                gridTemplateAreas: '"label" "textarea"',
                minHeight: 0,
                borderLeft: "var(--border)",
                borderTop: `2px solid ${state.isBusy ? "#ddd" : "#15df1d"}`,
                transition: `border-color ${
                  state.isBusy ? "0.2s" : "0.1s"
                } linear`,
              }}
            >
              <Editor
                ref={rightEditorRef}
                gridArea={"textarea"}
                value={state.elm.content}
                onChange={() => {}}
                editable={false}
                lineNumbers={true}
                extensions={[state.elm.contentType === "json" ? json() : xml()]}
              />
            </div>
          </div>
          <div
            style={{
              gridArea: "log",
              display: "grid",
              gridTemplateColumns: "1fr",
              gridTemplateRows: "auto 1fr",
              gridTemplateAreas: '"label" "textarea"',
              minHeight: 0,
              borderTop: "2px solid var(--primary-color)",
            }}
          >
            <Editor
              ref={logEditorRef}
              gridArea={"textarea"}
              value={state.log.join("\n")}
              onChange={() => {}}
              editable={false}
              lineNumbers={false}
              extensions={[logLanguage]}
            />
          </div>
        </div>

        <div
          style={{
            gridArea: "sidebar",
            padding: 20,
            minHeight: 0,
            borderTop: "2px solid var(--primary-color)",
            overflow: "auto",
          }}
        >
          <h2
            style={{
              fontSize: 20,
              fontWeight: 700,
              margin: "0 0 18px 0",
            }}
          >
            Settings
          </h2>

          <div
            style={{
              display: "grid",
              gap: 25,
              margin: "0 0 30px 0",
            }}
          >
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
                          value={state.compileCqlArgs.baseUrl}
                          onChange={(event) => {
                            const nextBaseUrl = event.target.value;
                            setState((prevState) => ({
                              ...prevState,
                              compileCqlArgs: {
                                ...prevState.compileCqlArgs,
                                baseUrl: nextBaseUrl,
                              },
                            }));
                          }}
                          style={{
                            width: "100%",
                            padding: "6px 12px",
                            border: "var(--border)",
                            borderRadius: "var(--border-radius)",
                          }}
                        />
                        <Caption>
                          Included libraries are fetched from this base URL +
                          library ID + .cql.
                        </Caption>
                      </Fragment>
                    ),
                  },
                ].map((librarySourceOption, librarySourceOptionIndex) => (
                  <div key={librarySourceOptionIndex}>
                    <label
                      style={{ display: "flex", gap: 5, margin: "0 0 5px 0" }}
                    >
                      <input
                        type={"radio"}
                        style={{
                          margin: 0,
                        }}
                        checked={
                          state.compileCqlArgs.librarySource ===
                          librarySourceOption.value
                        }
                        onChange={() => {
                          setState((prevState) => ({
                            ...prevState,
                            compileCqlArgs: {
                              ...prevState.compileCqlArgs,
                              librarySource: librarySourceOption.value,
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
                  gap: 5,
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
                      checked={state.compileCqlArgs.compilerOptions.includes(
                        compilerOption.value,
                      )}
                      onChange={(event) => {
                        const nextChecked = event.target.checked;
                        setState((prevState) => ({
                          ...prevState,
                          compileCqlArgs: {
                            ...prevState.compileCqlArgs,
                            compilerOptions: nextChecked
                              ? [
                                  ...prevState.compileCqlArgs.compilerOptions,
                                  compilerOption.value,
                                ]
                              : prevState.compileCqlArgs.compilerOptions.filter(
                                  (_) => _ !== compilerOption.value,
                                ),
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
                  value={state.compileCqlArgs.signatureLevel}
                  onChange={(event) => {
                    const nextSignatureLevel = event.target.value;
                    setState((prevState) => ({
                      ...prevState,
                      compileCqlArgs: {
                        ...prevState.compileCqlArgs,
                        signatureLevel: nextSignatureLevel,
                      },
                    }));
                  }}
                  style={{
                    width: "100%",
                    padding: "6px 12px 6px 8px",
                    border: "var(--border)",
                    borderRadius: "var(--border-radius)",
                  }}
                >
                  {signatureLevels.map(
                    (signatureLevel, signatureLevelIndex) => (
                      <option key={signatureLevelIndex} value={signatureLevel}>
                        {signatureLevel}
                      </option>
                    ),
                  )}
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
                          state.compileCqlArgs.outputContentType ===
                          outputContentType
                        }
                        onChange={() => {
                          setState((prevState) => ({
                            ...prevState,
                            compileCqlArgs: {
                              ...prevState.compileCqlArgs,
                              outputContentType: outputContentType,
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
              <Label>Tool configuraion</Label>
              <div style={{ margin: "0 0 10px 0" }}>
                <label style={{ display: "flex", gap: 5 }}>
                  <input
                    type={"checkbox"}
                    style={{
                      margin: 0,
                    }}
                    checked={state.compileCqlArgs.useWorker}
                    onChange={(event) => {
                      const nextUseWorker = event.target.checked;
                      setState((prevState) => ({
                        ...prevState,
                        compileCqlArgs: {
                          ...prevState.compileCqlArgs,
                          useWorker: nextUseWorker,
                        },
                      }));
                    }}
                  />
                  Use web worker
                </label>
                <Caption>
                  Run compilation in a persistent web worker. This avoids
                  blocking the UI thread and allows for synchronous HTTP
                  requests, but requires the XML parser and serializer
                  polyfills.
                </Caption>
              </div>
              <div>
                <label style={{ display: "flex", gap: 5 }}>
                  <input
                    type={"checkbox"}
                    style={{
                      margin: 0,
                    }}
                    checked={state.compileCqlArgs.useWasm}
                    onChange={(event) => {
                      const nextUseWasm = event.target.checked;
                      setState((prevState) => ({
                        ...prevState,
                        compileCqlArgs: {
                          ...prevState.compileCqlArgs,
                          useWasm: nextUseWasm,
                        },
                      }));
                    }}
                  />
                  Use WASM
                </label>
                <Caption>Use the WASM build of the CQL compiler.</Caption>
              </div>
            </div>
            <div>
              <Label>Tool logging</Label>
              <button
                type={"button"}
                style={{
                  ...buttonStyle,
                }}
                onClick={() => {
                  setState((prevState) => ({
                    ...prevState,
                    log: [],
                  }));
                }}
              >
                Clear log
              </button>
            </div>
          </div>

          <h2
            style={{
              fontSize: 20,
              fontWeight: 700,
              margin: "0 0 18px 0",
            }}
          >
            Supported models
          </h2>

          <div>
            <div style={{ fontSize: 12, margin: "0 0 5px 0" }}>
              You can use any of the models below, and they will be fetched
              automatically from GitHub.
            </div>
            <div
              style={{
                border: "var(--border)",
                borderRadius: "var(--border-radius)",
                background: "white",
                padding: "4px 12px",
                fontFamily: "var(--monospace-font-family)",
                fontSize: 12,
              }}
            >
              {supportedModels.map((model, modelIndex) => (
                <a
                  key={modelIndex}
                  style={{
                    display: "block",
                    padding: "1px 0",
                    color: "inherit",
                    textDecoration: "none",
                  }}
                  href={model.url}
                >
                  <span
                    style={{
                      color: "#aa0d91",
                      fontWeight: 700,
                    }}
                  >
                    using
                  </span>{" "}
                  {model.id}
                  {model.version && (
                    <Fragment>
                      {" "}
                      <span
                        style={{
                          color: "#aa0d91",
                          fontWeight: 700,
                        }}
                      >
                        version
                      </span>{" "}
                      <span style={{ color: "#D23423" }}>
                        {"'"}
                        {model.version}
                        {"'"}
                      </span>
                    </Fragment>
                  )}
                </a>
              ))}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
