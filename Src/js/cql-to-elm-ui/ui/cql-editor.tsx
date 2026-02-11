import { TSetState, TState } from "@/state";
import { useEffect, useRef } from "react";
import { customHighlightsEffectType, Editor } from "@/ui/editor/editor";
import { findRangesForCqlPos, TCqlToAstOutput } from "@/cql/cql-to-ast";
import { cqlLanguage } from "@/ui/editor/cql-language";
import { EditorView } from "@codemirror/view";
import { ReactCodeMirrorRef } from "@uiw/react-codemirror";

export function CqlEditor({
  state,
  setState,
}: {
  state: TState;
  setState: TSetState;
}) {
  const currentRunIdRef = useRef(0);

  const workerPromiseRef = useRef<Promise<Worker> | null>(null);

  const createWorkerPromise = async () => {
    const worker = new Worker(
      new URL("@/cql/cql-to-ast-worker", import.meta.url),
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
          const output = data.output as TCqlToAstOutput;

          setState((prevState) => {
            return {
              ...prevState,
              common: {
                ...prevState.common,
                cqlToAstOutput: output,
              },
            };
          });
        }
      }
    };

    return worker;
  };

  useEffect(() => {
    workerPromiseRef.current = createWorkerPromise();
  }, []);

  useEffect(() => {
    (async () => {
      const runId = ++currentRunIdRef.current;

      const worker = await workerPromiseRef.current!;

      worker.postMessage({
        type: "cqlToAst",
        data: {
          cql: state.common.cql,
          runId: runId,
        },
      });
    })();
  }, [state.common.cql]);

  const cqlToAstOutput = state.common.cqlToAstOutput;

  const editorRef = useRef<ReactCodeMirrorRef>(null);

  useEffect(() => {
    if (editorRef.current?.view && cqlToAstOutput) {
      if (cqlToAstOutput.ok) {
        const ranges = findRangesForCqlPos(
          cqlToAstOutput.ast,
          state.common.cursorPos,
        );
        editorRef.current.view.dispatch({
          effects: [customHighlightsEffectType.of(ranges)],
        });
      } else {
        editorRef.current.view.dispatch({
          effects: [customHighlightsEffectType.of([])],
        });
      }
    }
  }, [cqlToAstOutput, state.common.cursorPos]);

  return (
    <Editor
      ref={editorRef}
      value={state.common.cql}
      onChange={(nextCql) => {
        setState((prevState) => ({
          ...prevState,
          common: {
            ...prevState.common,
            cql: nextCql,
          },
        }));
      }}
      editable={true}
      lineNumbers={true}
      extensions={[
        cqlLanguage,
        EditorView.updateListener.of((update) => {
          if (update.docChanged || update.selectionSet) {
            setState((prevState) => ({
              ...prevState,
              common: {
                ...prevState.common,
                cursorPos: update.view.state.selection.main.head,
              },
            }));
          }
        }),
      ]}
    />
  );
}
