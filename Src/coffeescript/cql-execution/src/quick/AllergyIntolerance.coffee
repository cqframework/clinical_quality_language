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
A description of an undesirable physiologic or other reaction to an external stimulus.
 
###
require './Period'
require './CodeableConcept'
###*
@class AllergyIntolerance
@exports  AllergyIntolerance as AllergyIntolerance
###
class AllergyIntolerance
  constructor: (@json) ->
 
  ###*
  The potential seriousness of a future reaction. This represents a clinical judgment about the worst case scenario for a future reaction. It would be based on the severity of past reactions, the strength of the stimulus (e.g., the dose and route of exposure) that produced past reactions, and the life-threatening or organ system threatening potential of the reaction type.   
  ### 
  criticality: -> 
    if @json['criticality']
      for x in @json['criticality'] 
        new QUICK.CodeableConcept(x)
       
  ###*
  The time period during which the allergy or intolerance is effective.
  ### 
  effectiveTime: -> 
    if @json['effectiveTime']
      for x in @json['effectiveTime'] 
        new QUICK.Period(x)
       
  ###*
  The possible reactions to the stimulus, e.g., respiratory distress.
  ### 
  reaction: -> 
    if @json['reaction']
      for x in @json['reaction'] 
        new QUICK.CodeableConcept(x)
       
  ###*
  A code that indicates whether this sensitivity is of an allergic nature or an intolerance to a stimulus.
  ### 
  sensitivityType: -> if @json['sensitivityType'] then new CodeableConcept( @json['sensitivityType'] )
 
 
  ###*
  The stimulus that causes the undesirable effect, or when a non-allergy is being specified, the stimulus that does not lead to an undesirable effect.

The stimulus may be a substance (amount of a substance that would not produce a reaction in most individuals) or other agents, e.g., a signal, confined space.

A substance is a physical entity and for purposes of this aspect of the model can mean a drug or biologic, food, chemical agent, plants, animals, plastics etc.
  ### 
  stimulus: -> if @json['stimulus'] then new CodeableConcept( @json['stimulus'] )
 
 

module.exports.AllergyIntolerance = AllergyIntolerance
