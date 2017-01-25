
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
@class ProcedurePerformerComponent
@exports  ProcedurePerformerComponent as ProcedurePerformerComponent
###
class ProcedurePerformerComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  The practitioner who was involved in the procedure.
  @returns {Reference}
  ###
  person: -> if @json['person'] then new Reference(@json['person'])
  
  ###*
  E.g. surgeon, anaethetist, endoscopist.
  @returns {CodeableConcept}
  ###
  role: -> if @json['role'] then new CodeableConcept(@json['role'])
  

###* 
Embedded class
@class ProcedureRelatedItemComponent
@exports  ProcedureRelatedItemComponent as ProcedureRelatedItemComponent
###
class ProcedureRelatedItemComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  The nature of the relationship.
  @returns {Array} an array of {@link String} objects
  ###
  type:-> @json['type']
  
  ###*
  The related item - e.g. a procedure.
  @returns {Reference}
  ###
  target: -> if @json['target'] then new Reference(@json['target'])
  
###*
An action that is performed on a patient. This can be a physical 'thing' like an operation, or less invasive like counseling or hypnotherapy.
@class Procedure
@exports Procedure as Procedure
###
class Procedure extends DomainResource
  constructor: (@json) ->
    super(@json)
  ###*
  This records identifiers associated with this procedure that are defined by business processed and/ or used to refer to it when a direct URL reference to the resource itself is not appropriate (e.g. in CDA documents, or in written / printed documentation).
  @returns {Array} an array of {@link Identifier} objects
  ###
  identifier: ->
    if @json['identifier']
      for item in @json['identifier']
        new Identifier(item)
  
  ###*
  The person on whom the procedure was performed.
  @returns {Reference}
  ###
  patient: -> if @json['patient'] then new Reference(@json['patient'])
  
  ###*
  The specific procedure that is performed. Use text if the exact nature of the procedure can't be coded.
  @returns {CodeableConcept}
  ###
  type: -> if @json['type'] then new CodeableConcept(@json['type'])
  
  ###*
  Detailed and structured anatomical location information. Multiple locations are allowed - e.g. multiple punch biopsies of a lesion.
  @returns {Array} an array of {@link CodeableConcept} objects
  ###
  bodySite: ->
    if @json['bodySite']
      for item in @json['bodySite']
        new CodeableConcept(item)
  
  ###*
  The reason why the procedure was performed. This may be due to a Condition, may be coded entity of some type, or may simply be present as text.
  @returns {Array} an array of {@link CodeableConcept} objects
  ###
  indication: ->
    if @json['indication']
      for item in @json['indication']
        new CodeableConcept(item)
  
  ###*
  Limited to 'real' people rather than equipment.
  @returns {Array} an array of {@link ProcedurePerformerComponent} objects
  ###
  performer: ->
    if @json['performer']
      for item in @json['performer']
        new ProcedurePerformerComponent(item)
  
  ###*
  The dates over which the procedure was performed. Allows a period to support complex procedures that span more than one date, and also allows for the length of the procedure to be captured.
  @returns {Period}
  ###
  date: -> if @json['date'] then new Period(@json['date'])
  
  ###*
  The encounter during which the procedure was performed.
  @returns {Reference}
  ###
  encounter: -> if @json['encounter'] then new Reference(@json['encounter'])
  
  ###*
  What was the outcome of the procedure - did it resolve reasons why the procedure was performed?.
  @returns {Array} an array of {@link String} objects
  ###
  outcome:-> @json['outcome']
  
  ###*
  This could be a histology result. There could potentially be multiple reports - e.g. if this was a procedure that made multiple biopsies.
  @returns {Array} an array of {@link Reference} objects
  ###
  report: ->
    if @json['report']
      for item in @json['report']
        new Reference(item)
  
  ###*
  Any complications that occurred during the procedure, or in the immediate post-operative period. These are generally tracked separately from the notes, which typically will describe the procedure itself rather than any 'post procedure' issues.
  @returns {Array} an array of {@link CodeableConcept} objects
  ###
  complication: ->
    if @json['complication']
      for item in @json['complication']
        new CodeableConcept(item)
  
  ###*
  If the procedure required specific follow up - e.g. removal of sutures. The followup may be represented as a simple note, or potentially could be more complex in which case the CarePlan resource can be used.
  @returns {Array} an array of {@link String} objects
  ###
  followUp:-> @json['followUp']
  
  ###*
  Procedures may be related to other items such as procedures or medications. For example treating wound dehiscence following a previous procedure.
  @returns {Array} an array of {@link ProcedureRelatedItemComponent} objects
  ###
  relatedItem: ->
    if @json['relatedItem']
      for item in @json['relatedItem']
        new ProcedureRelatedItemComponent(item)
  
  ###*
  Any other notes about the procedure - e.g. the operative notes.
  @returns {Array} an array of {@link String} objects
  ###
  notes:-> @json['notes']
  



module.exports.Procedure = Procedure
