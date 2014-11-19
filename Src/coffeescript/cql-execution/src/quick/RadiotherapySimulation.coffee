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
The type of imaging and any accessories that will be used during a simulation session for radiotherapy.  For example, an order might indicate that the simulation should be done using a 4-dimensional PET-CT with 5mm slices, no bolus and wire (to mark surgical scar).
 
###
require './Range'
require './CodeableConcept'
###*
@class RadiotherapySimulation
@exports  RadiotherapySimulation as quick.RadiotherapySimulation
###
class QUICK.RadiotherapySimulation
  constructor: (@json) ->
 
  ###*
  Defines the thickness of the bolus material to be used. E.g., 5mm thick
  ### 
  bolusThickness: -> 
    if @json['bolusThickness']
      for x in @json['bolusThickness'] 
        new QUICK.Range(x)
       
  ###*
  Defines the type of tissue-equivalent material that will be placed on a patient’s skin at the time of treatment to minimize the skin-sparing effect of high energy photon beams.  For example, paraffin wax may be used as a bolus.
  ### 
  bolusType: -> 
    if @json['bolusType']
      for x in @json['bolusType'] 
        new QUICK.CodeableConcept(x)
       
  ###*
  Defines the type of marker that will be used to define the targeted area for treatment planning or localize the targeted area during treatment.  For example, gold coils may be placed within a tumor for localization during treatment.
  ### 
  markerType: -> 
    if @json['markerType']
      for x in @json['markerType'] 
        new QUICK.CodeableConcept(x)
       
  ###*
  Defines the distance between each imaging slice. E.g., 5mm between axial slices of a CT scan.
  ### 
  scanThickness: -> 
    if @json['scanThickness']
      for x in @json['scanThickness'] 
        new QUICK.Range(x)
       
  ###*
  Additional information pertaining to the simulation
  ### 
  simulationComment: ->  @json['simulationComment'] 
 
 
  ###*
  Defines whether the imaging is volumetric (3D) and whether motion over time will be modeled (4D). E.g., 2D, 3D or 4D
  ### 
  simulationDimensions: -> 
    if @json['simulationDimensions']
      for x in @json['simulationDimensions'] 
        new QUICK.CodeableConcept(x)
       
  ###*
  Defines the type of imaging modality to be used. E.g., PET-CT, CT alone, CT-PET, CT-MRI, MRI alone.
  ### 
  simulationImagingType: -> 
    if @json['simulationImagingType']
      for x in @json['simulationImagingType'] 
        new QUICK.CodeableConcept(x)
       

module.exports.QUICK = QUICK
