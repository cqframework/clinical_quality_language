{ Library, Context } = require './cql-exec'
{ PatientSource } = require './cql-patient'
measure = require './age'

lib = new Library(measure)
psource = new PatientSource [ {
    "id": 1,
    "name": "John Smith",
    "gender": "M",
    "birthdate" : "1980-02-17T06:15",
  }, {
    "id": 2,
    "name": "Sally Smith",
    "gender": "F",
    "birthdate" : "2007-08-02T11:47",
  } ]
ctx = new Context(lib, psource)

result = lib.exec(ctx)
print JSON.stringify(result, undefined, 2)