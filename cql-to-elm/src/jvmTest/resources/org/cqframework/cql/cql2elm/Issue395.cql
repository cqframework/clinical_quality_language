library Cataracts2040BCVAwithin90Days version '9.2.000'

using QDM version '5.5'

valueset "Acute and Subacute Iridocyclitis": 'urn:oid:2.16.840.1.113883.3.526.3.1241'
valueset "Amblyopia": 'urn:oid:2.16.840.1.113883.3.526.3.1448'
valueset "Best Corrected Visual Acuity Exam Using Snellen Chart": 'urn:oid:2.16.840.1.113883.3.526.3.1560'
valueset "Burn Confined to Eye and Adnexa": 'urn:oid:2.16.840.1.113883.3.526.3.1409'
valueset "Cataract Secondary to Ocular Disorders": 'urn:oid:2.16.840.1.113883.3.526.3.1410'
valueset "Cataract Surgery": 'urn:oid:2.16.840.1.113883.3.526.3.1411'
valueset "Cataract, Congenital": 'urn:oid:2.16.840.1.113883.3.526.3.1412'
valueset "Cataract, Mature or Hypermature": 'urn:oid:2.16.840.1.113883.3.526.3.1413'
valueset "Cataract, Posterior Polar": 'urn:oid:2.16.840.1.113883.3.526.3.1414'
valueset "Central Corneal Ulcer": 'urn:oid:2.16.840.1.113883.3.526.3.1428'
valueset "Certain Types of Iridocyclitis": 'urn:oid:2.16.840.1.113883.3.526.3.1415'
valueset "Choroidal Degenerations": 'urn:oid:2.16.840.1.113883.3.526.3.1450'
valueset "Choroidal Detachment": 'urn:oid:2.16.840.1.113883.3.526.3.1451'
valueset "Choroidal Hemorrhage and Rupture": 'urn:oid:2.16.840.1.113883.3.526.3.1452'
valueset "Chronic Iridocyclitis": 'urn:oid:2.16.840.1.113883.3.526.3.1416'
valueset "Vascular Disorders of Iris and Ciliary Body": 'urn:oid:2.16.840.1.113883.3.526.3.1445'
valueset "Visual Field Defects": 'urn:oid:2.16.840.1.113883.3.526.3.1446'


parameter "Measurement Period" Interval<DateTime>

context Patient


define "Cataract Surgery Between January and September of Measurement Period":
  ["Procedure, Performed": "Cataract Surgery"] CataractSurgery
    where CataractSurgery.relevantPeriod during "Measurement Period"
      and CataractSurgery.relevantPeriod starts 93 days or more before 
      end of "Measurement Period"


define "Denominator Exclusions":
  "Cataract Surgery Between January and September of Measurement Period" CataractSurgeryPerformed
    with ( ["Diagnosis": "Acute and Subacute Iridocyclitis"]
      union ["Diagnosis": "Amblyopia"]
      union ["Diagnosis": "Burn Confined to Eye and Adnexa"]
      union ["Diagnosis": "Cataract Secondary to Ocular Disorders"]
      union ["Diagnosis": "Cataract, Congenital"]
      union ["Diagnosis": "Cataract, Mature or Hypermature"]
      union ["Diagnosis": "Cataract, Posterior Polar"]
      union ["Diagnosis": "Central Corneal Ulcer"]
      union ["Diagnosis": "Certain Types of Iridocyclitis"]
      union ["Diagnosis": "Choroidal Degenerations"]
      union ["Diagnosis": "Choroidal Detachment"]
      union ["Diagnosis": "Choroidal Hemorrhage and Rupture"]
      union ["Diagnosis": "Chronic Iridocyclitis"]
      union ["Diagnosis": "Vascular Disorders of Iris and Ciliary Body"]
      union ["Diagnosis": "Visual Field Defects"] ) ComorbidDiagnosis
      such that ComorbidDiagnosis.prevalencePeriod overlaps before CataractSurgeryPerformed.relevantPeriod
