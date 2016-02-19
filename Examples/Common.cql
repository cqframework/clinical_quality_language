library Common version '1'

using QDM

context Patient

define function includesOrStartsDuring(P "Diagnosis, Active", E "Encounter, Performed"):
  Interval[P."start datetime", P."stop datetime"] includes Interval[E."start datetime", E."stop datetime"]
    or P."start datetime" in Interval[E."start datetime", E."stop datetime"]
