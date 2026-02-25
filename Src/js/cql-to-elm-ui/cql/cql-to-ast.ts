import { inspectCqlAst } from "cql-js/kotlin/cql.mjs";

export function cqlToAst(cql: string) {
  return JSON.parse(inspectCqlAst(cql));
}

type TAst = ReturnType<typeof cqlToAst>;

export type TCqlToAstOutput =
  | {
      ok: true;
      ast: TAst;
    }
  | {
      ok: false;
      error: string;
    };

export function findRangesForCqlPos(ast: TAst, cursorPosition: number) {
  if (ast.library.statements) {
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
  }
  return [];
}
