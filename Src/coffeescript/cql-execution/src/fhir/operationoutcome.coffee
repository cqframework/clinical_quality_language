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
@namespacing scoping into the FHIR namespace
###
require './core'

###* 
 Embedded class
@class OperationOutcomeIssueComponent
@exports  OperationOutcomeIssueComponent as OperationOutcomeIssueComponent
###
class OperationOutcomeIssueComponent
  constructor: (@json) ->
    super(@json)
  ###*
  Indicates whether the issue indicates a variation from successful processing.
  @returns {Array} an array of {@link String} objects
  ###
  severity:-> @json['severity']
  
  ###*
  A code indicating the type of error, warning or information message.
  @returns {Coding}
  ###
  fhirType: -> if @json['fhirType'] then new Coding(@json['fhirType'])
  
  ###*
  Additional description of the issue.
  @returns {Array} an array of {@link String} objects
  ###
  details:-> @json['details']
  
  ###*
  A simple XPath limited to element names, repetition indicators and the default child access that identifies one of the elements in the resource that caused this issue to be raised.
  @returns {Array} an array of {@link String} objects
  ###
  location:-> @json['location']
  
###*
A collection of error, warning or information messages that result from a system action.
@class OperationOutcome
@exports OperationOutcome as OperationOutcome
###
class OperationOutcome 
  constructor: (@json) ->
  ###*
  An error, warning or information message that results from a system action.
  @returns {Array} an array of {@link OperationOutcomeIssueComponent} objects
  ###
  issue: ->
    if @json['issue']
      for item in @json['issue']
        new OperationOutcomeIssueComponent(item)
  



module.exports.OperationOutcome = OperationOutcome
