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
Procedure to administer treatment using high energy radiation.
 
###
require './Schedule'
require './Quantity'
require './BodySite'
require './Dosage'
require './RadiotherapySimulation'
require './CodeableConcept'
require './LocalizationMethod'
require './MotionManagement'
###*
@class Radiotherapy
@exports  Radiotherapy as quick.Radiotherapy
###
class QUICK.Radiotherapy
  constructor: (@json) ->
 
  ###*
  The body site used for gaining access to the target body site. E.g., femoral artery for a coronary angiography.
  ### 
  approachBodySite: -> 
    if @json['approachBodySite']
      for x in @json['approachBodySite'] 
        new QUICK.BodySite(x)
       
  ###*
  The imaging modality and the frequency will be used to confirm that a tumor/target is in the same position at the time of treatment as it was at the time of simulation are defined.  For example, an order may indicate that a cone-beam CT (CBCT) should be acquired just prior to each treatment to confirm that a lung tumor is within a target volume.
  ### 
  localizationMethod: -> 
    if @json['localizationMethod']
      for x in @json['localizationMethod'] 
        new QUICK.LocalizationMethod(x)
       
  ###*
  The positioning and type of immobilization for various parts of the body are defined.  For example,  an order might indicate that the head should be hyper-extended and immobilized in a head-support and thermoplastic mask.
  ### 
  motionManagement: -> 
    if @json['motionManagement']
      for x in @json['motionManagement'] 
        new QUICK.MotionManagement(x)
       
  ###*
  Percent of the target body site structured of relevance to which the total dose volume applies site (e.g., 60% of the left kidney)
  ### 
  percentageCoveredOfBodySite: -> 
    if @json['percentageCoveredOfBodySite']
      for x in @json['percentageCoveredOfBodySite'] 
        new QUICK.Quantity(x)
       
  ###*
  This is the code that identifies the procedure with as much specificity as available, or as required.  E.g., appendectomy, coronary artery bypass graft surgery.
  ### 
  procedureCode: -> if @json['procedureCode'] then new QUICK.CodeableConcept( @json['procedureCode'] )
 
 
  ###*
  Describes the method used for the procedure and can vary depending on the procedure.  For example, a surgical procedure method might be laparoscopic surgery or robotic surgery; an imaging procedure such as a chest radiograph might have methods that represent the views such as PA and lateral; a laboratory procedure like urinalysis might have a method of clean catch; a respiratory care procedure such as supplemental oxygen might have a method of nasal cannula, hood, face mask, or non-rebreather mask.
  ### 
  procedureMethod: -> 
    if @json['procedureMethod']
      for x in @json['procedureMethod'] 
        new QUICK.CodeableConcept(x)
       
  ###*
  If the procedure is repeated, the frequency pattern for repetitions.
  ### 
  procedureSchedule: -> 
    if @json['procedureSchedule']
      for x in @json['procedureSchedule'] 
        new QUICK.Schedule(x)
       
  ###*
  The type of imaging and any accessories that will be used during the simulation session are defined.  For example, an order might indicate that the simulation should be done using a 4-dimensional PET-CT with 5mm slices, no bolus and wire (to mark surgical scar).
  ### 
  simulationMethod: -> 
    if @json['simulationMethod']
      for x in @json['simulationMethod'] 
        new QUICK.RadiotherapySimulation(x)
       
  ###*
  The body site where the procedure takes place.  E.g., left lower arm for fracture reduction.
  ### 
  targetBodySite: -> 
    if @json['targetBodySite']
      for x in @json['targetBodySite'] 
        new QUICK.BodySite(x)
       
  ###*
  The radiation delivery techniques to be used for treatment and the physician’s goals for how much radiation dose targets and normal tissues should receive are defined.  For example, an order might indicate that a treatment should use intensity modulate x-ray radiation (IMXT) to deliver at least 50 Gy to 95% of a planning target volume but no more than 20 Gy to 20% of the total lung volume.

Please note the following guidance vis-a-vis dose:
1. The target volume delineation is captured as the dose's targetBodySite. Values may include: GTV, ITV, CTV and PTV, for instance.
2. doseQuantity may be used to represent 'dose per fraction' - e.g., 2 GY
3. doseRestriction may be used to represent to total dose or the number of fractions for a given volume delineation. Note that this value may specify either a minimum or maximum volume - e.g., 30 GY
  ### 
  treatmentPlanningInstructions: -> 
    if @json['treatmentPlanningInstructions']
      for x in @json['treatmentPlanningInstructions'] 
        new QUICK.Dosage(x)
       

module.exports.QUICK = QUICK
