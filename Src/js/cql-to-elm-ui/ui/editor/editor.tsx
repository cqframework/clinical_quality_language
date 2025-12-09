import React, { forwardRef } from "react";
import CodeMirror, {
  Decoration,
  Extension,
  ReactCodeMirrorRef,
  StateEffect,
  StateField,
} from "@uiw/react-codemirror";
import { EditorView } from "@codemirror/view";
import { githubLight } from "@uiw/codemirror-theme-github";

// eslint-disable-next-line react/display-name
export const Editor = forwardRef<
  ReactCodeMirrorRef,
  {
    value: string;
    onChange: (nextValue: string) => void;
    editable: boolean;
    lineNumbers: boolean;
    extensions: Extension[];
  }
>(({ value, onChange, editable, lineNumbers, extensions }, ref) => {
  return (
    <CodeMirror
      ref={ref}
      style={{
        minWidth: 0,
        minHeight: 0,
        fontSize: "90%",
        filter: "saturate(1.1) contrast(1.1)",
      }}
      height={"100%"}
      width={"100%"}
      editable={editable}
      theme={[
        githubLight,
        EditorView.theme({
          "&.cm-focused": {
            outline: "none",
          },
          ".cm-content": {
            padding: "8px 0",
            fontFamily: "var(--monospace-font-family)",
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
            boxShadow: "0 0 0 2px #e8e8e8",
            background: "#e8e8e8",
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
