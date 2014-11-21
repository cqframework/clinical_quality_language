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
Exposure to an agent or a healthcare action that is believed to have consequences.
 
###
require './Period'
require './StatementOfOccurrence'
require './CodeableConcept'
###*
@class Exposure
@exports  Exposure as Exposure
###
class Exposure
  constructor: (@json) ->
 
  ###*
  Reference to an action believed to have caused the adverse event.
  ### 
  action: -> 
    if @json['action']
      for x in @json['action'] 
        new QUICK.StatementOfOccurrence(x)
       
  ###*
  The degree of certainty in whether the  exposure caused the event
  ### 
  causalityExpectation: -> 
    if @json['causalityExpectation']
      for x in @json['causalityExpectation'] 
        new QUICK.CodeableConcept(x)
       
  ###*
  When the exposure occurred 
  ### 
  exposureTime: -> 
    if @json['exposureTime']
      for x in @json['exposureTime'] 
        new QUICK.Period(x)
       
  ###*
  The stimulus, an agent or a type of action that may have caused the event.
  ### 
  stimulus: -> 
    if @json['stimulus']
      for x in @json['stimulus'] 
        new QUICK.CodeableConcept(x)
       

module.exports.Exposure = Exposure
