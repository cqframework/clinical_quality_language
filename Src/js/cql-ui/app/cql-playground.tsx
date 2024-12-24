"use client";

import { useState } from "react";
import * as cqlJs from "cql-all-cql";
import * as cqlWasm from "cql-all-cql-wasm-js";

export function CqlPlayground() {
  const [state, setState] = useState({
    cql: `library Test
  define x: 5`,
    useWasm: false,
  });

  const cqlLib = state.useWasm ? cqlWasm : cqlJs;

  const parseResult = (() => {
    try {
      const tree = cqlLib.parseToTree(state.cql);
      return {
        ok: true,
        tree,
      } as const;
    } catch (e) {
      return {
        ok: false,
        error: String(e),
      } as const;
    }
  })();

  return (
    <div style={{ padding: 20 }}>
      <h1>CQL Kotlin Playground</h1>
      <label style={{ display: "block", margin: "0 0 20px 0" }}>
        <div style={{ fontWeight: 700, margin: "0 0 5px 0" }}>CQL library</div>
        <textarea
          value={state.cql}
          onChange={(event) => {
            const nextCql = event.target.value;
            setState((prevState) => ({
              ...prevState,
              cql: nextCql,
            }));
          }}
          style={{
            width: "100%",
            fieldSizing: "content",
            padding: "8px 10px",
          }}
        />
      </label>

      <label style={{ display: "block", margin: "0 0 20px 0" }}>
        <input
          type={"checkbox"}
          checked={state.useWasm}
          onChange={(event) => {
            const nextUseWasm = event.target.checked;
            setState((prevState) => ({
              ...prevState,
              useWasm: nextUseWasm,
            }));
          }}
        />
        Use WebAssembly
      </label>

      <div style={{ fontWeight: 700, margin: "0 0 5px 0" }}>Parse result</div>

      {parseResult.ok ? (
        <div>
          <pre
            style={{
              margin: 0,
              whiteSpace: "pre-wrap",
            }}
          >
            {parseResult.tree}
          </pre>
        </div>
      ) : (
        <div>
          <pre
            style={{
              margin: 0,
              whiteSpace: "pre-wrap",
            }}
          >
            Error: {parseResult.error}
          </pre>
        </div>
      )}
    </div>
  );
}
