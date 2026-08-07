import { StreamLanguage } from "@codemirror/language";
import { simpleMode } from "@codemirror/legacy-modes/mode/simple-mode";

export const logLanguage = StreamLanguage.define(
  simpleMode({
    start: [
      { regex: /^(INFO|WARN)/, token: "keyword" },
      { regex: /.*/, token: "comment" },
    ],
  }),
);
