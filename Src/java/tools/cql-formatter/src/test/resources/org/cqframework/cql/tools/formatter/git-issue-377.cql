// https://github.com/cqframework/clinical_quality_language/issues/377
// Issue:   "end" triggering new line.
// Fix:     Adjusted condition to account for inFunctionInvocation() || inFunctionDefinition()

define "Issue377":
        exists ( ["Diagnostic Study, Performed": "Mammography"] Mammogram
        where Mammogram.result is not null
        and Mammogram.relevantPeriod ends 26 months or less on or before end of "Measurement Period"
        )