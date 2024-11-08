# CQL Kotlin UI

A front end for the CQL parser written in Kotlin.

## Local development

The Kotlin/JS-based CQL parser is in development, so you'll need to build it locally by running the `cql:build` task in
the `../../java` project. This will output the cql-all-cql package to the `../../java/build/js/packages/cql-all-cql`
directory.

To then run this project locally:

    npm install
    npm run dev
