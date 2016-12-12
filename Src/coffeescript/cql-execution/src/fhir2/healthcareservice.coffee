
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
DT = require '../cql-datatypes'
CORE = require('./core')
Element = CORE.Element
Resource = CORE.Resource
Timing = CORE.Timing
Period = CORE.Period
Parameters = CORE.Parameters
Coding = CORE.Coding
Resource = CORE.Resource
Range = CORE.Range
Quantity = CORE.Quantity
Attachment = CORE.Attachment
BackboneElement = CORE.BackboneElement
DomainResource = CORE.DomainResource
ContactPoint = CORE.ContactPoint
ElementDefinition = CORE.ElementDefinition
Extension = CORE.Extension
HumanName = CORE.HumanName
Address = CORE.Address
Ratio = CORE.Ratio
SampledData = CORE.SampledData
Reference = CORE.Reference
CodeableConcept = CORE.CodeableConcept
Identifier = CORE.Identifier
Narrative = CORE.Narrative
Element = CORE.Element

###* 
Embedded class
@class ServiceTypeComponent
@exports  ServiceTypeComponent as ServiceTypeComponent
###
class ServiceTypeComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  The specific type of service being delivered or performed.
  @returns {CodeableConcept}
  ###
  type: -> if @json['type'] then new CodeableConcept(@json['type'])
  
  ###*
  Collection of Specialties handled by the Service Site. This is more of a Medical Term.
  @returns {Array} an array of {@link CodeableConcept} objects
  ###
  specialty: ->
    if @json['specialty']
      for item in @json['specialty']
        new CodeableConcept(item)
  

###* 
Embedded class
@class HealthcareServiceAvailableTimeComponent
@exports  HealthcareServiceAvailableTimeComponent as HealthcareServiceAvailableTimeComponent
###
class HealthcareServiceAvailableTimeComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  Indicates which Days of the week are available between the Start and End Times.
  @returns {Array} an array of {@link CodeableConcept} objects
  ###
  daysOfWeek: ->
    if @json['daysOfWeek']
      for item in @json['daysOfWeek']
        new CodeableConcept(item)
  
  ###*
  Is this always available? (hence times are irrelevant) e.g. 24 hour service.
  @returns {Array} an array of {@link boolean} objects
  ###
  allDay:-> @json['allDay']
  
  ###*
  The opening time of day (the date is not included). Note: If the AllDay flag is set, then this time is ignored.
  @returns {Array} an array of {@link Date} objects
  ###
  availableStartTime:-> if @json['availableStartTime'] then DT.DateTime.parse(@json['availableStartTime'])
  
  ###*
  The closing time of day (the date is not included). Note: If the AllDay flag is set, then this time is ignored.
  @returns {Array} an array of {@link Date} objects
  ###
  availableEndTime:-> if @json['availableEndTime'] then DT.DateTime.parse(@json['availableEndTime'])
  

###* 
Embedded class
@class HealthcareServiceNotAvailableTimeComponent
@exports  HealthcareServiceNotAvailableTimeComponent as HealthcareServiceNotAvailableTimeComponent
###
class HealthcareServiceNotAvailableTimeComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  The reason that can be presented to the user as to why this time is not available.
  @returns {Array} an array of {@link String} objects
  ###
  description:-> @json['description']
  
  ###*
  Service is not available (seasonally or for a public holiday) from this date.
  @returns {Array} an array of {@link Date} objects
  ###
  startDate:-> if @json['startDate'] then DT.DateTime.parse(@json['startDate'])
  
  ###*
  Service is not available (seasonally or for a public holiday) until this date.
  @returns {Array} an array of {@link Date} objects
  ###
  endDate:-> if @json['endDate'] then DT.DateTime.parse(@json['endDate'])
  
###*
(informative) The details of a Healthcare Service available at a location.
@class HealthcareService
@exports HealthcareService as HealthcareService
###
class HealthcareService extends DomainResource
  constructor: (@json) ->
    super(@json)
  ###*
  External Ids for this item.
  @returns {Array} an array of {@link Identifier} objects
  ###
  identifier: ->
    if @json['identifier']
      for item in @json['identifier']
        new Identifier(item)
  
  ###*
  The location where this healthcare service may be provided.
  @returns {Reference}
  ###
  location: -> if @json['location'] then new Reference(@json['location'])
  
  ###*
  Identifies the broad category of service being performed or delivered. Selecting a Service Category then determines the list of relevant service types that can be selected in the Primary Service Type.
  @returns {CodeableConcept}
  ###
  serviceCategory: -> if @json['serviceCategory'] then new CodeableConcept(@json['serviceCategory'])
  
  ###*
  A specific type of service that may be delivered or performed.
  @returns {Array} an array of {@link ServiceTypeComponent} objects
  ###
  serviceType: ->
    if @json['serviceType']
      for item in @json['serviceType']
        new ServiceTypeComponent(item)
  
  ###*
  Further description of the service as it would be presented to a consumer while searching.
  @returns {Array} an array of {@link String} objects
  ###
  serviceName:-> @json['serviceName']
  
  ###*
  Additional description of the  or any specific issues not covered by the other attributes, which can be displayed as further detail under the serviceName.
  @returns {Array} an array of {@link String} objects
  ###
  comment:-> @json['comment']
  
  ###*
  Extra details about the service that can't be placed in the other fields.
  @returns {Array} an array of {@link String} objects
  ###
  extraDetails:-> @json['extraDetails']
  
  ###*
  The free provision code provides a link to the Free Provision reference entity to enable the selection of one free provision type.
  @returns {CodeableConcept}
  ###
  freeProvisionCode: -> if @json['freeProvisionCode'] then new CodeableConcept(@json['freeProvisionCode'])
  
  ###*
  Does this service have specific eligibility requirements that need to be met in order to use the service.
  @returns {CodeableConcept}
  ###
  eligibility: -> if @json['eligibility'] then new CodeableConcept(@json['eligibility'])
  
  ###*
  The description of service eligibility should, in general, not exceed one or two paragraphs. It should be sufficient for a prospective consumer to determine if they are likely to be eligible or not. Where eligibility requirements and conditions are complex, it may simply be noted that an eligibility assessment is required. Where eligibility is determined by an outside source, such as an Act of Parliament, this should be noted, preferably with a reference to a commonly available copy of the source document such as a web page.
  @returns {Array} an array of {@link String} objects
  ###
  eligibilityNote:-> @json['eligibilityNote']
  
  ###*
  Indicates whether or not a prospective consumer will require an appointment for a particular service at a Site to be provided by the Organization. Indicates if an appointment is required for access to this service. If this flag is 'NotDefined', then this flag is overridden by the Site's availability flag. (ConditionalIndicator Enum).
  @returns {CodeableConcept}
  ###
  appointmentRequired: -> if @json['appointmentRequired'] then new CodeableConcept(@json['appointmentRequired'])
  
  ###*
  If there is an image associated with this Service Site, its URI can be included here.
  @returns {Array} an array of {@link String} objects
  ###
  imageURI:-> @json['imageURI']
  
  ###*
  A Collection of times that the Service Site is available.
  @returns {Array} an array of {@link HealthcareServiceAvailableTimeComponent} objects
  ###
  availableTime: ->
    if @json['availableTime']
      for item in @json['availableTime']
        new HealthcareServiceAvailableTimeComponent(item)
  
  ###*
  Not avail times - need better description.
  @returns {Array} an array of {@link HealthcareServiceNotAvailableTimeComponent} objects
  ###
  notAvailableTime: ->
    if @json['notAvailableTime']
      for item in @json['notAvailableTime']
        new HealthcareServiceNotAvailableTimeComponent(item)
  
  ###*
  A description of Site availability exceptions, e.g., public holiday availability. Succinctly describing all possible exceptions to normal Site availability as details in the Available Times and Not Available Times.
  @returns {Array} an array of {@link String} objects
  ###
  availabilityExceptions:-> @json['availabilityExceptions']
  
  ###*
  The public part of the 'keys' allocated to an Organization by an accredited body to support secure exchange of data over the internet. To be provided by the Organization, where available.
  @returns {Array} an array of {@link String} objects
  ###
  publicKey:-> @json['publicKey']
  
  ###*
  Program Names that can be used to categorize the service.
  @returns {Array} an array of {@link String} objects
  ###
  programName:-> @json['programName']
  
  ###*
  List of contacts related to this specific healthcare service. If this is empty, then refer to the location's contacts.
  @returns {Array} an array of {@link ContactPoint} objects
  ###
  contactPoint: ->
    if @json['contactPoint']
      for item in @json['contactPoint']
        new ContactPoint(item)
  
  ###*
  Collection of Characteristics (attributes).
  @returns {Array} an array of {@link CodeableConcept} objects
  ###
  characteristic: ->
    if @json['characteristic']
      for item in @json['characteristic']
        new CodeableConcept(item)
  
  ###*
  Ways that the service accepts referrals.
  @returns {Array} an array of {@link CodeableConcept} objects
  ###
  referralMethod: ->
    if @json['referralMethod']
      for item in @json['referralMethod']
        new CodeableConcept(item)
  
  ###*
  The setting where this service can be provided, such is in home, or at location in organisation.
  @returns {Array} an array of {@link CodeableConcept} objects
  ###
  setting: ->
    if @json['setting']
      for item in @json['setting']
        new CodeableConcept(item)
  
  ###*
  Collection of Target Groups for the Service Site (The target audience that this service is for).
  @returns {Array} an array of {@link CodeableConcept} objects
  ###
  targetGroup: ->
    if @json['targetGroup']
      for item in @json['targetGroup']
        new CodeableConcept(item)
  
  ###*
  Need better description.
  @returns {Array} an array of {@link CodeableConcept} objects
  ###
  coverageArea: ->
    if @json['coverageArea']
      for item in @json['coverageArea']
        new CodeableConcept(item)
  
  ###*
  Need better description.
  @returns {Array} an array of {@link CodeableConcept} objects
  ###
  catchmentArea: ->
    if @json['catchmentArea']
      for item in @json['catchmentArea']
        new CodeableConcept(item)
  
  ###*
  List of the specific.
  @returns {Array} an array of {@link CodeableConcept} objects
  ###
  serviceCode: ->
    if @json['serviceCode']
      for item in @json['serviceCode']
        new CodeableConcept(item)
  



module.exports.HealthcareService = HealthcareService
