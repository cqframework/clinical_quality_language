import { TSetState, TState } from "@/state";
import { Fragment } from "react";
import { json } from "@codemirror/lang-json";
import { Editor } from "@/ui/editor/editor";
import { AstTree } from "@/ui/ast-tree";
import { Heading } from "@/ui/heading";

export function CqlToAstResult({
  state,
}: {
  state: TState;
  setState: TSetState;
}) {
  const cqlToAstOutput = state.common.cqlToAstOutput;

  if (cqlToAstOutput) {
    if (cqlToAstOutput.ok) {
      if (state.tabs["cql-to-ast"].showJson) {
        return (
          <Editor
            value={JSON.stringify(cqlToAstOutput.ast, null, 2)}
            onChange={() => {}}
            editable={false}
            lineNumbers={true}
            extensions={[json()]}
          />
        );
      }

      return (
        <div
          style={{
            padding: 20,
            minHeight: 0,
            overflow: "auto",
          }}
        >
          <Heading>AST</Heading>

          {cqlToAstOutput.ast.problems.length > 0 && (
            <div
              style={{
                background: "#fff0f0",
                padding: 14,
                borderRadius: 5,
                margin: "0 0 20px 0",
              }}
            >
              <div
                style={{
                  fontWeight: 700,
                  margin: "0 0 8px 0",
                  color: "#900",
                }}
              >
                Problems
              </div>
              <div
                style={{
                  display: "grid",
                  gap: 7,
                }}
              >
                {cqlToAstOutput.ast.problems.map(
                  (problem: unknown, problemIndex: number) => (
                    <div key={problemIndex}>
                      <AstTree ast={problem} />
                    </div>
                  ),
                )}
              </div>
            </div>
          )}

          <AstTree
            ast={{
              kind: "library",
              ...cqlToAstOutput.ast.library,
            }}
          />
        </div>
      );
    }

    return (
      <Editor
        value={cqlToAstOutput.error}
        onChange={() => {}}
        editable={false}
        lineNumbers={true}
        extensions={[]}
      />
    );
  }

  return <div />;
}

export function CqlToAstSettings({
  state,
  setState,
}: {
  state: TState;
  setState: TSetState;
}) {
  return (
    <Fragment>
      <div>
        <label style={{ display: "flex", gap: 5 }}>
          <input
            type={"checkbox"}
            style={{
              margin: 0,
            }}
            checked={state.tabs["cql-to-ast"].showJson}
            onChange={(event) => {
              const nextShowJson = event.target.checked;
              setState((prevState) => ({
                ...prevState,
                tabs: {
                  ...prevState.tabs,
                  "cql-to-ast": {
                    ...prevState.tabs["cql-to-ast"],
                    showJson: nextShowJson,
                  },
                },
              }));
            }}
          />
          Show AST as JSON
        </label>
      </div>
    </Fragment>
  );
}
