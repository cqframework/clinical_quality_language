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
A component of a multi-component substance administration. May be an additive in a composite IV.
 
###
require './Dosage'
require './CodeableConcept'
require './MedicationIngredient'
###*
@class Constituent
@exports  Constituent as quick.Constituent
###
class QUICK.Constituent
  constructor: (@json) ->
 
  ###*
  Generally the ingredient of the constituent (e.g., dopamine) and the quantity such as an additive in a composite IV.
  ### 
  constituent: -> if @json['constituent'] then new QUICK.MedicationIngredient( @json['constituent'] )
 
 
  ###*
  Indicates the category of the constituent. For instance, for a composite IV, the constituent may be either a 'diluent' or an 'additive'. For a TPN order, the constituent category may be a nutrient grouping such as 'electrolyte' or 'lipid', etc.
  ### 
  constituentType: -> if @json['constituentType'] then new QUICK.CodeableConcept( @json['constituentType'] )
 
 
  ###*
  The dose of the constituent that makes up the whole. E.g., 500ml 50% Dextrose solution
  ### 
  dose: -> if @json['dose'] then new QUICK.Dosage( @json['dose'] )
 
 

module.exports.QUICK = QUICK
