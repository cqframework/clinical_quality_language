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
Assertions and measurements made about a patient, device or other subject.

ObservationResults are a central element in healthcare, used to support diagnosis, monitor progress, determine baselines and patterns and even capture demographic characteristics. Fundamentally, observations are name/value pair assertions. Simple observation values, such a body temperature, are specified in the value attribute. Richer values, e.g., result panels, aggregate observations from diagnostic imaging, and microbiology sensitivity results,  are specified in the detailedResult attribute., 

This data type does not support the storage of the image or signal sequences such as electrocardiogram data.  However, the observations and interpretation made from the images and signals can be represented here.
 
###
require './Specimen'
require './StatementOfOccurrence'
require './BodySite'
require './CodeableConcept'
require './RelatedObservation'
###*
@class ObservationResultBase
@exports  ObservationResultBase as ObservationResultBase
###
class ObservationResultBase
  constructor: (@json) ->
 
  ###*
  Indicates where on the subject's body the observation was made.
  ### 
  bodySite: -> 
    if @json['bodySite']
      for x in @json['bodySite'] 
        new QUICK.BodySite(x)
       
  ###*
  The assessment made based on the result of the observation.
  ### 
  interpretation: -> 
    if @json['interpretation']
      for x in @json['interpretation'] 
        new QUICK.CodeableConcept(x)
       
  ###*
  The technique or mechanism used to perform the observation.
  ### 
  method: -> 
    if @json['method']
      for x in @json['method'] 
        new QUICK.CodeableConcept(x)
       
  ###*
  Identifies what type of observation was performed. e.g., body temperature
  ### 
  name: -> if @json['name'] then new CodeableConcept( @json['name'] )
 
 
  ###*
  An order placed by a provider that led to this observation result
  ### 
  order: -> 
    if @json['order']
      for x in @json['order'] 
        new QUICK.StatementOfOccurrence(x)
       
  ###*
  Observations related to this observation in some way, e.g., used to derive this observation, previous versions of this observation.

Related observations do not include components. Those are modeled in ObservationResultGroup. 
  ### 
  relatedObservation: -> 
    if @json['relatedObservation']
      for x in @json['relatedObservation'] 
        new QUICK.RelatedObservation(x)
       
  ###*
  An estimate of the degree to which quality issues have impacted on the value reported. e.g., result is ok, measurement still ongoing, results are questionable. Usually, unreliable results are not recorded, but that is not always possible. In such cases, this attribute makes the receiver aware of the quality of the result.
  ### 
  reliability: -> 
    if @json['reliability']
      for x in @json['reliability'] 
        new QUICK.CodeableConcept(x)
       
  ###*
  The specimen that was used when this observation was made.
Observations are not made on specimens themselves; they are made on a subject, but usually by the means of a specimen. Note that although specimens are often involved, they are not always tracked and reported explicitly. Also note that observation resources are often used in contexts that track the specimen explicity (e.g. Diagnostic Report).
  ### 
  specimen: -> 
    if @json['specimen']
      for x in @json['specimen'] 
        new QUICK.Specimen(x)
       
  ###*
  The status of the result value. e.g., preliminary, final
  ### 
  status: -> 
    if @json['status']
      for x in @json['status'] 
        new QUICK.CodeableConcept(x)
       
  ###*
  Method by which the observation result was validated, e.g., human review, sliding average.
  ### 
  validationMethod: -> 
    if @json['validationMethod']
      for x in @json['validationMethod'] 
        new QUICK.CodeableConcept(x)
       

module.exports.ObservationResultBase = ObservationResultBase
