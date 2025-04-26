"use client";

import { useState, useRef, useLayoutEffect } from "react";
// @ts-expect-error No type definitions available
import * as ucum from "@lhncbc/ucum-lhc";
import {
  getLibraryManager,
  getCqlTranslator,
  enableLocators,
  enableAnnotations,
  disableLocators,
  disableAnnotations,
} from "cql-all-cql-to-elm-js";
import { supportedModels } from "@/app/supported-models";

const ucumUtils = ucum.UcumLhcUtils.getInstance();

export function CqlCompilerPlayground() {
  const [state, setState] = useState({
    cql: `library Test

using FHIR version '4.0.1'

context Patient

define x: [Observation]

`,
    enableAnnotations: true,
    enableLocators: true,
    outputContentType: "json" as "json" | "xml",
    repoUrl: "",
    filePath: "",
    repos: [] as {
      url: string;
      files: {
        path: string;
        content: string;
      }[];
    }[],
    fetchedModels: [] as {
      id: string;
      system: string | null;
      version: string | null;
      xml: string;
    }[],
  });

  const stateRef = useRef(state);
  useLayoutEffect(() => {
    stateRef.current = state;
  }, [state]);
  // stateRef.current = state;

  const libraryManager = useRef(
    getLibraryManager(
      (id: string, system: string | null, version: string | null) => {
        console.log("Kotlin looking for model", id, system, version);
        const fetchedModel = stateRef.current.fetchedModels.find(
          (_) => _.id === id && _.system === system && _.version === version,
        );
        if (fetchedModel) {
          return fetchedModel.xml;
        }
        const supportedModel = supportedModels.find(
          (_) => _.id === id && _.system === system && _.version === version,
        );
        if (supportedModel) {
          (async () => {
            const response = await fetch(supportedModel.url);
            const xml = await response.text();
            setState((prevState) => ({
              ...prevState,
              fetchedModels: [
                ...prevState.fetchedModels,
                {
                  id: supportedModel.id,
                  system: supportedModel.system,
                  version: supportedModel.version,
                  xml,
                },
              ],
            }));
          })();
          throw `Busy loading model: id=${id} system=${system} version=${version} from ${supportedModel.url}`;
        }
        throw `Error: Requested unknown model: id=${id} system=${system} version=${version}`;
      },
      (id: string, system: string | null, version: string | null) => {
        console.log("Kotlin looking for library", id, system, version);
        const repo = stateRef.current.repos.find(
          (_) => _.url === stateRef.current.repoUrl,
        );
        const libraryFile =
          repo && repo.files.find((_) => _.path.endsWith(`/${id}.cql`));
        if (libraryFile) {
          return libraryFile.content;
        }
        throw `Error: Requested unknown library: id=${id} system=${system} version=${version}`;
      },
      (unit: string) => {
        const result = ucumUtils.validateUnitString(unit);

        if (result.status === "valid") {
          return null;
        } else {
          return result.msg[0];
        }
      },
    ),
  );

  const repo =
    state.repoUrl && state.repos.find((_) => _.url === state.repoUrl);
  const file = repo && repo.files.find((_) => _.path === state.filePath);
  const content = file ? file.content : state.cql;

  const parseResult = (() => {
    try {
      const translator = getCqlTranslator(content, libraryManager.current);
      return {
        ok: true,
        tree:
          state.outputContentType === "json"
            ? translator.toJson()
            : translator.toXml(),
      } as const;
    } catch (e) {
      return {
        ok: false,
        message: String(e),
      } as const;
    }
  })();

  return (
    <div
      style={{
        position: "absolute",
        inset: 0,
        padding: 20,
        display: "grid",
        gridTemplateColumns: "1fr",
        gridTemplateRows: "auto 1fr",
        gridTemplateAreas: '"header" "body"',
        gap: 20,
      }}
    >
      <h1
        style={{
          gridArea: "header",
          margin: 0,
          fontSize: 24,
        }}
      >
        CQL Compiler in Kotlin/JS &mdash; Demo
      </h1>
      <div
        style={{
          gridArea: "body",
          display: "grid",
          gridTemplateColumns: "1fr 1fr",
          gridTemplateRows: "auto 1fr",
          gridTemplateAreas:
            '"body-config body-config" "body-left-editor body-right-editor"',
          gap: 20,
        }}
      >
        <div
          style={{
            gridArea: "body-config",
            display: "grid",
            gridTemplateColumns: "1fr 1fr 1fr 1fr",
            gap: 20,
            alignItems: "center",
          }}
        >
          <label style={{ display: "block" }}>
            <div style={{ fontWeight: 700, margin: "0 0 5px 0" }}>
              IG
              {state.repoUrl && repo && " (loaded)"}
              {state.repoUrl && !repo && " (loading...)"}
            </div>
            <input
              placeholder={
                "E.g. https://github.com/cqframework/ecqm-content-r4"
              }
              value={state.repoUrl}
              onChange={async (event) => {
                const nextRepoUrl = event.target.value;
                setState((prevState) => ({
                  ...prevState,
                  repoUrl: nextRepoUrl,
                  filePath: "",
                }));

                const response = await fetch(
                  `/api/fetch-github-repo?repoUrl=${encodeURIComponent("https://github.com/cqframework/ecqm-content-r4")}`,
                );
                const json = await response.json();

                setState((prevState) => ({
                  ...prevState,
                  repos: [
                    ...prevState.repos,
                    {
                      url: nextRepoUrl,
                      files: json,
                    },
                  ],
                }));
              }}
              style={{
                width: "100%",
                padding: "8px 10px",
              }}
            />
          </label>
          <label style={{ display: "block" }}>
            <div style={{ fontWeight: 700, margin: "0 0 5px 0" }}>Library</div>
            <select
              value={state.filePath}
              onChange={(event) => {
                const nextFilePath = event.target.value;
                setState((prevState) => ({
                  ...prevState,
                  filePath: nextFilePath,
                }));
              }}
              style={{
                width: "100%",
                padding: "8px 10px",
              }}
            >
              <option value={""}>(Inline)</option>
              {((repo && repo.files) || []).map((file) => (
                <option key={file.path} value={file.path}>
                  {file.path.split("/input/cql/")[1]}
                </option>
              ))}
            </select>
          </label>

          <div
            style={{
              gridColumn: "3 / span 2",
              display: "grid",
              gap: 8,
            }}
          >
            <div style={{}}>
              <div style={{ display: "flex", gap: 5 }}>
                <div style={{ fontWeight: 700 }}>Compiler options:</div>
                <label style={{ display: "block" }}>
                  <input
                    type={"checkbox"}
                    checked={state.enableAnnotations}
                    onChange={(event) => {
                      const nextEnableAnnotations = event.target.checked;
                      setState((prevState) => ({
                        ...prevState,
                        enableAnnotations: nextEnableAnnotations,
                      }));
                      if (nextEnableAnnotations) {
                        enableAnnotations(libraryManager.current);
                      } else {
                        disableAnnotations(libraryManager.current);
                      }
                    }}
                  />
                  Enable annotations
                </label>

                <label style={{ display: "block" }}>
                  <input
                    type={"checkbox"}
                    checked={state.enableLocators}
                    onChange={(event) => {
                      const nextEnableLocators = event.target.checked;
                      setState((prevState) => ({
                        ...prevState,
                        enableLocators: nextEnableLocators,
                      }));
                      if (nextEnableLocators) {
                        enableLocators(libraryManager.current);
                      } else {
                        disableLocators(libraryManager.current);
                      }
                    }}
                  />
                  Enable locators
                </label>
              </div>
            </div>

            <div style={{}}>
              <div style={{ display: "flex", gap: 5 }}>
                <div style={{ fontWeight: 700 }}>Output content type:</div>
                {(["json", "xml"] as const).map((outputContentType) => (
                  <label key={outputContentType} style={{ display: "block" }}>
                    <input
                      type={"radio"}
                      checked={state.outputContentType === outputContentType}
                      onChange={() => {
                        setState((prevState) => ({
                          ...prevState,
                          outputContentType,
                        }));
                      }}
                    />
                    {outputContentType.toUpperCase()}
                  </label>
                ))}
              </div>
            </div>
          </div>
        </div>

        <div
          style={{
            gridArea: "body-left-editor",
            display: "grid",
            gridTemplateColumns: "1fr",
            gridTemplateRows: "auto 1fr",
            gridTemplateAreas:
              '"body-left-editor-label" "body-left-editor-textarea"',
          }}
        >
          <div
            style={{
              gridArea: "body-left-editor-label",
            }}
          >
            <div style={{ fontWeight: 700, margin: "0 0 5px 0" }}>
              Library CQL
            </div>
          </div>

          <textarea
            style={{
              gridArea: "body-left-editor-textarea",
              display: "block",
              width: "100%",
              height: "100%",
              padding: "8px 10px",
            }}
            spellCheck={false}
            value={file ? file.content : state.cql}
            onChange={(event) => {
              const nextContent = event.target.value;
              setState((prevState) => {
                const repo = prevState.repos.find(
                  (_) => _.url === prevState.repoUrl,
                );
                if (repo) {
                  const file = repo.files.find(
                    (_) => _.path === prevState.filePath,
                  );
                  if (file) {
                    return {
                      ...prevState,
                      repos: prevState.repos.map((_) =>
                        _.url === prevState.repoUrl
                          ? {
                              ..._,
                              files: _.files.map((_) =>
                                _.path === prevState.filePath
                                  ? {
                                      ..._,
                                      content: nextContent,
                                    }
                                  : _,
                              ),
                            }
                          : _,
                      ),
                    };
                  }
                }

                return {
                  ...prevState,
                  cql: nextContent,
                };
              });
            }}
          />
        </div>

        <div
          style={{
            gridArea: "body-right-editor",
            display: "grid",
            gridTemplateColumns: "1fr",
            gridTemplateRows: "auto 1fr",
            gridTemplateAreas:
              '"body-right-editor-label" "body-right-editor-textarea"',
          }}
        >
          <div
            style={{
              gridArea: "body-right-editor-label",
            }}
          >
            <div style={{ fontWeight: 700, margin: "0 0 5px 0" }}>
              Library ELM
            </div>
          </div>

          <textarea
            style={{
              gridArea: "body-right-editor-textarea",
              display: "block",
              width: "100%",
              height: "100%",
              padding: "8px 10px",
            }}
            readOnly={true}
            spellCheck={false}
            value={(() => {
              if (parseResult.ok) {
                if (state.outputContentType === "json") {
                  return JSON.stringify(JSON.parse(parseResult.tree), null, 2);
                }
                if (state.outputContentType === "xml") {
                  return parseResult.tree;
                }
                return "";
              }

              return parseResult.message;
            })()}
          />
        </div>
      </div>
    </div>
  );
}
