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
Specific factors about a patient, clinician, provider, or facility. Included are demographics, behavioral factors, social or cultural factors, available resources, and preferences. Behaviors reference responses or actions that affect (either positively or negatively) health or healthcare. Included in this category are mental- health issues, adherence issues unrelated to other factors or resources, coping ability, grief issues, and substance use/abuse. Social/cultural factors are characteristics of an individual related to family/caregiver support, education and literacy (including health literacy), primary language, cultural beliefs (including health beliefs), persistent life stressors, spiritual and religious beliefs, immigration status, and history of abuse or neglect. Resources are means available to a patient to meet health and healthcare needs, which would include caregiver support, insurance coverage, financial resources, and community resources to which the patient is already connected and receiving benefit. Preferences are choices made by patients and their caregivers relative to options for care or treatment (including scheduling, care experience, and meeting of personal health goals) and the sharing and disclosure of their health information. In the quality data element the attribute source is used to indicate whether it relates to the patient or the provider
 
###
require './CodeableConcept'
###*
@class EntityCharacteristic
@exports  EntityCharacteristic as quick.EntityCharacteristic
###
class QUICK.EntityCharacteristic
  constructor: (@json) ->
 
  ###*
  A code specifying the characteristic or feature
  ### 
  code: -> if @json['code'] then new QUICK.CodeableConcept( @json['code'] )
 
 
  ###*
  Whether the characteristic is present or absent
  ### 
  presence: ->  @json['presence'] 
 
 

module.exports.QUICK = QUICK
