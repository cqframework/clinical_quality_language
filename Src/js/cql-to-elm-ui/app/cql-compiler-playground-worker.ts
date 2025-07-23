import { createStatefulCompiler } from "@/app/cql-compiler-playground-compile-cql";

const { compileCql } = createStatefulCompiler();

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
