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
The imaging modality and the frequency with which it will be used to confirm that a tumor/target is in the same position at the time of treatment as it was at the time of simulation are defined.  For example, an order may indicate that a cone-beam CT (CBCT) should be acquired just prior to each treatment to confirm that a lung tumor is within a target volume.
 
###
require './Schedule'
require './CodeableConcept'
###*
@class LocalizationMethod
@exports  LocalizationMethod as LocalizationMethod
###
class LocalizationMethod
  constructor: (@json) ->
 
  ###*
  Additional comments pertaining to the localization method.
  ### 
  comment: ->  @json['comment'] 
 
 
  ###*
  Defines how often the localization imaging should be performed. For example, a patient may have a cone-beam CT taken only once every 5 treatments.
  ### 
  frequency: -> 
    if @json['frequency']
      for x in @json['frequency'] 
        new QUICK.Schedule(x)
       
  ###*
  Defines the imaging modality to be used to verify the positioning of a patient and/or target prior and/or during a radiation treatment. For example, a patient may have a cone-beam CT prior to treatment to verify that a lung tumor is within the targeted volume.
  ### 
  localizationModality: -> 
    if @json['localizationModality']
      for x in @json['localizationModality'] 
        new QUICK.CodeableConcept(x)
       

module.exports.LocalizationMethod = LocalizationMethod
