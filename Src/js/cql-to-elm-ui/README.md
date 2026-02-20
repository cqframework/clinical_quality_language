# CQL-to-ELM UI

**Live URL:** https://www.cqframework.org/clinical_quality_language/playground/

A front end for the Kotlin/JS-based CQL compiler.

## Local development

Build the CQL compiler locally by running the `:cql-to-elm:build` task in the `../../java` project.
This will output the cql-all-cql-to-elm and cql-all-cql-to-elm-wasm-js packages to the
`../../java/build/js/packages` directory.

Run:

    npm install -f
    npm run dev

And open `http://localhost:3000/clinical_quality_language/playground` in your browser.

## Deployment

This project is deployed to GitHub Pages (the `gh-pages` branch) automatically from the `main` branch.
