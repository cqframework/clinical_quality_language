# CQL Kotlin UI

A front end for the Kotlin/JS- and Kotlin/WASM-based CQL parser.

## Local development

The Kotlin version of CQL tooling is in development, so you'll need to build it locally by running the `cql:build` task in
the `../../java` project. This will output the cql-all-cql package to the `../../java/build/js/packages/cql-all-cql`
directory.

To then run this project locally:

    npm install
    npm run dev
