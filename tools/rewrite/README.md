# OpenRewrite

This module contains [OpenRewrite](https://openrewrite.org/) recipes for automated refactoring of the source code in this repository.

## Usage

To apply a recipe, run the `rewriteRun` gradle task on a specific module. Use the `activeRecipe` property to specify which recipe to run.

For example, to apply the `StringFormatToInterpolationRecipe` to the `engine-fhir` module, run the following command:

```shell
./gradlew :engine-fhir:rewriteRun -DactiveRecipe=internal.rewrite.StringFormatToInterpolationRecipe
```

