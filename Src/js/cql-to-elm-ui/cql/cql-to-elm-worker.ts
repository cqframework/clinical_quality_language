import { createStatefulCompiler } from "@/cql/compiler";

const { compileCql } = createStatefulCompiler(true);

onmessage = async (event) => {
  const { type, data } = event.data;

  if (type === "compileCql") {
    compileCql(data.args, (output) => {
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
