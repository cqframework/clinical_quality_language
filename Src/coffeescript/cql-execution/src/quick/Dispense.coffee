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
Details of the dispensation such as the days supply and quantity of medication (to be) dispensed.
 
###
require './Period'
require './StatementOfOccurrence'
require './Quantity'
require './Duration'
require './CodeableConcept'
###*
@class Dispense
@exports  Dispense as quick.Dispense
###
class QUICK.Dispense
  constructor: (@json) ->
 
  ###*
  The number of units of the supply to be or that are actually dispensed. e.g., 30 tablets
  ### 
  amount: -> 
    if @json['amount']
      for x in @json['amount'] 
        new QUICK.Quantity(x)
       
  ###*
  Indicates the medication order that is being dispensed against.
  ### 
  authorizingPrescription: -> 
    if @json['authorizingPrescription']
      for x in @json['authorizingPrescription'] 
        new QUICK.StatementOfOccurrence(x)
       
  ###*
  The duration (generally in days) this dispensation should last.
  ### 
  expectedSupplyDuration: -> 
    if @json['expectedSupplyDuration']
      for x in @json['expectedSupplyDuration'] 
        new QUICK.Duration(x)
       
  ###*
  The number of times the supply may be dispensed. For example, the number of times the prescribed quantity is to be supplied including the initial standard fill.
  ### 
  numberOfRepeatsAllowed: -> 
    if @json['numberOfRepeatsAllowed']
      for x in @json['numberOfRepeatsAllowed'] 
        new QUICK.Quantity(x)
       
  ###*
  Indicates the reason for the substitution of (or lack of substitution) from what was prescribed
  ### 
  substitutionReason: -> 
    if @json['substitutionReason']
      for x in @json['substitutionReason'] 
        new QUICK.CodeableConcept(x)
       
  ###*
  A code signifying whether a different drug was dispensed from what was prescribed.
  ### 
  substitutionType: -> 
    if @json['substitutionType']
      for x in @json['substitutionType'] 
        new QUICK.CodeableConcept(x)
       
  ###*
  This indicates the validity period of a prescription (stale dating the Prescription). It reflects the prescriber perspective for the validity of the prescription. Dispenses must not be made against the prescription outside of this period. The lower-bound of the Dispensing Window signifies the earliest date that the prescription can be filled for the first time. If an upper-bound is not specified then the Prescription is open-ended or will default to a stale-date based on regulations. Rationale: Indicates when the Prescription becomes valid, and when it ceases to be a dispensable Prescription.
  ### 
  validityPeriod: -> 
    if @json['validityPeriod']
      for x in @json['validityPeriod'] 
        new QUICK.Period(x)
       

module.exports.QUICK = QUICK
