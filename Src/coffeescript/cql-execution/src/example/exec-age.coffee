cql = require '../cql'
measure = require './age'

lib = new cql.Library(measure)
psource = new cql.PatientSource [ {
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
ctx = new cql.Context(lib, psource)

result = lib.exec(ctx)
console.log JSON.stringify(result, undefined, 2)
