
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
@class DiagnosticReportImageComponent
@exports  DiagnosticReportImageComponent as DiagnosticReportImageComponent
###
class DiagnosticReportImageComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  A comment about the image. Typically, this is used to provide an explanation for why the image is included, or to draw the viewer's attention to important features.
  @returns {Array} an array of {@link String} objects
  ###
  comment:-> @json['comment']
  
  ###*
  Reference to the image source.
  @returns {Reference}
  ###
  link: -> if @json['link'] then new Reference(@json['link'])
  
###*
The findings and interpretation of diagnostic  tests performed on patients, groups of patients, devices, and locations, and/or specimens derived from these. The report includes clinical context such as requesting and provider information, and some mix of atomic results, images, textual and coded interpretation, and formatted representation of diagnostic reports.
@class DiagnosticReport
@exports DiagnosticReport as DiagnosticReport
###
class DiagnosticReport extends DomainResource
  constructor: (@json) ->
    super(@json)
  ###*
  A code or name that describes this diagnostic report.
  @returns {CodeableConcept}
  ###
  name: -> if @json['name'] then new CodeableConcept(@json['name'])
  
  ###*
  The status of the diagnostic report as a whole.
  @returns {Array} an array of {@link String} objects
  ###
  status:-> @json['status']
  
  ###*
  The date and/or time that this version of the report was released from the source diagnostic service.
  @returns {Array} an array of {@link Date} objects
  ###
  issued:-> if @json['issued'] then DT.DateTime.parse(@json['issued'])
  
  ###*
  The subject of the report. Usually, but not always, this is a patient. However diagnostic services also perform analyses on specimens collected from a variety of other sources.
  @returns {Reference}
  ###
  subject: -> if @json['subject'] then new Reference(@json['subject'])
  
  ###*
  The diagnostic service that is responsible for issuing the report.
  @returns {Reference}
  ###
  performer: -> if @json['performer'] then new Reference(@json['performer'])
  
  ###*
  The local ID assigned to the report by the order filler, usually by the Information System of the diagnostic service provider.
  @returns {Identifier}
  ###
  identifier: -> if @json['identifier'] then new Identifier(@json['identifier'])
  
  ###*
  Details concerning a test requested.
  @returns {Array} an array of {@link Reference} objects
  ###
  requestDetail: ->
    if @json['requestDetail']
      for item in @json['requestDetail']
        new Reference(item)
  
  ###*
  The section of the diagnostic service that performs the examination e.g. biochemistry, hematology, MRI.
  @returns {CodeableConcept}
  ###
  serviceCategory: -> if @json['serviceCategory'] then new CodeableConcept(@json['serviceCategory'])
  
  ###*
  The time or time-period the observed values are related to. This is usually either the time of the procedure or of specimen collection(s), but very often the source of the date/time is not known, only the date/time itself.
  @returns {Array} an array of {@link Date} objects
  ###
  diagnosticDateTime:-> if @json['diagnosticDateTime'] then DT.DateTime.parse(@json['diagnosticDateTime'])
  ###*
  The time or time-period the observed values are related to. This is usually either the time of the procedure or of specimen collection(s), but very often the source of the date/time is not known, only the date/time itself.
  @returns {Period}
  ###
  diagnosticPeriod: -> if @json['diagnosticPeriod'] then new Period(@json['diagnosticPeriod'])
  
  ###*
  Details about the specimens on which this Disagnostic report is based.
  @returns {Array} an array of {@link Reference} objects
  ###
  specimen: ->
    if @json['specimen']
      for item in @json['specimen']
        new Reference(item)
  
  ###*
  Observations that are part of this diagnostic report. Observations can be simple name/value pairs (e.g. "atomic" results), or they can be grouping observations that include references to other members of the group (e.g. "panels").
  @returns {Array} an array of {@link Reference} objects
  ###
  result: ->
    if @json['result']
      for item in @json['result']
        new Reference(item)
  
  ###*
  One or more links to full details of any imaging performed during the diagnostic investigation. Typically, this is imaging performed by DICOM enabled modalities, but this is not required. A fully enabled PACS viewer can use this information to provide views of the source images.
  @returns {Array} an array of {@link Reference} objects
  ###
  imagingStudy: ->
    if @json['imagingStudy']
      for item in @json['imagingStudy']
        new Reference(item)
  
  ###*
  A list of key images associated with this report. The images are generally created during the diagnostic process, and may be directly of the patient, or of treated specimens (i.e. slides of interest).
  @returns {Array} an array of {@link DiagnosticReportImageComponent} objects
  ###
  image: ->
    if @json['image']
      for item in @json['image']
        new DiagnosticReportImageComponent(item)
  
  ###*
  Concise and clinically contextualized narrative interpretation of the diagnostic report.
  @returns {Array} an array of {@link String} objects
  ###
  conclusion:-> @json['conclusion']
  
  ###*
  Codes for the conclusion.
  @returns {Array} an array of {@link CodeableConcept} objects
  ###
  codedDiagnosis: ->
    if @json['codedDiagnosis']
      for item in @json['codedDiagnosis']
        new CodeableConcept(item)
  
  ###*
  Rich text representation of the entire result as issued by the diagnostic service. Multiple formats are allowed but they SHALL be semantically equivalent.
  @returns {Array} an array of {@link Attachment} objects
  ###
  presentedForm: ->
    if @json['presentedForm']
      for item in @json['presentedForm']
        new Attachment(item)
  



module.exports.DiagnosticReport = DiagnosticReport
