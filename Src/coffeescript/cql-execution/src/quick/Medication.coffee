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
Primarily used for identification and definition of Medication, but also covers ingredients and packaging.
 
###
require './EntityCharacteristic'
require './CodeableConcept'
require './MedicationIngredient'
require './Identifier'
###*
@class Medication
@exports  Medication as quick.Medication
###
class QUICK.Medication
  constructor: (@json) ->
 
  ###*
  The characteristics of this entity.
  ### 
  characteristic: -> 
    if @json['characteristic']
      for x in @json['characteristic'] 
        new QUICK.EntityCharacteristic(x)
       
  ###*
  A code (or set of codes) that identify this medication. Usage note: This could be a standard drug code such as a drug regulator code, RxNorm code, SNOMED CT code, etc. It could also be a local formulary code, optionally with translations to the standard drug codes.
  ### 
  code: -> if @json['code'] then new QUICK.CodeableConcept( @json['code'] )
 
 
  ###*
  Date of expiry of this product (if applicable).
  ### 
  expiry: ->  @json['expiry'] 
 
 
  ###*
  Describes the form of the item. Powder; tables; carton.
  ### 
  form: -> 
    if @json['form']
      for x in @json['form'] 
        new QUICK.CodeableConcept(x)
       
  ###*
  <font color="#0f0f0f">The entity's unique identifier.  Used for internal tracking purposes.  It must be provided if user wants it returned as part of any output, otherwise it will be auto-generated, if needed, by CDS system.  Does not need to be the entity's "real" identifier. </font>
  ### 
  id: -> 
    if @json['id']
      for x in @json['id'] 
        new QUICK.Identifier(x)
       
  ###*
  A constituent of interest in the medication product (e.g., sulfamethoxazole 800 mg)
  ### 
  ingredient: -> 
    if @json['ingredient']
      for x in @json['ingredient'] 
        new QUICK.MedicationIngredient(x)
       
  ###*
  Set to true if the item is attributable to a specific manufacturer
  ### 
  isBrand: ->  @json['isBrand'] 
 
 
  ###*
  Lot number assigned by the manufacturer.
  ### 
  lotNumber: ->  @json['lotNumber'] 
 
 
  ###*
  Name of the manufacturer of the product
  ### 
  manufacturerName: ->  @json['manufacturerName'] 
 
 
  ###*
  The identifier of a set of constraints placed on an Entity. If there are multiple templates specified for the element, then the element must satisfy ALL constraints defined in ANY template at that level.
  ### 
  profileId: -> 
    if @json['profileId']
      for x in @json['profileId'] 
        new QUICK.Identifier(x)
       

module.exports.QUICK = QUICK
