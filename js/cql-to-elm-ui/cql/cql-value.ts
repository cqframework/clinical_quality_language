import {
  Value,
  Boolean,
  Integer,
  Long,
  Decimal,
  String,
  Date,
  DateTime,
  Time,
  Interval,
  List,
  iterableToList,
  StructuredValue,
  getNamedTypeForCqlValue,
} from "cql-js/kotlin/engine.mjs";
import { QName } from "cql-js/kotlin/shared.mjs";
import { Nullable, isKtNull, TJsCqlValue, TJsQName } from "@/shared";

export function toJsCqlValue(ktCqlValue: Nullable<Value>): TJsCqlValue {
  if (isKtNull(ktCqlValue)) {
    return null;
  }

  if (ktCqlValue instanceof Boolean) {
    return {
      type: "Boolean",
      value: ktCqlValue.value,
    };
  }

  if (ktCqlValue instanceof Integer) {
    return {
      type: "Integer",
      value: ktCqlValue.value,
    };
  }

  if (ktCqlValue instanceof Long) {
    return {
      type: "Long",
      value: ktCqlValue.value,
    };
  }

  if (ktCqlValue instanceof Decimal) {
    return {
      type: "Decimal",
      value: ktCqlValue.value.toString(),
    };
  }

  if (ktCqlValue instanceof String) {
    return {
      type: "String",
      value: ktCqlValue.value,
    };
  }

  if (ktCqlValue instanceof Date) {
    return {
      type: "Date",
      value: ktCqlValue.toString(),
    };
  }

  if (ktCqlValue instanceof DateTime) {
    return {
      type: "DateTime",
      value: ktCqlValue.toString(),
    };
  }

  if (ktCqlValue instanceof Time) {
    return {
      type: "Time",
      value: ktCqlValue.toString(),
    };
  }

  if (ktCqlValue instanceof StructuredValue) {
    const typeQName = getNamedTypeForCqlValue(ktCqlValue);
    return {
      type: "Structured",
      structuredTypeQName: typeQName ? qNameToJsQName(typeQName) : null,
      elements: new Map(
        [...ktCqlValue.elements.asJsReadonlyMapView().entries()].map(
          ([k, v]) => [k, toJsCqlValue(v)],
        ),
      ),
    };
  }

  if (ktCqlValue instanceof Interval) {
    return {
      type: "Interval",
      // @ts-expect-error TypeScript error
      pointTypeQName: qNameToJsQName(ktCqlValue.pointType),
      // @ts-expect-error TypeScript error
      low: toJsCqlValue(ktCqlValue.low),
      // @ts-expect-error TypeScript error
      high: toJsCqlValue(ktCqlValue.high),
      // @ts-expect-error TypeScript error
      lowClosed: ktCqlValue.lowClosed,
      // @ts-expect-error TypeScript error
      highClosed: ktCqlValue.highClosed,
    };
  }

  if (ktCqlValue instanceof List) {
    return {
      type: "List",
      value: iterableToList<Nullable<Value>>(ktCqlValue)
        .asJsReadonlyArrayView()
        .map((_) => toJsCqlValue(_)),
    };
  }

  throw new Error(`Unsupported Kotlin CQL value type: ${ktCqlValue}`);
}

function qNameToJsQName(qName: QName): TJsQName {
  return {
    namespaceUri: qName.getNamespaceURI(),
    localPart: qName.getLocalPart(),
    prefix: qName.getPrefix(),
  };
}
