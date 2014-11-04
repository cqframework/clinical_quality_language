{ Library, Context } = require '../cql-exec'
measure = require './age'

lib = new Library(measure)
ctx = new Context(lib)
ctx.withPatients [ {
    "id": 1,
    "name": "John Smith",
    "birthdate" : "1980-02-17T06:15"
  }, {
    "id": 2,
    "name": "Sally Smith",
    "birthdate" : "2007-08-02T11:47"
} ]

result = lib.exec(ctx)
console.log JSON.stringify(result, undefined, 2)
