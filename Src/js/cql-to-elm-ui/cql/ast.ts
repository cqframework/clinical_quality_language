import { inspectCqlAst } from "cql-to-elm-js/kotlin/cql.mjs";

export function cqlToAst(cql: string) {
  return JSON.parse(inspectCqlAst(cql));
}

export function findRangesForCqlPos(cql: string, cursorPosition: number) {
  const ast = cqlToAst(cql);
  for (const statement of ast.library.statements) {
    if (
      statement.locator.startIndex <= cursorPosition &&
      cursorPosition <= statement.locator.stopIndex + 1
    ) {
      return [
        {
          start: statement.locator.startIndex,
          end: statement.locator.stopIndex + 1,
        },
      ];
    }
  }
  return [];
}
