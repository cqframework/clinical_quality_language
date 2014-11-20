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
An adverse event caused by exposure to some agent (e.g., a medication, immunization, food, or environmental agent).

An adverse reaction can range from a mild reaction, such as a harmless rash to a severe and life-threatening condition. They can occur immediately or develop over time. For example, a patient may develop a rash after taking a particular medication. 
 
###
require './Period'
require './ClinicalStatement'
require './Exposure'
require './CodeableConcept'
require './ManifestedSymptom'
###*
@class AdverseReaction
@exports  AdverseReaction as AdverseReaction
###
class AdverseReaction
  constructor: (@json) ->
 
  ###*
  Related clinical statement that is the cause of this adverse event. Note that this statement may not always exist or be available. 
  ### 
  cause: -> 
    if @json['cause']
      for x in @json['cause'] 
        new QUICK.ClinicalStatement(x)
       
  ###*
  The type of adverse outcome. For instance, a fall, a hospital-acquired infection, or a reaction to a substance.
  ### 
  code: -> if @json['code'] then new QUICK.CodeableConcept( @json['code'] )
 
 
  ###*
  The adverse signs and symptoms observed as a result of the exposure.
  ### 
  effect: -> 
    if @json['effect']
      for x in @json['effect'] 
        new QUICK.ManifestedSymptom(x)
       
  ###*
  The time period during which the adverse event occurred.
  ### 
  effectiveTime: -> if @json['effectiveTime'] then new QUICK.Period( @json['effectiveTime'] )
 
 
  ###*
  Exposure to an action that is presumed to have caused the action
  ### 
  exposure: -> 
    if @json['exposure']
      for x in @json['exposure'] 
        new QUICK.Exposure(x)
       
  ###*
  The state of the effects of this adverse outcome.  E.g., active, inactive, resolved.
  ### 
  status: -> 
    if @json['status']
      for x in @json['status'] 
        new QUICK.CodeableConcept(x)
       

module.exports.AdverseReaction = AdverseReaction
