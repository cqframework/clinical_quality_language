library Issue827

// https://github.com/cqframework/clinical_quality_language/issues/827

// The problem was that the escaped identifier was not resolving correctly
define "Referencing Expression":
    "This is the \"referenced\" expression"

define "This is the \"referenced\" expression":
    'The reference resolved!'