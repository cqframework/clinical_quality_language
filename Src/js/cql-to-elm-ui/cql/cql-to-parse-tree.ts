import { inspectCqlParseTree } from "cql-to-elm-js/kotlin/cql.mjs";

export function cqlToParseTree(cql: string) {
  return inspectCqlParseTree(cql);
}
