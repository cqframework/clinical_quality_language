# For backwards compatibility until FHIR models change references
DT = require "./datatypes/datatypes"

for element in Object.keys(DT)
  module.exports[element] = DT[element]
