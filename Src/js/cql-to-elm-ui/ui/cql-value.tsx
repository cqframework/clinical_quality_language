import React, { Fragment } from "react";
import { TJsCqlValue } from "@/shared";
import {
  anyTypeName,
  booleanTypeName,
  dateTimeTypeName,
  dateTypeName,
  decimalTypeName,
  integerTypeName,
  longTypeName,
  stringTypeName,
  timeTypeName,
} from "@/cql/cql-value";

export function CqlValue({ value }: { value: TJsCqlValue }) {
  return (
    <Fragment>
      {formatCqlValue(value)}
      <span
        style={{
          color: "#777",
          padding: "0 4px",
          fontSize: "80%",
        }}
      >
        {formatTypeLabel(value)}
      </span>
    </Fragment>
  );
}

function formatCqlValue(value: TJsCqlValue): React.ReactNode {
  if (value === null) {
    return "null";
  }

  switch (typeof value) {
    case "boolean":
      return <span style={{ color: "#e36209" }}>{String(value)}</span>;
    case "number":
      return <span style={{ color: "#005cc5" }}>{value}</span>;
    case "bigint":
      return <span style={{ color: "#005cc5" }}>{value}L</span>;
    case "string":
      return (
        <span style={{ color: "#032f62" }}>
          {"'"}
          {value}
          {"'"}
        </span>
      );
  }

  if (Array.isArray(value)) {
    return (
      <Fragment>
        {"{"}
        <div style={{ padding: "0 0 0 2ch" }}>
          {value.map((item, itemIndex) => (
            <div key={itemIndex}>
              <CqlValue value={item} />
              {itemIndex < value.length - 1 ? "," : ""}
            </div>
          ))}
        </div>
        {"}"}
      </Fragment>
    );
  }

  switch (value.type) {
    case "Decimal":
      return <span style={{ color: "#005cc5" }}>{value.value}</span>;
    case "Date":
    case "DateTime":
    case "Time":
      return <span style={{ color: "#116329" }}>{value.value}</span>;
    case "Interval":
      return (
        <Fragment>
          <span style={{ color: "#005cc5" }}>Interval</span>
          {value.lowClosed ? "[" : "("}
          <CqlValue value={value.low} />
          {", "}
          <CqlValue value={value.high} />
          {value.highClosed ? "]" : ")"}
        </Fragment>
      );
  }

  // structured types

  return (
    <Fragment>
      {value.qName || "Tuple"}
      {" {"}
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
      {"}"}
    </Fragment>
  );
}

function formatTypeLabel(value: TJsCqlValue): string {
  if (value === null) {
    return anyTypeName.toString();
  }

  switch (typeof value) {
    case "boolean":
      return booleanTypeName.toString();
    case "number":
      return integerTypeName.toString();
    case "bigint":
      return longTypeName.toString();
    case "string":
      return stringTypeName.toString();
  }

  if (Array.isArray(value)) {
    return ""; // List
  }

  switch (value.type) {
    case "Decimal":
      return decimalTypeName.toString();
    case "Date":
      return dateTypeName.toString();
    case "DateTime":
      return dateTimeTypeName.toString();
    case "Time":
      return timeTypeName.toString();
    case "Interval":
      return ""; // Interval
  }

  // structured types

  if (value.qName) {
    return value.qName;
  }

  return ""; // Tuple
}
