library Test version '0.0.000'

using QDM version '5.3'

valueset "Dapsone and pyrimethamine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1202' version 'urn:hl7:version:eCQM%20Update%202017-05-05'
valueset "Leucovorin": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1205' version 'urn:hl7:version:eCQM%20Update%202017-05-05'

parameter "Measurement Period" Interval<DateTime>

context Patient

define "CD4+ Count With Results and Medications 2":
                ["Medication, Order": "Leucovorin"] MOL
                                with ["Medication, Order": "Dapsone and pyrimethamine"] DapsoneOrder

                                // fdafds this statement
                                                such that DapsoneOrder.authorDatetime occurs 1 days or less after MOL.authorDatetime
                                with ["Medication, Order": "Dapsone and pyrimethamine"] MODP
                                                such that MODP.authorDatetime occurs 1 day or less after MOL.authorDatetime
                                where MOL.authorDatetime is not null