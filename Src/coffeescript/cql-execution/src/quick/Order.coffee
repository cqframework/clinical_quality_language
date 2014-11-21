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
An order is an instruction by a healthcare provider to another healthcare provider to perform some act.
 
###
require './Period'
require './Indication'
require './StatementOfOccurrence'
require './ActionStatus'
require './CodeableConcept'
require './Practitioner'
###*
@class Order
@exports  Order as Order
###
class Order
  constructor: (@json) ->
 
  ###*
  The status of an action. It is expected that the range of values for statusCode (i.e., the value set) will vary by the subtypes of Action. For example, Proposal might have one of its status value as Declined.
  ### 
  currentStatus: -> 
    if @json['currentStatus']
      for x in @json['currentStatus'] 
        new QUICK.ActionStatus(x)
       
  ###*
  The time when the ordered act is expected to be performed.
  ### 
  expectedPerformanceTime: -> 
    if @json['expectedPerformanceTime']
      for x in @json['expectedPerformanceTime'] 
        new QUICK.Period(x)
       
  ###*
  Identifies a proposal that led to this order.
  ### 
  fromProposal: -> if @json['fromProposal'] then new StatementOfOccurrence( @json['fromProposal'] )
 
 
  ###*
  Reason or justification for the action. Reasons may also be specified for not performing an action. 
  ### 
  indication: -> 
    if @json['indication']
      for x in @json['indication'] 
        new QUICK.Indication(x)
       
  ###*
  The time at which the order was created.
  ### 
  orderedAtTime: ->  @json['orderedAtTime'] 
 
 
  ###*
  The responsible person who places this order, e.g., physician. This may be different than the author of the order, e.g., clerk, who may be the statement's author.
  ### 
  orderedBy: -> 
    if @json['orderedBy']
      for x in @json['orderedBy'] 
        new QUICK.Practitioner(x)
       
  ###*
  The mode the order was received (such as by telephone, electronic, verbal, written). This describes 'how' the communication was done as opposed to dataSourceType which specifies the 'where' and 'from'.
  ### 
  originationMode: -> 
    if @json['originationMode']
      for x in @json['originationMode'] 
        new QUICK.CodeableConcept(x)
       
  ###*
  Preferences are choices made by patients about options for care or treatment (including scheduling, care experience, and meeting of personal health goals) and the sharing and disclosure of their health information.
  ### 
  patientPreference: -> 
    if @json['patientPreference']
      for x in @json['patientPreference'] 
        new QUICK.CodeableConcept(x)
       
  ###*
  The specific condition under which the act being ordered is performed. For example, Pain, Shortness of Breath, Insomnia, Nausea.

If this attribute is specified, it implies that the act must be performed as needed (i.e., is prn).

Reasons such as "SpO2 less than x%" should be addressed as a PRN Instruction rather than a PRN Reason as it is unlikely that a value set can be identified for such range of possible observations.
  ### 
  prnReason: -> 
    if @json['prnReason']
      for x in @json['prnReason'] 
        new QUICK.Indication(x)
       
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
       
  ###*
  Characterizes how quickly the action must be initiated. Includes concepts such as stat, urgent, routine.
  ### 
  urgency: -> 
    if @json['urgency']
      for x in @json['urgency'] 
        new QUICK.CodeableConcept(x)
       

module.exports.Order = Order
