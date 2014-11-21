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
###*
Indicates the patient has a susceptibility to an adverse reaction upon exposure to a specified substance.
@class AllergyIntolerance
@exports AllergyIntolerance as AllergyIntolerance
###
class AllergyIntolerance 
  constructor: (@json) ->
  ###*
  This records identifiers associated with this allergy/intolerance concern that are defined by business processed and/ or used to refer to it when a direct URL reference to the resource itself is not appropriate (e.g. in CDA documents, or in written / printed documentation).
  @returns {Array} an array of {@link Identifier} objects
  ###
  identifier: ->
    if @json['identifier']
      for item in @json['identifier']
        new Identifier(item)
  
  ###*
  Criticality of the sensitivity.
  @returns {Array} an array of {@link String} objects
  ###
  criticality:-> @json['criticality']
  
  ###*
  Type of the sensitivity.
  @returns {Array} an array of {@link String} objects
  ###
  sensitivityType:-> @json['sensitivityType']
  
  ###*
  Date when the sensitivity was recorded.
  @returns {Date}
  ###
  recordedDate: -> if @json['recordedDate'] then new Date(@json['recordedDate'])
  
  ###*
  Status of the sensitivity.
  @returns {Array} an array of {@link String} objects
  ###
  status:-> @json['status']
  
  ###*
  The patient who has the allergy or intolerance.
  @returns {Reference}
  ###
  subject: -> if @json['subject'] then new Reference(@json['subject'])
  
  ###*
  Indicates who has responsibility for the record.
  @returns {Reference}
  ###
  recorder: -> if @json['recorder'] then new Reference(@json['recorder'])
  
  ###*
  The substance that causes the sensitivity.
  @returns {Reference}
  ###
  substance: -> if @json['substance'] then new Reference(@json['substance'])
  
  ###*
  Reactions associated with the sensitivity.
  @returns {Array} an array of {@link Reference} objects
  ###
  reaction: ->
    if @json['reaction']
      for item in @json['reaction']
        new Reference(item)
  
  ###*
  Observations that confirm or refute the sensitivity.
  @returns {Array} an array of {@link Reference} objects
  ###
  sensitivityTest: ->
    if @json['sensitivityTest']
      for item in @json['sensitivityTest']
        new Reference(item)
  



module.exports.AllergyIntolerance = AllergyIntolerance
