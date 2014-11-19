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
Information about the protocol(s) under which the vaccine was administered
 
###
require './Quantity'
require './Organization'
require './CodeableConcept'
###*
@class VaccinationProtocol
@exports  VaccinationProtocol as quick.VaccinationProtocol
###
class QUICK.VaccinationProtocol
  constructor: (@json) ->
 
  ###*
  Indicates the authority who published the protocol? E.g. ACIP.
  ### 
  authority: -> 
    if @json['authority']
      for x in @json['authority'] 
        new QUICK.Organization(x)
       
  ###*
  The description about the protocol under which the vaccine was administered.
  ### 
  description: ->  @json['description'] 
 
 
  ###*
  Nominal position of dose in a series.
  ### 
  doseSequence: -> if @json['doseSequence'] then new QUICK.Quantity( @json['doseSequence'] )
 
 
  ###*
  Indicates if the immunization event should "count" against the protocol.
  ### 
  doseStatus: -> 
    if @json['doseStatus']
      for x in @json['doseStatus'] 
        new QUICK.CodeableConcept(x)
       
  ###*
  Provides an explanation as to why a immunization event should or should not count against the protocol
  ### 
  doseStatusReason: -> 
    if @json['doseStatusReason']
      for x in @json['doseStatusReason'] 
        new QUICK.CodeableConcept(x)
       
  ###*
  The targeted disease.
  ### 
  doseTarget: -> if @json['doseTarget'] then new QUICK.CodeableConcept( @json['doseTarget'] )
 
 
  ###*
  One possible path to achieve presumed immunity against a disease - within the context of an authority
  ### 
  series: ->  @json['series'] 
 
 
  ###*
  The recommended number of doses to achieve immunity
  ### 
  seriesDoses: -> if @json['seriesDoses'] then new QUICK.Quantity( @json['seriesDoses'] )
 
 

module.exports.QUICK = QUICK
