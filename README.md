[![Maven Central](https://img.shields.io/maven-central/v/org.cqframework/cql?color=blue)](https://central.sonatype.com/artifact/org.cqframework/cql) [![NPM](https://img.shields.io/npm/v/%40cqframework%2Fcql)](https://www.npmjs.com/package/@cqframework/cql) [![project chat](https://img.shields.io/badge/zulip-join_chat-brightgreen.svg)](https://chat.fhir.org/#narrow/stream/179220-cql) [![Contributor Covenant](https://img.shields.io/badge/Contributor%20Covenant-2.1-4baaaa.svg)](code_of_conduct.md) ![Dynamic JSON Badge](https://shields.foundry.hl7.org/badge/dynamic/json?url=https%3A%2F%2Fcql-tests-results.quality.hl7.org%2Flatest-cqf-java-results.json&query=testResultsSummary.passCount&suffix=%20passing&label=CQL%20Tests&color=green)

# Clinical Quality Language

Clinical Quality Language ([CQL](http://www.hl7.org/implement/standards/product_brief.cfm?product_id=400)) is a Health Level 7 ([HL7](http://www.hl7.org/index.cfm)) standard for the expression of clinical knowledge that can be used within a broad range of clinical domains, including Clinical Decision Support (CDS), and Clinical Quality Measurement (CQM).

This repository contains documentation, examples, and tooling in support of the [CQL specification](https://cql.hl7.org/), including a CQL compiler and ELM runtime.

* [CQL Specification](https://cql.hl7.org/) - Specification for CQL, along with an Authoring Guide and a Developer's guide
* [Getting Started](https://github.com/cqframework/CQL-Formatting-and-Usage-Wiki/wiki/Getting-Started) - A collection of resources to help new users get started
* [Cooking with CQL Examples](https://github.com/cqframework/CQL-Formatting-and-Usage-Wiki/wiki/Cooking-with-CQL-Examples) - Examples from Cooking with CQL sessions

# Background

CQL was developed as part of the Clinical Quality Framework ([CQF](https://oncprojectracking.healthit.gov/wiki/display/TechLabSC/CQF+Home)) initiative, a public-private partnership sponsored by the Centers for Medicare & Medicaid Services (CMS) and the U.S. Office of the National Coordinator for Health Information Technology (ONC) to identify, develop, and harmonize standards for clinical decision support and electronic clinical quality measurement.

The Clinical Quality Language specification is maintained by the HL7 Clinical Decision Support ([CDS](http://www.hl7.org/Special/committees/dss/index.cfm)) Work Group with co-sponsorship from the HL7 Clinical Quality Information ([CQI](http://www.hl7.org/Special/committees/cqi/index.cfm)) Work Group.

# Scope

The primary focus of the tooling in this repository is to support and enable adoption and implementation of the Clinical Quality Language specification. In particular, the CQL-to-ELM translator provides a reference implementation for syntactic and semantic validation of Clinical Quality Language. As such, the features and functionality provided by these tools are ultimately defined by the CQL specification, and that specification is the source-of-truth for those requirements. This relationship to the CQL standard heavily informs and shapes the change management policies for maintenance of these tools.

# Community

The CQL community consists of multiple stakeholders including EHR vendors, clinical knowledge content vendors, knowledge artifact authors, and clinical quality content tool vendors. We encourage participation from these and other relevant stakeholders, and the processes and policies described here are intended to enable participation and support of the CQL tooling. Because this community of stakeholders both depends on and provides feedback to these tools, their participation in these processes is vital.

# Change Management

Changes to the tooling maintained within this repository are managed using as lightweight a process as possible, while still ensuring stable, viable, production quality software. These processes are described in the [Change Management](CHANGE_MANAGEMENT.md) topic.

# Project Structure

This is a multi-module project for CQL Kotlin and Java libraries and tooling applications.
It contains the following sub-projects:

* **cql:** generates and builds Kotlin lexers, parsers, listeners, and visitors using the CQL ANTLR4 grammar; generates and builds Kotlin classes based on the ELM Model Info schema and CQL base type system
* **elm:** generates and builds Kotlin classes based on the ELM XML schema
* **shared:** contains shared classes used by other modules
* **elm-fhir:** contains data requirements processor and FHIR-related utilities
* **engine:** contains the ELM runtime (aka "CQL engine")
* **engine-fhir:** contains FHIR-related components for the ELM runtime
* **qdm:** contains schema and model info resources for QDM
* **quick:** contains schema and model info resources for QUICK, FHIR, QI-Core, US Core, as well as the FHIRHelpers library
* **cql-to-elm:** generates Expression Logical Model (ELM) XML and JSON from CQL source
* **cql-to-elm-cli:** provides command-line access to the CQL-to-ELM translator
* **ucum:** provides the default UCUM service
* **tools:cql-formatter:** formats input CQL based on standard formatting conventions as suggested by the CQL specification
* **tools:cql-parsetree:** provides simple command-line access to the debug information for a CQL parse tree
* **tools:rewrite:** contains [OpenRewrite](https://openrewrite.org/) recipes for automated refactoring.
* **tools:xsd-to-modelinfo:** generates model info given an XML Schema (XSD) as input

# Cloning This Repository

Some of the tests in this repository have long paths which require this setting for git on Windows:

`git config --system core.longpaths true`


# Building the Project

This project uses the [Gradle](http://www.gradle.org/) build system.  A gradle wrapper, which automatically downloads
and uses an instance of gradle, is provided for convenience.  To build the project, install the [JDK 17](https://adoptium.net/temurin/releases/?version=17), clone this
repository, then execute this command from within this directory:

    ./gradlew build

This will generate and build the ANTLR4 CQL artifacts and the XSD-based ELM library and model info artifacts.
It will also build and test the corresponding code libraries and applications.

To clean up the build artifacts:

    ./gradlew clean

# Versioning

The project version is derived from git state at configuration time — there is no hardcoded
`version` in `gradle.properties`. The logic lives in
[`build-logic/src/main/kotlin/GitVersion.kt`](build-logic/src/main/kotlin/GitVersion.kt) and is
applied by the `cqf.git-version` precompiled plugin (a shared convention plugin vendored in from
[`home/shared/gradle/conventions/`](https://gitlab.com/simpatico.ai/clinical-intelligence/shared)).
The resolved version is logged once per build (`Resolved project version: ...`).

Resolution rules, in order:

1. **Release** — if HEAD is tagged `vX.Y.Z` (optionally with a suffix), the version is the tag with
   the `v` stripped. Example: `v4.6.0` → `4.6.0`. A non-SNAPSHOT version triggers artifact signing
   in the maven-publish convention.
2. **`main` SNAPSHOT** — if on `main` with no tag at HEAD, take the most recent `vX.Y.Z` tag
   reachable from HEAD, bump the minor component, reset patch to 0, and append `-SNAPSHOT`.
   Example: last tag `v4.6.0` → `4.7.0-SNAPSHOT`.
3. **Branch SNAPSHOT** — any other branch (or detached HEAD) gets a per-branch identifier:
   `<bumped>-<sanitized-branch>-<short-sha>-SNAPSHOT`. Example:
   `4.7.0-my-feature-5ac419a5d-SNAPSHOT`. The branch name comes from `GITHUB_REF_NAME` (or
   `CI_COMMIT_REF_NAME`) if set, otherwise from `git rev-parse --abbrev-ref HEAD`; detached HEAD
   falls back to `detached`. The name is sanitized (non-alphanumerics → `-`, capped at 40 chars)
   before being embedded.

To cut a release, push a tag of the form `vX.Y.Z` at the commit you want to release; the build on
that commit will produce `X.Y.Z` and the publish step will sign the artifacts.

# Executing the Sample Code

You can execute the sample code using the `gradlew` command or a script generated by gradle.

To execute the sample code using `gradlew`, you must execute the `run` command, using the project-specific
path.  The following are current possibilities:

    ./gradlew :cql-to-elm-cli:run
    ./gradlew :tools:cql-parsetree:run

To execute the sample code using a script generated by gradle, first generate the scripts:

    ./gradlew installDist

Then execute the generated script (optionally passing in an argument).  The following example executes
the sample cql code with the path to a CQL file as an argument:

    ./cql/build/install/cql/bin/cql ./examples/CMS135_QDM.cql

You can also execute the cql-parsetree tool in a similar way:

    ./tools/cql-parsetree/build/install/cql-parsetree/bin/cql-parsetree ./examples/CMS135_QDM.cql

## Generate an ELM Representation of CQL Logic

To generate an ELM representation of CQL logic, build and execute the cql-to-elm app:

    ./gradlew :cql-to-elm-cli:installDist
    ./cql-to-elm-cli/build/install/cql-to-elm-cli/bin/cql-to-elm-cli --input ./examples/CMS135_QDM.cql

The following options are supported:

* `--input` or `-i`: Specify the input CQL file (REQUIRED).
* `--output` or `-o`: Specify the output file.  If not specified, the output file will have the
  same base name and location as the input file.  If only a directory is specified, the output
  file will have the same base name as the input file and be written to the requested directory.
* `--format` or `-f`: Output as `XML` (default), `JSON`, or `COFFEE`.
* _[more...](cql-to-elm/OVERVIEW.md)_

For a complete list of supported options as well as more information on the CQL-to-ELM translator, refer to the [Overview](cql-to-elm/OVERVIEW.md) for that project.

# Additional Developer Documentation

See the docs [README](docs/README.md)

# License

All code in this repository is licensed under the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0). All documentation is licensed under the [Creative Common Attribution 4.0 International license (CC BY 4.0)](https://creativecommons.org/licenses/by/4.0/).

CQL compiler/translator, grammar, and associated tooling:
Copyright 2014 The MITRE Corporation

ELM runtime and associated components (specifically the `java/engine` and `java/engine-fhir` modules):
Copyright 2016 University of Utah

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
