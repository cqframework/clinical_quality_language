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
###*
TextureModification specifies or modifies the texture for one or more types of food in a diet, e.g., ground, chopped, or puree. Texture modification is part of the diet specification and may have different textures ordered for different food groups, e.g., ground meat.
 
###
require './CodeableConcept'
###*
@class TextureModification
@exports  TextureModification as TextureModification
###
class TextureModification
  constructor: (@json) ->
 
  ###*
  Indicates the type of food to which the texture modification applies.
  ### 
  foodType: -> if @json['foodType'] then new CodeableConcept( @json['foodType'] )
 
 
  ###*
  A further modification to the texture, e.g. Pudding Thick. 
  ### 
  textureModifier: -> 
    if @json['textureModifier']
      for x in @json['textureModifier'] 
        new QUICK.CodeableConcept(x)
       
  ###*
  A code that identifies any texture  modifications that should be made, e.g., Pureed, Easy to Chew
  ### 
  textureType: -> if @json['textureType'] then new CodeableConcept( @json['textureType'] )
 
 

module.exports.TextureModification = TextureModification
