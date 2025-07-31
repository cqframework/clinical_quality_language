import CodeMirror, { Extension } from "@uiw/react-codemirror";
import { EditorView } from "@codemirror/view";

export function Editor({
  gridArea,
  value,
  onChange,
  editable,
  extensions,
}: {
  gridArea: string;
  value: string;
  onChange: (nextValue: string) => void;
  editable: boolean;
  extensions: Extension[];
}) {
  return (
    <CodeMirror
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
        foldGutter: false,
        highlightActiveLine: false,
        lineNumbers: false,
        searchKeymap: false,
      }}
      value={value}
      extensions={[EditorView.lineWrapping, ...extensions]}
      onChange={onChange}
    />
  );
}
