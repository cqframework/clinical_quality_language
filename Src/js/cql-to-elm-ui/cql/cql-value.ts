import { BigDecimal, QName } from "cql-js/kotlin/shared.mjs";
import {
  Date,
  DateTime,
  Time,
  Quantity,
  Ratio,
  Code,
  Concept,
  CodeSystem,
  ValueSet,
  CqlClassInstance,
  Tuple,
  Interval,
  isIterable,
  iterableToList,
} from "cql-js/kotlin/engine.mjs";
import { Nullable, isKtNull, TJsCqlValue } from "@/shared";

export const systemModelNamespaceUri = "urn:hl7-org:elm-types:r1";

export const anyTypeName = new QName(systemModelNamespaceUri, "Any");
export const booleanTypeName = new QName(systemModelNamespaceUri, "Boolean");
export const integerTypeName = new QName(systemModelNamespaceUri, "Integer");
export const longTypeName = new QName(systemModelNamespaceUri, "Long");
export const decimalTypeName = new QName(systemModelNamespaceUri, "Decimal");
export const stringTypeName = new QName(systemModelNamespaceUri, "String");
export const dateTypeName = new QName(systemModelNamespaceUri, "Date");
export const dateTimeTypeName = new QName(systemModelNamespaceUri, "DateTime");
export const timeTypeName = new QName(systemModelNamespaceUri, "Time");
export const quantityTypeName = new QName(systemModelNamespaceUri, "Quantity");
export const ratioTypeName = new QName(systemModelNamespaceUri, "Ratio");
export const codeTypeName = new QName(systemModelNamespaceUri, "Code");
export const conceptTypeName = new QName(systemModelNamespaceUri, "Concept");
export const codeSystemTypeName = new QName(
  systemModelNamespaceUri,
  "CodeSystem",
);
export const valueSetTypeName = new QName(systemModelNamespaceUri, "ValueSet");
export const vocabularyTypeName = new QName(
  systemModelNamespaceUri,
  "Vocabulary",
);

// eslint-disable-next-line @typescript-eslint/no-explicit-any
export function toJsCqlValue(ktCqlValue: Nullable<any>): TJsCqlValue {
  if (isKtNull(ktCqlValue)) {
    return null;
  }

  // Boolean
  if (typeof ktCqlValue === "boolean") {
    return ktCqlValue;
  }

  // Integer
  if (typeof ktCqlValue === "number") {
    return ktCqlValue;
  }

  // Long
  if (typeof ktCqlValue === "bigint") {
    return ktCqlValue;
  }

  // Decimal
  if (ktCqlValue instanceof BigDecimal) {
    return {
      type: "Decimal",
      value: ktCqlValue.toString(),
    };
  }

  // String
  if (typeof ktCqlValue === "string") {
    return ktCqlValue;
  }

  // Date
  if (ktCqlValue instanceof Date) {
    return {
      type: "Date",
      value: ktCqlValue.toString(),
    };
  }

  // DateTime
  if (ktCqlValue instanceof DateTime) {
    return {
      type: "DateTime",
      value: ktCqlValue.toString(),
    };
  }

  // Time
  if (ktCqlValue instanceof Time) {
    return {
      type: "Time",
      value: ktCqlValue.toString(),
    };
  }

  // Quantity
  if (ktCqlValue instanceof Quantity) {
    return {
      type: "Tuple",
      qName: quantityTypeName.toString(),
      elements: new Map([
        ["value", toJsCqlValue(ktCqlValue.value)],
        ["unit", toJsCqlValue(ktCqlValue.unit)],
      ]),
    };
  }

  // Ratio
  if (ktCqlValue instanceof Ratio) {
    return {
      type: "Tuple",
      qName: ratioTypeName.toString(),
      elements: new Map([
        ["numerator", toJsCqlValue(ktCqlValue.numerator)],
        ["denominator", toJsCqlValue(ktCqlValue.denominator)],
      ]),
    };
  }

  // Code
  if (ktCqlValue instanceof Code) {
    return {
      type: "Tuple",
      qName: codeTypeName.toString(),
      elements: new Map([
        ["code", toJsCqlValue(ktCqlValue.code)],
        ["system", toJsCqlValue(ktCqlValue.system)],
        // @ts-expect-error TypeScript error
        ["version", toJsCqlValue(ktCqlValue.version)],
        ["display", toJsCqlValue(ktCqlValue.display)],
      ]),
    };
  }

  // Concept
  if (ktCqlValue instanceof Concept) {
    return {
      type: "Tuple",
      qName: conceptTypeName.toString(),
      elements: new Map([
        // @ts-expect-error TypeScript error
        ["codes", toJsCqlValue(ktCqlValue.codes)],
        ["display", toJsCqlValue(ktCqlValue.display)],
      ]),
    };
  }

  // CodeSystem
  if (ktCqlValue instanceof CodeSystem) {
    return {
      type: "Tuple",
      qName: codeSystemTypeName.toString(),
      elements: new Map([
        ["id", toJsCqlValue(ktCqlValue.id)],
        ["version", toJsCqlValue(ktCqlValue.version)],
      ]),
    };
  }

  // ValueSet
  if (ktCqlValue instanceof ValueSet) {
    return {
      type: "Tuple",
      qName: valueSetTypeName.toString(),
      elements: new Map([
        ["id", toJsCqlValue(ktCqlValue.id)],
        ["version", toJsCqlValue(ktCqlValue.version)],
      ]),
    };
  }

  // CqlClassInstance
  if (ktCqlValue instanceof CqlClassInstance) {
    return {
      type: "Tuple",
      qName: ktCqlValue.type.toString(),
      elements: new Map(
        [...ktCqlValue.elements.asJsReadonlyMapView().entries()].map(
          ([k, v]) => [k, toJsCqlValue(v)],
        ),
      ),
    };
  }

  // untyped Tuple
  if (ktCqlValue instanceof Tuple) {
    return {
      type: "Tuple",
      elements: new Map(
        // @ts-expect-error TypeScript error
        [...ktCqlValue.elements.asJsReadonlyMapView().entries()].map(
          ([k, v]) => [k, toJsCqlValue(v)],
        ),
      ),
    };
  }

  // Interval
  if (ktCqlValue instanceof Interval) {
    // @ts-expect-error TypeScript error
    return {
      type: "Interval",
      low: toJsCqlValue(ktCqlValue.low),
      high: toJsCqlValue(ktCqlValue.high),
      lowClosed: ktCqlValue.lowClosed,
      highClosed: ktCqlValue.highClosed,
    };
  }

  // List
  if (isIterable(ktCqlValue)) {
    return iterableToList(ktCqlValue)
      .asJsReadonlyArrayView()
      .map((_) => toJsCqlValue(_));
  }

  throw new Error(`Unsupported Kotlin CQL value type: ${ktCqlValue}`);
}
