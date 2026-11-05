library Common version '1'

using QDM //version '4.2'

context Patient

define function includesOrStartsDuring(P "Diagnosis", E "Encounter, Performed"):
  Interval[P.onsetDatetime, P.abatementDatetime] includes Interval[E.admissionDatetime, E.dischargeDatetime]
    or P.onsetDatetime in Interval[E.admissionDatetime, E.dischargeDatetime]
