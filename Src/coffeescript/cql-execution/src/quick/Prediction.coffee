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
Concept representing the likely course of an existing disease or condition or the likelihood (risk) of acquiring a condition that is not currently manifested.

This class represents desirable and undesirable courses.

Examples: 5 year survival, 10-year risk of heart disease, likelihood to recover lower limb neuromuscular function after spinal cord injury
 
###
require './Period'
require './Inference'
require './StatementOfOccurrence'
require './CodeableConcept'
require './Element'
###*
@class Prediction
@exports  Prediction as quick.Prediction
###
class QUICK.Prediction
  constructor: (@json) ->
 
  ###*
  For assessments or prognosis specific to a particular condition, indicates the condition being assessed
  ### 
  condition: -> 
    if @json['condition']
      for x in @json['condition'] 
        new QUICK.StatementOfOccurrence(x)
       
  ###*
  How the prognosis was estimated or inferred.
  ### 
  inference: -> 
    if @json['inference']
      for x in @json['inference'] 
        new QUICK.Inference(x)
       
  ###*
  The likelihood of acquiring the condition specified as a numeric probability (less than or equal to 1) or a coded ordinal value.
  ### 
  likelihood: -> 
    if @json['likelihood']
      for x in @json['likelihood'] 
        new QUICK.Element(x)
       
  ###*
  The outcomes that is being predicted for the patient (e.g. remission, death, a particular condition).
  ### 
  outcome: -> if @json['outcome'] then new QUICK.CodeableConcept( @json['outcome'] )
 
 
  ###*
  The risk assessment procedure that led to this prognosis
  ### 
  riskAssessmentProcedure: -> 
    if @json['riskAssessmentProcedure']
      for x in @json['riskAssessmentProcedure'] 
        new QUICK.StatementOfOccurrence(x)
       
  ###*
  The time span within which the condition will be reached. e.g., 10 years.
  ### 
  timePeriod: -> 
    if @json['timePeriod']
      for x in @json['timePeriod'] 
        new QUICK.Period(x)
       

module.exports.QUICK = QUICK
