library UseofHighRiskMedicationsintheElderly version '9.3.000'

using QDM version '5.5'

include MATGlobalCommonFunctions version '5.0.000' called Global
include Hospice version '2.2.000' called Hospice

codesystem "LOINC": 'urn:oid:2.16.840.1.113883.6.1'

valueset "Amitriptyline Hydrochloride": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1373'
valueset "Amobarbital": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1512'
valueset "Amoxapine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1273'
valueset "Annual Wellness Visit": 'urn:oid:2.16.840.1.113883.3.526.3.1240'
valueset "Anti Infectives, other": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1481'
valueset "Atropine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1274'
valueset "Benztropine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1361'
valueset "Brompheniramine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1427'
valueset "Butabarbital": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1402'
valueset "Butalbital": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1514'
valueset "Carbinoxamine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1306'
valueset "Care Services in Long-Term Residential Facility": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1014'
valueset "Carisoprodol": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1369'
valueset "Chlorpheniramine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1352'
valueset "Chlorpropamide": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1303'
valueset "Chlorzoxazone": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1362'
valueset "Clemastine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1308'
valueset "Clomipramine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1336'
valueset "Conjugated Estrogens": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1357'
valueset "Cyclobenzaprine Hydrochloride": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1372'
valueset "Cyproheptadine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1277'
valueset "Desiccated Thyroid": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1354'
valueset "Desipramine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1278'
valueset "Dexbrompheniramine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1375'
valueset "Dexchlorpheniramine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1300'
valueset "Dicyclomine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1279'
valueset "Dimenhydrinate": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1500'
valueset "Diphenhydramine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1293'
valueset "Diphenhydramine Hydrochloride": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1371'
valueset "Dipyridamole": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1349'
valueset "Discharge Services - Nursing Facility": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1013'
valueset "Disopyramide": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1311'
valueset "Doxylamine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1515'
valueset "Ergoloid Mesylates": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1516'
valueset "Esterified Estrogens": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1419'
valueset "Estradiol": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1365'
valueset "Estropipate": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1319'
valueset "Ethnicity": 'urn:oid:2.16.840.1.114222.4.11.837'
valueset "Glyburide": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1368'
valueset "Guanfacine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1341'
valueset "Home Healthcare Services": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1016'
valueset "Hydroxyzine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1374'
valueset "Hyoscyamine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1501'
valueset "Imipramine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1359'
valueset "Indomethacin": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1366'
valueset "Isoxsuprine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1422'
valueset "Ketorolac Tromethamine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1364'
valueset "List of Single RxNorm Code Concepts for High Risk Drugs for the Elderly": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1272'
valueset "Meclizine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1506'
valueset "Megestrol": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1342'
valueset "Meperidine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1351'
valueset "Meprobamate": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1284'
valueset "Metaxalone": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1358'
valueset "Methocarbamol": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1370'
valueset "Methyldopa": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1331'
valueset "Nifedipine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1353'
valueset "Nonbenzodiazepine hypnotics": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1480'
valueset "Nortriptyline": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1507'
valueset "Nursing Facility Visit": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1012'
valueset "Office Visit": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1001'
valueset "ONC Administrative Sex": 'urn:oid:2.16.840.1.113762.1.4.1'
valueset "Ophthalmologic Services": 'urn:oid:2.16.840.1.113883.3.464.1003.101.11.1206'
valueset "Orphenadrine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1302'
valueset "Paroxetine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1508'
valueset "Payer": 'urn:oid:2.16.840.1.114222.4.11.3591'
valueset "Pentobarbital": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1518'
valueset "Phenobarbital": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1348'
valueset "Preventive Care Services - Established Office Visit, 18 and Up": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1025'
valueset "Preventive Care Services-Initial Office Visit, 18 and Up": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1023'
valueset "Promethazine Hydrochloride": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1367'
valueset "Propantheline": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1519'
valueset "Protriptyline": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1509'
valueset "Race": 'urn:oid:2.16.840.1.114222.4.11.836'
valueset "Scopolamine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1520'
valueset "Secobarbital": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1521'
valueset "Trihexyphenidyl": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1334'
valueset "Trimipramine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1285'
valueset "Triprolidine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1408'

code "Birth date": '21112-8' from "LOINC" display 'Birth date'

parameter "Measurement Period" Interval<DateTime>

context Patient

define "Denominator":
  "Initial Population"

define "Denominator Exclusions":
  Hospice."Has Hospice"

define "Initial Population":
  exists ["Patient Characteristic Birthdate": "Birth date"] BirthDate
    where Global."CalendarAgeInYearsAt" ( BirthDate.birthDatetime, start of "Measurement Period" ) >= 65
      and exists ( "Qualifying Encounters" )

define "More than One AntiInfective Order":
  //Anti Infectives, other
  exists ( "More Than One of Same Medication Order"(["Medication, Order": "Anti Infectives, other"]))

define "More than One Nonbenzodiazepine Hypnotics Order":
  //Nonbenzodiazepine hypnotics
  exists ( "More Than One of Same Medication Order"(["Medication, Order": "Nonbenzodiazepine hypnotics"]))

define "Qualifying Encounters":
  ( ["Encounter, Performed": "Office Visit"]
    union ["Encounter, Performed": "Ophthalmologic Services"]
    union ["Encounter, Performed": "Preventive Care Services - Established Office Visit, 18 and Up"]
    union ["Encounter, Performed": "Discharge Services - Nursing Facility"]
    union ["Encounter, Performed": "Nursing Facility Visit"]
    union ["Encounter, Performed": "Care Services in Long-Term Residential Facility"]
    union ["Encounter, Performed": "Preventive Care Services-Initial Office Visit, 18 and Up"]
    union ["Encounter, Performed": "Annual Wellness Visit"]
    union ["Encounter, Performed": "Home Healthcare Services"] ) ValidEncounter
    where ValidEncounter.relevantPeriod during "Measurement Period"

define "SDE Ethnicity":
  ["Patient Characteristic Ethnicity": "Ethnicity"]

define "SDE Payer":
  ["Patient Characteristic Payer": "Payer"]

define "SDE Race":
  ["Patient Characteristic Race": "Race"]

define "SDE Sex":
  ["Patient Characteristic Sex": "ONC Administrative Sex"]

define "Two High Risk Medications With Prolonged Duration":
  ( "Cumulative Medication Duration"("AntiInfectives During Measurement Period")>= 90
      and "More than One AntiInfective Order"
  )
    or ( "Cumulative Medication Duration"("Hypnotics During Measurement Period")>= 90
        and "More than One Nonbenzodiazepine Hypnotics Order"
    )

define "Same High Risk Medications Ordered on Different Days":
  ( from
      ["Medication, Order": "List of Single RxNorm Code Concepts for High Risk Drugs for the Elderly"] MedicationOrder1,
      ["Medication, Order": "List of Single RxNorm Code Concepts for High Risk Drugs for the Elderly"] MedicationOrder2
      where MedicationOrder1.authorDatetime during "Measurement Period"
        and MedicationOrder2.authorDatetime during "Measurement Period"
        and MedicationOrder1.authorDatetime 1 day or more after MedicationOrder2.authorDatetime
        and MedicationOrder1.code ~ MedicationOrder2.code
      return MedicationOrder1
  )
    union "More Than One of Same Medication Order"(["Medication, Order": "Amobarbital"])
    union "More Than One of Same Medication Order"(["Medication, Order": "Amitriptyline Hydrochloride"])
    union "More Than One of Same Medication Order"(["Medication, Order": "Amoxapine"])
    union "More Than One of Same Medication Order"(["Medication, Order": "Orphenadrine"])
    union "More Than One of Same Medication Order"(["Medication, Order": "Atropine"])
    union "More Than One of Same Medication Order"(["Medication, Order": "Benztropine"])
    union "More Than One of Same Medication Order"(["Medication, Order": "Brompheniramine"])
    union "More Than One of Same Medication Order"(["Medication, Order": "Butabarbital"])
    union "More Than One of Same Medication Order"(["Medication, Order": "Butalbital"])
    union "More Than One of Same Medication Order"(["Medication, Order": "Carbinoxamine"])
    union "More Than One of Same Medication Order"(["Medication, Order": "Carisoprodol"])
    union "More Than One of Same Medication Order"(["Medication, Order": "Chlorpheniramine"])
    union "More Than One of Same Medication Order"(["Medication, Order": "Chlorpropamide"])
    union "More Than One of Same Medication Order"(["Medication, Order": "Chlorzoxazone"])
    union "More Than One of Same Medication Order"(["Medication, Order": "Clemastine"])
    union "More Than One of Same Medication Order"(["Medication, Order": "Clomipramine"])
    union "More Than One of Same Medication Order"(["Medication, Order": "Conjugated Estrogens"])
    union "More Than One of Same Medication Order"(["Medication, Order": "Cyclobenzaprine Hydrochloride"])
    union "More Than One of Same Medication Order"(["Medication, Order": "Cyproheptadine"])
    union "More Than One of Same Medication Order"(["Medication, Order": "Desiccated Thyroid"])
    union "More Than One of Same Medication Order"(["Medication, Order": "Desipramine"])
    union "More Than One of Same Medication Order"(["Medication, Order": "Dexbrompheniramine"])
    union "More Than One of Same Medication Order"(["Medication, Order": "Dexchlorpheniramine"])
    union "More Than One of Same Medication Order"(["Medication, Order": "Dicyclomine"])
    union "More Than One of Same Medication Order"(["Medication, Order": "Dimenhydrinate"])
    union "More Than One of Same Medication Order"(["Medication, Order": "Diphenhydramine"])
    union "More Than One of Same Medication Order"(["Medication, Order": "Diphenhydramine Hydrochloride"])
    union "More Than One of Same Medication Order"(["Medication, Order": "Dipyridamole"])
    union "More Than One of Same Medication Order"(["Medication, Order": "Disopyramide"])
    union "More Than One of Same Medication Order"(["Medication, Order": "Doxylamine"])
    union "More Than One of Same Medication Order"(["Medication, Order": "Ergoloid Mesylates"])
    union "More Than One of Same Medication Order"(["Medication, Order": "Esterified Estrogens"])
    union "More Than One of Same Medication Order"(["Medication, Order": "Estradiol"])
    union "More Than One of Same Medication Order"(["Medication, Order": "Estropipate"])
    union "More Than One of Same Medication Order"(["Medication, Order": "Glyburide"])
    union "More Than One of Same Medication Order"(["Medication, Order": "Guanfacine"])
    union "More Than One of Same Medication Order"(["Medication, Order": "Hydroxyzine"])
    union "More Than One of Same Medication Order"(["Medication, Order": "Hyoscyamine"])
    union "More Than One of Same Medication Order"(["Medication, Order": "Imipramine"])
    union "More Than One of Same Medication Order"(["Medication, Order": "Indomethacin"])
    union "More Than One of Same Medication Order"(["Medication, Order": "Isoxsuprine"])
    union "More Than One of Same Medication Order"(["Medication, Order": "Ketorolac Tromethamine"])
    union "More Than One of Same Medication Order"(["Medication, Order": "Meclizine"])
    union "More Than One of Same Medication Order"(["Medication, Order": "Megestrol"])
    union "More Than One of Same Medication Order"(["Medication, Order": "Meperidine"])
    union "More Than One of Same Medication Order"(["Medication, Order": "Meprobamate"])
    union "More Than One of Same Medication Order"(["Medication, Order": "Metaxalone"])
    union "More Than One of Same Medication Order"(["Medication, Order": "Methocarbamol"])
    union "More Than One of Same Medication Order"(["Medication, Order": "Methyldopa"])
    union "More Than One of Same Medication Order"(["Medication, Order": "Nifedipine"])
    union "More Than One of Same Medication Order"(["Medication, Order": "Nortriptyline"])
    union "More Than One of Same Medication Order"(["Medication, Order": "Paroxetine"])
    union "More Than One of Same Medication Order"(["Medication, Order": "Pentobarbital"])
    union "More Than One of Same Medication Order"(["Medication, Order": "Phenobarbital"])
    union "More Than One of Same Medication Order"(["Medication, Order": "Promethazine Hydrochloride"])
    union "More Than One of Same Medication Order"(["Medication, Order": "Protriptyline"])
    union "More Than One of Same Medication Order"(["Medication, Order": "Propantheline"])
    union "More Than One of Same Medication Order"(["Medication, Order": "Scopolamine"])
    union "More Than One of Same Medication Order"(["Medication, Order": "Secobarbital"])
    union "More Than One of Same Medication Order"(["Medication, Order": "Trihexyphenidyl"])
    union "More Than One of Same Medication Order"(["Medication, Order": "Trimipramine"])
    union "More Than One of Same Medication Order"(["Medication, Order": "Triprolidine"])

define "Numerator":
  exists ( "Same High Risk Medications Ordered on Different Days" )
    or ( "Two High Risk Medications With Prolonged Duration" )

define "AntiInfectives During Measurement Period":
  ["Medication, Order": "Anti Infectives, other"] AntiInfectives
    where AntiInfectives.authorDatetime during "Measurement Period"

define "Hypnotics During Measurement Period":
  ["Medication, Order": "Nonbenzodiazepine hypnotics"] Hypnotics
    where Hypnotics.authorDatetime during "Measurement Period"

define function "Cumulative Medication Duration"(Medication List<"Medication, Order">):
  Sum((collapse(Medication.relevantPeriod))CumulativeDosages
      return all duration in days of CumulativeDosages
  )

define function "More Than One of Same Medication Order"(Medication List<"Medication, Order">):
  "Medication" OrderMedication1
    with "Medication" OrderMedication2
      such that OrderMedication1.authorDatetime 1 day or more after OrderMedication2.authorDatetime
        and OrderMedication1.authorDatetime during "Measurement Period"
        and OrderMedication2.authorDatetime during "Measurement Period"
    return OrderMedication1