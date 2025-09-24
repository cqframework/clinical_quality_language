import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "CQL Compiler in Kotlin/JS - Demo",
  description: "CQL Compiler in Kotlin/JS - Demo",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang={"en"}>
      <body>{children}</body>
    </html>
  );
}
