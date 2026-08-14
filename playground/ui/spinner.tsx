import React from "react";

export function Spinner({ active }: { active: boolean }) {
  return (
    <img
      src={
        "https://raw.githubusercontent.com/n3r4zzurr0/svg-spinners/refs/heads/main/svg-css/90-ring-with-bg.svg"
      }
      style={{
        display: "block",
        width: 32,
        height: 32,
        position: "absolute",
        top: 0,
        left: "50%",
        transform: active ? "translate(-50%, 15px)" : "translate(-50%, 0)",
        opacity: active ? 1 : 0,
        transition: "opacity 0.2s, transform 0.2s",
        pointerEvents: "none",
        background: "#fff",
        padding: 7,
        borderRadius: "50%",
        boxShadow: "0 2px 4px rgba(0, 0, 0, 0.2)",
      }}
    />
  );
}
