library PropertyTest

using QUICK

valueset "Female Administrative Sex": '2.16.840.1.113883.3.560.100.2'

context Patient

define InDemographic:
    AgeInYears() >= 18
        and Patient.gender in "Female Administrative Sex"
