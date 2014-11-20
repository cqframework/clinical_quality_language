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
A preparation intended to supplement the diet and provide calories or nutrients, such as vitamins, minerals, fiber, fatty acids, carbohydrates, or amino acids, that may be missing or may not be consumed in sufficient quantity in a person's diet. Such products may be ordered in addition to the diet (either general or therapeutic) to enhance a person’s intake. Supplemental food products provide some but not all of a patient’s nutritional needs. 
 
###
require './Range'
require './Schedule'
require './NutritionProduct'
###*
@class NutritionalSupplement
@exports  NutritionalSupplement as NutritionalSupplement
###
class NutritionalSupplement
  constructor: (@json) ->
 
  ###*
  Any additives to be provided or administered, e.g., protein supplement, fiber supplement
  ### 
  additiveProduct: -> 
    if @json['additiveProduct']
      for x in @json['additiveProduct'] 
        new QUICK.NutritionProduct(x)
       
  ###*
  The base supplement to be provided or administered, e.g., standard formula
  ### 
  baseProduct: -> if @json['baseProduct'] then new QUICK.NutritionProduct( @json['baseProduct'] )
 
 
  ###*
  The frequency with which this supplement is administered.
  ### 
  frequency: -> 
    if @json['frequency']
      for x in @json['frequency'] 
        new QUICK.Schedule(x)
       
  ###*
  How much of the nutritional supplement to administer
  ### 
  quantity: -> 
    if @json['quantity']
      for x in @json['quantity'] 
        new QUICK.Range(x)
       

module.exports.NutritionalSupplement = NutritionalSupplement
