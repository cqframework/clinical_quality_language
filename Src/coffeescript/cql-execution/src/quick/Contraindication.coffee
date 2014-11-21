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
Describes a contraindication to a healthcare related action, e.g., medication intake, procedure.

A contraindication is a specific situation in which a drug, procedure, or surgery should not be used because it may be harmful to the patient, due to the presence of other conditions (e.g., kidney dysfunction) or some other acts (e.g., another medication)
 
###
require './Inference'
require './Period'
require './Act'
require './CodeableConcept'
###*
@class Contraindication
@exports  Contraindication as Contraindication
###
class Contraindication
  constructor: (@json) ->
 
  ###*
  The action that is to be withheld in the context of the contraindication. Note that a contraindication may apply to the administration of a substance or to the performance of a procedure, for instance.
  ### 
  contraindicatedAct: -> if @json['contraindicatedAct'] then new Act( @json['contraindicatedAct'] )
 
 
  ###*
  The degree or strength of the contraindication; may be absolute or relative.

An absolute contraindication means that the course of action MUST be avoided.

A relative contraindication means that the course of action SHOULD be avoided but that the risk of proceeding with the course of action may be outweighed by other factors or mitigated in some way.
  ### 
  degree: -> 
    if @json['degree']
      for x in @json['degree'] 
        new QUICK.CodeableConcept(x)
       
  ###*
  The time period during which the contraindication holds. This may be an open interval if no end time is currently known.
  ### 
  effectiveTime: -> 
    if @json['effectiveTime']
      for x in @json['effectiveTime'] 
        new QUICK.Period(x)
       
  ###*
  How the conclusion was made about the act being contraindicated, i.e., the underlying patient conditions and the method used for inferring.
  ### 
  inference: -> 
    if @json['inference']
      for x in @json['inference'] 
        new QUICK.Inference(x)
       

module.exports.Contraindication = Contraindication
