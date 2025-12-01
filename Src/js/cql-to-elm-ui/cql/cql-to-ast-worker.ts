import { cqlToAst, TCqlToAstOutput } from "@/cql/cql-to-ast";

onmessage = async (event) => {
  const { type, data } = event.data;

  if (type === "cqlToAst") {
    const output = ((): TCqlToAstOutput => {
      try {
        return {
          ok: true,
          ast: cqlToAst(data.cql),
        };
      } catch (e) {
        console.error(e);
        return {
          ok: false,
          error: String(e),
        };
      }
    })();

    postMessage({
      type: "output",
      data: {
        output,
        runId: data.runId,
      },
    });
  }
};

postMessage({
  type: "ready",
  data: {},
});
