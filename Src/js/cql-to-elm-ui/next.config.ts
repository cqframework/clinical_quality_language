import type { NextConfig } from "next";
import { DefinePlugin } from "webpack";

const nextConfig: NextConfig = {
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

    return config;
  },
};

export default nextConfig;
