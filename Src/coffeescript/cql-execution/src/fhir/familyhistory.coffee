
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
@class FamilyHistoryRelationConditionComponent
@exports  FamilyHistoryRelationConditionComponent as FamilyHistoryRelationConditionComponent
###
class FamilyHistoryRelationConditionComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  The actual condition specified. Could be a coded condition (like MI or Diabetes) or a less specific string like 'cancer' depending on how much is known about the condition and the capabilities of the creating system.
  @returns {CodeableConcept}
  ###
  type: -> if @json['type'] then new CodeableConcept(@json['type'])
  
  ###*
  Indicates what happened as a result of this condition.  If the condition resulted in death, deceased date is captured on the relation.
  @returns {CodeableConcept}
  ###
  outcome: -> if @json['outcome'] then new CodeableConcept(@json['outcome'])
  
  onsetAge:->  new Quantity(@json['onsetAge'])
  ###*
  Either the age of onset, range of approximate age or descriptive string can be recorded.  For conditions with multiple occurrences, this describes the first known occurrence.
  @returns {Range}
  ###
  onsetRange: -> if @json['onsetRange'] then new Range(@json['onsetRange'])
  ###*
  Either the age of onset, range of approximate age or descriptive string can be recorded.  For conditions with multiple occurrences, this describes the first known occurrence.
  @returns {Array} an array of {@link String} objects
  ###
  onsetString:-> @json['onsetString']
  
  ###*
  An area where general notes can be placed about this specific condition.
  @returns {Array} an array of {@link String} objects
  ###
  note:-> @json['note']
  

###* 
Embedded class
@class FamilyHistoryRelationComponent
@exports  FamilyHistoryRelationComponent as FamilyHistoryRelationComponent
###
class FamilyHistoryRelationComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  This will either be a name or a description.  E.g. "Aunt Susan", "my cousin with the red hair".
  @returns {Array} an array of {@link String} objects
  ###
  name:-> @json['name']
  
  ###*
  The type of relationship this person has to the patient (father, mother, brother etc.).
  @returns {CodeableConcept}
  ###
  relationship: -> if @json['relationship'] then new CodeableConcept(@json['relationship'])
  
  ###*
  The actual or approximate date of birth of the relative.
  @returns {Period}
  ###
  bornPeriod: -> if @json['bornPeriod'] then new Period(@json['bornPeriod'])
  ###*
  The actual or approximate date of birth of the relative.
  @returns {Array} an array of {@link Date} objects
  ###
  bornDate:-> if @json['bornDate'] then DT.DateTime.parse(@json['bornDate'])
  ###*
  The actual or approximate date of birth of the relative.
  @returns {Array} an array of {@link String} objects
  ###
  bornString:-> @json['bornString']
  
  ageAge:->  new Quantity(@json['ageAge'])
  ###*
  The actual or approximate age of the relative at the time the family history is recorded.
  @returns {Range}
  ###
  ageRange: -> if @json['ageRange'] then new Range(@json['ageRange'])
  ###*
  The actual or approximate age of the relative at the time the family history is recorded.
  @returns {Array} an array of {@link String} objects
  ###
  ageString:-> @json['ageString']
  
  ###*
  If this resource is indicating that the related person is deceased, then an indicator of whether the person is deceased (yes) or not (no) or the age or age range or description of age at death - can be indicated here. If the reason for death is known, then it can be indicated in the outcome code of the condition - in this case the deceased property should still be set.
  @returns {Array} an array of {@link boolean} objects
  ###
  deceasedBoolean:-> @json['deceasedBoolean']
  deceasedAge:->  new Quantity(@json['deceasedAge'])
  ###*
  If this resource is indicating that the related person is deceased, then an indicator of whether the person is deceased (yes) or not (no) or the age or age range or description of age at death - can be indicated here. If the reason for death is known, then it can be indicated in the outcome code of the condition - in this case the deceased property should still be set.
  @returns {Range}
  ###
  deceasedRange: -> if @json['deceasedRange'] then new Range(@json['deceasedRange'])
  ###*
  If this resource is indicating that the related person is deceased, then an indicator of whether the person is deceased (yes) or not (no) or the age or age range or description of age at death - can be indicated here. If the reason for death is known, then it can be indicated in the outcome code of the condition - in this case the deceased property should still be set.
  @returns {Array} an array of {@link Date} objects
  ###
  deceasedDate:-> if @json['deceasedDate'] then DT.DateTime.parse(@json['deceasedDate'])
  ###*
  If this resource is indicating that the related person is deceased, then an indicator of whether the person is deceased (yes) or not (no) or the age or age range or description of age at death - can be indicated here. If the reason for death is known, then it can be indicated in the outcome code of the condition - in this case the deceased property should still be set.
  @returns {Array} an array of {@link String} objects
  ###
  deceasedString:-> @json['deceasedString']
  
  ###*
  This property allows a non condition-specific note to the made about the related person. Ideally, the note would be in the condition property, but this is not always possible.
  @returns {Array} an array of {@link String} objects
  ###
  note:-> @json['note']
  
  ###*
  The significant Conditions (or condition) that the family member had. This is a repeating section to allow a system to represent more than one condition per resource, though there is nothing stopping multiple resources - one per condition.
  @returns {Array} an array of {@link FamilyHistoryRelationConditionComponent} objects
  ###
  condition: ->
    if @json['condition']
      for item in @json['condition']
        new FamilyHistoryRelationConditionComponent(item)
  
###*
Significant health events and conditions for people related to the subject relevant in the context of care for the subject.
@class FamilyHistory
@exports FamilyHistory as FamilyHistory
###
class FamilyHistory extends DomainResource
  constructor: (@json) ->
    super(@json)
  ###*
  This records identifiers associated with this family history record that are defined by business processes and/ or used to refer to it when a direct URL reference to the resource itself is not appropriate (e.g. in CDA documents, or in written / printed documentation).
  @returns {Array} an array of {@link Identifier} objects
  ###
  identifier: ->
    if @json['identifier']
      for item in @json['identifier']
        new Identifier(item)
  
  ###*
  The person who this history concerns.
  @returns {Reference}
  ###
  patient: -> if @json['patient'] then new Reference(@json['patient'])
  
  ###*
  The date (and possibly time) when the family history was taken.
  @returns {Array} an array of {@link Date} objects
  ###
  date:-> if @json['date'] then DT.DateTime.parse(@json['date'])
  
  ###*
  Conveys information about family history not specific to individual relations.
  @returns {Array} an array of {@link String} objects
  ###
  note:-> @json['note']
  
  ###*
  The related person. Each FamilyHistory resource contains the entire family history for a single person.
  @returns {Array} an array of {@link FamilyHistoryRelationComponent} objects
  ###
  relation: ->
    if @json['relation']
      for item in @json['relation']
        new FamilyHistoryRelationComponent(item)
  



module.exports.FamilyHistory = FamilyHistory
