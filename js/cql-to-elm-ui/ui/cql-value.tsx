import React, { Fragment } from "react";
import { TJsCqlValue, TJsQName } from "@/shared";
import {
  quantityTypeName,
  ratioTypeName,
  systemModelNamespaceUri,
} from "cql-js/kotlin/engine.mjs";

export function CqlValue({
  value,
  showNullsInClassInstances,
  showTypeHints,
}: {
  value: TJsCqlValue;
  showNullsInClassInstances: boolean;
  showTypeHints: boolean;
}) {
  const typeHint = showTypeHints && getTypeHint(value);
  return (
    <Fragment>
      {formatCqlValue(value, showNullsInClassInstances, showTypeHints)}
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

function formatCqlValue(
  value: TJsCqlValue,
  showNullsInClassInstances: boolean,
  showTypeHints: boolean,
) {
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
      return <span style={{ color: "#116329" }}>{value.value}</span>;
    case "Time":
      return <span style={{ color: "#116329" }}>{value.value}</span>;
    case "Structured":
      if (
        value.structuredTypeQName?.namespaceUri ===
          systemModelNamespaceUri.get() &&
        value.structuredTypeQName?.localPart ===
          quantityTypeName.get().getLocalPart()
      ) {
        return (
          <Fragment>
            {formatCqlValue(
              value.elements.get("value")!,
              showNullsInClassInstances,
              showTypeHints,
            )}{" "}
            {formatCqlValue(
              value.elements.get("unit")!,
              showNullsInClassInstances,
              showTypeHints,
            )}
          </Fragment>
        );
      }
      if (
        value.structuredTypeQName?.namespaceUri ===
          systemModelNamespaceUri.get() &&
        value.structuredTypeQName?.localPart ===
          ratioTypeName.get().getLocalPart()
      ) {
        return (
          <Fragment>
            {formatCqlValue(
              value.elements.get("numerator")!,
              showNullsInClassInstances,
              showTypeHints,
            )}
            :
            {formatCqlValue(
              value.elements.get("denominator")!,
              showNullsInClassInstances,
              showTypeHints,
            )}
          </Fragment>
        );
      }
      const elementsEntriesFiltered =
        value.structuredTypeQName && !showNullsInClassInstances
          ? [...value.elements.entries()].filter(
              ([_, elementValue]) => elementValue !== null,
            )
          : [...value.elements.entries()];
      return (
        <Fragment>
          <span style={{ color: "#005cc5" }}>
            {value.structuredTypeQName
              ? formatTypeQName(value.structuredTypeQName)
              : "Tuple"}
          </span>
          {" {"}
          {elementsEntriesFiltered.length ? (
            <div style={{ padding: "0 0 0 2ch" }}>
              {elementsEntriesFiltered.map(
                ([elementName, elementValue], elementIndex, elements) => (
                  <div key={elementIndex}>
                    {elementName}:{" "}
                    <CqlValue
                      value={elementValue}
                      showNullsInClassInstances={showNullsInClassInstances}
                      showTypeHints={showTypeHints}
                    />
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
    case "Interval":
      return (
        <Fragment>
          <span style={{ color: "#005cc5" }}>Interval</span>
          {value.lowClosed ? "[" : "("}
          {formatCqlValue(
            value.low,
            showNullsInClassInstances,
            showTypeHints,
          )},{" "}
          {formatCqlValue(value.high, showNullsInClassInstances, showTypeHints)}
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
                  <CqlValue
                    value={item}
                    showNullsInClassInstances={showNullsInClassInstances}
                    showTypeHints={showTypeHints}
                  />
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
      return `Interval<${formatTypeQName(value.pointTypeQName)}>`;
    case "Structured":
      if (
        value.structuredTypeQName?.namespaceUri ===
          systemModelNamespaceUri.get() &&
        value.structuredTypeQName?.localPart ===
          quantityTypeName.get().getLocalPart()
      ) {
        return "Quantity";
      }
      if (
        value.structuredTypeQName?.namespaceUri ===
          systemModelNamespaceUri.get() &&
        value.structuredTypeQName?.localPart ===
          ratioTypeName.get().getLocalPart()
      ) {
        return "Ratio";
      }
      return "";
    case "List":
      return "";
  }
}

function formatTypeQName(typeQName: TJsQName) {
  if (typeQName.namespaceUri === systemModelNamespaceUri.get()) {
    return typeQName.localPart;
  }
  return `${typeQName.prefix}.${typeQName.localPart}`;
}
