import type { Metadata } from "next";
import "./globals.css";
import { Roboto } from "next/font/google";

const roboto = Roboto({
  weight: ["300", "400", "700"],
  subsets: ["latin"],
});

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
    <html lang={"en"} className={roboto.className}>
      <body>{children}</body>
    </html>
  );
}
