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
An patient safety incident (a type of adverse event) is an unintended action or workflow such administering the wrong dose, administering the medication to the wrong patient, administering a medication by the wrong route, a patient falling out of bed.  An adverse event might or might not result in an adverse reaction or harm to the patient.
 
###
require './Period'
require './ClinicalStatement'
require './CodeableConcept'
###*
@class PatientSafetyIncident
@exports  PatientSafetyIncident as PatientSafetyIncident
###
class PatientSafetyIncident
  constructor: (@json) ->
 
  ###*
  The clinical statement for the action that was unintentionally performed, if relevant and available.
  ### 
  action: -> if @json['action'] then new QUICK.ClinicalStatement( @json['action'] )
 
 
  ###*
  The type of adverse action. For instance, a code representing an unintentional and invalid medication administration.
  ### 
  code: -> if @json['code'] then new QUICK.CodeableConcept( @json['code'] )
 
 
  ###*
  The time period during which the adverse event occurred.
  ### 
  effectiveTime: -> if @json['effectiveTime'] then new QUICK.Period( @json['effectiveTime'] )
 
 

module.exports.PatientSafetyIncident = PatientSafetyIncident
