
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
@class ContractSignerComponent
@exports  ContractSignerComponent as ContractSignerComponent
###
class ContractSignerComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  Party or role who is signing.
  @returns {Coding}
  ###
  type: -> if @json['type'] then new Coding(@json['type'])
  
  ###*
  The DSIG signature contents in Base64.
  @returns {Array} an array of {@link String} objects
  ###
  singnature:-> @json['singnature']
  

###* 
Embedded class
@class ContractTermComponent
@exports  ContractTermComponent as ContractTermComponent
###
class ContractTermComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  Unique Id for this particular term.
  @returns {Identifier}
  ###
  identifier: -> if @json['identifier'] then new Identifier(@json['identifier'])
  
  ###*
  The type of the term.
  @returns {CodeableConcept}
  ###
  type: -> if @json['type'] then new CodeableConcept(@json['type'])
  
  ###*
  The subttype of the term which is appropriate to the term type.
  @returns {CodeableConcept}
  ###
  subtype: -> if @json['subtype'] then new CodeableConcept(@json['subtype'])
  
  ###*
  Who or what the contract term is about.
  @returns {Reference}
  ###
  subject: -> if @json['subject'] then new Reference(@json['subject'])
  
  ###*
  Human readable form of the term of the contract.
  @returns {Array} an array of {@link String} objects
  ###
  text:-> @json['text']
  
###*
A formal agreement between parties regarding the conduct of business, exchange of information or other matters.
@class Contract
@exports Contract as Contract
###
class Contract extends DomainResource
  constructor: (@json) ->
    super(@json)
  ###*
  Unique Id for this contract.
  @returns {Array} an array of {@link Identifier} objects
  ###
  identifier: ->
    if @json['identifier']
      for item in @json['identifier']
        new Identifier(item)
  
  ###*
  Who and/or what this is about: typically Patient, Organization, property.
  @returns {Array} an array of {@link Reference} objects
  ###
  subject: ->
    if @json['subject']
      for item in @json['subject']
        new Reference(item)
  
  ###*
  Type of contract (Privacy-Security, Agreement, Insurance).
  @returns {CodeableConcept}
  ###
  type: -> if @json['type'] then new CodeableConcept(@json['type'])
  
  ###*
  More specific type of contract (Privacy, Disclosure-Authorization, Advanced-Directive, DNR, Authorization-to-Treat).
  @returns {Array} an array of {@link CodeableConcept} objects
  ###
  subtype: ->
    if @json['subtype']
      for item in @json['subtype']
        new CodeableConcept(item)
  
  ###*
  When this was issued.
  @returns {Array} an array of {@link Date} objects
  ###
  issued:-> if @json['issued'] then DT.DateTime.parse(@json['issued'])
  
  ###*
  Relevant time/time-period when applicable.
  @returns {Period}
  ###
  applies: -> if @json['applies'] then new Period(@json['applies'])
  
  ###*
  The number of repetitions of a service or product.
  @returns {Quantity}
  ###
  quantity: -> if @json['quantity'] then new Quantity(@json['quantity'])
  
  ###*
  The unit price product.
  @returns {Money}
  ###
  unitPrice: -> if @json['unitPrice'] then new Money(@json['unitPrice'])
  
  ###*
  A real number that represents a multiplier used in determining the overall value of services delivered and/or goods received. The concept of a Factor allows for a discount or surcharge multiplier to be applied to a monetary amount.
  @returns {Array} an array of {@link Number} objects
  ###
  factor:-> @json['factor']
  
  ###*
  An amount that expresses the weighting (based on difficulty, cost and/or resource intensiveness) associated with the good or service delivered. The concept of Points allows for assignment of point values for services and/or goods, such that a monetary amount can be assigned to each point.
  @returns {Array} an array of {@link Number} objects
  ###
  points:-> @json['points']
  
  ###*
  The quantity times the unit price for an addtional service or product or charge. For example, the formula: unit Quantity * unit Price (Cost per Point) * factor Number  * points = net Amount. Quantity, factor and points are assumed to be 1 if not supplied.
  @returns {Money}
  ###
  net: -> if @json['net'] then new Money(@json['net'])
  
  ###*
  Contract author or responsible party.
  @returns {Array} an array of {@link Reference} objects
  ###
  author: ->
    if @json['author']
      for item in @json['author']
        new Reference(item)
  
  ###*
  First Party to the contract, may be the party who confers or delegates the rights defined in the contract.
  @returns {Array} an array of {@link Reference} objects
  ###
  grantor: ->
    if @json['grantor']
      for item in @json['grantor']
        new Reference(item)
  
  ###*
  The Second party to the contract, may be the party who accepts obligations or be that to which rights are delegated.
  @returns {Array} an array of {@link Reference} objects
  ###
  grantee: ->
    if @json['grantee']
      for item in @json['grantee']
        new Reference(item)
  
  ###*
  Who witnesses the contract.
  @returns {Array} an array of {@link Reference} objects
  ###
  witness: ->
    if @json['witness']
      for item in @json['witness']
        new Reference(item)
  
  ###*
  First Party to the contract, may be the party who confers or delegates the rights defined in the contract.
  @returns {Array} an array of {@link Reference} objects
  ###
  executor: ->
    if @json['executor']
      for item in @json['executor']
        new Reference(item)
  
  ###*
  First Party to the contract, may be the party who confers or delegates the rights defined in the contract.
  @returns {Array} an array of {@link Reference} objects
  ###
  notary: ->
    if @json['notary']
      for item in @json['notary']
        new Reference(item)
  
  ###*
  List or contract signatures.
  @returns {Array} an array of {@link ContractSignerComponent} objects
  ###
  signer: ->
    if @json['signer']
      for item in @json['signer']
        new ContractSignerComponent(item)
  
  ###*
  A contract provision.
  @returns {Array} an array of {@link ContractTermComponent} objects
  ###
  term: ->
    if @json['term']
      for item in @json['term']
        new ContractTermComponent(item)
  
  ###*
  Friendly Human readable form (might be a reference to the UI used to capture the contract).
  @returns {Attachment}
  ###
  friendly: -> if @json['friendly'] then new Attachment(@json['friendly'])
  
  ###*
  Legal text in Human readable form.
  @returns {Attachment}
  ###
  legal: -> if @json['legal'] then new Attachment(@json['legal'])
  
  ###*
  Computable Policy rules (e.g. XACML, DKAL, SecPal).
  @returns {Attachment}
  ###
  rule: -> if @json['rule'] then new Attachment(@json['rule'])
  



module.exports.Contract = Contract
