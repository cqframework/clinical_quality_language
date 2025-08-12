import { TElmContentType } from "@/shared";

export function findRangesForCqlPos(
  elm: {
    content: string;
    contentType: TElmContentType;
  },
  cqlLine: number,
  cqlCol: number,
) {
  if (elm.contentType === "json") {
    return findRangesForCqlPosInner(
      elm.content,
      cqlLine,
      cqlCol,
      /"locator": "(\d+):(\d+)-(\d+):(\d+)"/g,
    );
  }
  return findRangesForCqlPosInner(
    elm.content,
    cqlLine,
    cqlCol,
    /locator="(\d+):(\d+)-(\d+):(\d+)"/g,
  );
}

function findRangesForCqlPosInner(
  elm: string,
  cqlLine: number,
  cqlCol: number,
  regex: RegExp,
) {
  return [
    ...elm.matchAll(regex).flatMap((match) => {
      const [lineFrom, colFrom, lineTo, colTo] = match.slice(1).map(Number);

      if (
        (lineFrom < cqlLine || (lineFrom === cqlLine && colFrom <= cqlCol)) &&
        (lineTo > cqlLine || (lineTo === cqlLine && colTo >= cqlCol))
      ) {
        return [
          {
            cql: {
              lineFrom,
              colFrom,
              lineTo,
              colTo,
            },
            elm: {
              start: match.index,
              end: match.index + match[0].length,
            },
          },
        ];
      }

      return [];
    }),
  ];
}
