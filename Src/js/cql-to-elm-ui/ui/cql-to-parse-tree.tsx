import React, { Fragment } from "react";
import { TSetState, TState } from "@/shared";
import { Editor } from "@/ui/editor/editor";
import { cqlToParseTree } from "@/compiler/parse-tree";

export function CqlToParseTreeResult({
  state,
}: {
  state: TState;
  setState: TSetState;
}) {
  const parseTree = (() => {
    try {
      return cqlToParseTree(state.common.cql);
    } catch (e) {
      console.error(e);
      return String(e);
    }
  })();

  return (
    <Editor
      value={parseTree}
      onChange={() => {}}
      editable={false}
      lineNumbers={true}
      extensions={[]}
    />
  );
}

export function CqlToParseTreeSettings({}: {
  state: TState;
  setState: TSetState;
}) {
  return <Fragment />;
}
