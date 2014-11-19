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
A location on a person's body.  E.g., left breast, heart.
 
###
require './CodeableConcept'
###*
@class BodySite
@exports  BodySite as quick.BodySite
###
class QUICK.BodySite
  constructor: (@json) ->
 
  ###*
  A location on a patient's body.  May or may not encompass laterality. E.g., lung, left lung.
  ### 
  anatomicalLocation: -> if @json['anatomicalLocation'] then new QUICK.CodeableConcept( @json['anatomicalLocation'] )
 
 
  ###*
  This is further specification of the body part by adding directionality, such as "upper", "lower", "frontal", "medial", etc.
  ### 
  directionality: -> 
    if @json['directionality']
      for x in @json['directionality'] 
        new QUICK.CodeableConcept(x)
       
  ###*
  The side of the body, from the Patient's perspective. E.g., left, right, bilateral.
  ### 
  laterality: -> 
    if @json['laterality']
      for x in @json['laterality'] 
        new QUICK.CodeableConcept(x)
       

module.exports.QUICK = QUICK
