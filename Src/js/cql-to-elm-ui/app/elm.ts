import { TOutput } from "@/app/shared";

export function findElmRangesForCqlPos(
  output: TOutput,
  elm: string,
  cqlLine: number,
  cqlCol: number,
) {
  if (output.type === "log") {
    return [];
  }
  if (output.contentType === "json") {
    return findElmRangesForCqlPosInner(
      elm,
      cqlLine,
      cqlCol,
      /"locator": "(\d+):(\d+)-(\d+):(\d+)"/g,
    );
  }
  return findElmRangesForCqlPosInner(
    elm,
    cqlLine,
    cqlCol,
    /locator="(\d+):(\d+)-(\d+):(\d+)"/g,
  );
}

function findElmRangesForCqlPosInner(
  elm: string,
  cqlLine: number,
  cqlCol: number,
  regex: RegExp,
) {
  return [
    ...elm
      .matchAll(regex)
      .filter((match) => {
        const [lineFrom, colFrom, lineTo, colTo] = match.slice(1).map(Number);

        if (
          (lineFrom < cqlLine || (lineFrom === cqlLine && colFrom <= cqlCol)) &&
          (lineTo > cqlLine || (lineTo === cqlLine && colTo >= cqlCol))
        ) {
          return true;
        }
      })
      .map((match) => {
        return {
          start: match.index,
          end: match.index + match[0].length,
        };
      }),
  ];
}
