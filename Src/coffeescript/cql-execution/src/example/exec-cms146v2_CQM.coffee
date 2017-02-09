cql = require '../cql'
codes = require '../cql-code-service'
measure = require './CMS146v2_CQM'

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
parameters = {
  MeasurementPeriod: new cql.Interval(cql.DateTime.parse('2013-01-01'), cql.DateTime.parse('2014-01-01'), true, false)
}
executor = new cql.Executor(lib, cservice, parameters)
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

result = executor.exec(psource)
console.log JSON.stringify(result, undefined, 2)
