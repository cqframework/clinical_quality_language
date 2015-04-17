# Acute Pharyngitis and ED/Ambulatory Visits
module.exports.p1 = {
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
          "meta" :{ "profile" : ["patient-qicore-qicore-patient"]},
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
          "meta" :{ "profile" : ["encounter-qicore-qicore-encounter"]},
          "identifier": [{ "value": "http://cqframework.org/3/1", "system": "http://cqframework.org" }],
          "class": "outpatient",
          "type": [{"coding":[{ "code": "185349003", "system": "2.16.840.1.113883.6.96", "version": "2013-09", "display": "Encounter for \"check-up\" (procedure)" }]}],
          "period": { "start": "1978-07-15T10:00", "end": "1978-07-15T10:45" }
          }
    },
    {"resource": {
          "resourceType" : "Condition",
          "id" : "http://cqframework.org/3/2",
          "meta" :{ "profile" : ["condition-qicore-qicore-condition"]},
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
          "meta" :{ "profile" : ["encounter-qicore-qicore-encounter"]},
          "identifier": [{ "value": "http://cqframework.org/3/3", "system": "http://cqframework.org" }],
          "class": "outpatient" ,
          "type": [{"coding" : [{ "code": "406547006", "system": "2.16.840.1.113883.6.96", "version": "2013-09", "display": "Urgent follow-up (procedure)" }]}],
          "period": { "start": "1982-03-15T15:00", "end": "1982-03-15T15:30" }
          }
    }, {
      "resource":{
          "resourceType" : "Condition",
          "id" : "http://cqframework.org/3/4",
          "meta" :{ "profile" : ["condition-qicore-qicore-condition"]},
          "identifier": [{ "value": "http://cqframework.org/3/4", "system": "http://cqframework.org" }],
          "code": {"coding":[{ "code": "109962001", "system": "2.16.840.1.113883.6.96", "version": "2013-09", "display": "Diffuse non-Hodgkin's lymphoma (disorder)" }]},
          "onsetDateTime": "2010-10-24",
          "issued": "2011-02-01T11:55:00"
          }
    },
    { "resource": {
          "resourceType" : "Encounter",
          "id" : "http://cqframework.org/3/5",
          "meta" :{ "profile" : ["encounter-qicore-qicore-encounter"]},
          "identifier": [{ "value": "http://cqframework.org/3/5", "system": "http://cqframework.org" }],
          "class": "outpatient",
          "type": [{"coding" :[{ "code": "185349003", "system": "2.16.840.1.113883.6.96", "version": "2013-09", "display": "Encounter for \"check-up\" (procedure)" }]}],
          "period": { "start": "2013-05-23T10:00", "end": "2013-05-23T11:00" }
          }
    }
  ]
}
