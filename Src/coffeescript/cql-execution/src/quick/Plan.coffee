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
A plan to perform an act. Typically, this would include a time at which the action is expected or scheduled to be performed.
 
###
require './Indication'
require './StatementOfOccurrence'
require './ActionStatus'
require './CodeableConcept'
require './Person'
###*
@class Plan
@exports  Plan as quick.Plan
###
class QUICK.Plan
  constructor: (@json) ->
 
  ###*
  The status of an action. It is expected that the range of values for statusCode (i.e., the value set) will vary by the subtypes of Action. For example, Proposal might have one of its status value as Declined.
  ### 
  currentStatus: -> 
    if @json['currentStatus']
      for x in @json['currentStatus'] 
        new QUICK.ActionStatus(x)
       
  ###*
  The time when the action is expected to be performed.
  ### 
  expectedPerformanceTime: ->  @json['expectedPerformanceTime'] 
 
 
  ###*
  Identifies a proposal that led to this order.
  ### 
  fromProposal: -> if @json['fromProposal'] then new QUICK.StatementOfOccurrence( @json['fromProposal'] )
 
 
  ###*
  Identifies an order that is partly or wholly filled by the performance of this act
  ### 
  fulfillsOrder: -> if @json['fulfillsOrder'] then new QUICK.StatementOfOccurrence( @json['fulfillsOrder'] )
 
 
  ###*
  Reason or justification for the action. Reasons may also be specified for not performing an action. 
  ### 
  indication: -> 
    if @json['indication']
      for x in @json['indication'] 
        new QUICK.Indication(x)
       
  ###*
  Preferences are choices made by patients about options for care or treatment (including scheduling, care experience, and meeting of personal health goals) and the sharing and disclosure of their health information.
  ### 
  patientPreference: -> 
    if @json['patientPreference']
      for x in @json['patientPreference'] 
        new QUICK.CodeableConcept(x)
       
  ###*
  The time at which the plan was created.
  ### 
  plannedAtTime: ->  @json['plannedAtTime'] 
 
 
  ###*
  The person who is the primary planner of this action, e.g., the person who scheduled the appointment
  ### 
  plannedBy: -> 
    if @json['plannedBy']
      for x in @json['plannedBy'] 
        new QUICK.Person(x)
       
  ###*
  Provider preferences are choices made by care providers relative to options for care or treatment (including scheduling, care experience, and meeting of personal health goals).
  ### 
  providerPreference: -> 
    if @json['providerPreference']
      for x in @json['providerPreference'] 
        new QUICK.CodeableConcept(x)
       
  ###*
  The past statuses of this action, e.g., an order may evolve from draft to placed to in progress to completed or canceled.
  ### 
  statusHistory: -> 
    if @json['statusHistory']
      for x in @json['statusHistory'] 
        new QUICK.ActionStatus(x)
       

module.exports.QUICK = QUICK
