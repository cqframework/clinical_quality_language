
# Copyright (c) 2014 The MITRE Corporation
# All rights reserved.
# 
# Redistribution and use in source and binary forms, with or without modification, 
# are permitted provided that the following conditions are met:
# 
#     * Redistributions of source code must retain the above copyright notice, this 
#       list of conditions and the following disclaimer.
#     * Redistributions in binary form must reproduce the above copyright notice, 
#       this list of conditions and the following disclaimer in the documentation 
#       and/or other materials provided with the distribution.
#     * Neither the name of HL7 nor the names of its contributors may be used to 
#       endorse or promote products derived from this software without specific 
#       prior written permission.
# 
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
# ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
# WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
# IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
# INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT 
# NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR 
# PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
# WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
# ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
# POSSIBILITY OF SUCH DAMAGE.
DT = require '../cql-datatypes'
CORE = require('./core')
Element = CORE.Element
Resource = CORE.Resource
Timing = CORE.Timing
Period = CORE.Period
Parameters = CORE.Parameters
Coding = CORE.Coding
Resource = CORE.Resource
Range = CORE.Range
Quantity = CORE.Quantity
Attachment = CORE.Attachment
BackboneElement = CORE.BackboneElement
DomainResource = CORE.DomainResource
ContactPoint = CORE.ContactPoint
ElementDefinition = CORE.ElementDefinition
Extension = CORE.Extension
HumanName = CORE.HumanName
Address = CORE.Address
Ratio = CORE.Ratio
SampledData = CORE.SampledData
Reference = CORE.Reference
CodeableConcept = CORE.CodeableConcept
Identifier = CORE.Identifier
Narrative = CORE.Narrative
Element = CORE.Element

###* 
Embedded class
@class MedicationDispenseDispenseDosageComponent
@exports  MedicationDispenseDispenseDosageComponent as MedicationDispenseDispenseDosageComponent
###
class MedicationDispenseDispenseDosageComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  Additional instructions such as "Swallow with plenty of water" which may or may not be coded.
  @returns {CodeableConcept}
  ###
  additionalInstructions: -> if @json['additionalInstructions'] then new CodeableConcept(@json['additionalInstructions'])
  
  ###*
  The timing schedule for giving the medication to the patient.  The Schedule data type allows many different expressions, for example.  "Every  8 hours"; "Three times a day"; "1/2 an hour before breakfast for 10 days from 23-Dec 2011:";  "15 Oct 2013, 17 Oct 2013 and 1 Nov 2013".
  @returns {Array} an array of {@link Date} objects
  ###
  scheduleDateTime:-> if @json['scheduleDateTime'] then DT.DateTime.parse(@json['scheduleDateTime'])
  ###*
  The timing schedule for giving the medication to the patient.  The Schedule data type allows many different expressions, for example.  "Every  8 hours"; "Three times a day"; "1/2 an hour before breakfast for 10 days from 23-Dec 2011:";  "15 Oct 2013, 17 Oct 2013 and 1 Nov 2013".
  @returns {Period}
  ###
  schedulePeriod: -> if @json['schedulePeriod'] then new Period(@json['schedulePeriod'])
  ###*
  The timing schedule for giving the medication to the patient.  The Schedule data type allows many different expressions, for example.  "Every  8 hours"; "Three times a day"; "1/2 an hour before breakfast for 10 days from 23-Dec 2011:";  "15 Oct 2013, 17 Oct 2013 and 1 Nov 2013".
  @returns {Timing}
  ###
  scheduleTiming: -> if @json['scheduleTiming'] then new Timing(@json['scheduleTiming'])
  
  ###*
  If set to true or if specified as a CodeableConcept, indicates that the medication is only taken when needed within the specified schedule rather than at every scheduled dose.  If a CodeableConcept is present, it indicates the pre-condition for taking the Medication.
  @returns {Array} an array of {@link boolean} objects
  ###
  asNeededBoolean:-> @json['asNeededBoolean']
  ###*
  If set to true or if specified as a CodeableConcept, indicates that the medication is only taken when needed within the specified schedule rather than at every scheduled dose.  If a CodeableConcept is present, it indicates the pre-condition for taking the Medication.
  @returns {CodeableConcept}
  ###
  asNeededCodeableConcept: -> if @json['asNeededCodeableConcept'] then new CodeableConcept(@json['asNeededCodeableConcept'])
  
  ###*
  A coded specification of the anatomic site where the medication first enters the body.
  @returns {CodeableConcept}
  ###
  site: -> if @json['site'] then new CodeableConcept(@json['site'])
  
  ###*
  A code specifying the route or physiological path of administration of a therapeutic agent into or onto a subject.
  @returns {CodeableConcept}
  ###
  route: -> if @json['route'] then new CodeableConcept(@json['route'])
  
  ###*
  A coded value indicating the method by which the medication is introduced into or onto the body. Most commonly used for injections.  Examples:  Slow Push; Deep IV.

Terminologies used often pre-coordinate this term with the route and or form of administration.
  @returns {CodeableConcept}
  ###
  method: -> if @json['method'] then new CodeableConcept(@json['method'])
  
  ###*
  The amount of therapeutic or other substance given at one administration event.
  @returns {Quantity}
  ###
  quantity: -> if @json['quantity'] then new Quantity(@json['quantity'])
  
  ###*
  Identifies the speed with which the substance is introduced into the subject. Typically the rate for an infusion. 200ml in 2 hours.
  @returns {Ratio}
  ###
  rate: -> if @json['rate'] then new Ratio(@json['rate'])
  
  ###*
  The maximum total quantity of a therapeutic substance that may be administered to a subject over the period of time,  e.g. 1000mg in 24 hours.
  @returns {Ratio}
  ###
  maxDosePerPeriod: -> if @json['maxDosePerPeriod'] then new Ratio(@json['maxDosePerPeriod'])
  

###* 
Embedded class
@class MedicationDispenseDispenseComponent
@exports  MedicationDispenseDispenseComponent as MedicationDispenseDispenseComponent
###
class MedicationDispenseDispenseComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  Identifier assigned by the dispensing facility.   This is an identifier assigned outside FHIR.
  @returns {Identifier}
  ###
  identifier: -> if @json['identifier'] then new Identifier(@json['identifier'])
  
  ###*
  A code specifying the state of the dispense event.
  @returns {Array} an array of {@link String} objects
  ###
  status:-> @json['status']
  
  ###*
  Indicates the type of dispensing event that is performed. Examples include: Trial Fill, Completion of Trial, Partial Fill, Emergency Fill, Samples, etc.
  @returns {CodeableConcept}
  ###
  type: -> if @json['type'] then new CodeableConcept(@json['type'])
  
  ###*
  The amount of medication that has been dispensed. Includes unit of measure.
  @returns {Quantity}
  ###
  quantity: -> if @json['quantity'] then new Quantity(@json['quantity'])
  
  ###*
  Identifies the medication being administered. This is either a link to a resource representing the details of the medication or a simple attribute carrying a code that identifies the medication from a known list of medications.
  @returns {Reference}
  ###
  medication: -> if @json['medication'] then new Reference(@json['medication'])
  
  ###*
  The time when the dispensed product was packaged and reviewed.
  @returns {Array} an array of {@link Date} objects
  ###
  whenPrepared:-> if @json['whenPrepared'] then DT.DateTime.parse(@json['whenPrepared'])
  
  ###*
  The time the dispensed product was provided to the patient or their representative.
  @returns {Array} an array of {@link Date} objects
  ###
  whenHandedOver:-> if @json['whenHandedOver'] then DT.DateTime.parse(@json['whenHandedOver'])
  
  ###*
  Identification of the facility/location where the medication was shipped to, as part of the dispense event.
  @returns {Reference}
  ###
  destination: -> if @json['destination'] then new Reference(@json['destination'])
  
  ###*
  Identifies the person who picked up the medication.  This will usually be a patient or their carer, but some cases exist where it can be a healthcare professional.
  @returns {Array} an array of {@link Reference} objects
  ###
  receiver: ->
    if @json['receiver']
      for item in @json['receiver']
        new Reference(item)
  
  ###*
  Indicates how the medication is to be used by the patient.
  @returns {Array} an array of {@link MedicationDispenseDispenseDosageComponent} objects
  ###
  dosage: ->
    if @json['dosage']
      for item in @json['dosage']
        new MedicationDispenseDispenseDosageComponent(item)
  

###* 
Embedded class
@class MedicationDispenseSubstitutionComponent
@exports  MedicationDispenseSubstitutionComponent as MedicationDispenseSubstitutionComponent
###
class MedicationDispenseSubstitutionComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  A code signifying whether a different drug was dispensed from what was prescribed.
  @returns {CodeableConcept}
  ###
  type: -> if @json['type'] then new CodeableConcept(@json['type'])
  
  ###*
  Indicates the reason for the substitution of (or lack of substitution) from what was prescribed.
  @returns {Array} an array of {@link CodeableConcept} objects
  ###
  reason: ->
    if @json['reason']
      for item in @json['reason']
        new CodeableConcept(item)
  
  ###*
  The person or organization that has primary responsibility for the substitution.
  @returns {Array} an array of {@link Reference} objects
  ###
  responsibleParty: ->
    if @json['responsibleParty']
      for item in @json['responsibleParty']
        new Reference(item)
  
###*
Dispensing a medication to a named patient.  This includes a description of the supply provided and the instructions for administering the medication.
@class MedicationDispense
@exports MedicationDispense as MedicationDispense
###
class MedicationDispense extends DomainResource
  constructor: (@json) ->
    super(@json)
  ###*
  Identifier assigned by the dispensing facility - this is an identifier assigned outside FHIR.
  @returns {Identifier}
  ###
  identifier: -> if @json['identifier'] then new Identifier(@json['identifier'])
  
  ###*
  A code specifying the state of the set of dispense events.
  @returns {Array} an array of {@link String} objects
  ###
  status:-> @json['status']
  
  ###*
  A link to a resource representing the person to whom the medication will be given.
  @returns {Reference}
  ###
  patient: -> if @json['patient'] then new Reference(@json['patient'])
  
  ###*
  The individual responsible for dispensing the medication.
  @returns {Reference}
  ###
  dispenser: -> if @json['dispenser'] then new Reference(@json['dispenser'])
  
  ###*
  Indicates the medication order that is being dispensed against.
  @returns {Array} an array of {@link Reference} objects
  ###
  authorizingPrescription: ->
    if @json['authorizingPrescription']
      for item in @json['authorizingPrescription']
        new Reference(item)
  
  ###*
  Indicates the details of the dispense event such as the days supply and quantity of medication dispensed.
  @returns {Array} an array of {@link MedicationDispenseDispenseComponent} objects
  ###
  dispense: ->
    if @json['dispense']
      for item in @json['dispense']
        new MedicationDispenseDispenseComponent(item)
  
  ###*
  Indicates whether or not substitution was made as part of the dispense.  In some cases substitution will be expected but doesn't happen, in other cases substitution is not expected but does happen.  This block explains what substitition did or did not happen and why.
  @returns {MedicationDispenseSubstitutionComponent}
  ###
  substitution: -> if @json['substitution'] then new MedicationDispenseSubstitutionComponent(@json['substitution'])
  



module.exports.MedicationDispense = MedicationDispense
