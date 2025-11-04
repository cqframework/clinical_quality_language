import { TState, TSetState } from "@/shared";
import { Fragment } from "react";
import { json } from "@codemirror/lang-json";
import { Editor } from "@/ui/editor/editor";
import { cqlToAst } from "@/compiler/ast";

export function CqlToAstResult({
  state,
}: {
  state: TState;
  setState: TSetState;
}) {
  const ast = (() => {
    try {
      return JSON.stringify(cqlToAst(state.common.cql), null, 2);
    } catch (e) {
      console.error(e);
      return String(e);
    }
  })();

  return (
    <Editor
      value={ast}
      onChange={() => {}}
      editable={false}
      lineNumbers={true}
      extensions={[json()]}
    />
  );
}

export function CqlToAstSettings({}: { state: TState; setState: TSetState }) {
  return <Fragment />;
}
