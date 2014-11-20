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
A method to control the positioning and movement of a specific area of the body. Such motion management may be conducted during a procedure.
 
###
require './BodySite'
require './CodeableConcept'
###*
@class MotionManagement
@exports  MotionManagement as MotionManagement
###
class MotionManagement
  constructor: (@json) ->
 
  ###*
  Immobilization device refers to the device or devices used to maximize reproducibility of positioning and to minimize motion of a part of a body for each radiation treatment.   For example, a commonly used immobilization device is a thermoplastic mask for patients being treated to the head and neck region. 
  ### 
  immobilizationDevice: -> 
    if @json['immobilizationDevice']
      for x in @json['immobilizationDevice'] 
        new QUICK.CodeableConcept(x)
       
  ###*
  Position defines the way that a patient should be positioned for a given procedure. Examples might include:

- Head: Tilted left or right, neck extended
- Body: Prone, supine, on left/right side
- Arms: Down by side, on chest, above head
- Legs: flat, bent
  ### 
  position: -> 
    if @json['position']
      for x in @json['position'] 
        new QUICK.CodeableConcept(x)
       
  ###*
  The area of the body whose motion is to be managed.
  ### 
  targetBodySite: -> 
    if @json['targetBodySite']
      for x in @json['targetBodySite'] 
        new QUICK.BodySite(x)
       

module.exports.MotionManagement = MotionManagement
