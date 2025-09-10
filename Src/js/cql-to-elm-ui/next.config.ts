import type { NextConfig } from "next";

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

    config.module.rules.push({
      test: /wasm\/packages\/cql-all-cql-to-elm\/kotlin\/cql-all-cql-to-elm\.uninstantiated\.mjs$/,
      loader: "string-replace-loader",
      options: {
        multiple: [
          {
            search: `const module = await import(/* webpackIgnore: true */'node:module');
        const importMeta = import.meta;
        require = module.default.createRequire(importMeta.url);
        const fs = require('fs');
        const url = require('url');
        const filepath = import.meta.resolve(wasmFilePath);
        const wasmBuffer = fs.readFileSync(url.fileURLToPath(filepath));
`,
            replace: `const module = await import(/* webpackIgnore: true */'node:module');
        require = module.default.createRequire(__filename);
        const fs = require('fs');
        const wasmBuffer = fs.readFileSync('node_modules/cql-all-cql-to-elm-wasm-js/kotlin/cql-all-cql-to-elm.wasm');
`,
          },
          {
            search: `const isNodeJs = (typeof process !== 'undefined') && (process.release.name === 'node');`,
            replace: `const isNodeJs = ${isServer};`,
          },
        ],
      },
    });

    // Patch the `isNodeJs` function inside the ANTLR Kotlin runtime to make it work in a web worker
    config.module.rules.push(
      {
        test: /js\/packages\/cql-all-cql-to-elm\/kotlin\/antlr-kotlin-antlr-kotlin-runtime\.mjs$/,
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
        test: /wasm\/packages\/cql-all-cql-to-elm\/kotlin\/cql-all-cql-to-elm\.uninstantiated\.mjs$/,
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
