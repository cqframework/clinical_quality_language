import React, { forwardRef } from "react";
import CodeMirror, {
  EditorState,
  Extension,
  ReactCodeMirrorRef,
} from "@uiw/react-codemirror";
import { EditorView } from "@codemirror/view";

// eslint-disable-next-line react/display-name
export const Editor = forwardRef<
  ReactCodeMirrorRef,
  {
    gridArea: string;
    value: string;
    onChange: (nextValue: string) => void;
    editable: boolean;
    extensions: Extension[];
  }
>(({ gridArea, value, onChange, editable, extensions }, ref) => {
  return (
    <CodeMirror
      ref={ref}
      style={{
        gridArea: gridArea,
        minWidth: 0,
        minHeight: 0,
        background: "white",
        border: "1px solid #777",
        borderRadius: 2.5,
      }}
      height={"100%"}
      width={"100%"}
      editable={editable}
      theme={EditorView.theme({
        ".cm-content": {
          padding: "8px 0",
        },
        ".cm-line": {
          padding: "0 10px",
        },
      })}
      basicSetup={{
        allowMultipleSelections: true,
        foldGutter: false,
        highlightActiveLine: false,
        highlightActiveLineGutter: false,
        highlightSelectionMatches: false,
        lineNumbers: true,
        searchKeymap: false,
      }}
      value={value}
      extensions={[EditorView.lineWrapping, ...extensions]}
      onChange={onChange}
    />
  );
});

export function getCursorLineAndCol(editorState: EditorState): {
  line: number;
  col: number;
} {
  const pos = editorState.selection.main.head;
  const line = editorState.doc.lineAt(pos);
  return { line: line.number, col: pos - line.from + 1 };
}
