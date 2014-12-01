
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
A request for a procedure to be performed. May be a proposal or an order.
@class ProcedureRequest
@exports ProcedureRequest as ProcedureRequest
###
class ProcedureRequest extends DomainResource
  constructor: (@json) ->
    super(@json)
  ###*
  Identifiers assigned to this order by the order or by the receiver.
  @returns {Array} an array of {@link Identifier} objects
  ###
  identifier: ->
    if @json['identifier']
      for item in @json['identifier']
        new Identifier(item)
  
  ###*
  The patient who will receive the procedure.
  @returns {Reference}
  ###
  subject: -> if @json['subject'] then new Reference(@json['subject'])
  
  ###*
  The specific procedure that is ordered. Use text if the exact nature of the procedure can't be coded.
  @returns {CodeableConcept}
  ###
  type: -> if @json['type'] then new CodeableConcept(@json['type'])
  
  ###*
  The site where the procedure is to be performed.
  @returns {Array} an array of {@link CodeableConcept} objects
  ###
  bodySite: ->
    if @json['bodySite']
      for item in @json['bodySite']
        new CodeableConcept(item)
  
  ###*
  The reason why the procedure is proposed or ordered. This procedure request may be motivated by a Condition for instance.
  @returns {Array} an array of {@link CodeableConcept} objects
  ###
  indication: ->
    if @json['indication']
      for item in @json['indication']
        new CodeableConcept(item)
  
  ###*
  The timing schedule for the proposed or ordered procedure. The Schedule data type allows many different expressions, for example. "Every 8 hours"; "Three times a day"; "1/2 an hour before breakfast for 10 days from 23-Dec 2011:"; "15 Oct 2013, 17 Oct 2013 and 1 Nov 2013".
  @returns {Array} an array of {@link Date} objects
  ###
  timingDateTime:-> if @json['timingDateTime'] then DT.DateTime.parse(@json['timingDateTime'])
  ###*
  The timing schedule for the proposed or ordered procedure. The Schedule data type allows many different expressions, for example. "Every 8 hours"; "Three times a day"; "1/2 an hour before breakfast for 10 days from 23-Dec 2011:"; "15 Oct 2013, 17 Oct 2013 and 1 Nov 2013".
  @returns {Period}
  ###
  timingPeriod: -> if @json['timingPeriod'] then new Period(@json['timingPeriod'])
  ###*
  The timing schedule for the proposed or ordered procedure. The Schedule data type allows many different expressions, for example. "Every 8 hours"; "Three times a day"; "1/2 an hour before breakfast for 10 days from 23-Dec 2011:"; "15 Oct 2013, 17 Oct 2013 and 1 Nov 2013".
  @returns {Timing}
  ###
  timingTiming: -> if @json['timingTiming'] then new Timing(@json['timingTiming'])
  
  ###*
  The encounter within which the procedure proposal or request was created.
  @returns {Reference}
  ###
  encounter: -> if @json['encounter'] then new Reference(@json['encounter'])
  
  ###*
  E.g. surgeon, anaethetist, endoscopist.
  @returns {Reference}
  ###
  performer: -> if @json['performer'] then new Reference(@json['performer'])
  
  ###*
  The status of the order.
  @returns {Array} an array of {@link String} objects
  ###
  status:-> @json['status']
  
  ###*
  The status of the order.
  @returns {Array} an array of {@link String} objects
  ###
  mode:-> @json['mode']
  
  ###*
  Any other notes associated with this proposal or order - e.g., provider instructions.
  @returns {Array} an array of {@link String} objects
  ###
  notes:-> @json['notes']
  
  ###*
  If a CodeableConcept is present, it indicates the pre-condition for performing the procedure.
  @returns {Array} an array of {@link boolean} objects
  ###
  asNeededBoolean:-> @json['asNeededBoolean']
  ###*
  If a CodeableConcept is present, it indicates the pre-condition for performing the procedure.
  @returns {CodeableConcept}
  ###
  asNeededCodeableConcept: -> if @json['asNeededCodeableConcept'] then new CodeableConcept(@json['asNeededCodeableConcept'])
  
  ###*
  The time when the request was made.
  @returns {Array} an array of {@link Date} objects
  ###
  orderedOn:-> if @json['orderedOn'] then DT.DateTime.parse(@json['orderedOn'])
  
  ###*
  The healthcare professional responsible for proposing or ordering the procedure.
  @returns {Reference}
  ###
  orderer: -> if @json['orderer'] then new Reference(@json['orderer'])
  
  ###*
  The clinical priority associated with this order.
  @returns {Array} an array of {@link String} objects
  ###
  priority:-> @json['priority']
  



module.exports.ProcedureRequest = ProcedureRequest
