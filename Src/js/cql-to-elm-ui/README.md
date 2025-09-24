# CQL-to-ELM UI

A front end for the Kotlin/JS-based CQL compiler.

## Local development

Build the CQL compiler locally by running the `:cql-to-elm:build` task in the `../../java` project.
This will output the cql-all-cql-to-elm and cql-all-cql-to-elm-wasm-js packages to the
`../../java/build/js/packages` directory.

Run:

    npm install -f
    npm run dev

And open `http://localhost:3000/cql-to-elm-ui` in your browser.
