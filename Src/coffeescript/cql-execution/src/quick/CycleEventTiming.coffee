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
Identifies a repeating pattern to the intended time periods such as the number of occurrences in a given time period, the days in a multi-day cycle, or a code representing the frequency of occurrence for a given cycle.
 
###
require './CodeableConcept'
###*
@class CycleEventTiming
@exports  CycleEventTiming as CycleEventTiming
###
class CycleEventTiming
  constructor: (@json) ->
 
  ###*
  A code that identifies the occurrence of daily life that determine timing.

This is an example value set with codes taken from http://hl7.org/fhir/v3/TimingEvent:

HS HS event occurs [duration] before the hour of sleep (or trying to).
WAKE WAKE event occurs [duration] after waking.
AC AC event occurs [duration] before a meal (from the Latin ante cibus).
ACM ACM event occurs [duration] before breakfast (from the Latin ante cibus matutinus).
ACD ACD event occurs [duration] before lunch (from the Latin ante cibus diurnus).
ACV ACV event occurs [duration] before dinner (from the Latin ante cibus vespertinus).
PC PC event occurs [duration] after a meal (from the Latin post cibus).
PCM PCM event occurs [duration] after breakfast (from the Latin post cibus matutinus).
PCD PCD event occurs [duration] after lunch (from the Latin post cibus diurnus).
PCV PCV event occurs [duration] after dinner (from the Latin post cibus vespertinus).
  ### 
  when: -> if @json['when'] then new QUICK.CodeableConcept( @json['when'] )
 
 

module.exports.CycleEventTiming = CycleEventTiming
