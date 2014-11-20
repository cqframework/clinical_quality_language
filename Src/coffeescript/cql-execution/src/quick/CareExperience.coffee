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
Information collected from a consumer, patient, or family member about their perception of the care they received or from a care giver about the care provided. Information collected includes the elements of care coordination, communication, whole-person approach to care, access to care, timeliness of care, and information sharing. Experience also encompasses the patient’s outcomes with respect to care provided in the past. For example, a patient receiving chemotherapy who has not responded to first line medication treatment or who no longer responds to such therapy may require second tier treatment. Such a patient’s experience of care is an important factor in defining subsequent treatment which can be driven by patient preference. 
 
###
require './ClinicalStatement'
require './CodeableConcept'
###*
@class CareExperience
@exports  CareExperience as CareExperience
###
class CareExperience
  constructor: (@json) ->
 
  ###*
  The statement (e.g., encounter, procedure) that is the basis for the experience
  ### 
  about: -> if @json['about'] then new QUICK.ClinicalStatement( @json['about'] )
 
 
  ###*
  The actual experience, e.g., poor communication.
  ### 
  experience: -> if @json['experience'] then new QUICK.CodeableConcept( @json['experience'] )
 
 

module.exports.CareExperience = CareExperience
