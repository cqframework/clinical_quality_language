import type { NextConfig } from "next";
import { DefinePlugin } from "webpack";

const nextConfig: NextConfig = {
  basePath: "/cql-to-elm-ui",
  output: "export",
  reactStrictMode: false,
  webpack: (config, { isServer }) => {
    // Adjust config to make Next.js work with cql-all-cql-to-elm-wasm-js
    config.ignoreWarnings = [
      /Accessing import\.meta directly is unsupported \(only property access or destructuring is supported\)/,
      /The generated code contains 'async\/await' because this module is using "topLevelAwait"/,
    ];

    if (isServer) {
      config.plugins.push(
        new DefinePlugin({
          "import.meta": DefinePlugin.runtimeValue((arg0) => {
            if (arg0.module.resource.includes("cql-all-cql-to-elm-wasm-js")) {
              // Get WASM files from node_modules/cql-all-cql-to-elm-wasm-js/kotlin
              return "{ url: __filename, resolve: (_) => url.pathToFileURL('node_modules/cql-all-cql-to-elm-wasm-js/kotlin/' + _) }";
            }
            return "import.meta";
          }),
        }),
      );
    } else {
      config.plugins.push(
        new DefinePlugin({
          "process.release.name": "'browser'",
        }),
      );
    }

    // Patch the `isNodeJs` function inside the ANTLR Kotlin runtime to make it work in a web worker
    config.module.rules.push(
      {
        test: /cql-all-cql-to-elm\/kotlin\/antlr-kotlin-antlr-kotlin-runtime\.mjs$/,
        loader: "string-replace-loader",
        options: {
          search: `function isNodeJs() {
  return typeof process !== 'undefined' && process.versions != null && process.versions.node != null || (typeof window !== 'undefined' && typeof window.process !== 'undefined' && window.process.versions != null && window.process.versions.node != null);
}
`,
          replace: `function isNodeJs() {
  return typeof process !== 'undefined';
}
`,
        },
      },
      {
        test: /cql-all-cql-to-elm-wasm-js\/kotlin\/cql-all-cql-to-elm-wasm-js\.uninstantiated\.mjs$/,
        loader: "string-replace-loader",
        options: {
          search: `        'com.strumenta.antlrkotlin.runtime.isNodeJs' : () => 
            (typeof process !== 'undefined'
              && process.versions != null
              && process.versions.node != null) ||
            (typeof window !== 'undefined'
              && typeof window.process !== 'undefined'
              && window.process.versions != null
              && window.process.versions.node != null)
            ,
`,
          replace: `        'com.strumenta.antlrkotlin.runtime.isNodeJs' : () => typeof process !== 'undefined',
`,
        },
      },
    );

    return config;
  },
};

export default nextConfig;
