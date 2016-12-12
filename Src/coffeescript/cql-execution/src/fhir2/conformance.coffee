
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
@class ConformanceSoftwareComponent
@exports  ConformanceSoftwareComponent as ConformanceSoftwareComponent
###
class ConformanceSoftwareComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  Name software is known by.
  @returns {Array} an array of {@link String} objects
  ###
  name:-> @json['name']
  
  ###*
  The version identifier for the software covered by this statement.
  @returns {Array} an array of {@link String} objects
  ###
  version:-> @json['version']
  
  ###*
  Date this version of the software released.
  @returns {Array} an array of {@link Date} objects
  ###
  releaseDate:-> if @json['releaseDate'] then DT.DateTime.parse(@json['releaseDate'])
  

###* 
Embedded class
@class ConformanceImplementationComponent
@exports  ConformanceImplementationComponent as ConformanceImplementationComponent
###
class ConformanceImplementationComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  Information about the specific installation that this conformance statement relates to.
  @returns {Array} an array of {@link String} objects
  ###
  description:-> @json['description']
  
  ###*
  A base URL for the implementation.  This forms the base for REST interfaces as well as the mailbox and document interfaces.
  @returns {Array} an array of {@link String} objects
  ###
  url:-> @json['url']
  

###* 
Embedded class
@class ConformanceRestSecurityCertificateComponent
@exports  ConformanceRestSecurityCertificateComponent as ConformanceRestSecurityCertificateComponent
###
class ConformanceRestSecurityCertificateComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  Mime type for certificate.
  @returns {Array} an array of {@link String} objects
  ###
  type:-> @json['type']
  
  ###*
  Actual certificate.
  @returns {Array} an array of {@link } objects
  ###
  blob:-> @json['blob']
  

###* 
Embedded class
@class ConformanceRestSecurityComponent
@exports  ConformanceRestSecurityComponent as ConformanceRestSecurityComponent
###
class ConformanceRestSecurityComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  Server adds CORS headers when responding to requests - this enables javascript applications to use the server.
  @returns {Array} an array of {@link boolean} objects
  ###
  cors:-> @json['cors']
  
  ###*
  Types of security services are supported/required by the system.
  @returns {Array} an array of {@link CodeableConcept} objects
  ###
  service: ->
    if @json['service']
      for item in @json['service']
        new CodeableConcept(item)
  
  ###*
  General description of how security works.
  @returns {Array} an array of {@link String} objects
  ###
  description:-> @json['description']
  
  ###*
  Certificates associated with security profiles.
  @returns {Array} an array of {@link ConformanceRestSecurityCertificateComponent} objects
  ###
  certificate: ->
    if @json['certificate']
      for item in @json['certificate']
        new ConformanceRestSecurityCertificateComponent(item)
  

###* 
Embedded class
@class ResourceInteractionComponent
@exports  ResourceInteractionComponent as ResourceInteractionComponent
###
class ResourceInteractionComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  Coded identifier of the operation, supported by the system resource.
  @returns {Array} an array of {@link String} objects
  ###
  code:-> @json['code']
  
  ###*
  Guidance specific to the implementation of this operation, such as 'delete is a logical delete' or 'updates are only allowed with version id' or 'creates permitted from pre-authorized certificates only'.
  @returns {Array} an array of {@link String} objects
  ###
  documentation:-> @json['documentation']
  

###* 
Embedded class
@class ConformanceRestResourceSearchParamComponent
@exports  ConformanceRestResourceSearchParamComponent as ConformanceRestResourceSearchParamComponent
###
class ConformanceRestResourceSearchParamComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  The name of the search parameter used in the interface.
  @returns {Array} an array of {@link String} objects
  ###
  name:-> @json['name']
  
  ###*
  A formal reference to where this parameter was first defined, so that a client can be confident of the meaning of the search parameter.
  @returns {Array} an array of {@link String} objects
  ###
  definition:-> @json['definition']
  
  ###*
  The type of value a search parameter refers to, and how the content is interpreted.
  @returns {Array} an array of {@link String} objects
  ###
  type:-> @json['type']
  
  ###*
  This allows documentation of any distinct behaviors about how the search parameter is used.  For example, text matching algorithms.
  @returns {Array} an array of {@link String} objects
  ###
  documentation:-> @json['documentation']
  
  ###*
  Types of resource (if a resource is referenced).
  @returns {Array} an array of {@link String} objects
  ###
  target:-> @json['target']
  
  ###*
  Chained names supported.
  @returns {Array} an array of {@link String} objects
  ###
  chain:-> @json['chain']
  

###* 
Embedded class
@class ConformanceRestResourceComponent
@exports  ConformanceRestResourceComponent as ConformanceRestResourceComponent
###
class ConformanceRestResourceComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  A type of resource exposed via the restful interface.
  @returns {Array} an array of {@link String} objects
  ###
  type:-> @json['type']
  
  ###*
  A specification of the profile that describes the solution's support for the resource, including any constraints on cardinality, bindings, lengths or other limitations.
  @returns {Reference}
  ###
  profile: -> if @json['profile'] then new Reference(@json['profile'])
  
  ###*
  Identifies a restful operation supported by the solution.
  @returns {Array} an array of {@link ResourceInteractionComponent} objects
  ###
  interaction: ->
    if @json['interaction']
      for item in @json['interaction']
        new ResourceInteractionComponent(item)
  
  ###*
  Thi field is set to true to specify that the system does not support (server) or use (client) versioning for this resource type. If this is not set to true, the server must at least correctly track and populate the versionId meta-property on resources.
  @returns {Array} an array of {@link String} objects
  ###
  versioning:-> @json['versioning']
  
  ###*
  A flag for whether the server is able to return past versions as part of the vRead operation.
  @returns {Array} an array of {@link boolean} objects
  ###
  readHistory:-> @json['readHistory']
  
  ###*
  A flag to indicate that the server allows the client to create new identities on the server. If the update operation is used (client) or allowed (server) to a new location where a resource doesn't already exist. This means that the server allows the client to create new identities on the server.
  @returns {Array} an array of {@link boolean} objects
  ###
  updateCreate:-> @json['updateCreate']
  
  ###*
  A list of _include values supported by the server.
  @returns {Array} an array of {@link String} objects
  ###
  searchInclude:-> @json['searchInclude']
  
  ###*
  Additional search parameters for implementations to support and/or make use of.
  @returns {Array} an array of {@link ConformanceRestResourceSearchParamComponent} objects
  ###
  searchParam: ->
    if @json['searchParam']
      for item in @json['searchParam']
        new ConformanceRestResourceSearchParamComponent(item)
  

###* 
Embedded class
@class SystemInteractionComponent
@exports  SystemInteractionComponent as SystemInteractionComponent
###
class SystemInteractionComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  A coded identifier of the operation, supported by the system.
  @returns {Array} an array of {@link String} objects
  ###
  code:-> @json['code']
  
  ###*
  Guidance specific to the implementation of this operation, such as limitations on the kind of transactions allowed, or information about system wide search is implemented.
  @returns {Array} an array of {@link String} objects
  ###
  documentation:-> @json['documentation']
  

###* 
Embedded class
@class ConformanceRestOperationComponent
@exports  ConformanceRestOperationComponent as ConformanceRestOperationComponent
###
class ConformanceRestOperationComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  The name of a query, which is used in the _query parameter when the query is called.
  @returns {Array} an array of {@link String} objects
  ###
  name:-> @json['name']
  
  ###*
  Where the formal definition can be found.
  @returns {Reference}
  ###
  definition: -> if @json['definition'] then new Reference(@json['definition'])
  

###* 
Embedded class
@class ConformanceRestComponent
@exports  ConformanceRestComponent as ConformanceRestComponent
###
class ConformanceRestComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  Identifies whether this portion of the statement is describing ability to initiate or receive restful operations.
  @returns {Array} an array of {@link String} objects
  ###
  mode:-> @json['mode']
  
  ###*
  Information about the system's restful capabilities that apply across all applications, such as security.
  @returns {Array} an array of {@link String} objects
  ###
  documentation:-> @json['documentation']
  
  ###*
  Information about security of implementation.
  @returns {ConformanceRestSecurityComponent}
  ###
  security: -> if @json['security'] then new ConformanceRestSecurityComponent(@json['security'])
  
  ###*
  A specification of the restful capabilities of the solution for a specific resource type.
  @returns {Array} an array of {@link ConformanceRestResourceComponent} objects
  ###
  resource: ->
    if @json['resource']
      for item in @json['resource']
        new ConformanceRestResourceComponent(item)
  
  ###*
  A specification of restful operations supported by the system.
  @returns {Array} an array of {@link SystemInteractionComponent} objects
  ###
  interaction: ->
    if @json['interaction']
      for item in @json['interaction']
        new SystemInteractionComponent(item)
  
  ###*
  Definition of an operation or a named query and with its parameters and their meaning and type.
  @returns {Array} an array of {@link ConformanceRestOperationComponent} objects
  ###
  operation: ->
    if @json['operation']
      for item in @json['operation']
        new ConformanceRestOperationComponent(item)
  
  ###*
  A list of profiles that this server implements for accepting documents in the mailbox. If this list is empty, then documents are not accepted. The base specification has the profile identifier "http://hl7.org/fhir/documents/mailbox". Other specifications can declare their own identifier for this purpose.
  @returns {Array} an array of {@link String} objects
  ###
  documentMailbox:-> @json['documentMailbox']
  

###* 
Embedded class
@class ConformanceMessagingEventComponent
@exports  ConformanceMessagingEventComponent as ConformanceMessagingEventComponent
###
class ConformanceMessagingEventComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  A coded identifier of a supported messaging event.
  @returns {Coding}
  ###
  code: -> if @json['code'] then new Coding(@json['code'])
  
  ###*
  The impact of the content of the message.
  @returns {Array} an array of {@link String} objects
  ###
  category:-> @json['category']
  
  ###*
  The mode of this event declaration - whether application is sender or receiver.
  @returns {Array} an array of {@link String} objects
  ###
  mode:-> @json['mode']
  
  ###*
  A list of the messaging transport protocol(s) identifiers, supported by this endpoint.
  @returns {Array} an array of {@link Coding} objects
  ###
  protocol: ->
    if @json['protocol']
      for item in @json['protocol']
        new Coding(item)
  
  ###*
  A resource associated with the event.  This is the resource that defines the event.
  @returns {Array} an array of {@link String} objects
  ###
  focus:-> @json['focus']
  
  ###*
  Information about the request for this event.
  @returns {Reference}
  ###
  request: -> if @json['request'] then new Reference(@json['request'])
  
  ###*
  Information about the response for this event.
  @returns {Reference}
  ###
  response: -> if @json['response'] then new Reference(@json['response'])
  
  ###*
  Guidance on how this event is handled, such as internal system trigger points, business rules, etc.
  @returns {Array} an array of {@link String} objects
  ###
  documentation:-> @json['documentation']
  

###* 
Embedded class
@class ConformanceMessagingComponent
@exports  ConformanceMessagingComponent as ConformanceMessagingComponent
###
class ConformanceMessagingComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  An address to which messages and/or replies are to be sent.
  @returns {Array} an array of {@link String} objects
  ###
  endpoint:-> @json['endpoint']
  
  ###*
  Length if the receiver's reliable messaging cache in minutes (if a receiver) or how long the cache length on the receiver should be (if a sender).
  @returns {Array} an array of {@link Number} objects
  ###
  reliableCache:-> @json['reliableCache']
  
  ###*
  Documentation about the system's messaging capabilities for this endpoint not otherwise documented by the conformance statement.  For example, process for becoming an authorized messaging exchange partner.
  @returns {Array} an array of {@link String} objects
  ###
  documentation:-> @json['documentation']
  
  ###*
  A description of the solution's support for an event at this end point.
  @returns {Array} an array of {@link ConformanceMessagingEventComponent} objects
  ###
  event: ->
    if @json['event']
      for item in @json['event']
        new ConformanceMessagingEventComponent(item)
  

###* 
Embedded class
@class ConformanceDocumentComponent
@exports  ConformanceDocumentComponent as ConformanceDocumentComponent
###
class ConformanceDocumentComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  Mode of this document declaration - whether application is producer or consumer.
  @returns {Array} an array of {@link String} objects
  ###
  mode:-> @json['mode']
  
  ###*
  A description of how the application supports or uses the specified document profile.  For example, when are documents created, what action is taken with consumed documents, etc.
  @returns {Array} an array of {@link String} objects
  ###
  documentation:-> @json['documentation']
  
  ###*
  A constraint on a resource used in the document.
  @returns {Reference}
  ###
  profile: -> if @json['profile'] then new Reference(@json['profile'])
  
###*
A conformance statement is a set of requirements for a desired implementation or a description of how a target application fulfills those requirements in a particular implementation.
@class Conformance
@exports Conformance as Conformance
###
class Conformance extends DomainResource
  constructor: (@json) ->
    super(@json)
  ###*
  The identifier that is used to identify this conformance statement when it is referenced in a specification, model, design or an instance (should be globally unique OID, UUID, or URI).
  @returns {Array} an array of {@link String} objects
  ###
  identifier:-> @json['identifier']
  
  ###*
  The identifier that is used to identify this version of the conformance statement when it is referenced in a specification, model, design or instance. This is an arbitrary value managed by the profile author manually and the value should be a timestamp.
  @returns {Array} an array of {@link String} objects
  ###
  version:-> @json['version']
  
  ###*
  A free text natural language name identifying the conformance statement.
  @returns {Array} an array of {@link String} objects
  ###
  name:-> @json['name']
  
  ###*
  Name of Organization publishing this conformance statement.
  @returns {Array} an array of {@link String} objects
  ###
  publisher:-> @json['publisher']
  
  ###*
  Contacts for Organization relevant to this conformance statement.  The contacts may be a website, email, phone numbers, etc.
  @returns {Array} an array of {@link ContactPoint} objects
  ###
  telecom: ->
    if @json['telecom']
      for item in @json['telecom']
        new ContactPoint(item)
  
  ###*
  A free text natural language description of the conformance statement and its use. Typically, this is used when the profile describes a desired rather than an actual solution, for example as a formal expression of requirements as part of an RFP.
  @returns {Array} an array of {@link String} objects
  ###
  description:-> @json['description']
  
  ###*
  The status of this conformance statement.
  @returns {Array} an array of {@link String} objects
  ###
  status:-> @json['status']
  
  ###*
  A flag to indicate that this conformance statement is authored for testing purposes (or education/evaluation/marketing), and is not intended to be used for genuine usage.
  @returns {Array} an array of {@link boolean} objects
  ###
  experimental:-> @json['experimental']
  
  ###*
  The date  (and optionally time) when the conformance statement was published.
  @returns {Array} an array of {@link Date} objects
  ###
  date:-> if @json['date'] then DT.DateTime.parse(@json['date'])
  
  ###*
  Software that is covered by this conformance statement.  It is used when the profile describes the capabilities of a particular software version, independent of an installation.
  @returns {ConformanceSoftwareComponent}
  ###
  software: -> if @json['software'] then new ConformanceSoftwareComponent(@json['software'])
  
  ###*
  Identifies a specific implementation instance that is described by the conformance statement - i.e. a particular installation, rather than the capabilities of a software program.
  @returns {ConformanceImplementationComponent}
  ###
  implementation: -> if @json['implementation'] then new ConformanceImplementationComponent(@json['implementation'])
  
  ###*
  The version of the FHIR specification on which this conformance statement is based.
  @returns {Array} an array of {@link String} objects
  ###
  fhirVersion:-> @json['fhirVersion']
  
  ###*
  A flag that indicates whether the application accepts unknown elements as part of a resource.
  @returns {Array} an array of {@link boolean} objects
  ###
  acceptUnknown:-> @json['acceptUnknown']
  
  ###*
  A list of the formats supported by this implementation.
  @returns {Array} an array of {@link String} objects
  ###
  format:-> @json['format']
  
  ###*
  A list of profiles supported by the system. For a server, "supported by the system" means the system hosts/produces a set of resources, conformant to a particular profile, and allows its clients to search using this profile and to find appropriate data. For a client, it means the system will search by this profile and process data according to the guidance implicit in the profile.
  @returns {Array} an array of {@link Reference} objects
  ###
  profile: ->
    if @json['profile']
      for item in @json['profile']
        new Reference(item)
  
  ###*
  A definition of the restful capabilities of the solution, if any.
  @returns {Array} an array of {@link ConformanceRestComponent} objects
  ###
  rest: ->
    if @json['rest']
      for item in @json['rest']
        new ConformanceRestComponent(item)
  
  ###*
  A description of the messaging capabilities of the solution.
  @returns {Array} an array of {@link ConformanceMessagingComponent} objects
  ###
  messaging: ->
    if @json['messaging']
      for item in @json['messaging']
        new ConformanceMessagingComponent(item)
  
  ###*
  A document definition.
  @returns {Array} an array of {@link ConformanceDocumentComponent} objects
  ###
  document: ->
    if @json['document']
      for item in @json['document']
        new ConformanceDocumentComponent(item)
  



module.exports.Conformance = Conformance
