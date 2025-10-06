export function Heading({ children }: { children: React.ReactNode }) {
  return (
    <h2
      style={{
        fontSize: 24,
        fontWeight: 300,
        margin: "0 0 18px 0",
        color: "#777",
      }}
    >
      {children}
    </h2>
  );
}
