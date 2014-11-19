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
A way to provide food through a tube placed in the nose, mouth, the stomach, or the small intestine. 
 
###
require './Quantity'
require './NutritionProduct'
require './DosageInstruction'
###*
@class EnteralFormula
@exports  EnteralFormula as quick.EnteralFormula
###
class QUICK.EnteralFormula
  constructor: (@json) ->
 
  ###*
  Dosage and administration instructions for the enteral nutrition.
  ### 
  administration: -> 
    if @json['administration']
      for x in @json['administration'] 
        new QUICK.DosageInstruction(x)
       
  ###*
  An amount of calories per volume which identifies the type of formula.
  ### 
  caloricDensity: -> 
    if @json['caloricDensity']
      for x in @json['caloricDensity'] 
        new QUICK.Quantity(x)
       
  ###*
  The nutritional product to be administered
  ### 
  product: -> if @json['product'] then new QUICK.NutritionProduct( @json['product'] )
 
 

module.exports.QUICK = QUICK
