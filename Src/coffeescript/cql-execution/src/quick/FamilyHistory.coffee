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
Significant health event or condition for people related to the subject, relevant in the context of care for the subject.

This information can be known to different levels of accuracy. Sometimes the exact condition ('asthma') is known, and sometimes it is less precise ('some sort of cancer'). Equally, sometimes the person can be identified ('my aunt agatha') and sometimes all that is known is that the person was an uncle.
 
###
require './Quantity'
require './CodeableConcept'
require './RelatedPerson'
###*
@class FamilyHistory
@exports  FamilyHistory as FamilyHistory
###
class FamilyHistory
  constructor: (@json) ->
 
  ###*
  Condition that the related person had.
  ### 
  condition: -> if @json['condition'] then new QUICK.CodeableConcept( @json['condition'] )
 
 
  ###*
  When condition first manifested
  ### 
  onsetAge: -> 
    if @json['onsetAge']
      for x in @json['onsetAge'] 
        new QUICK.Quantity(x)
       
  ###*
  Indicates what happened as a result of this condition.  e.g., deceased, permanent disability. 
  ### 
  outcome: -> 
    if @json['outcome']
      for x in @json['outcome'] 
        new QUICK.CodeableConcept(x)
       
  ###*
  The person, related to the patient, who is affected by the condition.
  ### 
  subject: -> if @json['subject'] then new QUICK.RelatedPerson( @json['subject'] )
 
 

module.exports.FamilyHistory = FamilyHistory
