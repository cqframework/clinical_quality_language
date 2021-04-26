define "Issue377":
        exists ( ["Diagnostic Study, Performed": "Mammography"] Mammogram
        where Mammogram.result is not null
        and Mammogram.relevantPeriod ends 26 months or less on or before end of "Measurement Period"
        )