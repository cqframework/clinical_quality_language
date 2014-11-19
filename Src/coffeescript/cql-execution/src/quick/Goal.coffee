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
A defined target or measure to be achieved in the process of patient care; a desired outcome. A typical goal is expressed as a change in status expected at a defined future time.
 
###
require './Period'
require './CodeableConcept'
require './Element'
###*
@class Goal
@exports  Goal as quick.Goal
###
class QUICK.Goal
  constructor: (@json) ->
 
  ###*
  The time that is targeted for the goal to be attained.  For example, there may be a goal to reach a weight of X pounds by a particular date.
  ### 
  goalAchievementTargetTime: -> 
    if @json['goalAchievementTargetTime']
      for x in @json['goalAchievementTargetTime'] 
        new QUICK.Period(x)
       
  ###*
  The metric that is the clinical subject of the goal.  Typically a measurable clinical attribute of the subject.  E.g., weight, blood pressure, hemoglobin A1c level.
  ### 
  goalFocus: -> if @json['goalFocus'] then new QUICK.CodeableConcept( @json['goalFocus'] )
 
 
  ###*
  The time in which the subject pursues the goal.  This includes pursuing maintenance of a goal that has already been achieved.  
The end time of the interval may be "open" or not stated, if the goal is being indefinitely pursued.  This time is optional, as, for example, one may simply wish to propose weight loss without specifying a pursuit effective time.
  ### 
  goalPursuitEffectiveTime: -> 
    if @json['goalPursuitEffectiveTime']
      for x in @json['goalPursuitEffectiveTime'] 
        new QUICK.Period(x)
       
  ###*
  The metric whose achievement would signify the fulfillment of the goal.  E.g., 150 pounds, 7.0%.
  ### 
  goalValue: -> if @json['goalValue'] then new QUICK.Element( @json['goalValue'] )
 
 

module.exports.QUICK = QUICK
