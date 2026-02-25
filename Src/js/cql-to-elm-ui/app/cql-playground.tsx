"use client";

import { useState, Fragment } from "react";
import { initialState } from "@/state";
import { logLanguage } from "@/ui/editor/log-language";
import { Editor } from "@/ui/editor/editor";
import { EditorView } from "@codemirror/view";
import { Label } from "@/ui/label";
import { buttonStyle } from "@/ui/button";
import { supportedModels } from "@/cql/supported-models";
import { readFile } from "@/cql/utils";
import { Heading } from "@/ui/heading";
import {
  CqlToParseTreeResult,
  CqlToParseTreeSettings,
} from "@/ui/cql-to-parse-tree";
import { CqlToAstResult, CqlToAstSettings } from "@/ui/cql-to-ast";
import { CqlToElmResult, CqlToElmSettings } from "@/ui/cql-to-elm";
import { Caption } from "@/ui/caption";
import { CqlEditor } from "@/ui/cql-editor";
import { CqlEngineResult, CqlEngineSettings } from "@/ui/cql-engine";
import { compilerOptions, signatureLevels, TLibrarySource } from "@/shared";

const tabs = [
  {
    key: "cql-to-parse-tree",
    label: "Parse tree",
    result: CqlToParseTreeResult,
    settings: CqlToParseTreeSettings,
  },
  {
    key: "cql-to-ast",
    label: "AST",
    result: CqlToAstResult,
    settings: CqlToAstSettings,
  },
  {
    key: "cql-to-elm",
    label: "ELM",
    result: CqlToElmResult,
    settings: CqlToElmSettings,
  },
  {
    key: "cql-engine",
    label: "Expression results",
    result: CqlEngineResult,
    settings: CqlEngineSettings,
  },
];

export function CqlPlayground() {
  const [state, setState] = useState(initialState);

  const selectedTab = tabs.find((_) => _.key === state.common.selectedTab)!;

  return (
    <div
      style={{
        position: "absolute",
        inset: 0,
        display: "grid",
        gridTemplateColumns: "1fr",
        gridTemplateRows: "auto 1fr",
        gridTemplateAreas: '"header" "body"',
        minHeight: 0,
      }}
    >
      <div
        style={{
          gridArea: "header",
          boxShadow: "0 2px 5px rgba(0,0,0,0.2)",
          zIndex: 1,
        }}
      >
        <div
          style={{
            display: "flex",
            minHeight: 44,
            padding: "0 4px 0 0",
            gap: 10,
          }}
        >
          <h1
            style={{
              flex: "0 0 auto",
              padding: "0 20px",
              margin: 0,
              fontSize: 18,
              fontWeight: 700,
              display: "grid",
              placeItems: "center start",
            }}
          >
            CQL Playground
          </h1>

          {tabs.map((tab, tabIndex) => (
            <label
              key={tabIndex}
              style={{
                padding: "0 5px",
                display: "flex",
                gap: 5,
                alignItems: "center",
                fontSize: 14,
                fontWeight: 700,
              }}
            >
              <input
                type={"radio"}
                style={{
                  margin: 0,
                }}
                checked={state.common.selectedTab === tab.key}
                onChange={() => {
                  setState((prevState) => ({
                    ...prevState,
                    common: {
                      ...prevState.common,
                      selectedTab: tab.key,
                    },
                  }));
                }}
              />
              {tab.label}
            </label>
          ))}

          <div style={{ flex: "1 1 auto" }} />

          <a
            style={{
              flex: "0 0 auto",
              alignSelf: "center",
              width: 32,
              height: 32,
              background:
                "url(https://raw.githubusercontent.com/microsoft/vscode-icons/refs/heads/main/icons/light/github-inverted.svg) center/18px 18px no-repeat",
            }}
            href={
              "https://github.com/cqframework/clinical_quality_language/tree/main/Src/js/cql-to-elm-ui"
            }
          />
        </div>
      </div>

      <div
        style={{
          gridArea: "body",
          display: "grid",
          gridTemplateColumns: "4fr 1fr",
          gridTemplateRows: "1fr",
          gridTemplateAreas: '"main sidebar"',
          minHeight: 0,
        }}
      >
        <div
          style={{
            gridArea: "main",
            display: "grid",
            gridTemplateColumns: "1fr",
            gridTemplateRows: "4fr 1fr",
            gridTemplateAreas: '"editors" "log"',
            minHeight: 0,
            background: "white",
          }}
        >
          <div
            style={{
              gridArea: "editors",
              display: "grid",
              gridTemplateColumns: "1fr 1fr",
              gridTemplateRows: "1fr",
              gridTemplateAreas: '"left-editor right-editor"',
              minHeight: 0,
            }}
          >
            <div
              style={{
                gridArea: "left-editor",
                display: "grid",
                minHeight: 0,
              }}
            >
              <CqlEditor state={state} setState={setState} />
            </div>

            <div
              style={{
                gridArea: "right-editor",
                display: "grid",
                minHeight: 0,
                borderLeft: "var(--border)",
              }}
            >
              {<selectedTab.result state={state} setState={setState} />}
            </div>
          </div>
          <div
            style={{
              gridArea: "log",
              display: "grid",
              minHeight: 0,
              borderTop: "var(--border)",
            }}
          >
            <Editor
              value={state.common.log.join("\n")}
              onChange={() => {}}
              editable={false}
              lineNumbers={false}
              extensions={[
                logLanguage,
                EditorView.updateListener.of((update) => {
                  if (update.docChanged) {
                    update.view.dispatch({
                      effects: EditorView.scrollIntoView(
                        update.state.doc.length,
                        { y: "end" },
                      ),
                    });
                  }
                }),
              ]}
            />
          </div>
        </div>

        <div
          style={{
            gridArea: "sidebar",
            padding: 20,
            minHeight: 0,
            borderLeft: "var(--border)",
            overflow: "auto",
          }}
        >
          <div
            style={{
              display: "grid",
              gap: 25,
              margin: "0 0 30px 0",
            }}
          >
            <div
              style={{
                display: "grid",
                gap: 10,
              }}
            >
              <div>
                <button
                  type={"button"}
                  style={{
                    ...buttonStyle,
                    flex: "0 0 auto",
                    padding: "4px 12px",
                    alignSelf: "center",
                    maxWidth: "100%",
                  }}
                  onClick={async () => {
                    const dirHandle: FileSystemDirectoryHandle =
                      await // eslint-disable-next-line @typescript-eslint/no-explicit-any
                      (window as any).showDirectoryPicker({
                        mode: "read",
                      });
                    const dirScan: FileSystemHandle[] = await Array.fromAsync(
                      // eslint-disable-next-line @typescript-eslint/no-explicit-any
                      (dirHandle as any).values(),
                    );
                    const files = dirScan
                      .filter(
                        (handle): handle is FileSystemFileHandle =>
                          handle.kind === "file",
                      )
                      .map((handle) => ({
                        handle: handle,
                      }));

                    setState((prevState) => ({
                      ...prevState,
                      common: {
                        ...prevState.common,
                        mountedDir: {
                          handle: dirHandle,
                          files: files,
                        },
                      },
                    }));
                  }}
                >
                  {state.common.mountedDir ? (
                    <Fragment>
                      <b>{state.common.mountedDir.handle.name}</b> folder
                      selected
                    </Fragment>
                  ) : (
                    "Mount directory..."
                  )}
                </button>
                <Caption>
                  Mount a local directory to load CQL files from your computer.
                </Caption>
              </div>

              <div>
                <select
                  disabled={!state.common.mountedDir}
                  style={{
                    flex: "0 0 auto",
                    alignSelf: "center",
                    width: "100%",
                    padding: "4px 12px 4px 8px",
                    border: "var(--border)",
                    borderRadius: "var(--border-radius)",
                    whiteSpace: "nowrap",
                    overflow: "hidden",
                    textOverflow: "ellipsis",
                    fontFamily: "inherit",
                  }}
                  defaultValue={""}
                  onChange={async (event) => {
                    const fileName = event.target.value;

                    if (state.common.mountedDir) {
                      const content = await readFile(
                        state.common.mountedDir.files.find(
                          (_) => _.handle.name === fileName,
                        )!.handle,
                      );
                      if (content !== null) {
                        setState((prevState) => ({
                          ...prevState,
                          common: {
                            ...prevState.common,
                            cql: content,
                          },
                        }));
                      }
                    }
                  }}
                >
                  <option value={""}>Import CQL from file...</option>
                  {state.common.mountedDir &&
                    state.common.mountedDir.files.map((file, fileIndex) => (
                      <option key={fileIndex} value={file.handle.name}>
                        {file.handle.name}
                      </option>
                    ))}
                </select>
                <Caption>
                  Changing CQL content in the playground editor does not modify
                  the file on disk.
                </Caption>
              </div>
            </div>

            {(state.common.selectedTab === "cql-to-elm" ||
              state.common.selectedTab === "cql-engine") && (
              <Fragment>
                <div>
                  <Label>Library sources</Label>
                  <div
                    style={{
                      display: "grid",
                      gap: 10,
                    }}
                  >
                    {[
                      {
                        value: "local" as TLibrarySource,
                        label: "Mounted directory",
                        content: (
                          <Fragment>
                            <Caption>
                              Included libraries are loaded from files in the
                              local directory (library ID + .cql).
                            </Caption>
                          </Fragment>
                        ),
                      },
                      {
                        value: "remote" as TLibrarySource,
                        label: "Remote URL",
                        content: (
                          <Fragment>
                            <input
                              placeholder={
                                "E.g. https://raw.githubusercontent.com/cqframework/cqf-exercises/refs/heads/master/input/cql/"
                              }
                              value={state.common.baseUrl}
                              onChange={(event) => {
                                const nextBaseUrl = event.target.value;
                                setState((prevState) => ({
                                  ...prevState,
                                  common: {
                                    ...prevState.common,
                                    baseUrl: nextBaseUrl,
                                  },
                                }));
                              }}
                              style={{
                                width: "100%",
                                padding: "6px 12px",
                                border: "var(--border)",
                                borderRadius: "var(--border-radius)",
                                fontFamily: "inherit",
                              }}
                            />
                            <Caption>
                              Included libraries are fetched from this base URL
                              + library ID + .cql.
                            </Caption>
                          </Fragment>
                        ),
                      },
                    ].map((librarySourceOption, librarySourceOptionIndex) => (
                      <div key={librarySourceOptionIndex}>
                        <label
                          style={{
                            display: "flex",
                            gap: 5,
                            margin: "0 0 5px 0",
                          }}
                        >
                          <input
                            type={"radio"}
                            style={{
                              margin: 0,
                            }}
                            checked={
                              state.common.librarySource ===
                              librarySourceOption.value
                            }
                            onChange={() => {
                              setState((prevState) => ({
                                ...prevState,
                                common: {
                                  ...prevState.common,
                                  librarySource: librarySourceOption.value,
                                },
                              }));
                            }}
                          />
                          {librarySourceOption.label}
                        </label>
                        {librarySourceOption.content}
                      </div>
                    ))}
                  </div>
                </div>

                <div>
                  <Label>Compiler options</Label>

                  <div
                    style={{
                      display: "grid",
                      gap: 3,
                      margin: "0 0 12px 0",
                    }}
                  >
                    {compilerOptions.map(
                      (compilerOption, compilerOptionIndex) => (
                        <label
                          key={compilerOptionIndex}
                          style={{ display: "flex", gap: 5 }}
                        >
                          <input
                            type={"checkbox"}
                            style={{
                              margin: 0,
                            }}
                            checked={state.common.compilerOptions.includes(
                              compilerOption.value,
                            )}
                            onChange={(event) => {
                              const nextChecked = event.target.checked;
                              setState((prevState) => ({
                                ...prevState,
                                common: {
                                  ...prevState.common,
                                  compilerOptions: nextChecked
                                    ? [
                                        ...prevState.common.compilerOptions,
                                        compilerOption.value,
                                      ]
                                    : prevState.common.compilerOptions.filter(
                                        (_) => _ !== compilerOption.value,
                                      ),
                                },
                              }));
                            }}
                          />
                          <div>{compilerOption.label}</div>
                        </label>
                      ),
                    )}
                  </div>

                  <div>
                    <div style={{ margin: "0 0 5px 0" }}>Signature level</div>
                    <select
                      value={state.common.signatureLevel}
                      onChange={(event) => {
                        const nextSignatureLevel = event.target.value;
                        setState((prevState) => ({
                          ...prevState,
                          common: {
                            ...prevState.common,
                            signatureLevel: nextSignatureLevel,
                          },
                        }));
                      }}
                      style={{
                        width: "100%",
                        padding: "6px 12px 6px 8px",
                        border: "var(--border)",
                        borderRadius: "var(--border-radius)",
                        fontFamily: "inherit",
                      }}
                    >
                      {signatureLevels.map(
                        (signatureLevel, signatureLevelIndex) => (
                          <option
                            key={signatureLevelIndex}
                            value={signatureLevel}
                          >
                            {signatureLevel}
                          </option>
                        ),
                      )}
                    </select>
                  </div>
                </div>
              </Fragment>
            )}

            <selectedTab.settings state={state} setState={setState} />

            <div>
              <Label>Tool logging</Label>
              <button
                type={"button"}
                style={{
                  ...buttonStyle,
                }}
                onClick={() => {
                  setState((prevState) => ({
                    ...prevState,
                    common: {
                      ...prevState.common,
                      log: [],
                    },
                  }));
                }}
              >
                Clear log
              </button>
            </div>
          </div>

          {(state.common.selectedTab === "cql-to-elm" ||
            state.common.selectedTab === "cql-engine") && (
            <Fragment>
              <Heading>Supported models</Heading>

              <div>
                <div style={{ fontSize: 12, margin: "0 0 5px 0" }}>
                  You can use any of the models below, and they will be fetched
                  automatically from GitHub.
                </div>
                <div
                  style={{
                    border: "var(--border)",
                    borderRadius: "var(--border-radius)",
                    background: "white",
                    padding: "4px 12px",
                    fontFamily: "var(--monospace-font-family)",
                    fontSize: 12,
                  }}
                >
                  {supportedModels.map((model, modelIndex) => (
                    <a
                      key={modelIndex}
                      style={{
                        display: "block",
                        padding: "1px 0",
                        color: "inherit",
                        textDecoration: "none",
                      }}
                      href={model.url}
                    >
                      <span
                        style={{
                          color: "#d73a49",
                        }}
                      >
                        using
                      </span>{" "}
                      {model.id}
                      {model.version && (
                        <Fragment>
                          {" "}
                          <span
                            style={{
                              color: "#d73a49",
                            }}
                          >
                            version
                          </span>{" "}
                          <span style={{ color: "#032f62" }}>
                            {"'"}
                            {model.version}
                            {"'"}
                          </span>
                        </Fragment>
                      )}
                    </a>
                  ))}
                </div>
              </div>
            </Fragment>
          )}
        </div>
      </div>
    </div>
  );
}
