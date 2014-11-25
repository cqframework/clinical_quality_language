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
 Embedded class
@class AdverseReactionSymptomComponent
@exports  AdverseReactionSymptomComponent as AdverseReactionSymptomComponent
###
class AdverseReactionSymptomComponent extends Element
  constructor: (@json) ->
    super(@json)
  ###*
  Indicates the specific sign or symptom that was observed.
  @returns {CodeableConcept}
  ###
  code: -> if @json['code'] then new CodeableConcept(@json['code'])
  
  ###*
  The severity of the sign or symptom.
  @returns {Array} an array of {@link String} objects
  ###
  severity:-> @json['severity']
  

###* 
 Embedded class
@class AdverseReactionExposureComponent
@exports  AdverseReactionExposureComponent as AdverseReactionExposureComponent
###
class AdverseReactionExposureComponent extends Element
  constructor: (@json) ->
    super(@json)
  ###*
  Identifies the initial date of the exposure that is suspected to be related to the reaction.
  @returns {Date}
  ###
  date: -> if @json['date'] then new Date(@json['date'])
  
  ###*
  The type of exposure: Drug Administration, Immunization, Coincidental.
  @returns {Array} an array of {@link String} objects
  ###
  fhirType:-> @json['fhirType']
  
  ###*
  A statement of how confident that the recorder was that this exposure caused the reaction.
  @returns {Array} an array of {@link String} objects
  ###
  causalityExpectation:-> @json['causalityExpectation']
  
  ###*
  Substance that is presumed to have caused the adverse reaction.
  @returns {Reference}
  ###
  substance: -> if @json['substance'] then new Reference(@json['substance'])
  
###*
Records an unexpected reaction suspected to be related to the exposure of the reaction subject to a substance.
@class AdverseReaction
@exports AdverseReaction as AdverseReaction
###
class AdverseReaction extends  Resource
  constructor: (@json) ->
    super(@json)
  ###*
  This records identifiers associated with this reaction that are defined by business processed and/ or used to refer to it when a direct URL reference to the resource itself is not appropriate (e.g. in CDA documents, or in written / printed documentation).
  @returns {Array} an array of {@link Identifier} objects
  ###
  identifier: ->
    if @json['identifier']
      for item in @json['identifier']
        new Identifier(item)
  
  ###*
  The date (and possibly time) when the reaction began.
  @returns {Date}
  ###
  date: -> if @json['date'] then new Date(@json['date'])
  
  ###*
  The subject of the adverse reaction.
  @returns {Reference}
  ###
  subject: -> if @json['subject'] then new Reference(@json['subject'])
  
  ###*
  If true, indicates that no reaction occurred.
  @returns {Array} an array of {@link boolean} objects
  ###
  didNotOccurFlag:-> @json['didNotOccurFlag']
  
  ###*
  Identifies the individual responsible for the information in the reaction record.
  @returns {Reference}
  ###
  recorder: -> if @json['recorder'] then new Reference(@json['recorder'])
  
  ###*
  The signs and symptoms that were observed as part of the reaction.
  @returns {Array} an array of {@link AdverseReactionSymptomComponent} objects
  ###
  symptom: ->
    if @json['symptom']
      for item in @json['symptom']
        new AdverseReactionSymptomComponent(item)
  
  ###*
  An exposure to a substance that preceded a reaction occurrence.
  @returns {Array} an array of {@link AdverseReactionExposureComponent} objects
  ###
  exposure: ->
    if @json['exposure']
      for item in @json['exposure']
        new AdverseReactionExposureComponent(item)
  



module.exports.AdverseReaction = AdverseReaction
