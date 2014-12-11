
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
@class InstanceComponent
@exports  InstanceComponent as InstanceComponent
###
class InstanceComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  SOP class uid of the selected instance.
  @returns {oid}
  ###
  sopClass: -> if @json['sopClass'] then new oid(@json['sopClass'])
  
  ###*
  SOP Instance uid of the selected instance.
  @returns {oid}
  ###
  uid: -> if @json['uid'] then new oid(@json['uid'])
  
  ###*
  The DICOM Application Entity Title where the DICOM SOP instance can be retrieved.
  @returns {Array} an array of {@link String} objects
  ###
  retrieveAETitle:-> @json['retrieveAETitle']
  
  ###*
  WADO-RS URL to retrieve the DICOM SOP Instance.
  @returns {Array} an array of {@link String} objects
  ###
  retrieveUrl:-> @json['retrieveUrl']
  

###* 
Embedded class
@class SeriesComponent
@exports  SeriesComponent as SeriesComponent
###
class SeriesComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  Series instance uid of the SOP instances in the selection.
  @returns {oid}
  ###
  uid: -> if @json['uid'] then new oid(@json['uid'])
  
  ###*
  The DICOM Application Entity Title where the series can be retrieved.
Note that this AE Title is provided to retrieve all SOP instances of the series not only those in the selection.
  @returns {Array} an array of {@link String} objects
  ###
  retrieveAETitle:-> @json['retrieveAETitle']
  
  ###*
  WADO-RS URL to retrieve the series Note that this URL retrieves all SOP instances of the series not only those in the selection.
  @returns {Array} an array of {@link String} objects
  ###
  retrieveUrl:-> @json['retrieveUrl']
  
  ###*
  Identity and locating information of the selected DICOM SOP instances.
  @returns {Array} an array of {@link InstanceComponent} objects
  ###
  instance: ->
    if @json['instance']
      for item in @json['instance']
        new InstanceComponent(item)
  

###* 
Embedded class
@class StudyComponent
@exports  StudyComponent as StudyComponent
###
class StudyComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  Study instance uid of the SOP instances in the selection.
  @returns {oid}
  ###
  uid: -> if @json['uid'] then new oid(@json['uid'])
  
  ###*
  The DICOM Application Entity Title where the study can be retrieved.
Note that this AE Title is provided to retrieve all SOP instances of the study, not only those in the selection.
  @returns {Array} an array of {@link String} objects
  ###
  retrieveAETitle:-> @json['retrieveAETitle']
  
  ###*
  WADO-RS URL to retrieve the study. Note that this URL retrieves all SOP instances of the study, not only those in the selection.
  @returns {Array} an array of {@link String} objects
  ###
  retrieveUrl:-> @json['retrieveUrl']
  
  ###*
  Series indetity and locating information of the DICOM SOP instances in the selection.
  @returns {Array} an array of {@link SeriesComponent} objects
  ###
  series: ->
    if @json['series']
      for item in @json['series']
        new SeriesComponent(item)
  
###*
A set of DICOM SOP Instances of a patient, selected for some application purpose, e.g., quality assurance, teaching, conference, consulting, etc.  Objects selected can be from different studies, but must be of the same patient.
@class ImagingObjectSelection
@exports ImagingObjectSelection as ImagingObjectSelection
###
class ImagingObjectSelection extends DomainResource
  constructor: (@json) ->
    super(@json)
  ###*
  Instance UID of the DICOM KOS SOP Instances represenetd in this resource.
  @returns {oid}
  ###
  uid: -> if @json['uid'] then new oid(@json['uid'])
  
  ###*
  A patient resource reference which is the patient subject of all DICOM SOP Instances in this key object selection.
  @returns {Reference}
  ###
  patient: -> if @json['patient'] then new Reference(@json['patient'])
  
  ###*
  The reason for, or significance of, the selection of objects referenced in the resource.
  @returns {CodeableConcept}
  ###
  title: -> if @json['title'] then new CodeableConcept(@json['title'])
  
  ###*
  Text description of the DICOM SOP instances selected in the key object selection. This should be aligned with the content of the title element, and can provide further explanation of the SOP instances in the selection.
  @returns {Array} an array of {@link String} objects
  ###
  description:-> @json['description']
  
  ###*
  Author of key object selection. It can be a human authtor or a device which made the decision of the SOP instances selected. For example, a radiologist selected a set of imaging SOP instances to attached in a diagnostic report, and a CAD application may author a selection to describe SOP instances it used to generate a detection conclusion.
  @returns {Reference}
  ###
  author: -> if @json['author'] then new Reference(@json['author'])
  
  ###*
  Date and time when the key object selection was authored. Note that this is the date and time the DICOM SOP instances in the selection were selected (selection decision making). It is different from the creation date and time of the selection resource.
  @returns {Array} an array of {@link Date} objects
  ###
  authoringTime:-> if @json['authoringTime'] then DT.DateTime.parse(@json['authoringTime'])
  
  ###*
  Study identity and locating information of the DICOM SOP instances in the selection.
  @returns {Array} an array of {@link StudyComponent} objects
  ###
  study: ->
    if @json['study']
      for item in @json['study']
        new StudyComponent(item)
  



module.exports.ImagingObjectSelection = ImagingObjectSelection
