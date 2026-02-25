import { engineOptions, TCqlEngineArgs, TCqlEngineOutput } from "@/shared";
import { TSetState, TState } from "@/state";
import { Fragment, useEffect, useRef } from "react";
import { Spinner } from "@/ui/spinner";
import { Label } from "@/ui/label";

export function CqlEngineResult({
  state,
  setState,
}: {
  state: TState;
  setState: TSetState;
}) {
  const currentRunIdRef = useRef(0);

  const workerPromiseRef = useRef<Promise<Worker> | null>(null);

  const createWorkerPromise = async () => {
    const worker = new Worker(
      new URL("@/cql/cql-engine-worker", import.meta.url),
    );

    await new Promise<void>((resolve) => {
      worker.onmessage = (event) => {
        if (event.data.type === "ready") {
          resolve();
        }
      };
    });

    worker.onmessage = (event) => {
      const { type, data } = event.data;

      if (type === "output") {
        if (data.runId === currentRunIdRef.current) {
          const output = data.output as TCqlEngineOutput;

          setState((prevState) => {
            if (output.type === "log") {
              return {
                ...prevState,
                common: {
                  ...prevState.common,
                  log: [...prevState.common.log, output.log],
                },
              };
            }
            return {
              ...prevState,
              common: {
                ...prevState.common,
                log: [
                  ...prevState.common.log,
                  `INFO Evaluation finished in ${(Date.now() - data.startTime) / 1000}s.`,
                ],
              },
              tabs: {
                ...prevState.tabs,
                "cql-engine": {
                  ...prevState.tabs["cql-engine"],
                  result: output,
                  isBusy: false,
                },
              },
            };
          });
        }
      }
    };

    return worker;
  };

  useEffect(() => {
    // Reset all caches by recreating the worker.
    // This also runs on initial load.

    const workerPromise = createWorkerPromise();
    if (workerPromiseRef.current) {
      workerPromiseRef.current.then((worker) => worker.terminate());
    }
    workerPromiseRef.current = workerPromise;

    setState((prevState) => ({
      ...prevState,
      common: {
        ...prevState.common,
        log: [...prevState.common.log, "INFO Caches reset."],
      },
    }));
  }, [
    state.common.librarySource,
    state.common.mountedDir,
    state.common.baseUrl,
    state.common.compilerOptions,
    state.common.signatureLevel,
  ]);

  useEffect(() => {
    (async () => {
      setState((prevState) => ({
        ...prevState,
        common: {
          ...prevState.common,
          log: [...prevState.common.log, "INFO Starting evaluation..."],
        },
        tabs: {
          ...prevState.tabs,
          "cql-engine": {
            ...prevState.tabs["cql-engine"],
            isBusy: true,
          },
        },
      }));

      const runId = ++currentRunIdRef.current;
      const startTime = Date.now();

      const cqlEngineArgs: TCqlEngineArgs = {
        cql: state.common.cql,
        compilerOptions: state.common.compilerOptions,
        signatureLevel: state.common.signatureLevel,
        librarySource: state.common.librarySource,
        baseUrl: state.common.baseUrl,
        mountedDir: state.common.mountedDir,
        engineOptions: state.tabs["cql-engine"].engineOptions,
      };

      const worker = await workerPromiseRef.current!;

      worker.postMessage({
        type: "runCql",
        data: {
          args: cqlEngineArgs,
          runId: runId,
          startTime: startTime,
        },
      });
    })();
  }, [
    state.common.cql,
    state.common.compilerOptions,
    state.common.signatureLevel,
    state.common.librarySource,
    state.common.baseUrl,
    state.common.mountedDir,
    state.tabs["cql-engine"].engineOptions,
  ]);

  return (
    <div
      style={{
        display: "grid",
        width: "100%",
        height: "100%",
        minHeight: 0,
        position: "relative",
      }}
    >
      <div
        style={{
          padding: 20,
          overflow: "auto",
          display: "grid",
        }}
      >
        <div>
          <CqlEngineResultInner state={state} />
        </div>
      </div>
      <div
        style={{
          position: "absolute",
          top: 5,
          right: 5,
          opacity: state.tabs["cql-engine"].isBusy ? 1 : 0,
          transition: "opacity 0.2s",
          pointerEvents: "none",
        }}
      >
        <Spinner />
      </div>
    </div>
  );
}

function CqlEngineResultInner({ state }: { state: TState }) {
  const output = state.tabs["cql-engine"].result;

  if (output) {
    if (output.type === "expressionResults") {
      return (
        <Fragment>
          {!output.expressionResults.length && (
            <div
              style={{
                background: "#fff8e1",
                padding: 14,
                borderRadius: 5,
              }}
            >
              No expression results.
            </div>
          )}
          <div
            style={{
              fontFamily: "var(--monospace-font-family)",
              fontSize: "90%",
              whiteSpace: "pre",
              display: "grid",
              gap: 7,
            }}
          >
            {output.expressionResults.map(
              ({ expressionName, expressionResult }, resultIndex) => (
                <div key={resultIndex}>
                  <b style={{ color: "rgb(149, 56, 0)" }}>{expressionName}</b> ={" "}
                  {expressionResult}
                </div>
              ),
            )}
          </div>
        </Fragment>
      );
    }

    if (output.type === "evaluationException") {
      return (
        <div
          style={{
            background: "#fff0f0",
            padding: 14,
            borderRadius: 5,
          }}
        >
          <div
            style={{
              fontWeight: 700,
              margin: "0 0 8px 0",
              color: "#900",
            }}
          >
            Evaluation exception
          </div>
          <div
            style={{
              fontFamily: "var(--monospace-font-family)",
              fontSize: "90%",
              whiteSpace: "pre",
            }}
          >
            {output.stack}
          </div>
        </div>
      );
    }
  }

  return <Fragment />;
}

export function CqlEngineSettings({
  state,
  setState,
}: {
  state: TState;
  setState: TSetState;
}) {
  return (
    <Fragment>
      <div>
        <Label>Engine options</Label>

        <div
          style={{
            display: "grid",
            gap: 3,
          }}
        >
          {engineOptions.map((engineOption, engineOptionIndex) => (
            <label key={engineOptionIndex} style={{ display: "flex", gap: 5 }}>
              <input
                type={"checkbox"}
                style={{
                  margin: 0,
                }}
                checked={state.tabs["cql-engine"].engineOptions.includes(
                  engineOption.value,
                )}
                onChange={(event) => {
                  const nextChecked = event.target.checked;
                  setState((prevState) => ({
                    ...prevState,
                    tabs: {
                      ...prevState.tabs,
                      "cql-engine": {
                        ...prevState.tabs["cql-engine"],
                        engineOptions: nextChecked
                          ? [
                              ...prevState.tabs["cql-engine"].engineOptions,
                              engineOption.value,
                            ]
                          : prevState.tabs["cql-engine"].engineOptions.filter(
                              (_) => _ !== engineOption.value,
                            ),
                      },
                    },
                  }));
                }}
              />
              <div>{engineOption.label}</div>
            </label>
          ))}
        </div>
      </div>
    </Fragment>
  );
}
