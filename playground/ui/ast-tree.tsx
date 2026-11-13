import { Fragment } from "react";

export function AstTree({ ast }: { ast: unknown }) {
  return (
    <div
      style={{
        fontFamily: "var(--monospace-font-family)",
        fontSize: "90%",
      }}
    >
      <AstNode node={ast} />
    </div>
  );
}

// eslint-disable-next-line @typescript-eslint/no-explicit-any
type TNode = any;

/** An identifier, `{ value, style?, locator }`: the only object with a string `value` and no `kind`. */
function isIdentifier(node: TNode): boolean {
  return (
    typeof node === "object" &&
    node !== null &&
    !Array.isArray(node) &&
    !("kind" in node) &&
    typeof node.value === "string" &&
    Object.keys(node).every((key) =>
      ["value", "style", "locator"].includes(key),
    )
  );
}

/** A qualified identifier, `{ parts, locator }`, such as `FHIR.Patient`. */
function isQualifiedIdentifier(node: TNode): boolean {
  return (
    typeof node === "object" &&
    node !== null &&
    Array.isArray(node.parts) &&
    node.parts.every(isIdentifier) &&
    Object.keys(node).every((key) => ["parts", "locator"].includes(key))
  );
}

/** The identifier as written in the source, with its quotes. */
function writtenIdentifier(identifier: TNode): string {
  switch (identifier.style) {
    case "quoted":
      return `"${identifier.value}"`;
    case "delimited":
      return `\`${identifier.value}\``;
    default:
      return identifier.value;
  }
}

function Locator({ locator }: { locator: TNode }) {
  return (
    <span
      style={{
        color: "#777",
        padding: "0 4px",
        fontSize: "80%",
      }}
    >
      {locator.line}:{locator.column} ({locator.startIndex}-{locator.stopIndex})
    </span>
  );
}

function AstNode({ node }: { node: TNode }) {
  if (isIdentifier(node) || isQualifiedIdentifier(node)) {
    const parts = isIdentifier(node) ? [node] : node.parts;
    return (
      <Fragment>
        <span style={{ color: "#24292e" }}>
          {parts.map(writtenIdentifier).join(".")}
        </span>
        {node.locator && <Locator locator={node.locator} />}
      </Fragment>
    );
  }
  if (typeof node === "string") {
    return (
      <span style={{ color: "#24292e" }}>
        {'"'}
        {node}
        {'"'}
      </span>
    );
  }
  if (typeof node === "number") {
    return <span style={{ color: "#032f62" }}>{node}</span>;
  }
  if (typeof node === "boolean") {
    return <span style={{ color: "#032f62" }}>{String(node)}</span>;
  }
  if (node === null) {
    return <span style={{ color: "#032f62" }}>null</span>;
  }
  if (Array.isArray(node)) {
    const inline =
      node.length === 0 || node.every((_) => typeof _ === "string");
    if (inline) {
      return (
        <Fragment>
          [{" "}
          {node.map((item, itemIndex) => (
            <span key={itemIndex}>
              {itemIndex > 0 ? ", " : ""}
              <AstNode node={item} />
            </span>
          ))}{" "}
          ]
        </Fragment>
      );
    }
    return (
      <Fragment>
        [
        <div style={{ padding: "0 0 0 2ch" }}>
          {node.map((item, itemIndex) => (
            <div key={itemIndex}>
              <AstNode node={item} />
            </div>
          ))}
        </div>
        ]
      </Fragment>
    );
  }
  if (typeof node === "object") {
    const dataKeys = Object.keys(node).filter(
      (key) => !["kind", "locator"].includes(key),
    );
    const inlineFields = dataKeys.length === 0;
    const inner = dataKeys.map((key) => {
      const Wrapper = inlineFields ? Fragment : "div";
      return (
        <Wrapper key={key}>
          <span
            style={{
              color: "rgb(101,131,0)",
            }}
          >
            {key}:
          </span>{" "}
          <AstNode node={node[key]} />
        </Wrapper>
      );
    });
    return (
      <Fragment>
        {node.kind && (
          <Fragment>
            <b style={{ color: "rgb(149, 56, 0)" }}>{node.kind}</b>{" "}
          </Fragment>
        )}
        {"{"}
        {node.locator && (
          <Fragment>
            {" "}
            <Locator locator={node.locator} />
          </Fragment>
        )}
        {inlineFields ? (
          <Fragment> {inner} </Fragment>
        ) : (
          <div style={{ padding: "0 0 0 2ch" }}>{inner}</div>
        )}

        {"}"}
      </Fragment>
    );
  }
  return <b>Bad node type</b>;
}
