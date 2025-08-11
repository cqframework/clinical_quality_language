"use client";

import { useState, useRef, useEffect } from "react";
import { json } from "@codemirror/lang-json";
import { xml } from "@codemirror/lang-xml";
import { TCompileCqlArgs, TOutput } from "@/app/shared";
import { createStatefulCompiler } from "@/app/compiler";
import { cqlLanguage } from "@/app/cql-language";
import { Editor, getCursorLineAndCol } from "@/app/editor";
import { EditorSelection, ReactCodeMirrorRef } from "@uiw/react-codemirror";
import { EditorView } from "@codemirror/view";
import { findElmRangesForCqlPos } from "@/app/elm";

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
  enableAnnotations: false,
  enableLocators: true,
  outputContentType: "json",
  baseUrl:
    "https://raw.githubusercontent.com/cqframework/cqf-exercises/refs/heads/master/input/cql/",
};

export function CqlCompilerPlayground() {
  const [state, setState] = useState({
    ...initialCompileCqlArgs,

    isBusy: true,
    output: {
      type: "log",
      log: "Getting ready...",
    } as TOutput,

    useWorker: true,
  });

  const statefulCompilerRef = useRef<ReturnType<
    typeof createStatefulCompiler
  > | null>(null);

  const workerPromiseRef = useRef<Promise<Worker> | null>(null);

  const currentRunIdRef = useRef(0);

  useEffect(() => {
    (async () => {
      setState((prevState) => ({
        ...prevState,
        isBusy: true,
      }));

      const runId = ++currentRunIdRef.current;

      if (state.useWorker) {
        const workerPromise = (() => {
          if (workerPromiseRef.current) {
            return workerPromiseRef.current;
          }

          const workerPromise = (async () => {
            const worker = new Worker(new URL("./worker.ts", import.meta.url));

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
                  setState((prevState) => ({
                    ...prevState,
                    isBusy: data.output.type === "log",
                    output: data.output,
                  }));
                }
              }
            };

            return worker;
          })();

          workerPromiseRef.current = workerPromise;
          return workerPromise;
        })();

        const worker = await workerPromise;

        worker.postMessage({
          type: "compileCql",
          data: {
            args: {
              cql: state.cql,
              useWasm: state.useWasm,
              enableAnnotations: state.enableAnnotations,
              enableLocators: state.enableLocators,
              outputContentType: state.outputContentType,
              baseUrl: state.baseUrl,
            } satisfies TCompileCqlArgs,
            runId: runId,
          },
        });
      } else {
        const { compileCql } = (() => {
          if (statefulCompilerRef.current) {
            return statefulCompilerRef.current;
          }

          const statefulCompiler = createStatefulCompiler(false);
          statefulCompilerRef.current = statefulCompiler;
          return statefulCompiler;
        })();

        compileCql(
          {
            cql: state.cql,
            useWasm: state.useWasm,
            enableAnnotations: state.enableAnnotations,
            enableLocators: state.enableLocators,
            outputContentType: state.outputContentType,
            baseUrl: state.baseUrl,
          },
          (output) => {
            if (runId === currentRunIdRef.current) {
              setState((prevState) => ({
                ...prevState,
                isBusy: output.type === "log",
                output: output,
              }));
            }
          },
        );
      }
    })();
  }, [
    state.cql,
    state.useWasm,
    state.enableAnnotations,
    state.enableLocators,
    state.outputContentType,
    state.baseUrl,
    state.useWorker,
  ]);

  const resultContent = (() => {
    if (state.output.type === "log") {
      return state.output.log;
    }
    if (state.output.contentType === "json") {
      try {
        return JSON.stringify(JSON.parse(state.output.elm), null, 2);
      } catch (e) {
        console.error(e);
      }
      return state.output.elm;
    }
    return state.output.elm;
  })();

  const leftEditorRef = useRef<ReactCodeMirrorRef>(null);
  const rightEditorRef = useRef<ReactCodeMirrorRef>(null);

  useEffect(() => {
    try {
      const { line, col } = getCursorLineAndCol(
        leftEditorRef.current!.view!.state,
      );
      const elmRanges = findElmRangesForCqlPos(
        state.output,
        rightEditorRef.current!.view!.state.doc.toString(),
        line,
        col,
      );
      if (elmRanges.length) {
        rightEditorRef.current!.view!.dispatch({
          selection: EditorSelection.create(
            elmRanges.map((range) => {
              return EditorSelection.range(range.start, range.end);
            }),
          ),
          effects: EditorView.scrollIntoView(elmRanges[0].start, {
            y: "nearest",
          }),
        });
      } else {
        rightEditorRef.current!.view!.dispatch({
          selection: {
            anchor: 0,
            head: 0,
          },
        });
      }

      // eslint-disable-next-line @typescript-eslint/no-unused-vars
    } catch (e) {}
  }, [state.output]);

  return (
    <div
      style={{
        position: "absolute",
        inset: 0,
        padding: 20,
        display: "grid",
        gridTemplateColumns: "1fr",
        gridTemplateRows: "auto 1fr",
        gridTemplateAreas: '"header" "body"',
        gap: 10,
        minHeight: 0,
      }}
    >
      <div style={{ display: "flex", gap: 20 }}>
        <h1
          style={{
            gridArea: "header",
            margin: 0,
            fontSize: 24,
            flex: "1 0 auto",
          }}
        >
          CQL Compiler in Kotlin/JS &mdash; Demo
        </h1>
        <a
          style={{
            flex: "0 0 auto",
          }}
          href={
            "https://github.com/cqframework/clinical_quality_language/tree/feature-kotlin/Src/js/cql-to-elm-ui"
          }
        >
          View source
        </a>
      </div>
      <div
        style={{
          gridArea: "body",
          display: "grid",
          gridTemplateColumns: "1fr 1fr",
          gridTemplateRows: "auto 1fr",
          gridTemplateAreas:
            '"body-config body-config" "body-left-editor body-right-editor"',
          gap: "15px 20px",
          minHeight: 0,
        }}
      >
        <div
          style={{
            gridArea: "body-config",
            display: "grid",
            gridTemplateColumns: "1fr 1fr 1fr 1fr",
            gap: 20,
            alignItems: "center",
          }}
        >
          <label style={{ display: "block", gridColumn: "1 / span 2" }}>
            <div style={{ fontWeight: 700, margin: "0 0 5px 0" }}>
              Library base URL
            </div>
            <input
              placeholder={
                "E.g. https://raw.githubusercontent.com/cqframework/cqf-exercises/refs/heads/master/input/cql/"
              }
              value={state.baseUrl}
              onChange={async (event) => {
                const nextBaseUrl = event.target.value;
                setState((prevState) => ({
                  ...prevState,
                  baseUrl: nextBaseUrl,
                }));
              }}
              style={{
                width: "100%",
                padding: "8px 10px",
              }}
            />
          </label>

          <div
            style={{
              gridColumn: "3 / span 2",
              display: "grid",
              gap: 8,
            }}
          >
            <div>
              <div style={{ display: "flex", gap: 5 }}>
                <div style={{ fontWeight: 700 }}>Compiler options:</div>
                <label style={{ display: "block" }}>
                  <input
                    type={"checkbox"}
                    checked={state.enableAnnotations}
                    onChange={(event) => {
                      const nextEnableAnnotations = event.target.checked;
                      setState((prevState) => ({
                        ...prevState,
                        enableAnnotations: nextEnableAnnotations,
                      }));
                    }}
                  />
                  Enable annotations
                </label>

                <label style={{ display: "block" }}>
                  <input
                    type={"checkbox"}
                    checked={state.enableLocators}
                    onChange={(event) => {
                      const nextEnableLocators = event.target.checked;
                      setState((prevState) => ({
                        ...prevState,
                        enableLocators: nextEnableLocators,
                      }));
                    }}
                  />
                  Enable locators
                </label>
              </div>
            </div>

            <div
              style={{
                display: "grid",
                gap: 5,
              }}
            >
              <div style={{ display: "flex", gap: 5, margin: "0 0 2px 0" }}>
                <div style={{ fontWeight: 700 }}>Output content type:</div>
                {(["json", "xml"] as const).map((outputContentType) => (
                  <label key={outputContentType} style={{ display: "block" }}>
                    <input
                      type={"radio"}
                      checked={state.outputContentType === outputContentType}
                      onChange={() => {
                        setState((prevState) => ({
                          ...prevState,
                          outputContentType,
                        }));
                      }}
                    />
                    {outputContentType.toUpperCase()}
                  </label>
                ))}
              </div>
              <div style={{ display: "flex", gap: 10 }}>
                <label style={{ display: "block" }}>
                  <input
                    type={"checkbox"}
                    checked={state.useWorker}
                    onChange={(event) => {
                      const nextUseWorker = event.target.checked;
                      setState((prevState) => ({
                        ...prevState,
                        useWorker: nextUseWorker,
                      }));
                    }}
                  />
                  Use worker
                </label>
                <label style={{ display: "block" }}>
                  <input
                    type={"checkbox"}
                    checked={state.useWasm}
                    onChange={(event) => {
                      const nextUseWasm = event.target.checked;
                      setState((prevState) => ({
                        ...prevState,
                        useWasm: nextUseWasm,
                      }));
                    }}
                  />
                  Use WASM
                </label>
              </div>
            </div>
          </div>
        </div>

        <div
          style={{
            gridArea: "body-left-editor",
            display: "grid",
            gridTemplateColumns: "1fr",
            gridTemplateRows: "auto 1fr",
            gridTemplateAreas:
              '"body-left-editor-label" "body-left-editor-textarea"',
            minHeight: 0,
          }}
        >
          <div
            style={{
              gridArea: "body-left-editor-label",
            }}
          >
            <div style={{ fontWeight: 700, margin: "0 0 5px 0" }}>
              Library CQL
            </div>
          </div>

          <Editor
            ref={leftEditorRef}
            gridArea={"body-left-editor-textarea"}
            value={state.cql}
            onChange={(nextCql) => {
              setState((prevState) => ({
                ...prevState,
                cql: nextCql,
              }));
            }}
            editable={true}
            extensions={[
              cqlLanguage,
              EditorView.updateListener.of((update) => {
                if (update.docChanged || update.selectionSet) {
                  const { line, col } = getCursorLineAndCol(
                    leftEditorRef.current!.view!.state,
                  );
                  const elmRanges = findElmRangesForCqlPos(
                    state.output,
                    rightEditorRef.current!.view!.state.doc.toString(),
                    line,
                    col,
                  );
                  if (elmRanges.length) {
                    rightEditorRef.current!.view!.dispatch({
                      selection: EditorSelection.create(
                        elmRanges.map((range) => {
                          return EditorSelection.range(range.start, range.end);
                        }),
                      ),
                      effects: EditorView.scrollIntoView(elmRanges[0].start, {
                        y: "nearest",
                      }),
                    });
                  } else {
                    rightEditorRef.current!.view!.dispatch({
                      selection: {
                        anchor: 0,
                        head: 0,
                      },
                    });
                  }
                }
              }),
            ]}
          />
        </div>

        <div
          style={{
            gridArea: "body-right-editor",
            display: "grid",
            gridTemplateColumns: "1fr",
            gridTemplateRows: "auto 1fr",
            gridTemplateAreas:
              '"body-right-editor-label" "body-right-editor-textarea"',
            minHeight: 0,
          }}
        >
          <div
            style={{
              gridArea: "body-right-editor-label",
            }}
          >
            <div
              style={{
                fontWeight: 700,
                margin: "0 0 5px 0",
                background: state.isBusy
                  ? "url(https://upload.wikimedia.org/wikipedia/en/6/6f/Windows_hourglass_cursor.png) right center/auto no-repeat"
                  : "none",
              }}
            >
              Library ELM
            </div>
          </div>

          <Editor
            ref={rightEditorRef}
            gridArea={"body-right-editor-textarea"}
            value={resultContent}
            onChange={() => {}}
            editable={false}
            extensions={(() => {
              if (state.output.type === "log") {
                return [];
              }
              if (state.output.contentType === "json") {
                return [json()];
              }
              return [xml()];
            })()}
          />
        </div>
      </div>
    </div>
  );
}
