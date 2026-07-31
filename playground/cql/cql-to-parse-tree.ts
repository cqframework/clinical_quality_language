import { inspectCqlParseTree } from "cql-js/kotlin/cql.mjs";

export function cqlToParseTree(cql: string) {
  return inspectCqlParseTree(cql);
}
