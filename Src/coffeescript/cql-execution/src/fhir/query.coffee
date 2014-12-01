
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
@class QueryResponseComponent
@exports  QueryResponseComponent as QueryResponseComponent
###
class QueryResponseComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  Links response to source query.
  @returns {Array} an array of {@link String} objects
  ###
  identifier:-> @json['identifier']
  
  ###*
  Outcome of processing the query.
  @returns {Array} an array of {@link String} objects
  ###
  outcome:-> @json['outcome']
  
  ###*
  Total number of matching records.
  @returns {Array} an array of {@link Number} objects
  ###
  total:-> @json['total']
  
  ###*
  Parameters server used.
  @returns {Array} an array of {@link Extension} objects
  ###
  parameter: ->
    if @json['parameter']
      for item in @json['parameter']
        new Extension(item)
  
  ###*
  To get first page (if paged).
  @returns {Array} an array of {@link Extension} objects
  ###
  first: ->
    if @json['first']
      for item in @json['first']
        new Extension(item)
  
  ###*
  To get previous page (if paged).
  @returns {Array} an array of {@link Extension} objects
  ###
  previous: ->
    if @json['previous']
      for item in @json['previous']
        new Extension(item)
  
  ###*
  To get next page (if paged).
  @returns {Array} an array of {@link Extension} objects
  ###
  next: ->
    if @json['next']
      for item in @json['next']
        new Extension(item)
  
  ###*
  To get last page (if paged).
  @returns {Array} an array of {@link Extension} objects
  ###
  last: ->
    if @json['last']
      for item in @json['last']
        new Extension(item)
  
  ###*
  Resources that are the results of the search.
  @returns {Array} an array of {@link Reference} objects
  ###
  reference: ->
    if @json['reference']
      for item in @json['reference']
        new Reference(item)
  
###*
A description of a query with a set of parameters.
@class Query
@exports Query as Query
###
class Query extends DomainResource
  constructor: (@json) ->
    super(@json)
  ###*
  Links query and its response(s).
  @returns {Array} an array of {@link String} objects
  ###
  identifier:-> @json['identifier']
  
  ###*
  Set of query parameters with values.
  @returns {Array} an array of {@link Extension} objects
  ###
  parameter: ->
    if @json['parameter']
      for item in @json['parameter']
        new Extension(item)
  
  ###*
  If this is a response to a query.
  @returns {QueryResponseComponent}
  ###
  response: -> if @json['response'] then new QueryResponseComponent(@json['response'])
  



module.exports.Query = Query
