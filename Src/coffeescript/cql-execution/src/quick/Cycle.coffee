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
Represents a predictable periodic interval where events may occur at specific points within this interval. Examples may include:

1. An event that may occur TID.
2. An event that may occur TID but at specific times such as 8am, noon, and 3pm.
3. An event that may occur three times a day but the interval is not important.
4. An event that may occur three times a day where the interval between events must be 8hrs (Q8H).

Note that cycles may be nested. For instance, 
A chemotherapy regimen where a substance is administered TID on day 1,5,10 of a 10-day cycle.
 
###
require './Period'
require './Quantity'
require './CycleEventTiming'
###*
@class Cycle
@exports  Cycle as Cycle
###
class Cycle
  constructor: (@json) ->
 
  ###*
  Positive offset between the end of the first cycle and the start of the second one. That is, the start of the next cycle shall start after then end of the previous cycle.
  ### 
  cycleLagTime: -> if @json['cycleLagTime'] then new QUICK.Quantity( @json['cycleLagTime'] )
 
 
  ###*
  Negative offset between the end of the previous cycle and the start of the next cycle. That is, the start of the next cycle shall start before the end of the previous cycle.
  ### 
  cycleLeadTime: -> if @json['cycleLeadTime'] then new QUICK.Quantity( @json['cycleLeadTime'] )
 
 
  ###*
  The duration of the overall cycle or subcycle.
  ### 
  cycleLength: -> if @json['cycleLength'] then new QUICK.Quantity( @json['cycleLength'] )
 
 
  ###*
  Identifies a repeating pattern to the intended time periods such as the number of occurrences in a given time period, the days in a multi-day cycle, or a code representing the frequency of occurrence for a given cycle.
  ### 
  cycleTiming: -> 
    if @json['cycleTiming']
      for x in @json['cycleTiming'] 
        new QUICK.CycleEventTiming(x)
       
  ###*
  Point in time when the cycle should end.
  ### 
  endsOn: -> if @json['endsOn'] then new QUICK.Period( @json['endsOn'] )
 
 
  ###*
  Number of times to repeat the cycle including the first one. When not specified, assumed to be 1.
  ### 
  totalCycleCount: -> if @json['totalCycleCount'] then new QUICK.Quantity( @json['totalCycleCount'] )
 
 

module.exports.Cycle = Cycle
