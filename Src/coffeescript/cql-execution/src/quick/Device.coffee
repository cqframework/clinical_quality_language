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
This element identifies an instance of a manufactured thing that is used in the provision of healthcare without being substantially changed through that activity. The device may be a machine, an insert, a computer, an application, etc. This includes durable (reusable) medical equipment as well as disposable equipment used for diagnostic, treatment, and research for healthcare and public health.
 
###
require './EntityCharacteristic'
require './Patient'
require './Organization'
require './Location'
require './CodeableConcept'
require './Identifier'
###*
@class Device
@exports  Device as quick.Device
###
class QUICK.Device
  constructor: (@json) ->
 
  ###*
  The characteristics of this entity.
  ### 
  characteristic: -> 
    if @json['characteristic']
      for x in @json['characteristic'] 
        new QUICK.EntityCharacteristic(x)
       
  ###*
  Date of expiry of this product (if applicable).
  ### 
  expiry: ->  @json['expiry'] 
 
 
  ###*
  <font color="#0f0f0f">The entity's unique identifier.  Used for internal tracking purposes.  It must be provided if user wants it returned as part of any output, otherwise it will be auto-generated, if needed, by CDS system.  Does not need to be the entity's "real" identifier. </font>
  ### 
  id: -> 
    if @json['id']
      for x in @json['id'] 
        new QUICK.Identifier(x)
       
  ###*
  The resource may be found in a literal location (i.e. GPS coordinates), a logical place (i.e. "in/with the patient"), or a coded location.
  ### 
  location: -> 
    if @json['location']
      for x in @json['location'] 
        new QUICK.Location(x)
       
  ###*
  Lot number assigned by the manufacturer.
  ### 
  lotNumber: ->  @json['lotNumber'] 
 
 
  ###*
  Name of the manufacturer of the product
  ### 
  manufacturerName: ->  @json['manufacturerName'] 
 
 
  ###*
  Model identifier assigned by the manufacturer 
  ### 
  model: ->  @json['model'] 
 
 
  ###*
  Information collected from a consumer, patient, or family member about their perception of the care they received or from a care giver about the care provided.
  ### 
  owner: -> 
    if @json['owner']
      for x in @json['owner'] 
        new QUICK.Organization(x)
       
  ###*
  Patient information, if the device is affixed to a person.
  ### 
  patient: -> 
    if @json['patient']
      for x in @json['patient'] 
        new QUICK.Patient(x)
       
  ###*
  The identifier of a set of constraints placed on an Entity. If there are multiple templates specified for the element, then the element must satisfy ALL constraints defined in ANY template at that level.
  ### 
  profileId: -> 
    if @json['profileId']
      for x in @json['profileId'] 
        new QUICK.Identifier(x)
       
  ###*
  A code that identifies the type of device supplied with as much specificity as available.  E.g., wheelchair
  ### 
  type: -> 
    if @json['type']
      for x in @json['type'] 
        new QUICK.CodeableConcept(x)
       
  ###*
  FDA Mandated Unique Device Identifier. Use the human readable information (the content that the user sees, which is sometimes different to the exact syntax represented in the barcode) - see http://www.fda.gov/MedicalDevices/DeviceRegulationandGuidance/UniqueDeviceIdentification/default.htm.
  ### 
  udi: -> 
    if @json['udi']
      for x in @json['udi'] 
        new QUICK.Identifier(x)
       
  ###*
  A network address on which the device may be contacted directly.
  ### 
  url: ->  @json['url'] 
 
 
  ###*
  The version of the device, if the device has multiple releases under the same model, or if the device is software or carries firmware.
  ### 
  version: ->  @json['version'] 
 
 

module.exports.QUICK = QUICK
