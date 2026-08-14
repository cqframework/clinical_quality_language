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
function AstNode({ node }: { node: any }) {
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
    const inlineFields =
      dataKeys.length === 0 ||
      (dataKeys.length === 1 && dataKeys[0] === "parts");
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
            <span
              style={{
                color: "#777",
                padding: "0 4px",
                fontSize: "80%",
              }}
            >
              {node.locator.line}:{node.locator.column} (
              {node.locator.startIndex}-{node.locator.stopIndex})
            </span>
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
