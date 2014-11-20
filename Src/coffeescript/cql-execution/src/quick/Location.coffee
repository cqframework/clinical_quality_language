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
###*
@namespacing scoping into the QUICK namespace
###
this.QUICK ||= {}
###*
Details for a physical place where services are provided and resources and participants may be stored, found, contained or accommodated.

A Location includes both incidental locations (a place which is used for healthcare without prior designation or authorization) and dedicated, formally appointed locations. Locations may be private, public, mobile or fixed and scale from small freezers to full hospital buildings or parking garages.

Examples of Locations are:

Building, ward, corridor or room
Freezer, incubator
Vehicle or lift
Home, shed, or a garage
Road, parking place, a park
 
###
require './Contact'
require './Address'
require './EntityCharacteristic'
require './CodeableConcept'
require './Identifier'
###*
@class Location
@exports  Location as Location
###
class Location
  constructor: (@json) ->
 
  ###*
  An address for the location.
  ### 
  address: -> 
    if @json['address']
      for x in @json['address'] 
        new QUICK.Address(x)
       
  ###*
  The characteristics of this entity.
  ### 
  characteristic: -> 
    if @json['characteristic']
      for x in @json['characteristic'] 
        new QUICK.EntityCharacteristic(x)
       
  ###*
  Indicates the type of function performed at the location.
  ### 
  function: -> 
    if @json['function']
      for x in @json['function'] 
        new QUICK.CodeableConcept(x)
       
  ###*
  <font color="#0f0f0f">The entity's unique identifier.  Used for internal tracking purposes.  It must be provided if user wants it returned as part of any output, otherwise it will be auto-generated, if needed, by CDS system.  Does not need to be the entity's "real" identifier. </font>
  ### 
  id: -> 
    if @json['id']
      for x in @json['id'] 
        new QUICK.Identifier(x)
       
  ###*
  A name for the location. Does not need to be unique.
  ### 
  name: ->  @json['name'] 
 
 
  ###*
  Another Location which this Location is physically part of.
  ### 
  partOf: -> 
    if @json['partOf']
      for x in @json['partOf'] 
        new QUICK.Location(x)
       
  ###*
  The identifier of a set of constraints placed on an Entity. If there are multiple templates specified for the element, then the element must satisfy ALL constraints defined in ANY template at that level.
  ### 
  profileId: -> 
    if @json['profileId']
      for x in @json['profileId'] 
        new QUICK.Identifier(x)
       
  ###*
  The contact details of communication devices available at the location. This can include phone numbers, fax numbers, mobile numbers, email addresses and web sites.
  ### 
  telecom: -> 
    if @json['telecom']
      for x in @json['telecom'] 
        new QUICK.Contact(x)
       

module.exports.Location = Location
