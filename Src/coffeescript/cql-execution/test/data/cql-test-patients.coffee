# Born in 1980
p1 = {
  "identifier": { "value": "1" },
  "name": "John Smith",
  "gender": "M",
  "birthDate" : "1980-02-17T06:15",
}
module.exports.P1 = p1

# Born in 2007
p2 = {
  "identifier": { "value": "2" },
  "name": "Sally Smith",
  "gender": "F",
  "birthDate" : "2007-08-02T11:47",
}
module.exports.P2 = p2

# Acute Pharyngitis and ED/Ambulatory Visits
p3 = {
  "identifier": { "value": "3" },
  "name": "Bob Jones",
  "gender": "M",
  "birthDate" : "1974-07-12T11:15",
  "records": [
    {
      "identifier": { "value": "http://cqframework.org/3/1", "system": "http://cqframework.org" },
      "profile": "cqf-encounter",
      "topic": "Encounter",
      "class": { "code": "185349003", "system": "2.16.840.1.113883.6.96", "version": "2013-09", "display": "Encounter for \"check-up\" (procedure)" },
      "type": { "code": "G0438", "system": "2.16.840.1.113883.6.285", "version": "2014", "display": "Annual wellness visit; includes a personalized prevention plan of service (pps), initial visit" },
      "period": { "start": "1978-07-15T10:00", "end": "1978-07-15T10:45" }
    }, {
      "identifier": { "value": "http://cqframework.org/3/2", "system": "http://cqframework.org" },
      "profile": "cqf-condition",
      "topic": "Condition",
      "code": { "code": "1532007", "system": "2.16.840.1.113883.6.96", "version": "2013-09", "display": "Viral pharyngitis (disorder)" },
      "onsetDateTime": "1982-03-12",
      "abatementDateTime": "1982-03-26",
      "issued": "1982-03-15T15:15:00"
    }, {
      "identifier": { "value": "http://cqframework.org/3/3", "system": "http://cqframework.org" },
      "profile": "cqf-encounter",
      "topic": "Encounter",
      "class": { "code": "406547006", "system": "2.16.840.1.113883.6.96", "version": "2013-09", "display": "Urgent follow-up (procedure)" },
      "type": { "code": "183616001", "system": "2.16.840.1.113883.6.96", "version": "2013-09", "display": "Follow-up arranged (finding)" },
      "period": { "start": "1982-03-15T15:00", "end": "1982-03-15T15:30" }
    }, {
      "identifier": { "value": "http://cqframework.org/3/4", "system": "http://cqframework.org" },
      "profile": "cqf-condition",
      "topic": "Condition",
      "code": { "code": "109962001", "system": "2.16.840.1.113883.6.96", "version": "2013-09", "display": "Diffuse non-Hodgkin's lymphoma (disorder)" },
      "onsetDateTime": "2010-10-24",
      "issued": "2011-02-01T11:55:00"
    },
    {
      "identifier": { "value": "http://cqframework.org/3/5", "system": "http://cqframework.org" },
      "profile": "cqf-encounter",
      "topic": "Encounter",
      "class": { "code": "185349003", "system": "2.16.840.1.113883.6.96", "version": "2013-09", "display": "Encounter for \"check-up\" (procedure)" },
      "type": { "code": "G0438", "system": "2.16.840.1.113883.6.285", "version": "2014", "display": "Annual wellness visit; includes a personalized prevention plan of service (pps), initial visit" },
      "period": { "start": "2013-05-23T10:00", "end": "2013-05-23T11:00" }
    }
  ]
}
module.exports.P3 = p3

# ED/Ambulatory Visit
p4 = {
  "identifier": { "value": "4" },
  "name": "Jane Jones",
  "gender": "F",
  "birthDate" : "1976-11-09T14:12",
  "records": [
    {
      "identifier": { "value": "http://cqframework.org/4/1", "system": "http://cqframework.org" },
      "profile": "cqf-encounter",
      "topic": "Encounter",
      "class": { "code": "439708006", "system": "2.16.840.1.113883.6.96", "version": "2013-09", "display": "Home visit (procedure)" },
      "period": { "start": "1985-05-01T13:00", "end": "1985-05-01T14:00" }
    }
  ]
}
module.exports.P4 = p4

module.exports.P1AndP2 = [p1, p2]