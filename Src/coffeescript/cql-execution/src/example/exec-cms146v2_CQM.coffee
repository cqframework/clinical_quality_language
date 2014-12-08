cql = require '../cql'
codes = require '../cql-code-service'
measure = require './cms146v2_CQM'

cservice = new codes.CodeService {
    "1.2.3.4.5": {
      "1": [
        {
          "code": "ABC",
          "system": "5.4.3.2.1",
          "version": "1"
        }, {
          "code": "DEF",
          "system": "5.4.3.2.1",
          "version": "2"
        }, {
          "code": "GHI",
          "system": "5.4.3.4.5",
          "version": "3"
        }
      ],
      "2": [
        {
          "code": "ABC",
          "system": "5.4.3.2.1",
          "version": "1"
        }, {
          "code": "DEF",
          "system": "5.4.3.2.1",
          "version": "2"
        }, {
          "code": "JKL",
          "system": "5.4.3.2.1",
          "version": "3"
        }
      ]
    },
    "6.7.8.9.0": {
      "A": [
        {
          "code": "MNO",
          "system": "2.4.6.8.0",
          "version": "3"
        }, {
          "code": "PQR",
          "system": "2.4.6.8.0",
          "version": "2"
        }, {
          "code": "STU",
          "system": "2.4.6.8.0",
          "version": "1"
        }
      ]
    }
  }

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
ctx = new cql.Context(lib, psource, cservice)

result = lib.exec(ctx)
console.log JSON.stringify(result, undefined, 2)
