# Born in 1980
p1 = {
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
        "meta" :{ "profile" : ["cqf-patient"]},
        "resourceType" : "Patient",
        "identifier": [{ "value": "1" }],
        "name": {"given":["John"], "family": ["Smith"]},
        "gender": "M",
        "birthDate" : "1980-02-17T06:15"}
        }
  ]
}
module.exports.P1 = p1

# Born in 2007
p2 = {
  "resourceType": "Bundle",
  "id": "example1",
  "meta": {
    "versionId": "1",
    "lastUpdated": "2014-08-18T01:43:30Z"
  },
  "base": "http://example.com/base",
  "entry" : [
      "resource": {
      "resourceType" : "Patient",
      "meta" :{ "profile" : ["cqf-patient"]},
      "id" : "2",
      "identifier": [{ "value": "2" }],
      "name": {"given":["Sally"], "family": ["Smith"]},
      "gender": "F",
      "birthDate" : "2007-08-02T11:47"
      }]
}
module.exports.P2 = p2

# Acute Pharyngitis and ED/Ambulatory Visits
p3 = {
  "resourceType": "Bundle",
  "id": "example2",
  "meta": {
    "versionId": "1",
    "lastUpdated": "2014-08-18T01:43:30Z"
  },
  "base": "http://example.com/base",
  "entry": [
    { "resource":{
          "resourceType" : "Patient",
          "meta" :{ "profile" : ["cqf-patient"]},
          "id" : "3",
          "identifier": [{ "value": "3" }],
          "name": {"given":["Bob"], "family": ["Jones"]},
          "gender": "M",
          "birthDate" : "1974-07-12T11:15"
          }
    },
    { "resource": {
          "resourceType" : "Encounter",
          "id" : "http://cqframework.org/3/1",
          "meta" :{ "profile" : ["cqf-encounter"]},
          "identifier": [{ "value": "http://cqframework.org/3/1", "system": "http://cqframework.org" }],
          "class": "outpatient",
          "type": [{"coding":[{ "code": "185349003", "system": "2.16.840.1.113883.6.96", "version": "2013-09", "display": "Encounter for \"check-up\" (procedure)" }]}],
          "period": { "start": "1978-07-15T10:00", "end": "1978-07-15T10:45" }
          }
    }, 
    {"resource": {
          "resourceType" : "Condition",
          "id" : "http://cqframework.org/3/2",
          "meta" :{ "profile" : ["cqf-condition"]},
          "identifier": [{ "value": "http://cqframework.org/3/2", "system": "http://cqframework.org" }],
          "code": {"coding":[{ "code": "1532007", "system": "2.16.840.1.113883.6.96", "version": "2013-09", "display": "Viral pharyngitis (disorder)" }]},
          "onsetDateTime": "1982-03-12",
          "abatementDateTime": "1982-03-26",
          "issued": "1982-03-15T15:15:00"
          }
    }, 
    {"resource":{
          "resourceType" : "Encounter",
          "id" : "http://cqframework.org/3/3",
          "meta" :{ "profile" : ["cqf-encounter"]},
          "identifier": [{ "value": "http://cqframework.org/3/3", "system": "http://cqframework.org" }],
          "class": "outpatient" ,
          "type": [{"coding" : [{ "code": "406547006", "system": "2.16.840.1.113883.6.96", "version": "2013-09", "display": "Urgent follow-up (procedure)" }]}],
          "period": { "start": "1982-03-15T15:00", "end": "1982-03-15T15:30" }
          }
    }, {
      "resource":{
          "resourceType" : "Condition",
          "id" : "http://cqframework.org/3/4",
          "meta" :{ "profile" : ["cqf-condition"]},
          "identifier": [{ "value": "http://cqframework.org/3/4", "system": "http://cqframework.org" }],
          "code": {"coding":[{ "code": "109962001", "system": "2.16.840.1.113883.6.96", "version": "2013-09", "display": "Diffuse non-Hodgkin's lymphoma (disorder)" }]},
          "onsetDateTime": "2010-10-24",
          "issued": "2011-02-01T11:55:00"
          }
    },
    { "resource": {
          "resourceType" : "Encounter",
          "id" : "http://cqframework.org/3/5",
          "meta" :{ "profile" : ["cqf-encounter"]},
          "identifier": [{ "value": "http://cqframework.org/3/5", "system": "http://cqframework.org" }],
          "class": "outpatient",
          "type": [{"coding" :[{ "code": "185349003", "system": "2.16.840.1.113883.6.96", "version": "2013-09", "display": "Encounter for \"check-up\" (procedure)" }]}],
          "period": { "start": "2013-05-23T10:00", "end": "2013-05-23T11:00" }
          }
    }
  ]
}
module.exports.P3 = p3

# ED/Ambulatory Visit
p4 = {
  "resourceType": "Bundle",
  "id": "example3",
  "meta": {
    "versionId": "1",
    "lastUpdated": "2014-08-18T01:43:30Z"
  },
  "base": "http://example.com/base",
  "entry": [
    {
    "resource": {
        "resourceType" : "Patient",
        "meta" :{ "profile" : ["cqf-patient"]},
        "id" : "4",
        "identifier": [{ "value": "4" }],
        "name": {"given":["Jane"], "family": ["Jones"]},
        "gender": "F",
        "birthDate" : "1976-11-09T14:12"
      }
    },
    {
      "resource": {
        "resourceType" : "Encounter",
        "id" : "http://cqframework.org/4/1",
        "meta" :{ "profile" : ["cqf-encounter"]},
        "identifier": [{ "value": "http://cqframework.org/4/1", "system": "http://cqframework.org" }],
        "class": "outpatient",
        "type" : [{"coding" : [{ "code": "439708006", "system": "2.16.840.1.113883.6.96", "version": "2013-09", "display": "Home visit (procedure)" }]}],
        "period": { "start": "1985-05-01T13:00", "end": "1985-05-01T14:00" }
      }
    }
  ]
}
module.exports.P4 = p4

module.exports.P1AndP2 = [p1, p2]