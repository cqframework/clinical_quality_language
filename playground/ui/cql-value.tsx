import React, { Fragment } from "react";
import { TJsCqlValue } from "@/shared";
import {
  quantityTypeName,
  ratioTypeName,
  codeTypeName,
  codeSystemTypeName,
  conceptTypeName,
  valueSetTypeName,
  systemModelNamespaceUri,
} from "cql-js";

export function CqlValue({ value }: { value: TJsCqlValue }) {
  const typeHint = getTypeHint(value);
  return (
    <Fragment>
      {formatCqlValue(value)}
      {typeHint && (
        <span
          style={{
            color: "#999",
            fontSize: "80%",
            margin: "0 0 0 4px",
            padding: "0 3px",
            background: "#f8f8f8",
            borderRadius: 3,
          }}
        >
          {typeHint}
        </span>
      )}
    </Fragment>
  );
}

function formatCqlValue(value: TJsCqlValue) {
  if (value === null) {
    return "null";
  }

  switch (value.type) {
    case "Boolean":
      return <span style={{ color: "#e36209" }}>{String(value.value)}</span>;
    case "Integer":
      return <span style={{ color: "#005cc5" }}>{value.value}</span>;
    case "Long":
      return <span style={{ color: "#005cc5" }}>{value.value}L</span>;
    case "String":
      return (
        <span style={{ color: "#032f62" }}>
          {"'"}
          {value.value}
          {"'"}
        </span>
      );
    case "Decimal":
      return <span style={{ color: "#005cc5" }}>{value.value}</span>;
    case "Date":
    case "DateTime":
      return <span style={{ color: "#116329" }}>@{value.value}</span>;
    case "Time":
      return <span style={{ color: "#116329" }}>@T{value.value}</span>;
    case "Structured":
      switch (value.structuredTypeName) {
        case quantityTypeName.get().toString():
          return (
            <Fragment>
              {formatCqlValue(value.elements.get("value")!)}{" "}
              {formatCqlValue(value.elements.get("unit")!)}
            </Fragment>
          );
        case ratioTypeName.get().toString():
          return (
            <Fragment>
              {formatCqlValue(value.elements.get("numerator")!)} :{" "}
              {formatCqlValue(value.elements.get("denominator")!)}
            </Fragment>
          );
        default:
          return (
            <Fragment>
              {value.structuredTypeName ? (
                formatTypeQName(value.structuredTypeName)
              ) : (
                <span style={{ color: "#005cc5" }}>Tuple</span>
              )}
              {" {"}
              {value.elements.size ? (
                <div style={{ padding: "0 0 0 2ch" }}>
                  {[...value.elements.entries()].map(
                    ([elementName, elementValue], elementIndex, elements) => (
                      <div key={elementIndex}>
                        {elementName}: <CqlValue value={elementValue} />
                        {elementIndex < elements.length - 1 ? "," : ""}
                      </div>
                    ),
                  )}
                </div>
              ) : (
                " : "
              )}
              {"}"}
            </Fragment>
          );
      }
    case "Interval":
      return (
        <Fragment>
          <span style={{ color: "#005cc5" }}>Interval</span>
          {value.lowClosed ? "[" : "("}
          {formatCqlValue(value.low)}, {formatCqlValue(value.high)}
          {value.highClosed ? "]" : ")"}
        </Fragment>
      );
    case "List":
      return (
        <Fragment>
          {"{"}
          {value.value.length > 0 && (
            <div style={{ padding: "0 0 0 2ch" }}>
              {value.value.map((item, itemIndex) => (
                <div key={itemIndex}>
                  <CqlValue value={item} />
                  {itemIndex < value.value.length - 1 ? "," : ""}
                </div>
              ))}
            </div>
          )}
          {"}"}
        </Fragment>
      );
  }
}

function getTypeHint(value: TJsCqlValue) {
  if (value === null) {
    return "Any";
  }

  switch (value.type) {
    case "Boolean":
    case "Integer":
    case "Long":
    case "Decimal":
    case "String":
    case "Date":
    case "DateTime":
    case "Time":
      return value.type;
    case "Interval":
      return `Interval<${formatTypeQName(value.pointTypeName)}>`;
    case "Structured":
      switch (value.structuredTypeName) {
        case quantityTypeName.get().toString():
          return "Quantity";
        case ratioTypeName.get().toString():
          return "Ratio";
        default:
          return "";
      }
    case "List":
      return "";
  }
}

function formatTypeQName(typeQName: string) {
  if (typeQName.startsWith(`{${systemModelNamespaceUri.get()}}`)) {
    return typeQName.slice(`{${systemModelNamespaceUri.get()}}`.length);
  }
  return typeQName;
}
