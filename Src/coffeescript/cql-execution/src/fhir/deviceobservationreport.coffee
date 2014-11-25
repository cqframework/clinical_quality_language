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
@namespacing scoping into the FHIR namespace
###
require './core'
require './element'
require './resource'

###* 
 Embedded class
@class DeviceObservationReportVirtualDeviceChannelMetricComponent
@exports  DeviceObservationReportVirtualDeviceChannelMetricComponent as DeviceObservationReportVirtualDeviceChannelMetricComponent
###
class DeviceObservationReportVirtualDeviceChannelMetricComponent extends Element
  constructor: (@json) ->
    super(@json)
  ###*
  The data for the metric.
  @returns {Reference}
  ###
  observation: -> if @json['observation'] then new Reference(@json['observation'])
  

###* 
 Embedded class
@class DeviceObservationReportVirtualDeviceChannelComponent
@exports  DeviceObservationReportVirtualDeviceChannelComponent as DeviceObservationReportVirtualDeviceChannelComponent
###
class DeviceObservationReportVirtualDeviceChannelComponent extends Element
  constructor: (@json) ->
    super(@json)
  ###*
  Describes the channel.
  @returns {CodeableConcept}
  ###
  code: -> if @json['code'] then new CodeableConcept(@json['code'])
  
  ###*
  A piece of measured or derived data that is reported by the machine.
  @returns {Array} an array of {@link DeviceObservationReportVirtualDeviceChannelMetricComponent} objects
  ###
  metric: ->
    if @json['metric']
      for item in @json['metric']
        new DeviceObservationReportVirtualDeviceChannelMetricComponent(item)
  

###* 
 Embedded class
@class DeviceObservationReportVirtualDeviceComponent
@exports  DeviceObservationReportVirtualDeviceComponent as DeviceObservationReportVirtualDeviceComponent
###
class DeviceObservationReportVirtualDeviceComponent extends Element
  constructor: (@json) ->
    super(@json)
  ###*
  Describes the compartment.
  @returns {CodeableConcept}
  ###
  code: -> if @json['code'] then new CodeableConcept(@json['code'])
  
  ###*
  Groups together physiological measurement data and derived data.
  @returns {Array} an array of {@link DeviceObservationReportVirtualDeviceChannelComponent} objects
  ###
  channel: ->
    if @json['channel']
      for item in @json['channel']
        new DeviceObservationReportVirtualDeviceChannelComponent(item)
  
###*
Describes the data produced by a device at a point in time.
@class DeviceObservationReport
@exports DeviceObservationReport as DeviceObservationReport
###
class DeviceObservationReport extends  Resource
  constructor: (@json) ->
    super(@json)
  ###*
  The point in time that the values are reported.
  @returns {Date}
  ###
  instant: -> if @json['instant'] then new Date(@json['instant'])
  
  ###*
  An identifier assigned to this observation bu the source device that made the observation.
  @returns {Identifier}
  ###
  identifier: -> if @json['identifier'] then new Identifier(@json['identifier'])
  
  ###*
  Identification information for the device that is the source of the data.
  @returns {Reference}
  ###
  source: -> if @json['source'] then new Reference(@json['source'])
  
  ###*
  The subject of the measurement.
  @returns {Reference}
  ###
  subject: -> if @json['subject'] then new Reference(@json['subject'])
  
  ###*
  A medical-related subsystem of a medical device.
  @returns {Array} an array of {@link DeviceObservationReportVirtualDeviceComponent} objects
  ###
  virtualDevice: ->
    if @json['virtualDevice']
      for item in @json['virtualDevice']
        new DeviceObservationReportVirtualDeviceComponent(item)
  



module.exports.DeviceObservationReport = DeviceObservationReport
