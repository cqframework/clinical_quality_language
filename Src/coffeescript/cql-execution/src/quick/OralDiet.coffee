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
Concept generally representing food and/or a nutritional supplement prepared from food ingredients that is self-administered by a patient and consumed orally. 

A patient can have only one effective oral diet at a time.
 
###
require './Schedule'
require './NutrientModification'
require './CodeableConcept'
require './TextureModification'
###*
@class OralDiet
@exports  OralDiet as OralDiet
###
class OralDiet
  constructor: (@json) ->
 
  ###*
  Specifies the type of diet ordered.  The dietCode may specify what kind of diet is ordered such as 'Consistent carbohydrate diet'.
  ### 
  dietType: -> 
    if @json['dietType']
      for x in @json['dietType'] 
        new QUICK.CodeableConcept(x)
       
  ###*
  Indicates what type of food the diet should contain.
  ### 
  foodType: -> 
    if @json['foodType']
      for x in @json['foodType'] 
        new QUICK.CodeableConcept(x)
       
  ###*
  The frequency with which this diet item is administered.
  ### 
  frequency: -> 
    if @json['frequency']
      for x in @json['frequency'] 
        new QUICK.Schedule(x)
       
  ###*
  Indicates whether the diet item is currently in effect for the patient. 
  ### 
  isInEffect: ->  @json['isInEffect'] 
 
 
  ###*
  Consists of the nutrient (e.g., Sodium) and the amount in the diet (e.g., 20-30g)
  ### 
  nutrient: -> 
    if @json['nutrient']
      for x in @json['nutrient'] 
        new QUICK.NutrientModification(x)
       
  ###*
  Specifies or modifies the texture for one or more types of food in a diet
  ### 
  texture: -> 
    if @json['texture']
      for x in @json['texture'] 
        new QUICK.TextureModification(x)
       

module.exports.OralDiet = OralDiet
