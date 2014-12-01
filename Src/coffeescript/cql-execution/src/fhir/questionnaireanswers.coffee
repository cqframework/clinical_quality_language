
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
@class QuestionAnswerComponent
@exports  QuestionAnswerComponent as QuestionAnswerComponent
###
class QuestionAnswerComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  Single-valued answer to the question.
  @returns {Array} an array of {@link boolean} objects
  ###
  valueBoolean:-> @json['valueBoolean']
  ###*
  Single-valued answer to the question.
  @returns {Array} an array of {@link Number} objects
  ###
  valueDecimal:-> @json['valueDecimal']
  ###*
  Single-valued answer to the question.
  @returns {Array} an array of {@link Number} objects
  ###
  valueInteger:-> @json['valueInteger']
  ###*
  Single-valued answer to the question.
  @returns {Array} an array of {@link Date} objects
  ###
  valueDate:-> if @json['valueDate'] then DT.DateTime.parse(@json['valueDate'])
  ###*
  Single-valued answer to the question.
  @returns {Array} an array of {@link Date} objects
  ###
  valueDateTime:-> if @json['valueDateTime'] then DT.DateTime.parse(@json['valueDateTime'])
  ###*
  Single-valued answer to the question.
  @returns {Array} an array of {@link Date} objects
  ###
  valueInstant:-> if @json['valueInstant'] then DT.DateTime.parse(@json['valueInstant'])
  ###*
  Single-valued answer to the question.
  @returns {time}
  ###
  valueTime: -> if @json['valueTime'] then new time(@json['valueTime'])
  ###*
  Single-valued answer to the question.
  @returns {Array} an array of {@link String} objects
  ###
  valueString:-> @json['valueString']
  ###*
  Single-valued answer to the question.
  @returns {Attachment}
  ###
  valueAttachment: -> if @json['valueAttachment'] then new Attachment(@json['valueAttachment'])
  ###*
  Single-valued answer to the question.
  @returns {Coding}
  ###
  valueCoding: -> if @json['valueCoding'] then new Coding(@json['valueCoding'])
  ###*
  Single-valued answer to the question.
  @returns {Quantity}
  ###
  valueQuantity: -> if @json['valueQuantity'] then new Quantity(@json['valueQuantity'])
  ###*
  Single-valued answer to the question.
  @returns {Reference}
  ###
  valueReference: -> if @json['valueReference'] then new Reference(@json['valueReference'])
  

###* 
Embedded class
@class QuestionComponent
@exports  QuestionComponent as QuestionComponent
###
class QuestionComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  Identifies the question from the Questionnaire that corresponds to this question in the QuestionnaireAnswers resource.
  @returns {Array} an array of {@link String} objects
  ###
  linkId:-> @json['linkId']
  
  ###*
  Text of the question as it is shown to the user.
  @returns {Array} an array of {@link String} objects
  ###
  text:-> @json['text']
  
  ###*
  The respondent's answer(s) to the question.
  @returns {Array} an array of {@link QuestionAnswerComponent} objects
  ###
  answer: ->
    if @json['answer']
      for item in @json['answer']
        new QuestionAnswerComponent(item)
  
  ###*
  Nested group, containing nested question for this question. The order of groups within the question is relevant.
  @returns {Array} an array of {@link GroupComponent} objects
  ###
  group: ->
    if @json['group']
      for item in @json['group']
        new GroupComponent(item)
  

###* 
Embedded class
@class GroupComponent
@exports  GroupComponent as GroupComponent
###
class GroupComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  Identifies the group from the Questionnaire that corresponds to this group in the QuestionnaireAnswers resource.
  @returns {Array} an array of {@link String} objects
  ###
  linkId:-> @json['linkId']
  
  ###*
  Text that is displayed above the contents of the group.
  @returns {Array} an array of {@link String} objects
  ###
  title:-> @json['title']
  
  ###*
  Additional text for the group, used for display purposes.
  @returns {Array} an array of {@link String} objects
  ###
  text:-> @json['text']
  
  ###*
  More specific subject this section's answers are about, details the subject given in QuestionnaireAnswers.
  @returns {Reference}
  ###
  subject: -> if @json['subject'] then new Reference(@json['subject'])
  
  ###*
  A sub-group within a group. The ordering of groups within this group is relevant.
  @returns {Array} an array of {@link GroupComponent} objects
  ###
  group: ->
    if @json['group']
      for item in @json['group']
        new GroupComponent(item)
  
  ###*
  Set of questions within this group. The order of questions within the group is relevant.
  @returns {Array} an array of {@link QuestionComponent} objects
  ###
  question: ->
    if @json['question']
      for item in @json['question']
        new QuestionComponent(item)
  
###*
A structured set of questions and their answers. The questions are ordered and grouped into coherent subsets, corresponding to the structure of the grouping of the underlying questions.
@class QuestionnaireAnswers
@exports QuestionnaireAnswers as QuestionnaireAnswers
###
class QuestionnaireAnswers extends DomainResource
  constructor: (@json) ->
    super(@json)
  ###*
  A business identifier assigned to a particular completed (or partially completed) questionnaire.
  @returns {Identifier}
  ###
  identifier: -> if @json['identifier'] then new Identifier(@json['identifier'])
  
  ###*
  Indicates the Questionnaire resource that defines the form for which answers are being provided.
  @returns {Reference}
  ###
  questionnaire: -> if @json['questionnaire'] then new Reference(@json['questionnaire'])
  
  ###*
  The lifecycle status of the questionnaire answers as a whole.
  @returns {Array} an array of {@link String} objects
  ###
  status:-> @json['status']
  
  ###*
  The subject of the questionnaire answers.  This could be a patient, organization, practitioner, device, etc.  This is who/what the answers apply to, but is not necessarily the source of information.
  @returns {Reference}
  ###
  subject: -> if @json['subject'] then new Reference(@json['subject'])
  
  ###*
  Person who received the answers to the questions in the QuestionnaireAnswers and recorded them in the system.
  @returns {Reference}
  ###
  author: -> if @json['author'] then new Reference(@json['author'])
  
  ###*
  The date and/or time that this version of the questionnaire answers was authored.
  @returns {Array} an array of {@link Date} objects
  ###
  authored:-> if @json['authored'] then DT.DateTime.parse(@json['authored'])
  
  ###*
  The person who answered the questions about the subject. Only used when this is not the subject him/herself.
  @returns {Reference}
  ###
  source: -> if @json['source'] then new Reference(@json['source'])
  
  ###*
  Encounter during which this set of questionnaire answers were collected. When there were multiple encounters, this is the one considered most relevant to the context of the answers.
  @returns {Reference}
  ###
  encounter: -> if @json['encounter'] then new Reference(@json['encounter'])
  
  ###*
  A group of questions to a possibly similarly grouped set of questions in the questionnaire answers.
  @returns {GroupComponent}
  ###
  group: -> if @json['group'] then new GroupComponent(@json['group'])
  



module.exports.QuestionnaireAnswers = QuestionnaireAnswers
