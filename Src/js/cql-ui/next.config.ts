import type { NextConfig } from "next";
import { DefinePlugin } from "webpack";

const nextConfig: NextConfig = {
  reactStrictMode: false,
  transpilePackages: ["cql-all-cql-wasm-js"],
  webpack: (config, { isServer }) => {
    config.ignoreWarnings = [
      /Accessing import\.meta directly is unsupported \(only property access or destructuring is supported\)/,
      /The generated code contains 'async\/await' because this module is using "topLevelAwait"/,
    ];

    if (isServer) {
      config.plugins.push(
        new DefinePlugin({
          "import.meta":
            "{ url: __filename, resolve: (_) => url.pathToFileURL('node_modules/cql-all-cql-wasm-js/kotlin/' + _) }",
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
