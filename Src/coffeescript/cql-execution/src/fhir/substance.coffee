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
@namespacing scoping into the FHIR namespace
###
require './core'

###* 
 Embedded class
@class SubstanceInstanceComponent
@exports  SubstanceInstanceComponent as SubstanceInstanceComponent
###
class SubstanceInstanceComponent
  constructor: (@json) ->
    super(@json)
  ###*
  Identifier associated with the package/container (usually a label affixed directly).
  @returns {Identifier}
  ###
  identifier: -> if @json['identifier'] then new Identifier(@json['identifier'])
  
  ###*
  When the substance is no longer valid to use. For some substances, a single arbitrary date is used for expiry.
  @returns {Date}
  ###
  expiry: -> if @json['expiry'] then new Date(@json['expiry'])
  
  ###*
  The amount of the substance.
  @returns {Quantity}
  ###
  quantity: -> if @json['quantity'] then new Quantity(@json['quantity'])
  

###* 
 Embedded class
@class SubstanceIngredientComponent
@exports  SubstanceIngredientComponent as SubstanceIngredientComponent
###
class SubstanceIngredientComponent
  constructor: (@json) ->
    super(@json)
  ###*
  The amount of the ingredient in the substance - a concentration ratio.
  @returns {Ratio}
  ###
  quantity: -> if @json['quantity'] then new Ratio(@json['quantity'])
  
  ###*
  Another substance that is a component of this substance.
  @returns {Reference}
  ###
  substance: -> if @json['substance'] then new Reference(@json['substance'])
  
###*
A homogeneous material with a definite composition.
@class Substance
@exports Substance as Substance
###
class Substance 
  constructor: (@json) ->
  ###*
  A code (or set of codes) that identify this substance.
  @returns {CodeableConcept}
  ###
  fhirType: -> if @json['fhirType'] then new CodeableConcept(@json['fhirType'])
  
  ###*
  A description of the substance - its appearance, handling requirements, and other usage notes.
  @returns {Array} an array of {@link String} objects
  ###
  description:-> @json['description']
  
  ###*
  Substance may be used to describe a kind of substance, or a specific package/container of the substance: an instance.
  @returns {SubstanceInstanceComponent}
  ###
  instance: -> if @json['instance'] then new SubstanceInstanceComponent(@json['instance'])
  
  ###*
  A substance can be composed of other substances.
  @returns {Array} an array of {@link SubstanceIngredientComponent} objects
  ###
  ingredient: ->
    if @json['ingredient']
      for item in @json['ingredient']
        new SubstanceIngredientComponent(item)
  



module.exports.Substance = Substance
