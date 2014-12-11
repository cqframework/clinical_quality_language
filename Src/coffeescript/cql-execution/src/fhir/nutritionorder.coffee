
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
@class NutritionOrderItemOralDietNutrientsComponent
@exports  NutritionOrderItemOralDietNutrientsComponent as NutritionOrderItemOralDietNutrientsComponent
###
class NutritionOrderItemOralDietNutrientsComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  Identifies the type of nutrient that is being modified such as cabohydrate or sodium.
  @returns {CodeableConcept}
  ###
  modifier: -> if @json['modifier'] then new CodeableConcept(@json['modifier'])
  
  ###*
  The quantity or range of the specified nutrient to supply.
  @returns {Quantity}
  ###
  amountQuantity: -> if @json['amountQuantity'] then new Quantity(@json['amountQuantity'])
  ###*
  The quantity or range of the specified nutrient to supply.
  @returns {Range}
  ###
  amountRange: -> if @json['amountRange'] then new Range(@json['amountRange'])
  

###* 
Embedded class
@class NutritionOrderItemOralDietTextureComponent
@exports  NutritionOrderItemOralDietTextureComponent as NutritionOrderItemOralDietTextureComponent
###
class NutritionOrderItemOralDietTextureComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  Identifies any texture modifications (for solid foods) that should be made, e.g. easy to chew, chopped, ground, pureed.
  @returns {CodeableConcept}
  ###
  modifier: -> if @json['modifier'] then new CodeableConcept(@json['modifier'])
  
  ###*
  Indicates what specific type of food (e.g., meats) the texture modification applies to or may apply to all foods in the diet.
  @returns {CodeableConcept}
  ###
  foodType: -> if @json['foodType'] then new CodeableConcept(@json['foodType'])
  

###* 
Embedded class
@class NutritionOrderItemOralDietComponent
@exports  NutritionOrderItemOralDietComponent as NutritionOrderItemOralDietComponent
###
class NutritionOrderItemOralDietComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  A set of one or more codes representing diets that describe what can be consumed orally (i.e., take via the mouth).
  @returns {Array} an array of {@link CodeableConcept} objects
  ###
  code: ->
    if @json['code']
      for item in @json['code']
        new CodeableConcept(item)
  
  ###*
  Class that defines the details of any nutrient modifications required for the oral diet.
  @returns {Array} an array of {@link NutritionOrderItemOralDietNutrientsComponent} objects
  ###
  nutrients: ->
    if @json['nutrients']
      for item in @json['nutrients']
        new NutritionOrderItemOralDietNutrientsComponent(item)
  
  ###*
  Class that describes any texture modifications required for the patient to safely consume various types of solid foods.
  @returns {Array} an array of {@link NutritionOrderItemOralDietTextureComponent} objects
  ###
  texture: ->
    if @json['texture']
      for item in @json['texture']
        new NutritionOrderItemOralDietTextureComponent(item)
  
  ###*
  Identifies the required consistency (e.g., honey-thick, nectar-thick, thin, thickened.) of liquids or fluids served to the patient.
  @returns {Array} an array of {@link CodeableConcept} objects
  ###
  fluidConsistencyType: ->
    if @json['fluidConsistencyType']
      for item in @json['fluidConsistencyType']
        new CodeableConcept(item)
  
  ###*
  A descriptive name of the required diets that describe what can be consumed orally (i.e., take via the mouth).
  @returns {Array} an array of {@link String} objects
  ###
  description:-> @json['description']
  

###* 
Embedded class
@class NutritionOrderItemSupplementComponent
@exports  NutritionOrderItemSupplementComponent as NutritionOrderItemSupplementComponent
###
class NutritionOrderItemSupplementComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  Indicates the type of nutritional supplement product required such as high protein or pediatric clear liquid supplement.
  @returns {Array} an array of {@link CodeableConcept} objects
  ###
  type: ->
    if @json['type']
      for item in @json['type']
        new CodeableConcept(item)
  
  ###*
  The amount of the nutritional supplement product to provide to the patient.
  @returns {Quantity}
  ###
  quantity: -> if @json['quantity'] then new Quantity(@json['quantity'])
  
  ###*
  The name of the nutritional supplement product to be provided to the patient.
  @returns {Array} an array of {@link String} objects
  ###
  name:-> @json['name']
  

###* 
Embedded class
@class NutritionOrderItemEnteralFormulaComponent
@exports  NutritionOrderItemEnteralFormulaComponent as NutritionOrderItemEnteralFormulaComponent
###
class NutritionOrderItemEnteralFormulaComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  Indicates the type of enteral or infant formula requested such as pediatric elemental formula or.
  @returns {CodeableConcept}
  ###
  baseFormulaType: -> if @json['baseFormulaType'] then new CodeableConcept(@json['baseFormulaType'])
  
  ###*
  Indicates the type of modular component such as protein, carbohydrate or fiber to be provided in addition to or mixed with the base formula.
  @returns {Array} an array of {@link CodeableConcept} objects
  ###
  additiveType: ->
    if @json['additiveType']
      for item in @json['additiveType']
        new CodeableConcept(item)
  
  ###*
  TODO ***CARD AND TYPE ARE PLACEHOLDERS TO COMPLETE BUILD.  Need to discuss***.
  @returns {Array} an array of {@link Quantity} objects
  ###
  caloricDensity: ->
    if @json['caloricDensity']
      for item in @json['caloricDensity']
        new Quantity(item)
  
  ###*
  ***CARD AND TYPE ARE PLACEHOLDERS TO COMPLETE BUILD.  Need to discuss***administration details including rate (ml per hour), route of adminstration, total volume.
  @returns {Array} an array of {@link CodeableConcept} objects
  ###
  routeofAdministration: ->
    if @json['routeofAdministration']
      for item in @json['routeofAdministration']
        new CodeableConcept(item)
  
  ###*
  TODO ***CARD AND TYPE ARE PLACEHOLDERS TO COMPLETE BUILD.  Need to discuss***.
  @returns {Array} an array of {@link Quantity} objects
  ###
  rate: ->
    if @json['rate']
      for item in @json['rate']
        new Quantity(item)
  
  ###*
  TODO.
  @returns {Array} an array of {@link String} objects
  ###
  baseFormulaName:-> @json['baseFormulaName']
  

###* 
Embedded class
@class NutritionOrderItemComponent
@exports  NutritionOrderItemComponent as NutritionOrderItemComponent
###
class NutritionOrderItemComponent extends BackboneElement
  constructor: (@json) ->
    super(@json)
  ###*
  The frequency at which the diet, oral supplement or enteral formula should be given.
  @returns {Timing}
  ###
  scheduledTiming: -> if @json['scheduledTiming'] then new Timing(@json['scheduledTiming'])
  ###*
  The frequency at which the diet, oral supplement or enteral formula should be given.
  @returns {Period}
  ###
  scheduledPeriod: -> if @json['scheduledPeriod'] then new Period(@json['scheduledPeriod'])
  
  ###*
  Indicates whether the nutrition item is  currently in effect for the patient.
  @returns {Array} an array of {@link boolean} objects
  ###
  isInEffect:-> @json['isInEffect']
  
  ###*
  Class that defines the components of an oral diet order for the patient.
  @returns {NutritionOrderItemOralDietComponent}
  ###
  oralDiet: -> if @json['oralDiet'] then new NutritionOrderItemOralDietComponent(@json['oralDiet'])
  
  ###*
  Class that defines the components of a supplement order for the patient.
  @returns {NutritionOrderItemSupplementComponent}
  ###
  supplement: -> if @json['supplement'] then new NutritionOrderItemSupplementComponent(@json['supplement'])
  
  ###*
  Class that defines the components of an enteral formula order for the patient.
  @returns {NutritionOrderItemEnteralFormulaComponent}
  ###
  enteralFormula: -> if @json['enteralFormula'] then new NutritionOrderItemEnteralFormulaComponent(@json['enteralFormula'])
  
###*
A request to supply a diet, formula feeding (enteral) or oral nutritional supplement to a patient/resident.
@class NutritionOrder
@exports NutritionOrder as NutritionOrder
###
class NutritionOrder extends DomainResource
  constructor: (@json) ->
    super(@json)
  ###*
  The person (patient) who needs the nutrition order for an oral diet, nutritional supplement and/or enteral or formula feeding.
  @returns {Reference}
  ###
  subject: -> if @json['subject'] then new Reference(@json['subject'])
  
  ###*
  The practitioner that holds legal responsibility for ordering the diet, nutritional supplement, or formula feedings.
  @returns {Reference}
  ###
  orderer: -> if @json['orderer'] then new Reference(@json['orderer'])
  
  ###*
  Identifiers assigned to this order by the order sender or by the order receiver.
  @returns {Array} an array of {@link Identifier} objects
  ###
  identifier: ->
    if @json['identifier']
      for item in @json['identifier']
        new Identifier(item)
  
  ###*
  An encounter that provides additional informaton about the healthcare context in which this request is made.
  @returns {Reference}
  ###
  encounter: -> if @json['encounter'] then new Reference(@json['encounter'])
  
  ###*
  The date and time that this nutrition order was requested.
  @returns {Array} an array of {@link Date} objects
  ###
  dateTime:-> if @json['dateTime'] then DT.DateTime.parse(@json['dateTime'])
  
  ###*
  The ability to list substances that may cause allergies or intolerances which should be included in the nutrition order.
  @returns {Array} an array of {@link Reference} objects
  ###
  allergyIntolerance: ->
    if @json['allergyIntolerance']
      for item in @json['allergyIntolerance']
        new Reference(item)
  
  ###*
  This modifier is used to convey order-specific modifiers about the type of food that should be given. These can be derived from patient allergies, intolerances, or preferences such as Halal, Vegan or Kosher. This modifier applies to the entire nutrition order inclusive of the oral diet, nutritional supplements and enteral formula feedings.
  @returns {Array} an array of {@link CodeableConcept} objects
  ###
  foodPreferenceModifier: ->
    if @json['foodPreferenceModifier']
      for item in @json['foodPreferenceModifier']
        new CodeableConcept(item)
  
  ###*
  This modifier is used to convey order-specific modifiers about the type of food that should NOT be given. These can be derived from patient allergies, intolerances, or preferences such as No Red Meat, No Soy or No Wheat or  Gluten-Free. This modifier applies to the entire nutrition order inclusive of the oral diet, nutritional supplements and enteral formula feedings.
  @returns {Array} an array of {@link CodeableConcept} objects
  ###
  excludeFoodModifier: ->
    if @json['excludeFoodModifier']
      for item in @json['excludeFoodModifier']
        new CodeableConcept(item)
  
  ###*
  Different items that combine to make a complete description of the nutrition to be provided via oral diet, nutritional supplement and/or formula order.
  @returns {Array} an array of {@link NutritionOrderItemComponent} objects
  ###
  item: ->
    if @json['item']
      for item in @json['item']
        new NutritionOrderItemComponent(item)
  
  ###*
  The workflow status of the nutrition order request, e.g., Active, Inactive, Pending, Held, Canceled, Suspended.
  @returns {Array} an array of {@link String} objects
  ###
  status:-> @json['status']
  



module.exports.NutritionOrder = NutritionOrder
