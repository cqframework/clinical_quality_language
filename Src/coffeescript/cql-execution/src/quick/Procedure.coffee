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
A procedure is an activity that is performed with or on a patient as part of the provision of care. This can be a physical 'thing' like an operation, or less invasive like counseling or hypnotherapy. Examples include surgical procedures, diagnostic procedures, endoscopic procedures, biopsies, and exclude things for which there are specific types of acts defined, such as those for immunizations, medication administrations, nutrition administration, and use of devices.
 
###
require './Schedule'
require './BodySite'
require './CodeableConcept'
###*
@class Procedure
@exports  Procedure as Procedure
###
class Procedure
  constructor: (@json) ->
 
  ###*
  The body site used for gaining access to the target body site. E.g., femoral artery for a coronary angiography.
  ### 
  approachBodySite: -> 
    if @json['approachBodySite']
      for x in @json['approachBodySite'] 
        new QUICK.BodySite(x)
       
  ###*
  This is the code that identifies the procedure with as much specificity as available, or as required.  E.g., appendectomy, coronary artery bypass graft surgery.
  ### 
  procedureCode: -> if @json['procedureCode'] then new CodeableConcept( @json['procedureCode'] )
 
 
  ###*
  Describes the method used for the procedure and can vary depending on the procedure.  For example, a surgical procedure method might be laparoscopic surgery or robotic surgery; an imaging procedure such as a chest radiograph might have methods that represent the views such as PA and lateral; a laboratory procedure like urinalysis might have a method of clean catch; a respiratory care procedure such as supplemental oxygen might have a method of nasal cannula, hood, face mask, or non-rebreather mask.
  ### 
  procedureMethod: -> 
    if @json['procedureMethod']
      for x in @json['procedureMethod'] 
        new QUICK.CodeableConcept(x)
       
  ###*
  If the procedure is repeated, the frequency pattern for repetitions.
  ### 
  procedureSchedule: -> 
    if @json['procedureSchedule']
      for x in @json['procedureSchedule'] 
        new QUICK.Schedule(x)
       
  ###*
  The body site where the procedure takes place.  E.g., left lower arm for fracture reduction.
  ### 
  targetBodySite: -> 
    if @json['targetBodySite']
      for x in @json['targetBodySite'] 
        new QUICK.BodySite(x)
       

module.exports.Procedure = Procedure
