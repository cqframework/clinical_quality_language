
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
@class BundleLinkComponent
@exports  BundleLinkComponent as BundleLinkComponent
###
class BundleLinkComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  A name which details the functional use for this link - see [[http://www.iana.org/assignments/link-relations/link-relations.xhtml]].
  @returns {Array} an array of {@link String} objects
  ###
  relation:-> @json['relation']
  
  ###*
  The reference details for the link.
  @returns {Array} an array of {@link String} objects
  ###
  url:-> @json['url']
  

###* 
Embedded class
@class BundleEntryDeletedComponent
@exports  BundleEntryDeletedComponent as BundleEntryDeletedComponent
###
class BundleEntryDeletedComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  The type of resource that was deleted (required to construct the identity).
  @returns {Array} an array of {@link String} objects
  ###
  type:-> @json['type']
  
  ###*
  The id of the resource that was deleted.
  @returns {Array} an array of {@link String} objects
  ###
  id:-> @json['id']
  
  ###*
  Version id for releted resource.
  @returns {Array} an array of {@link String} objects
  ###
  versionId:-> @json['versionId']
  
  ###*
  The date/time that the resource was deleted.
  @returns {Array} an array of {@link Date} objects
  ###
  instant:-> if @json['instant'] then DT.DateTime.parse(@json['instant'])
  

###* 
Embedded class
@class BundleEntryComponent
@exports  BundleEntryComponent as BundleEntryComponent
###
class BundleEntryComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  The Base URL for the resource, if different to the base URL specified for the bundle as a whole.
  @returns {Array} an array of {@link String} objects
  ###
  base:-> @json['base']
  
  ###*
  The status of a resource in the bundle. Used for search (to differentiate between resources included as a match, and resources included as an _include), for history (deleted resources), and for transactions (create/update/delete).
  @returns {Array} an array of {@link String} objects
  ###
  status:-> @json['status']
  
  ###*
  Search URL for this resource when processing a transaction (see transaction documentation).
  @returns {Array} an array of {@link String} objects
  ###
  search:-> @json['search']
  
  ###*
  When searching, the server's search ranking score for the entry.
  @returns {Array} an array of {@link Number} objects
  ###
  score:-> @json['score']
  
  ###*
  If this is an entry that represents a deleted resource. Only used when the bundle is a transaction or a history type. See RESTful API documentation for further informatino.
  @returns {BundleEntryDeletedComponent}
  ###
  deleted: -> if @json['deleted'] then new BundleEntryDeletedComponent(@json['deleted'])
  
  ###*
  The Resources for the entry.
  @returns {Resource}
  ###
  resource: -> 
    if @json['resource']
      typeName = @json['resource'].resourceType
      req = require('./'+typeName.toLowerCase())[typeName]
      new req(@json['resource'])
  
###*
A container for a group of resources.
@class Bundle
@exports Bundle as Bundle
###
class Bundle extends Resource
  constructor: (@json) ->
    super(@json)
  ###*
  Indicates the purpose of this bundle- how it was intended to be used.
  @returns {Array} an array of {@link String} objects
  ###
  type:-> @json['type']
  
  ###*
  The base URL for the service that provided these resources. All relative URLs are relative to this one (equivalent to xml:base).
  @returns {Array} an array of {@link String} objects
  ###
  base:-> @json['base']
  
  ###*
  If a set of search matches, this is the total number of matches for the search (as opposed to the number of results in this bundle).
  @returns {Array} an array of {@link Number} objects
  ###
  total:-> @json['total']
  
  ###*
  A series of links that provide context to this bundle.
  @returns {Array} an array of {@link BundleLinkComponent} objects
  ###
  link: ->
    if @json['link']
      for item in @json['link']
        new BundleLinkComponent(item)
  
  ###*
  An entry in a bundle resource - will either contain a resource, or a deleted entry (transaction and history bundles only).
  @returns {Array} an array of {@link BundleEntryComponent} objects
  ###
  entry: ->
    if @json['entry']
      for item in @json['entry']
        new BundleEntryComponent(item)
  
  ###*
  XML Digital Signature - base64 encoded.
  @returns {Array} an array of {@link } objects
  ###
  signature:-> @json['signature']
  



module.exports.Bundle = Bundle
