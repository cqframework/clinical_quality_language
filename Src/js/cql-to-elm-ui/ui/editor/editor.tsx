import React, { forwardRef } from "react";
import CodeMirror, {
  Decoration,
  EditorState,
  Extension,
  ReactCodeMirrorRef,
  StateEffect,
  StateField,
} from "@uiw/react-codemirror";
import { EditorView } from "@codemirror/view";
import { xcodeLight } from "@uiw/codemirror-theme-xcode";

// eslint-disable-next-line react/display-name
export const Editor = forwardRef<
  ReactCodeMirrorRef,
  {
    gridArea: string;
    value: string;
    onChange: (nextValue: string) => void;
    editable: boolean;
    lineNumbers: boolean;
    extensions: Extension[];
  }
>(({ gridArea, value, onChange, editable, lineNumbers, extensions }, ref) => {
  return (
    <CodeMirror
      ref={ref}
      style={{
        gridArea: gridArea,
        minWidth: 0,
        minHeight: 0,
        fontSize: "90%",
      }}
      height={"100%"}
      width={"100%"}
      editable={editable}
      theme={[
        xcodeLight,
        EditorView.theme({
          "&.cm-focused": {
            outline: "none",
          },
          ".cm-content": {
            padding: "8px 0",
          },
          ".cm-gutterElement": {
            padding: "0 7px 0 12px !important",
          },
          ".cm-gutters": {
            borderRight: "0 !important",
          },
          ".cm-line": {
            padding: "0 10px",
          },
          ".custom-highlights": {
            boxShadow: "0 0 0 2px #eee",
            background: "#eee",
            mixBlendMode: "darken",
            boxDecorationBreak: "clone",
          },
        }),
      ]}
      basicSetup={{
        allowMultipleSelections: true,
        foldGutter: false,
        highlightActiveLine: false,
        highlightActiveLineGutter: false,
        highlightSelectionMatches: false,
        lineNumbers: lineNumbers,
        searchKeymap: false,
      }}
      value={value}
      extensions={[
        EditorView.lineWrapping,
        customHighlightsExtension,
        ...extensions,
      ]}
      onChange={onChange}
    />
  );
});

export function getCursorLineAndCol(editorState: EditorState) {
  const pos = editorState.selection.main.head;
  const line = editorState.doc.lineAt(pos);
  return { line: line.number, col: pos - line.from + 1 };
}

export function getRangeFromLinesAndCols(
  editorState: EditorState,
  {
    lineFrom,
    colFrom,
    lineTo,
    colTo,
  }: {
    lineFrom: number;
    colFrom: number;
    lineTo: number;
    colTo: number;
  },
) {
  return {
    start: editorState.doc.line(lineFrom).from + colFrom - 1,
    end: editorState.doc.line(lineTo).from + colTo,
  };
}

export const customHighlightsEffectType =
  StateEffect.define<{ start: number; end: number }[]>();

const customHighlightsExtension = StateField.define({
  create() {
    return Decoration.none;
  },
  update(decorations, tr) {
    for (const effect of tr.effects) {
      if (effect.is(customHighlightsEffectType)) {
        const highlights = effect.value;
        const newDecorations = highlights.map(({ start, end }) =>
          Decoration.mark({ class: "custom-highlights" }).range(start, end),
        );
        return Decoration.set(newDecorations, true);
      }
    }

    return decorations;
  },
  provide: (field) => EditorView.decorations.from(field),
});
