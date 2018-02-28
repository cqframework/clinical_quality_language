# Clinical Quality Language
Clinical Quality Language ([CQL](http://www.hl7.org/implement/standards/product_brief.cfm?product_id=400)) is a Health Level 7 ([HL7](http://www.hl7.org/index.cfm)) standard for the expression of clinical knowledge that can be used within a broad range of clinical domains, including Clinical Decision Support (CDS), and Clinical Quality Measurement (CQM).

This repository contains documentation, examples, and tooling in support of the CQL specification, including a CQL verifier/translator, and a JavaScript CQL evaluation engine.

# Background
CQL was developed as part of the Clinical Quality Framework ([CQF](https://oncprojectracking.healthit.gov/wiki/display/TechLabSC/CQF+Home)) initiative, a public-private partnership sponsored by the Centers for Medicare & Medicaid Services (CMS) and the U.S. Office of the National Coordinator for Health Information Technology (ONC) to identify, develop, and harmonize standards for clinical decision support and electronic clinical quality measurement.

The Clinical Quality Language specification is maintained by the HL7 Clinical Decision Support ([CDS](http://www.hl7.org/Special/committees/dss/index.cfm)) Work Group with co-sponsorship from the HL7 Clinical Quality Information ([CQI](http://www.hl7.org/Special/committees/cqi/index.cfm)) Work Group.

# Scope
The primary focus of the tooling in this repository is to support and enable adoption and implementation of the Clinical Quality Language specification. In particular, the CQL-to-ELM translator provides a reference implementation for syntactic and semantic validation of Clinical Quality Language. As such, the features and functionality provided by these tools are ultimately defined by the CQL specification, and that specification is the source-of-truth for those requirements. This relationship to the CQL standard heavily informs and shapes the change management policies for maintenance of these tools. 

# Community
The CQL community consists of multiple stakeholders including EHR vendors, clinical knowledge content vendors, knowledge artifact authors, and clinical quality content tool vendors. We encourage participation from these and other relevant stakeholders, and the processes and policies described here are intended to enable participation and support of the CQL tooling. Because this community of stakeholders both depends on and provides feedback to these tools, their participation in these processes is vital.

# Change Management
Changes to the tooling maintained within this repository are managed using as lightweight a process as possible, while still ensuring stable, viable, production quality software. These processes are described in the [Change Management](CHANGE_MANAGEMENT.md) topic.

# Contents

* [Java Tools for CQL](Src/java/README.md)
* [CQL Execution Framework](Src/coffeescript/cql-execution/README.md)
* [CQL-to-ELM Translator](Src/java/cql-to-elm/OVERVIEW.md)
* [Java Quickstart](Src/java-quickstart/README.md)

# License
All code in this repository is licensed under the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0). All documentation is licensed under the [Creative Common Attribution 4.0 International license (CC BY 4.0)](https://creativecommons.org/licenses/by/4.0/).

Copyright 2014 The MITRE Corporation

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
