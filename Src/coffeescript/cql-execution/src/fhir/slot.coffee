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
@namespacing scoping into the FHIR namespace
###
require './core'
require './element'
require './resource'
###*
(informative) A slot of time on a schedule that may be available for booking appointments.
@class Slot
@exports Slot as Slot
###
class Slot extends  Resource
  constructor: (@json) ->
    super(@json)
  ###*
  External Ids for this item.
  @returns {Array} an array of {@link Identifier} objects
  ###
  identifier: ->
    if @json['identifier']
      for item in @json['identifier']
        new Identifier(item)
  
  ###*
  The type of appointments that can be booked into this slot (ideally this would be an identifiable service - which is at a location, rather than the location itself). If provided then this overrides the value provided on the availability resource.
  @returns {CodeableConcept}
  ###
  fhirType: -> if @json['fhirType'] then new CodeableConcept(@json['fhirType'])
  
  ###*
  The availability resource that this slot defines an interval of status information.
  @returns {Reference}
  ###
  availability: -> if @json['availability'] then new Reference(@json['availability'])
  
  ###*
  BUSY | FREE | BUSY-UNAVAILABLE | BUSY-TENTATIVE.
  @returns {Array} an array of {@link String} objects
  ###
  freeBusyType:-> @json['freeBusyType']
  
  ###*
  Date/Time that the slot is to begin.
  @returns {Date}
  ###
  start: -> if @json['start'] then new Date(@json['start'])
  
  ###*
  Date/Time that the slot is to conclude.
  @returns {Date}
  ###
  end: -> if @json['end'] then new Date(@json['end'])
  
  ###*
  This slot has already been overbooked, appointments are unlikely to be accepted for this time.
  @returns {Array} an array of {@link boolean} objects
  ###
  overbooked:-> @json['overbooked']
  
  ###*
  Comments on the slot to describe any extended information. Such as custom constraints on the slot.
  @returns {Array} an array of {@link String} objects
  ###
  comment:-> @json['comment']
  
  ###*
  When this slot was created, or last revised.
  @returns {Date}
  ###
  lastModified: -> if @json['lastModified'] then new Date(@json['lastModified'])
  



module.exports.Slot = Slot
