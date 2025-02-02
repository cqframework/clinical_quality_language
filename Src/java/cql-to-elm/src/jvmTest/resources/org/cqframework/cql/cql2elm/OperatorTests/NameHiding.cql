library NameHiding

valueset "Test Value Set": '1.2.3.4.5'

parameter "Test Period" Interval<DateTime>

define "Source 1": { { id: 1, relevantPeriod: Interval[@2016-01-01, @2017-12-31] } }

// Name-hiding is allowed
define "Query 1":
  "Test Value Set" A
    where exists ("Source 1" A where A.relevantPeriod during "Test Period")

// Out-of-scope access should fail (Could not resolve identifier B)
define "Query 2": "Source 1" B where exists ("Query 3")
define "Query 3": "Source 1" A where B.relevantPeriod during "Test Period"
