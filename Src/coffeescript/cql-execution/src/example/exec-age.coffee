{ Library, Context } = require '../cql-exec'
{ PatientSource } = require '../cql-patient'
measure = require './age'

lib = new Library(measure)
psource = new PatientSource [ {
    "identifier": { "value": "1" },
    "name": "John Smith",
    "gender": "M",
    "birthDate" : "1980-02-17T06:15",
  }, {
    "identifier": { "value": "2" },
    "name": "Sally Smith",
    "gender": "F",
    "birthDate" : "2007-08-02T11:47",
  } ]
ctx = new Context(lib, psource)

result = lib.exec(ctx)
console.log JSON.stringify(result, undefined, 2)
