import { createStatefulEngine } from "@/cql/cql-engine";

const { runCql } = createStatefulEngine();

onmessage = async (event) => {
  const { type, data } = event.data;

  if (type === "runCql") {
    runCql(data.args, (output) => {
      postMessage({
        type: "output",
        data: {
          output,
          runId: data.runId,
          startTime: data.startTime,
        },
      });
    });
  }
};

postMessage({
  type: "ready",
  data: {},
});
