QUICK = require("./quick/models")
class PatientExtention extends QUICK.Patient
  constructor: (@json) ->
    super(@json)
    @records = @json.records

  findRecords: (type) ->
    @records


QUICK.Patient= PatientExtention
module.exports = QUICK  