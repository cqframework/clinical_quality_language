cql = require '../cql'
measure = require './age'

lib = new cql.Library(measure)
psource = new cql.PatientSource [ {
    "resourceType": "Bundle",
    "id": "example1",
    "meta": {
      "versionId": "1",
      "lastUpdated": "2014-08-18T01:43:30Z"
    },
    "base": "http://example.com/base",
    "entry" : [{
        "resource": {
        "id" : "1",
        "meta" :{ "profile" : ["patient-qicore-qicore-patient"]},
        "resourceType" : "Patient",
        "identifier": [{ "value": "1" }],
        "name": {"given":["John"], "family": ["Smith"]},
        "gender": "M",
        "birthDate" : "1980-02-17T06:15"}
        }
    ]
  }, {
    "resourceType": "Bundle",
    "id": "example1",
    "meta": {
      "versionId": "1",
      "lastUpdated": "2014-08-18T01:43:30Z"
    },
    "base": "http://example.com/base",
    "entry" : [{
        "resource": {
        "id" : "2",
        "meta" :{ "profile" : ["patient-qicore-qicore-patient"]},
        "resourceType" : "Patient",
        "identifier": [{ "value": "2" }],
        "name": {"given":["Sally"], "family": ["Smith"]},
        "gender": "F",
        "birthDate" : "2007-08-02T11:47"}
        }
    ]
  } ]

ctx = new cql.Context(lib, psource)

result = lib.exec(ctx)
console.log JSON.stringify(result, undefined, 2)
