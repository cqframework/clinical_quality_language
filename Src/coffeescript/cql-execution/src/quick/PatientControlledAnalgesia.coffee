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
Patient Controlled Analgesia administration. For instance, morphine PCA, 5 mg loading dose, followed by 10 mg/hr basal rate, 1 mg demand dose, lockout interval 10 min.
 
###
require './Medication'
require './Range'
require './Dispense'
require './Dosage'
###*
@class PatientControlledAnalgesia
@exports  PatientControlledAnalgesia as quick.PatientControlledAnalgesia
###
class QUICK.PatientControlledAnalgesia
  constructor: (@json) ->
 
  ###*
  Dispensation details to be used only when needed, e.g., as part of a statement about a prescription or a dispensation event.
  ### 
  dispense: -> 
    if @json['dispense']
      for x in @json['dispense'] 
        new QUICK.Dispense(x)
       
  ###*
  Details for the dose or doses of medication administered or to be administered to the patient
  ### 
  dosage: -> 
    if @json['dosage']
      for x in @json['dosage'] 
        new QUICK.Dosage(x)
       
  ###*
  The amount of time that must elapse after a PCA demand dose is administered before the next PCA demand dose can be delivered. For example, 10 minutes.
  ### 
  lockoutInterval: -> if @json['lockoutInterval'] then new QUICK.Range( @json['lockoutInterval'] )
 
 
  ###*
  Identifies the medication being dispensed or administered.
  ### 
  medication: -> 
    if @json['medication']
      for x in @json['medication'] 
        new QUICK.Medication(x)
       

module.exports.QUICK = QUICK
