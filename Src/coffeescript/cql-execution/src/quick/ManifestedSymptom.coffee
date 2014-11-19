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
The signs and symptoms that were observed as part of the event.
 
###
require './BodySite'
require './CodeableConcept'
###*
@class ManifestedSymptom
@exports  ManifestedSymptom as quick.ManifestedSymptom
###
class QUICK.ManifestedSymptom
  constructor: (@json) ->
 
  ###*
  The body site of the symptom or sign
  ### 
  bodySite: -> 
    if @json['bodySite']
      for x in @json['bodySite'] 
        new QUICK.BodySite(x)
       
  ###*
  Characterizes impact on life, or durable impact on physiological function or on quality of life. Includes concepts such as life-threatening, or potential loss of function or capacity. E.g., Life threatening, potentially requires hospitalization, self-resolving. Different from severity in that a moderate subarachnoid hemorrhage is likely to be highly important, whereas a moderate headache is not.
  ### 
  criticality: -> 
    if @json['criticality']
      for x in @json['criticality'] 
        new QUICK.CodeableConcept(x)
       
  ###*
  Characterizes the intensity of the manifestation of the sign or symptom. Includes concepts such as mild, moderate, severe. If the symptom is rash and severity is moderate, it means that the symptom was a moderate rash.
  ### 
  severity: -> 
    if @json['severity']
      for x in @json['severity'] 
        new QUICK.CodeableConcept(x)
       
  ###*
  The specific sign or symptom that was observed.
  ### 
  symptomCode: -> if @json['symptomCode'] then new QUICK.CodeableConcept( @json['symptomCode'] )
 
 

module.exports.QUICK = QUICK
