
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
@class DocumentReferenceRelatesToComponent
@exports  DocumentReferenceRelatesToComponent as DocumentReferenceRelatesToComponent
###
class DocumentReferenceRelatesToComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  The type of relationship that this document has with anther document.
  @returns {Array} an array of {@link String} objects
  ###
  code:-> @json['code']
  
  ###*
  The target document of this relationship.
  @returns {Reference}
  ###
  target: -> if @json['target'] then new Reference(@json['target'])
  

###* 
Embedded class
@class DocumentReferenceServiceParameterComponent
@exports  DocumentReferenceServiceParameterComponent as DocumentReferenceServiceParameterComponent
###
class DocumentReferenceServiceParameterComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  The name of a parameter.
  @returns {Array} an array of {@link String} objects
  ###
  name:-> @json['name']
  
  ###*
  The value of the named parameter.
  @returns {Array} an array of {@link String} objects
  ###
  value:-> @json['value']
  

###* 
Embedded class
@class DocumentReferenceServiceComponent
@exports  DocumentReferenceServiceComponent as DocumentReferenceServiceComponent
###
class DocumentReferenceServiceComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  The type of the service that can be used to access the documents.
  @returns {CodeableConcept}
  ###
  type: -> if @json['type'] then new CodeableConcept(@json['type'])
  
  ###*
  Where the service end-point is located.
  @returns {Array} an array of {@link String} objects
  ###
  address:-> @json['address']
  
  ###*
  A list of named parameters that is used in the service call.
  @returns {Array} an array of {@link DocumentReferenceServiceParameterComponent} objects
  ###
  parameter: ->
    if @json['parameter']
      for item in @json['parameter']
        new DocumentReferenceServiceParameterComponent(item)
  

###* 
Embedded class
@class DocumentReferenceContextComponent
@exports  DocumentReferenceContextComponent as DocumentReferenceContextComponent
###
class DocumentReferenceContextComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  This list of codes represents the main clinical acts, such as a colonoscopy or an appendectomy, being documented. In some cases, the event is inherent in the typeCode, such as a "History and Physical Report" in which the procedure being documented is necessarily a "History and Physical" act.
  @returns {Array} an array of {@link CodeableConcept} objects
  ###
  event: ->
    if @json['event']
      for item in @json['event']
        new CodeableConcept(item)
  
  ###*
  The time period over which the service that is described by the document was provided.
  @returns {Period}
  ###
  period: -> if @json['period'] then new Period(@json['period'])
  
  ###*
  The kind of facility where the patient was seen.
  @returns {CodeableConcept}
  ###
  facilityType: -> if @json['facilityType'] then new CodeableConcept(@json['facilityType'])
  
###*
A reference to a document.
@class DocumentReference
@exports DocumentReference as DocumentReference
###
class DocumentReference extends DomainResource
  constructor: (@json) ->
    super(@json)
  ###*
  Document identifier as assigned by the source of the document. This identifier is specific to this version of the document. This unique identifier may be used elsewhere to identify this version of the document.
  @returns {Identifier}
  ###
  masterIdentifier: -> if @json['masterIdentifier'] then new Identifier(@json['masterIdentifier'])
  
  ###*
  Other identifiers associated with the document, including version independent, source record and workflow related identifiers.
  @returns {Array} an array of {@link Identifier} objects
  ###
  identifier: ->
    if @json['identifier']
      for item in @json['identifier']
        new Identifier(item)
  
  ###*
  Who or what the document is about. The document can be about a person, (patient or healthcare practitioner), a device (I.e. machine) or even a group of subjects (such as a document about a herd of farm animals, or a set of patients that share a common exposure).
  @returns {Reference}
  ###
  subject: -> if @json['subject'] then new Reference(@json['subject'])
  
  ###*
  Specifies the particular kind of document (e.g. Patient Summary, Discharge Summary, Prescription, etc.).
  @returns {CodeableConcept}
  ###
  type: -> if @json['type'] then new CodeableConcept(@json['type'])
  
  ###*
  A categorization for the type of the document. This may be implied by or derived from the code specified in the Document Type.
  @returns {CodeableConcept}
  ###
  class: -> if @json['class'] then new CodeableConcept(@json['class'])
  
  ###*
  Identifies who is responsible for adding the information to the document.
  @returns {Array} an array of {@link Reference} objects
  ###
  author: ->
    if @json['author']
      for item in @json['author']
        new Reference(item)
  
  ###*
  Identifies the organization or group who is responsible for ongoing maintenance of and access to the document.
  @returns {Reference}
  ###
  custodian: -> if @json['custodian'] then new Reference(@json['custodian'])
  
  ###*
  A reference to a domain or server that manages policies under which the document is accessed and/or made available.
  @returns {Array} an array of {@link String} objects
  ###
  policyManager:-> @json['policyManager']
  
  ###*
  Which person or organization authenticates that this document is valid.
  @returns {Reference}
  ###
  authenticator: -> if @json['authenticator'] then new Reference(@json['authenticator'])
  
  ###*
  When the document was created.
  @returns {Array} an array of {@link Date} objects
  ###
  created:-> if @json['created'] then DT.DateTime.parse(@json['created'])
  
  ###*
  When the document reference was created.
  @returns {Array} an array of {@link Date} objects
  ###
  indexed:-> if @json['indexed'] then DT.DateTime.parse(@json['indexed'])
  
  ###*
  The status of this document reference.
  @returns {Array} an array of {@link String} objects
  ###
  status:-> @json['status']
  
  ###*
  The status of the underlying document.
  @returns {CodeableConcept}
  ###
  docStatus: -> if @json['docStatus'] then new CodeableConcept(@json['docStatus'])
  
  ###*
  Relationships that this document has with other document references that already exist.
  @returns {Array} an array of {@link DocumentReferenceRelatesToComponent} objects
  ###
  relatesTo: ->
    if @json['relatesTo']
      for item in @json['relatesTo']
        new DocumentReferenceRelatesToComponent(item)
  
  ###*
  Human-readable description of the source document. This is sometimes known as the "title".
  @returns {Array} an array of {@link String} objects
  ###
  description:-> @json['description']
  
  ###*
  A code specifying the level of confidentiality of the XDS Document.
  @returns {Array} an array of {@link CodeableConcept} objects
  ###
  confidentiality: ->
    if @json['confidentiality']
      for item in @json['confidentiality']
        new CodeableConcept(item)
  
  ###*
  The primary language in which the source document is written.
  @returns {Array} an array of {@link String} objects
  ###
  primaryLanguage:-> @json['primaryLanguage']
  
  ###*
  The mime type of the source document.
  @returns {Array} an array of {@link String} objects
  ###
  mimeType:-> @json['mimeType']
  
  ###*
  An identifier that identifies that the format and content of the document conforms to additional rules beyond the base format indicated in the mimeType.
  @returns {Array} an array of {@link String} objects
  ###
  format:-> @json['format']
  
  ###*
  The size of the source document this reference refers to in bytes.
  @returns {Array} an array of {@link Number} objects
  ###
  size:-> @json['size']
  
  ###*
  A hash of the source document to ensure that changes have not occurred.
  @returns {Array} an array of {@link } objects
  ###
  hash:-> @json['hash']
  
  ###*
  A url at which the document can be accessed.
  @returns {Array} an array of {@link String} objects
  ###
  location:-> @json['location']
  
  ###*
  A description of a service call that can be used to retrieve the document.
  @returns {DocumentReferenceServiceComponent}
  ###
  service: -> if @json['service'] then new DocumentReferenceServiceComponent(@json['service'])
  
  ###*
  The clinical context in which the document was prepared.
  @returns {DocumentReferenceContextComponent}
  ###
  context: -> if @json['context'] then new DocumentReferenceContextComponent(@json['context'])
  



module.exports.DocumentReference = DocumentReference
