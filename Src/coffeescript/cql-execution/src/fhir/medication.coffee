
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
@class MedicationProductIngredientComponent
@exports  MedicationProductIngredientComponent as MedicationProductIngredientComponent
###
class MedicationProductIngredientComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  The actual ingredient - either a substance (simple ingredient) or another medication.
  @returns {Reference}
  ###
  item: -> if @json['item'] then new Reference(@json['item'])
  
  ###*
  Specifies how many (or how much) of the items there are in this Medication.  E.g. 250 mg per tablet.
  @returns {Ratio}
  ###
  amount: -> if @json['amount'] then new Ratio(@json['amount'])
  

###* 
Embedded class
@class MedicationProductComponent
@exports  MedicationProductComponent as MedicationProductComponent
###
class MedicationProductComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  Describes the form of the item.  Powder; tables; carton.
  @returns {CodeableConcept}
  ###
  form: -> if @json['form'] then new CodeableConcept(@json['form'])
  
  ###*
  Identifies a particular constituent of interest in the product.
  @returns {Array} an array of {@link MedicationProductIngredientComponent} objects
  ###
  ingredient: ->
    if @json['ingredient']
      for item in @json['ingredient']
        new MedicationProductIngredientComponent(item)
  

###* 
Embedded class
@class MedicationPackageContentComponent
@exports  MedicationPackageContentComponent as MedicationPackageContentComponent
###
class MedicationPackageContentComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  Identifies one of the items in the package.
  @returns {Reference}
  ###
  item: -> if @json['item'] then new Reference(@json['item'])
  
  ###*
  The amount of the product that is in the package.
  @returns {Quantity}
  ###
  amount: -> if @json['amount'] then new Quantity(@json['amount'])
  

###* 
Embedded class
@class MedicationPackageComponent
@exports  MedicationPackageComponent as MedicationPackageComponent
###
class MedicationPackageComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  The kind of container that this package comes as.
  @returns {CodeableConcept}
  ###
  container: -> if @json['container'] then new CodeableConcept(@json['container'])
  
  ###*
  A set of components that go to make up the described item.
  @returns {Array} an array of {@link MedicationPackageContentComponent} objects
  ###
  content: ->
    if @json['content']
      for item in @json['content']
        new MedicationPackageContentComponent(item)
  
###*
Primarily used for identification and definition of Medication, but also covers ingredients and packaging.
@class Medication
@exports Medication as Medication
###
class Medication extends DomainResource
  constructor: (@json) ->
    super(@json)
  ###*
  The common/commercial name of the medication absent information such as strength, form, etc.  E.g. Acetaminophen, Tylenol 3, etc.  The fully coordinated name is communicated as the display of Medication.code.
  @returns {Array} an array of {@link String} objects
  ###
  name:-> @json['name']
  
  ###*
  A code (or set of codes) that identify this medication.   Usage note: This could be a standard drug code such as a drug regulator code, RxNorm code, SNOMED CT code, etc. It could also be a local formulary code, optionally with translations to the standard drug codes.
  @returns {CodeableConcept}
  ###
  code: -> if @json['code'] then new CodeableConcept(@json['code'])
  
  ###*
  Set to true if the item is attributable to a specific manufacturer (even if we don't know who that is).
  @returns {Array} an array of {@link boolean} objects
  ###
  isBrand:-> @json['isBrand']
  
  ###*
  Describes the details of the manufacturer.
  @returns {Reference}
  ###
  manufacturer: -> if @json['manufacturer'] then new Reference(@json['manufacturer'])
  
  ###*
  Medications are either a single administrable product or a package that contains one or more products.
  @returns {Array} an array of {@link String} objects
  ###
  kind:-> @json['kind']
  
  ###*
  Information that only applies to products (not packages).
  @returns {MedicationProductComponent}
  ###
  product: -> if @json['product'] then new MedicationProductComponent(@json['product'])
  
  ###*
  Information that only applies to packages (not products).
  @returns {MedicationPackageComponent}
  ###
  package: -> if @json['package'] then new MedicationPackageComponent(@json['package'])
  



module.exports.Medication = Medication
