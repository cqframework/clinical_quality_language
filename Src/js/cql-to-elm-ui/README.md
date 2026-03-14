# CQL Playground

**Live URL:** https://www.cqframework.org/clinical_quality_language/playground/

A playground for the Kotlin/JS-based CQL compiler and engine.

## Local development

Build the CQL engine and its dependencies locally by running the `:engine:build` task in the `../../java` project.
This will output the JS and WASM/JS packages to `../../java/build/(js|wasm)/packages/engine`.

Run:

    npm install -f
    npm run dev

And open `http://localhost:3000/clinical_quality_language/playground` in your browser.

## Deployment

This project is deployed to GitHub Pages (the `gh-pages` branch) automatically from the `main` branch.
