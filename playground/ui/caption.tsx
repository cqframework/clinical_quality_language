export function Caption({ children }: { children: React.ReactNode }) {
  return (
    <div
      style={{
        fontSize: 12,
        margin: "6px 0 0 0",
      }}
    >
      {children}
    </div>
  );
}
