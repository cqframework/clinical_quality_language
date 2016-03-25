library Common version '1'

using QDM version '4.2'

context Patient

define function includesOrStartsDuring(P "Diagnosis", E "EncounterPerformed"):
  Interval[P."startDatetime", P."stopDatetime"] includes Interval[E."startDatetime", E."stopDatetime"]
    or P."startDatetime" in Interval[E."startDatetime", E."stopDatetime"]
