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
An asserted clinical reason to perform a test, prescribe a medication, procedure, or perform a procedure, or perform any act.

The reason can be specified as a code or as another statement, e.g., code for diabetes (ICD-9-CM 250.0) or Condition (with diabetes code) documented elsewhere in a patient's record.
 
###
require './ClinicalStatement'
require './CodeableConcept'
###*
@class Indication
@exports  Indication as quick.Indication
###
class QUICK.Indication
  constructor: (@json) ->
 
  ###*
  A human readable description of the indicated reason.
  ### 
  narrative: ->  @json['narrative'] 
 
 
  ###*
  A code representing the reason.
  ### 
  reason: -> 
    if @json['reason']
      for x in @json['reason'] 
        new QUICK.CodeableConcept(x)
       
  ###*
  A clinical statement that lends support for the indication.
  ### 
  supportingStatement: -> 
    if @json['supportingStatement']
      for x in @json['supportingStatement'] 
        new QUICK.ClinicalStatement(x)
       

module.exports.QUICK = QUICK
