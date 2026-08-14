export function Label({ children }: { children: React.ReactNode }) {
  return (
    <div
      style={{
        fontSize: 14,
        fontWeight: 700,
        margin: "0 0 10px 0",
        color: "#777",
      }}
    >
      {children}
    </div>
  );
}
