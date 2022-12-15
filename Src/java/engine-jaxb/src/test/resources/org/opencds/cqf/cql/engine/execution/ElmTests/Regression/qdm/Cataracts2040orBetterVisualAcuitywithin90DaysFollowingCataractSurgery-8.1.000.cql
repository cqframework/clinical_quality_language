library Cataracts2040orBetterVisualAcuitywithin90DaysFollowingCataractSurgery version '8.1.000'

using QDM version '5.4'

include MATGlobalCommonFunctions version '4.0.000' called Global

codesystem "SNOMEDCT": 'urn:oid:2.16.840.1.113883.6.96'
codesystem "LOINC": 'urn:oid:2.16.840.1.113883.6.1'

valueset "Acute and Subacute Iridocyclitis": 'urn:oid:2.16.840.1.113883.3.526.3.1241'
valueset "Amblyopia": 'urn:oid:2.16.840.1.113883.3.526.3.1448'
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
valueset "Cloudy Cornea": 'urn:oid:2.16.840.1.113883.3.526.3.1417'
valueset "Corneal Edema": 'urn:oid:2.16.840.1.113883.3.526.3.1418'
valueset "Corneal Opacity and Other Disorders of Cornea": 'urn:oid:2.16.840.1.113883.3.526.3.1419'
valueset "Degeneration of Macula and Posterior Pole": 'urn:oid:2.16.840.1.113883.3.526.3.1453'
valueset "Degenerative Disorders of Globe": 'urn:oid:2.16.840.1.113883.3.526.3.1454'
valueset "Diabetic Macular Edema": 'urn:oid:2.16.840.1.113883.3.526.3.1455'
valueset "Diabetic Retinopathy": 'urn:oid:2.16.840.1.113883.3.526.3.327'
valueset "Disorders of Optic Chiasm": 'urn:oid:2.16.840.1.113883.3.526.3.1457'
valueset "Disorders of Visual Cortex": 'urn:oid:2.16.840.1.113883.3.526.3.1458'
valueset "Disseminated Chorioretinitis and Disseminated Retinochoroiditis": 'urn:oid:2.16.840.1.113883.3.526.3.1459'
valueset "Ethnicity": 'urn:oid:2.16.840.1.114222.4.11.837'
valueset "Focal Chorioretinitis and Focal Retinochoroiditis": 'urn:oid:2.16.840.1.113883.3.526.3.1460'
valueset "Glaucoma": 'urn:oid:2.16.840.1.113883.3.526.3.1423'
valueset "Glaucoma Associated with Congenital Anomalies, Dystrophies, and Systemic Syndromes": 'urn:oid:2.16.840.1.113883.3.526.3.1461'
valueset "Hereditary Choroidal Dystrophies": 'urn:oid:2.16.840.1.113883.3.526.3.1462'
valueset "Hereditary Corneal Dystrophies": 'urn:oid:2.16.840.1.113883.3.526.3.1424'
valueset "Hereditary Retinal Dystrophies": 'urn:oid:2.16.840.1.113883.3.526.3.1463'
valueset "Hypotony of Eye": 'urn:oid:2.16.840.1.113883.3.526.3.1426'
valueset "Injury to Optic Nerve and Pathways": 'urn:oid:2.16.840.1.113883.3.526.3.1427'
valueset "Macular Scar of Posterior Polar": 'urn:oid:2.16.840.1.113883.3.526.3.1559'
valueset "Morgagnian Cataract": 'urn:oid:2.16.840.1.113883.3.526.3.1558'
valueset "Nystagmus and Other Irregular Eye Movements": 'urn:oid:2.16.840.1.113883.3.526.3.1465'
valueset "ONC Administrative Sex": 'urn:oid:2.16.840.1.113762.1.4.1'
valueset "Open Wound of Eyeball": 'urn:oid:2.16.840.1.113883.3.526.3.1430'
valueset "Optic Atrophy": 'urn:oid:2.16.840.1.113883.3.526.3.1466'
valueset "Optic Neuritis": 'urn:oid:2.16.840.1.113883.3.526.3.1467'
valueset "Other and Unspecified Forms of Chorioretinitis and Retinochoroiditis": 'urn:oid:2.16.840.1.113883.3.526.3.1468'
valueset "Other Background Retinopathy and Retinal Vascular Changes": 'urn:oid:2.16.840.1.113883.3.526.3.1469'
valueset "Other Disorders of Optic Nerve": 'urn:oid:2.16.840.1.113883.3.526.3.1471'
valueset "Other Endophthalmitis": 'urn:oid:2.16.840.1.113883.3.526.3.1473'
valueset "Other Proliferative Retinopathy": 'urn:oid:2.16.840.1.113883.3.526.3.1480'
valueset "Pathologic Myopia": 'urn:oid:2.16.840.1.113883.3.526.3.1432'
valueset "Payer": 'urn:oid:2.16.840.1.114222.4.11.3591'
valueset "Posterior Lenticonus": 'urn:oid:2.16.840.1.113883.3.526.3.1433'
valueset "Prior Penetrating Keratoplasty": 'urn:oid:2.16.840.1.113883.3.526.3.1475'
valueset "Purulent Endophthalmitis": 'urn:oid:2.16.840.1.113883.3.526.3.1477'
valueset "Race": 'urn:oid:2.16.840.1.114222.4.11.836'
valueset "Retinal Detachment with Retinal Defect": 'urn:oid:2.16.840.1.113883.3.526.3.1478'
valueset "Retinal Vascular Occlusion": 'urn:oid:2.16.840.1.113883.3.526.3.1479'
valueset "Retrolental Fibroplasias": 'urn:oid:2.16.840.1.113883.3.526.3.1438'
valueset "Scleritis and Episcleritis": 'urn:oid:2.16.840.1.113883.3.526.3.1481'
valueset "Separation of Retinal Layers": 'urn:oid:2.16.840.1.113883.3.526.3.1482'
valueset "Traumatic Cataract": 'urn:oid:2.16.840.1.113883.3.526.3.1443'
valueset "Uveitis": 'urn:oid:2.16.840.1.113883.3.526.3.1444'
valueset "Vascular Disorders of Iris and Ciliary Body": 'urn:oid:2.16.840.1.113883.3.526.3.1445'
valueset "Visual Acuity 20/40 or Better": 'urn:oid:2.16.840.1.113883.3.526.3.1483'
valueset "Visual Field Defects": 'urn:oid:2.16.840.1.113883.3.526.3.1446'

code "Best corrected visual acuity (observable entity)": '419775003' from "SNOMEDCT" display 'Best corrected visual acuity (observable entity)'
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

define "Cataract Surgery Between January and September of Measurement Period":
	["Procedure, Performed": "Cataract Surgery"] CataractSurgery
		where CataractSurgery.relevantPeriod during "Measurement Period"
			and CataractSurgery.relevantPeriod starts 93 days or more before 
			end of "Measurement Period"

define "Numerator":
	"Cataract Surgery Between January and September of Measurement Period" CataractSurgeryPerformed
		with ["Physical Exam, Performed": "Best corrected visual acuity (observable entity)"] VisualAcuityExamPerformed
			such that VisualAcuityExamPerformed.relevantPeriod starts 90 days or less after day of 
			end of CataractSurgeryPerformed.relevantPeriod
				and VisualAcuityExamPerformed.result in "Visual Acuity 20/40 or Better"

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
			union ["Diagnosis": "Cloudy Cornea"]
			union ["Diagnosis": "Corneal Edema"]
			union ["Diagnosis": "Corneal Opacity and Other Disorders of Cornea"]
			union ["Diagnosis": "Degeneration of Macula and Posterior Pole"]
			union ["Diagnosis": "Degenerative Disorders of Globe"]
			union ["Diagnosis": "Diabetic Macular Edema"]
			union ["Diagnosis": "Diabetic Retinopathy"]
			union ["Diagnosis": "Disorders of Optic Chiasm"]
			union ["Diagnosis": "Disorders of Visual Cortex"]
			union ["Diagnosis": "Disseminated Chorioretinitis and Disseminated Retinochoroiditis"]
			union ["Diagnosis": "Focal Chorioretinitis and Focal Retinochoroiditis"]
			union ["Diagnosis": "Glaucoma"]
			union ["Diagnosis": "Glaucoma Associated with Congenital Anomalies, Dystrophies, and Systemic Syndromes"]
			union ["Diagnosis": "Hereditary Choroidal Dystrophies"]
			union ["Diagnosis": "Hereditary Corneal Dystrophies"]
			union ["Diagnosis": "Hereditary Retinal Dystrophies"]
			union ["Diagnosis": "Hypotony of Eye"]
			union ["Diagnosis": "Injury to Optic Nerve and Pathways"]
			union ["Diagnosis": "Macular Scar of Posterior Polar"]
			union ["Diagnosis": "Morgagnian Cataract"]
			union ["Diagnosis": "Nystagmus and Other Irregular Eye Movements"]
			union ["Diagnosis": "Open Wound of Eyeball"]
			union ["Diagnosis": "Optic Atrophy"]
			union ["Diagnosis": "Optic Neuritis"]
			union ["Diagnosis": "Other and Unspecified Forms of Chorioretinitis and Retinochoroiditis"]
			union ["Diagnosis": "Other Background Retinopathy and Retinal Vascular Changes"]
			union ["Diagnosis": "Other Disorders of Optic Nerve"]
			union ["Diagnosis": "Other Endophthalmitis"]
			union ["Diagnosis": "Other Proliferative Retinopathy"]
			union ["Diagnosis": "Pathologic Myopia"]
			union ["Diagnosis": "Posterior Lenticonus"]
			union ["Diagnosis": "Prior Penetrating Keratoplasty"]
			union ["Diagnosis": "Purulent Endophthalmitis"]
			union ["Diagnosis": "Retinal Detachment with Retinal Defect"]
			union ["Diagnosis": "Retinal Vascular Occlusion"]
			union ["Diagnosis": "Retrolental Fibroplasias"]
			union ["Diagnosis": "Scleritis and Episcleritis"]
			union ["Diagnosis": "Separation of Retinal Layers"]
			union ["Diagnosis": "Traumatic Cataract"]
			union ["Diagnosis": "Uveitis"]
			union ["Diagnosis": "Vascular Disorders of Iris and Ciliary Body"]
			union ["Diagnosis": "Visual Field Defects"] ) ComorbidDiagnosis
			such that ComorbidDiagnosis.prevalencePeriod overlaps before CataractSurgeryPerformed.relevantPeriod

define "Initial Population":
	"Cataract Surgery Between January and September of Measurement Period" CataractSurgeryPerformed
		with ["Patient Characteristic Birthdate": "Birth date"] BirthDate
			such that Global."CalendarAgeInYearsAt"(BirthDate.birthDatetime, start of "Measurement Period")>= 18