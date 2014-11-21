library Common version '1'

using QUICK

context Patient

define Patient = tuple from [Patient]
define Patient = [Patient][]

define function CalculateAgeInYearsAt(birthDate: DateTime, asOf: DateTime)
{
    return ...
}

define function CalculateAgeInYears(birthDate: DateTime)
{
    return CalculateAgeInYearsAt(birthDate, Today())
}

define function AgeInYears()
{
    return CalculateAgeInYears(Patient.birthDate)
}

