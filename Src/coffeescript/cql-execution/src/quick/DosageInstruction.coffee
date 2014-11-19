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
Indicates how the medication is to be administered to or used by the patient.
 
###
require './Period'
require './Range'
require './Quantity'
require './BodySite'
require './Schedule'
require './Ratio'
require './CodeableConcept'
###*
@class DosageInstruction
@exports  DosageInstruction as quick.DosageInstruction
###
class QUICK.DosageInstruction
  constructor: (@json) ->
 
  ###*
  Additional instructions such as "Swallow with plenty of water" which may or may not be coded.
  ### 
  additionalInstructions: -> 
    if @json['additionalInstructions']
      for x in @json['additionalInstructions'] 
        new QUICK.CodeableConcept(x)
       
  ###*
  The frequency pattern for administration of doses. e.g., three times per day after meals
  ### 
  administrationFrequency: -> 
    if @json['administrationFrequency']
      for x in @json['administrationFrequency'] 
        new QUICK.Schedule(x)
       
  ###*
  The body site used for gaining access to the target body site for the purposes of the substance administration. This is the anatomic site where the substance first enters the body, e.g., left subclavian vein.
  ### 
  approachBodySite: -> 
    if @json['approachBodySite']
      for x in @json['approachBodySite'] 
        new QUICK.BodySite(x)
       
  ###*
  Free text dosage instructions for cases where the instructions are too complex to code.
  ### 
  dosageInstructionsText: ->  @json['dosageInstructionsText'] 
 
 
  ###*
  The amount of the therapeutic or other substance given at one administration event. e.g., 500 mg, 1 tablet, 1 teaspoon
  ### 
  doseQuantity: -> 
    if @json['doseQuantity']
      for x in @json['doseQuantity'] 
        new QUICK.Quantity(x)
       
  ###*
  The type of dose.  E.g., initial, maintenance, loading.
  ### 
  doseType: -> 
    if @json['doseType']
      for x in @json['doseType'] 
        new QUICK.CodeableConcept(x)
       
  ###*
  Represents the actual time the substance is infused. Note the difference between infuseOver and duration of treatment (specified in administrationFrequency). An order may call for infusing a patient TID for an hour each time over a duration of 5 days.
  ### 
  infuseOver: -> 
    if @json['infuseOver']
      for x in @json['infuseOver'] 
        new QUICK.Quantity(x)
       
  ###*
  The maximum rate of substance administration. This value may be used as a stopping condition when a deliveryRateIncrement is specified without a count.
  ### 
  maximumDeliveryRate: -> 
    if @json['maximumDeliveryRate']
      for x in @json['maximumDeliveryRate'] 
        new QUICK.Quantity(x)
       
  ###*
  The maximum total quantity of a therapeutic substance that may be administered to a subject over the period of time. E.g. 1000mg in 24 hours.
  ### 
  maximumDosePerPeriod: -> 
    if @json['maximumDosePerPeriod']
      for x in @json['maximumDosePerPeriod'] 
        new QUICK.Quantity(x)
       
  ###*
  The maximum volume of fluid to administer to a patient
  ### 
  maximumVolumeToDeliver: -> 
    if @json['maximumVolumeToDeliver']
      for x in @json['maximumVolumeToDeliver'] 
        new QUICK.Quantity(x)
       
  ###*
  A coded value indicating the method by which the substance is introduced into or onto the body. Most commonly used for injections. Examples: Slow Push; Deep IV. Terminologies used often pre-coordinate this term with the route and or form of administration.
  ### 
  method: -> 
    if @json['method']
      for x in @json['method'] 
        new QUICK.CodeableConcept(x)
       
  ###*
  The minimum total quantity of a therapeutic substance that may be administered to a subject over the period of time. E.g., 10 mg in 24 hours.
  ### 
  minimumDosePerPeriod: -> 
    if @json['minimumDosePerPeriod']
      for x in @json['minimumDosePerPeriod'] 
        new QUICK.Ratio(x)
       
  ###*
  The speed with which the substance is introduced into the subject. Typically the rate for an infusion. e.g., 200ml in 2 hours.
  ### 
  rate: -> 
    if @json['rate']
      for x in @json['rate'] 
        new QUICK.Quantity(x)
       
  ###*
  The target rate to reach for this infusion.  Note that deliveryRateGoal is typically less than the maximum delivery rate which is the rate not to exceed. For enteral feeding orders, a target tube feeding rate of 75ml/hour may be specified.
  ### 
  rateGoal: -> 
    if @json['rateGoal']
      for x in @json['rateGoal'] 
        new QUICK.Range(x)
       
  ###*
  Change in the dosing rate; usually an increase for a patient who is initiating tube feeding. E.g., 20 mL/hour.
  ### 
  rateIncrement: -> 
    if @json['rateIncrement']
      for x in @json['rateIncrement'] 
        new QUICK.Range(x)
       
  ###*
  Period of time after which the deliveryRateIncrement should be attempted. E.g., 4 hours.
  ### 
  rateIncrementInterval: -> 
    if @json['rateIncrementInterval']
      for x in @json['rateIncrementInterval'] 
        new QUICK.Range(x)
       
  ###*
  The physical route through which the substance is administered. E.g., IV, PO.
  ### 
  route: -> 
    if @json['route']
      for x in @json['route'] 
        new QUICK.CodeableConcept(x)
       
  ###*
  The body site where the substance is delivered.
  ### 
  targetBodySite: -> 
    if @json['targetBodySite']
      for x in @json['targetBodySite'] 
        new QUICK.BodySite(x)
       
  ###*
  Acceptable time for administering the substance. Includes acceptable but suboptimal administration times.  This is an important aspect of immunizations, which have recommended and acceptable/valid timeframes for administration that can differ.
  ### 
  validAdministrationInterval: -> 
    if @json['validAdministrationInterval']
      for x in @json['validAdministrationInterval'] 
        new QUICK.Period(x)
       

module.exports.QUICK = QUICK
