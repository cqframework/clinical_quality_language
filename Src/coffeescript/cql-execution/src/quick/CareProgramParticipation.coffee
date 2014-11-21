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
###*
Description of the participation of a patient in a recognized program of care such as a care plan, a chemotherapy protocol, or a clinical trial.
 
###
require './StatementOfOccurrence'
require './CodeableConcept'
require './Identifier'
###*
@class CareProgramParticipation
@exports  CareProgramParticipation as CareProgramParticipation
###
class CareProgramParticipation
  constructor: (@json) ->
 
  ###*
  The goals that have been established for the patient as part of the care plan and the performance against those goals.
  ### 
  goals: -> 
    if @json['goals']
      for x in @json['goals'] 
        new QUICK.StatementOfOccurrence(x)
       
  ###*
  A patient's state of participation within the care plan, e.g., enrolled, ongoing, completed, suspended.

This status is different than the status of the action, e.g., proposal to enroll a patient in a care plan will have status as "accepted", but the patient may still not be enrolled.
  ### 
  participationStatus: -> if @json['participationStatus'] then new CodeableConcept( @json['participationStatus'] )
 
 
  ###*
  The specific program in which the patient is enrolled, was enrolled, or is being enrolled.
  ### 
  program: -> 
    if @json['program']
      for x in @json['program'] 
        new QUICK.Identifier(x)
       
  ###*
  The type of the care program such as Care Plan, Clinical Trial, Chemotherapy Protocol
  ### 
  programType: -> if @json['programType'] then new CodeableConcept( @json['programType'] )
 
 

module.exports.CareProgramParticipation = CareProgramParticipation
