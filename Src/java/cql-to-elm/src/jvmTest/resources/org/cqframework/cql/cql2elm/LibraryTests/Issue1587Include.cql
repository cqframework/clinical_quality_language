// See Issue1587.cql for context
library Issue1587Include

using FHIR version '4.0.1'

codesystem "Codes": 'http://example.org/codesystem/test-codesystem'
private codesystem "PrivateCodes": 'http://example.org/codesystem/test-codesystem-private'