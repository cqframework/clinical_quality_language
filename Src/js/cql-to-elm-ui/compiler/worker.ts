import { createStatefulCompiler } from "@/compiler/compiler";

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
        },
      });
    });
  }
};

postMessage({
  type: "ready",
  data: {},
});
