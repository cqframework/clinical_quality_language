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
Descriptor for the administration of vaccines to patients across all healthcare disciplines in all care settings and all regions. This does not include the administration of non-vaccine agents, even those that may have or claim immunological effects.
 
###
require './Vaccine'
require './Dosage'
require './CodeableConcept'
require './VaccinationProtocol'
###*
@class Immunization
@exports  Immunization as Immunization
###
class Immunization
  constructor: (@json) ->
 
  ###*
  The dose of the vaccine administered or to be administered
  ### 
  dosage: -> 
    if @json['dosage']
      for x in @json['dosage'] 
        new QUICK.Dosage(x)
       
  ###*
  The role of the dose in an immunization protocol
  ### 
  protocol: -> 
    if @json['protocol']
      for x in @json['protocol'] 
        new QUICK.VaccinationProtocol(x)
       
  ###*
  True if this statement describes the reported prior administration of a dose of vaccine rather than directly administered
  ### 
  reported: -> 
    if @json['reported']
      for x in @json['reported'] 
        new QUICK.CodeableConcept(x)
       
  ###*
  The vaccine product that is administered
  ### 
  vaccine: -> if @json['vaccine'] then new Vaccine( @json['vaccine'] )
 
 

module.exports.Immunization = Immunization
