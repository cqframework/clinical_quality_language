# CQL Playground

**Live URL:** https://www.cqframework.org/clinical_quality_language/playground/

A playground for the Kotlin/JS-based CQL compiler and engine.

## Local development

The `:playground:npmInstall` Gradle task builds and installs the dependencies,  including
the CQL compiler and engine.

To start the dev server, run

    ./gradlew :playground:npmRunDev

and open `http://localhost:3000/clinical_quality_language/playground`.

## Deployment

This project is deployed to GitHub Pages (the `gh-pages` branch) automatically from the `main` branch.
