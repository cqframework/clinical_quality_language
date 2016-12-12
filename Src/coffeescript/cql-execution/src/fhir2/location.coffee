
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
@class LocationPositionComponent
@exports  LocationPositionComponent as LocationPositionComponent
###
class LocationPositionComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  Longitude. The value domain and the interpretation are the same as for the text of the longitude element in KML (see notes below).
  @returns {Array} an array of {@link Number} objects
  ###
  longitude:-> @json['longitude']
  
  ###*
  Latitude. The value domain and the interpretation are the same as for the text of the latitude element in KML (see notes below).
  @returns {Array} an array of {@link Number} objects
  ###
  latitude:-> @json['latitude']
  
  ###*
  Altitude. The value domain and the interpretation are the same as for the text of the altitude element in KML (see notes below).
  @returns {Array} an array of {@link Number} objects
  ###
  altitude:-> @json['altitude']
  
###*
Details and position information for a physical place where services are provided  and resources and participants may be stored, found, contained or accommodated.
@class Location
@exports Location as Location
###
class Location extends DomainResource
  constructor: (@json) ->
    super(@json)
  ###*
  Unique code or number identifying the location to its users.
  @returns {Array} an array of {@link Identifier} objects
  ###
  identifier: ->
    if @json['identifier']
      for item in @json['identifier']
        new Identifier(item)
  
  ###*
  Name of the location as used by humans. Does not need to be unique.
  @returns {Array} an array of {@link String} objects
  ###
  name:-> @json['name']
  
  ###*
  Description of the Location, which helps in finding or referencing the place.
  @returns {Array} an array of {@link String} objects
  ###
  description:-> @json['description']
  
  ###*
  Indicates the type of function performed at the location.
  @returns {CodeableConcept}
  ###
  type: -> if @json['type'] then new CodeableConcept(@json['type'])
  
  ###*
  The contact details of communication devices available at the location. This can include phone numbers, fax numbers, mobile numbers, email addresses and web sites.
  @returns {Array} an array of {@link ContactPoint} objects
  ###
  telecom: ->
    if @json['telecom']
      for item in @json['telecom']
        new ContactPoint(item)
  
  ###*
  Physical location.
  @returns {Address}
  ###
  address: -> if @json['address'] then new Address(@json['address'])
  
  ###*
  Physical form of the location, e.g. building, room, vehicle, road.
  @returns {CodeableConcept}
  ###
  physicalType: -> if @json['physicalType'] then new CodeableConcept(@json['physicalType'])
  
  ###*
  The absolute geographic location of the Location, expressed in a KML compatible manner (see notes below for KML).
  @returns {LocationPositionComponent}
  ###
  position: -> if @json['position'] then new LocationPositionComponent(@json['position'])
  
  ###*
  The organization that is responsible for the provisioning and upkeep of the location.
  @returns {Reference}
  ###
  managingOrganization: -> if @json['managingOrganization'] then new Reference(@json['managingOrganization'])
  
  ###*
  active | suspended | inactive.
  @returns {Array} an array of {@link String} objects
  ###
  status:-> @json['status']
  
  ###*
  Another Location which this Location is physically part of.
  @returns {Reference}
  ###
  partOf: -> if @json['partOf'] then new Reference(@json['partOf'])
  
  ###*
  Indicates whether a resource instance represents a specific location or a class of locations.
  @returns {Array} an array of {@link String} objects
  ###
  mode:-> @json['mode']
  



module.exports.Location = Location
