library UseofHighRiskMedicationsintheElderly version '8.3.000'

using QDM version '5.4'

include MATGlobalCommonFunctions version '4.0.000' called Global
include Hospice version '2.0.000' called Hospice

codesystem "LOINC": 'urn:oid:2.16.840.1.113883.6.1'

valueset "Acetaminophen / Butalbital": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1317'
valueset "Acetaminophen / Butalbital / Caffeine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1363'
valueset "Acetaminophen / Butalbital / Caffeine / Codeine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1326'
valueset "Acetaminophen / Chlorpheniramine / Dextromethorphan": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1421'
valueset "Acetaminophen / Chlorpheniramine / Dextromethorphan / Phenylephrine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1310'
valueset "Acetaminophen / Chlorpheniramine / Dextromethorphan / Pseudoephedrine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1321'
valueset "Acetaminophen / Chlorpheniramine / Phenylephrine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1328'
valueset "Acetaminophen / Chlorpheniramine / Pseudoephedrine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1316'
valueset "Acetaminophen / Dexbrompheniramine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1405'
valueset "Acetaminophen / Dextromethorphan / Diphenhydramine / Phenylephrine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1399'
valueset "Acetaminophen / Dextromethorphan / Doxylamine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1338'
valueset "Acetaminophen / Dextromethorphan / Doxylamine / Phenylephrine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1287'
valueset "Acetaminophen / Diphenhydramine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1350'
valueset "Acetaminophen / Diphenhydramine / Phenylephrine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1318'
valueset "Amitriptyline  /  Chlordiazepoxide": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1286'
valueset "Amitriptyline / Perphenazine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1428'
valueset "Amitriptyline Hydrochloride": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1373'
valueset "Amoxapine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1273'
valueset "Annual Wellness Visit": 'urn:oid:2.16.840.1.113883.3.526.3.1240'
valueset "Anti Infectives, other": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1481'
valueset "Aspirin / Butalbital / Caffeine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1347'
valueset "Aspirin / Caffeine / Orphenadrine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1302'
valueset "Atropine / Diphenoxylate": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1274'
valueset "Atropine / Hyoscyamine / Phenobarbital / Scopolamine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1355'
valueset "Benztropine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1361'
valueset "Brompheniramine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1427'
valueset "Brompheniramine / Codeine / Phenylephrine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1409'
valueset "Brompheniramine / Codeine / Pseudoephedrine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1450'
valueset "Brompheniramine / Dextromethorphan / Phenylephrine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1325'
valueset "Brompheniramine / Dextromethorphan / Pseudoephedrine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1339'
valueset "Brompheniramine / Pseudoephedrine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1332'
valueset "Butabarbital": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1402'
valueset "Carbinoxamine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1306'
valueset "Care Services in Long-Term Residential Facility": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1014'
valueset "Carisoprodol": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1369'
valueset "Chlophedianol / Chlorpheniramine / Phenylephrine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1412'
valueset "Chlophedianol / Dexchlorpheniramine / Pseudoephedrine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1447'
valueset "Chlorpheniramine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1352'
valueset "Chlorpheniramine / Dextromethorphan / Phenylephrine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1312'
valueset "Chlorpheniramine / Dextromethorphan / Pseudoephedrine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1337'
valueset "Chlorpheniramine / Hydrocodone": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1330'
valueset "Chlorpheniramine / Hydrocodone / Pseudoephedrine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1299'
valueset "Chlorpheniramine / Phenylephrine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1343'
valueset "Chlorpheniramine / Pseudoephedrine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1315'
valueset "Chlorpropamide": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1303'
valueset "Chlorzoxazone": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1362'
valueset "Clemastine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1308'
valueset "Clomipramine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1336'
valueset "Conjugated Estrogens": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1357'
valueset "Conjugated Estrogens / Medroxyprogesterone": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1324'
valueset "Cyclobenzaprine Hydrochloride": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1372'
valueset "Cyproheptadine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1277'
valueset "Desiccated Thyroid": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1354'
valueset "Desipramine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1278'
valueset "Dexbrompheniramine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1375'
valueset "Dexbrompheniramine / Dextromethorphan / Phenylephrine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1426'
valueset "Dexbrompheniramine / Pseudoephedrine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1430'
valueset "Dexbrompheniramine Maleate / Pseudoephedrine Hydrochloride": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1429'
valueset "Dexchlorpheniramine / Dextromethorphan / Pseudoephedrine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1300'
valueset "Dexchlorpheniramine / Pseudoephedrine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1425'
valueset "Dextromethorphan / Diphenhydramine / Phenylephrine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1397'
valueset "Dextromethorphan / Doxylamine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1452'
valueset "Dicyclomine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1279'
valueset "Dienogest / Estradiol Multiphasic": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1398'
valueset "Dimenhydrinate": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1500'
valueset "Diphenhydramine / Ibuprofen": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1293'
valueset "Diphenhydramine / Phenylephrine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1307'
valueset "Diphenhydramine Hydrochloride": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1371'
valueset "Dipyridamole": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1349'
valueset "Discharge Services - Nursing Facility": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1013'
valueset "Disopyramide": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1311'
valueset "Drospirenone / Estradiol": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1410'
valueset "Esterified Estrogens": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1419'
valueset "Esterified Estrogens / Methyltestosterone": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1320'
valueset "Estradiol": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1365'
valueset "Estradiol / Norethindrone": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1323'
valueset "Estropipate": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1319'
valueset "Ethnicity": 'urn:oid:2.16.840.1.114222.4.11.837'
valueset "Glyburide": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1368'
valueset "Glyburide / Metformin": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1360'
valueset "Guanfacine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1341'
valueset "High Risk Medications for the Elderly": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1253'
valueset "High-Risk Medications With Days Supply Criteria": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1254'
valueset "Home Healthcare Services": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1016'
valueset "Hydrochlorothiazide / Methyldopa": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1414'
valueset "Hydroxyzine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1374'
valueset "Hyoscyamine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1501'
valueset "Hyoscyamine / Methenamine / Mblue / Phenyl Salicyl": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1503'
valueset "Hyoscyamine / Methenamine / Mblue / Phenyl Salicyl / Sodium Biphosphate": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1504'
valueset "Hyoscyamine / Methenamine / Mblue / Sodium Biphosphate": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1505'
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
valueset "Paroxetine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1508'
valueset "Payer": 'urn:oid:2.16.840.1.114222.4.11.3591'
valueset "Phenobarbital": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1348'
valueset "Preventive Care Services - Established Office Visit, 18 and Up": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1025'
valueset "Preventive Care Services-Initial Office Visit, 18 and Up": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1023'
valueset "Promethazine Hydrochloride": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1367'
valueset "Protriptyline": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1509'
valueset "Pseudoephedrine / Triprolidine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1345'
valueset "Race": 'urn:oid:2.16.840.1.114222.4.11.836'
valueset "Trihexyphenidyl": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1334'
valueset "Trimipramine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1285'
valueset "Triprolidine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1408'

code "Birth date": '21112-8' from "LOINC" display 'Birth date'

parameter "Measurement Period" Interval<DateTime>

context Patient

define "SDE Ethnicity":
	["Patient Characteristic Ethnicity": "Ethnicity"]

define "SDE Payer":
	["Patient Characteristic Payer": "Payer"]

define "SDE Race":
	["Patient Characteristic Race": "Race"]

define "SDE Sex":
	["Patient Characteristic Sex": "ONC Administrative Sex"]

define "Denominator":
	"Initial Population"

define "Denominator Exclusions":
	Hospice."Has Hospice"

define "AntiInfectives During Measurement Period":
	["Medication, Order": "Anti Infectives, other"] AntiInfectives
		where AntiInfectives.relevantPeriod during "Measurement Period"

define "Hypnotics During Measurement Period":
	["Medication, Order": "Nonbenzodiazepine hypnotics"] Hypnotics
		where Hypnotics.relevantPeriod during "Measurement Period"

define "High Risk Medications With Prolonged Duration":
	( ["Medication, Order": "High-Risk Medications With Days Supply Criteria"] HighRiskMedicationOrder
			where duration in days of HighRiskMedicationOrder.relevantPeriod >= 90
				and HighRiskMedicationOrder.relevantPeriod during "Measurement Period"
	)

define "Numerator2":
	exists ( "Same High Risk Medications Ordered on Different Days" )
		or ( "Two High Risk Medications With Prolonged Duration" )

define "Order for High Risk Medication":
	["Medication, Order": "High Risk Medications for the Elderly"] HighRiskMedicationOrder
		where HighRiskMedicationOrder.authorDatetime during "Measurement Period"

define "Numerator1":
	exists ( "Order for High Risk Medication" )
		or exists ( "High Risk Medications With Prolonged Duration" )

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

define "Initial Population":
	exists ["Patient Characteristic Birthdate": "Birth date"] BirthDate
		where Global."CalendarAgeInYearsAt"(BirthDate.birthDatetime, start of "Measurement Period")>= 65
			and exists ( "Qualifying Encounters" )

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
	
	//Acetaminophen / Butalbital
		union "More than 1 Of"(["Medication, Order": "Acetaminophen / Butalbital"])
	
	//Acetaminophen / Butalbital / Caffeine
		union "More than 1 Of"(["Medication, Order": "Acetaminophen / Butalbital / Caffeine"])
	
	//Acetaminophen / Butalbital / Caffeine / Codeine
		union "More than 1 Of"(["Medication, Order": "Acetaminophen / Butalbital / Caffeine / Codeine"])
	
	//Acetaminophen / Chlorpheniramine / Dextromethorphan
		union "More than 1 Of"(["Medication, Order": "Acetaminophen / Chlorpheniramine / Dextromethorphan"])
	
	//Acetaminophen / Chlorpheniramine / Dextromethorphan / Phenylephrine
		union "More than 1 Of"(["Medication, Order": "Acetaminophen / Chlorpheniramine / Dextromethorphan / Phenylephrine"])
	
	//Acetaminophen / Chlorpheniramine / Dextromethorphan / Pseudoephedrine
		union "More than 1 Of"(["Medication, Order": "Acetaminophen / Chlorpheniramine / Dextromethorphan / Pseudoephedrine"])
	
	//Acetaminophen / Chlorpheniramine / Phenylephrine
		union "More than 1 Of"(["Medication, Order": "Acetaminophen / Chlorpheniramine / Phenylephrine"])
	
	//Acetaminophen / Chlorpheniramine / Pseudoephedrine
		union "More than 1 Of"(["Medication, Order": "Acetaminophen / Chlorpheniramine / Pseudoephedrine"])
	
	//Acetaminophen / Dexbrompheniramine
		union "More than 1 Of"(["Medication, Order": "Acetaminophen / Dexbrompheniramine"])
	
	//Acetaminophen / Dextromethorphan / Diphenhydramine / Phenylephrine
		union "More than 1 Of"(["Medication, Order": "Acetaminophen / Dextromethorphan / Diphenhydramine / Phenylephrine"])
	
	//Acetaminophen / Dextromethorphan / Doxylamine
		union "More than 1 Of"(["Medication, Order": "Acetaminophen / Dextromethorphan / Doxylamine"])
	
	//Acetaminophen / Dextromethorphan / Doxylamine / Phenylephrine
		union "More than 1 Of"(["Medication, Order": "Acetaminophen / Dextromethorphan / Doxylamine / Phenylephrine"])	
	
	//Acetaminophen / Diphenhydramine
		union "More than 1 Of"(["Medication, Order": "Acetaminophen / Diphenhydramine"])
	
	//Acetaminophen / Diphenhydramine / Phenylephrine
		union "More than 1 Of"(["Medication, Order": "Acetaminophen / Diphenhydramine / Phenylephrine"])
	
	
	//Amitriptyline / Chlordiazepoxide
		union "More than 1 Of"(["Medication, Order": "Amitriptyline  /  Chlordiazepoxide"])	
	
	//Amitriptyline / Perphenazine
		union "More than 1 Of"(["Medication, Order": "Amitriptyline / Perphenazine"])
	
	//Amitriptyline Hydrochloride
		union "More than 1 Of"(["Medication, Order": "Amitriptyline Hydrochloride"])
	
	//Aspirin / Butalbital / Caffeine
		union "More than 1 Of"(["Medication, Order": "Aspirin / Butalbital / Caffeine"])
	
	//Amoxapine
		union "More than 1 Of"(["Medication, Order": "Amoxapine"])	
	
	//Aspirin / Caffeine / Orphenadrine
		union "More than 1 Of"(["Medication, Order": "Aspirin / Caffeine / Orphenadrine"])
	
	//Atropine / Hyoscyamine / Phenobarbital / Scopolamine
		union "More than 1 Of"(["Medication, Order": "Atropine / Hyoscyamine / Phenobarbital / Scopolamine"])
	
	//Atropine / Diphenoxylate 
		union "More than 1 Of"(["Medication, Order": "Atropine / Diphenoxylate"])
	
	//Benztropine
		union "More than 1 Of"(["Medication, Order": "Benztropine"])
	
	//Brompheniramine
		union "More than 1 Of"(["Medication, Order": "Brompheniramine"])
	
	
	//Brompheniramine / Codeine / Phenylephrine
		union "More than 1 Of"(["Medication, Order": "Brompheniramine / Codeine / Phenylephrine"])
	
	//Brompheniramine / Codeine / Pseudoephedrine
		union "More than 1 Of"(["Medication, Order": "Brompheniramine / Codeine / Pseudoephedrine"])
	
	//Brompheniramine / Dextromethorphan / Phenylephrine
		union "More than 1 Of"(["Medication, Order": "Brompheniramine / Dextromethorphan / Phenylephrine"])
	
	//Brompheniramine / Dextromethorphan / Pseudoephedrine
		union "More than 1 Of"(["Medication, Order": "Brompheniramine / Dextromethorphan / Pseudoephedrine"])
	        
	
	//Brompheniramine / Pseudoephedrine
		union "More than 1 Of"(["Medication, Order": "Brompheniramine / Pseudoephedrine"])
	
	//Butabarbital
		union "More than 1 Of"(["Medication, Order": "Butabarbital"])
	
	//Carbinoxamine
		union "More than 1 Of"(["Medication, Order": "Carbinoxamine"])
	
	//Carisoprodol
		union "More than 1 Of"(["Medication, Order": "Carisoprodol"])
	
	//Chlophedianol / Chlorpheniramine / Phenylephrine
		union "More than 1 Of"(["Medication, Order": "Chlophedianol / Chlorpheniramine / Phenylephrine"])
	
	//Chlophedianol / Dexchlorpheniramine / Pseudoephedrine
		union "More than 1 Of"(["Medication, Order": "Chlophedianol / Dexchlorpheniramine / Pseudoephedrine"])
	
	//Chlorpheniramine
		union "More than 1 Of"(["Medication, Order": "Chlorpheniramine"])
	
	//Chlorpheniramine / Dextromethorphan / Phenylephrine
		union "More than 1 Of"(["Medication, Order": "Chlorpheniramine / Dextromethorphan / Phenylephrine"])
	
	//Chlorpheniramine / Dextromethorphan / Pseudoephedrine
		union "More than 1 Of"(["Medication, Order": "Chlorpheniramine / Dextromethorphan / Pseudoephedrine"])
	
	
	//Chlorpheniramine / Hydrocodone
		union "More than 1 Of"(["Medication, Order": "Chlorpheniramine / Hydrocodone"])
	
	//Chlorpheniramine / Hydrocodone / Pseudoephedrine
		union "More than 1 Of"(["Medication, Order": "Chlorpheniramine / Hydrocodone / Pseudoephedrine"])
	
	//Chlorpheniramine / Phenylephrine
		union "More than 1 Of"(["Medication, Order": "Chlorpheniramine / Phenylephrine"])
	
	//Chlorpheniramine / Pseudoephedrine
		union "More than 1 Of"(["Medication, Order": "Chlorpheniramine / Pseudoephedrine"])
	
	//Chlorpropamide
		union "More than 1 Of"(["Medication, Order": "Chlorpropamide"])
	
	//Chlorzoxazone
		union "More than 1 Of"(["Medication, Order": "Chlorzoxazone"])
	
	//Clemastine
		union "More than 1 Of"(["Medication, Order": "Clemastine"])
	
	//Clomipramine
		union "More than 1 Of"(["Medication, Order": "Clomipramine"])
	
	//Conjugated Estrogens
		union "More than 1 Of"(["Medication, Order": "Conjugated Estrogens"])
	
	//Conjugated Estrogens / Medroxyprogesterone
		union "More than 1 Of"(["Medication, Order": "Conjugated Estrogens / Medroxyprogesterone"])
	
	//Cyclobenzaprine Hydrochloride
		union "More than 1 Of"(["Medication, Order": "Cyclobenzaprine Hydrochloride"])
	
	//Cyproheptadine
		union "More than 1 Of"(["Medication, Order": "Cyproheptadine"])
	
	//Desiccated Thyroid
		union "More than 1 Of"(["Medication, Order": "Desiccated Thyroid"])
	
	//Desipramine
		union "More than 1 Of"(["Medication, Order": "Desipramine"])
	
	//Dexbrompheniramine
		union "More than 1 Of"(["Medication, Order": "Dexbrompheniramine"])
	
	//Dexbrompheniramine / Dextromethorphan / Phenylephrine
		union "More than 1 Of"(["Medication, Order": "Dexbrompheniramine / Dextromethorphan / Phenylephrine"])
	
	//Dexbrompheniramine / Pseudoephedrine
		union "More than 1 Of"(["Medication, Order": "Dexbrompheniramine / Pseudoephedrine"])
	
	//Dexbrompheniramine Maleate / Pseudoephedrine Hydrochloride
		union "More than 1 Of"(["Medication, Order": "Dexbrompheniramine Maleate / Pseudoephedrine Hydrochloride"])
	
	//Dexchlorpheniramine / Dextromethorphan / Pseudoephedrine
		union "More than 1 Of"(["Medication, Order": "Dexchlorpheniramine / Dextromethorphan / Pseudoephedrine"])
	
	//Dexchlorpheniramine / Pseudoephedrine
		union "More than 1 Of"(["Medication, Order": "Dexchlorpheniramine / Pseudoephedrine"])
	
	//Dextromethorphan / Diphenhydramine / Phenylephrine
		union "More than 1 Of"(["Medication, Order": "Dextromethorphan / Diphenhydramine / Phenylephrine"])
	
	//Dextromethorphan / Doxylamine
		union "More than 1 Of"(["Medication, Order": "Dextromethorphan / Doxylamine"])
	
	//Dicyclomine
		union "More than 1 Of"(["Medication, Order": "Dicyclomine"])
	
	//Dienogest / Estradiol Multiphasic
		union "More than 1 Of"(["Medication, Order": "Dienogest / Estradiol Multiphasic"])
	
	//Dimenhydrinate
		union "More than 1 Of"(["Medication, Order": "Dimenhydrinate"])
	
	//Diphenhydramine / Ibuprofen
		union "More than 1 Of"(["Medication, Order": "Diphenhydramine / Ibuprofen"])
	
	//Diphenhydramine / Phenylephrine
		union "More than 1 Of"(["Medication, Order": "Diphenhydramine / Phenylephrine"])
	
	//Diphenhydramine Hydrochloride
		union "More than 1 Of"(["Medication, Order": "Diphenhydramine Hydrochloride"])
	
	//Dipyridamole
		union "More than 1 Of"(["Medication, Order": "Dipyridamole"])
	
	//Disopyramide
		union "More than 1 Of"(["Medication, Order": "Disopyramide"])
	
	//Drospirenone / Estradiol
		union "More than 1 Of"(["Medication, Order": "Drospirenone / Estradiol"])
	
	//Esterified Estrogens
		union "More than 1 Of"(["Medication, Order": "Esterified Estrogens"])
	
	//Esterified Estrogens / Methyltestosterone
		union "More than 1 Of"(["Medication, Order": "Esterified Estrogens / Methyltestosterone"])
	
	//Estradiol
		union "More than 1 Of"(["Medication, Order": "Estradiol"])
	
	//Estradiol / Norethindrone
		union "More than 1 Of"(["Medication, Order": "Estradiol / Norethindrone"])
	
	//Estropipate
		union "More than 1 Of"(["Medication, Order": "Estropipate"])
	
	//Glyburide
		union "More than 1 Of"(["Medication, Order": "Glyburide"])
	
	//Glyburide / Metformin
		union "More than 1 Of"(["Medication, Order": "Glyburide / Metformin"])
	
	//Guanfacine
		union "More than 1 Of"(["Medication, Order": "Guanfacine"])
	
	//Hydrochlorothiazide / Methyldopa
		union "More than 1 Of"(["Medication, Order": "Hydrochlorothiazide / Methyldopa"])
	
	//Hydroxyzine
		union "More than 1 Of"(["Medication, Order": "Hydroxyzine"])
	
	//Hyoscyamine
		union "More than 1 Of"(["Medication, Order": "Hyoscyamine"])
	
	//Hyoscyamine / Methenamine / Mblue / Phenyl Salicyl
		union "More than 1 Of"(["Medication, Order": "Hyoscyamine / Methenamine / Mblue / Phenyl Salicyl"])
	
	//Hyoscyamine / Methenamine / Mblue / Phenyl Salicyl / Sodium Biphosphate
		union "More than 1 Of"(["Medication, Order": "Hyoscyamine / Methenamine / Mblue / Phenyl Salicyl / Sodium Biphosphate"])
	
	//Hyoscyamine / Methenamine / Mblue / Sodium Biphosphate
		union "More than 1 Of"(["Medication, Order": "Hyoscyamine / Methenamine / Mblue / Sodium Biphosphate"])
	
	//Imipramine
		union "More than 1 Of"(["Medication, Order": "Imipramine"])
	
	//Indomethacin
		union "More than 1 Of"(["Medication, Order": "Indomethacin"])
	
	//Isoxsuprine
		union "More than 1 Of"(["Medication, Order": "Isoxsuprine"])
	
	//Ketorolac Tromethamine
		union "More than 1 Of"(["Medication, Order": "Ketorolac Tromethamine"])
	
	//Meclizine
		union "More than 1 Of"(["Medication, Order": "Meclizine"])
	
	//Megestrol
		union "More than 1 Of"(["Medication, Order": "Megestrol"])
	
	//Meperidine
		union "More than 1 Of"(["Medication, Order": "Meperidine"])
	
	//Meprobamate
		union "More than 1 Of"(["Medication, Order": "Meprobamate"])
	
	//Metaxalone
		union "More than 1 Of"(["Medication, Order": "Metaxalone"])
	
	//Methocarbamol
		union "More than 1 Of"(["Medication, Order": "Methocarbamol"])
	
	//Methyldopa
		union "More than 1 Of"(["Medication, Order": "Methyldopa"])
	
	//Nifedipine
		union "More than 1 Of"(["Medication, Order": "Nifedipine"])
	
	//Nortriptyline
		union "More than 1 Of"(["Medication, Order": "Nortriptyline"])
	
	//Paroxetine
		union "More than 1 Of"(["Medication, Order": "Paroxetine"])
	
	//Phenobarbital
		union "More than 1 Of"(["Medication, Order": "Phenobarbital"])
	
	//Promethazine Hydrochloride
		union "More than 1 Of"(["Medication, Order": "Promethazine Hydrochloride"])
	
	//Protriptyline
		union "More than 1 Of"(["Medication, Order": "Protriptyline"])
	
	//Pseudoephedrine / Triprolidine
		union "More than 1 Of"(["Medication, Order": "Pseudoephedrine / Triprolidine"])
	
	//Trihexyphenidyl
		union "More than 1 Of"(["Medication, Order": "Trihexyphenidyl"])
	
	//Trimipramine
		union "More than 1 Of"(["Medication, Order": "Trimipramine"])
	
	//Triprolidine
		union "More than 1 Of"(["Medication, Order": "Triprolidine"])

define "More than One AntiInfective Order":
	//Anti Infectives, other
	exists ( "More than 1 Of"(["Medication, Order": "Anti Infectives, other"]))

define "More than One Nonbenzodiazepine Hypnotics Order":
	//Nonbenzodiazepine hypnotics
	exists ( "More than 1 Of"(["Medication, Order": "Nonbenzodiazepine hypnotics"]))

define "Two High Risk Medications With Prolonged Duration":
	( "Cumulative Medication Duration"("AntiInfectives During Measurement Period")>= 90
			and "More than One AntiInfective Order"
	)
		or ( "Cumulative Medication Duration"("Hypnotics During Measurement Period")>= 90
				and "More than One Nonbenzodiazepine Hypnotics Order"
		)

define function "Cumulative Medication Duration"(Medication List<"Medication, Order">):
	Sum((collapse(Medication.relevantPeriod))CumulativeDosages
			return all duration in days of CumulativeDosages
	)

define function "More than 1 Of"(Medication List<"Medication, Order">):
	"Medication" OrderMedication1
		with "Medication" OrderMedication2
			such that OrderMedication1.authorDatetime 1 day or more after OrderMedication2.authorDatetime
				and OrderMedication1.authorDatetime during "Measurement Period"
				and OrderMedication2.authorDatetime during "Measurement Period"
		return OrderMedication1