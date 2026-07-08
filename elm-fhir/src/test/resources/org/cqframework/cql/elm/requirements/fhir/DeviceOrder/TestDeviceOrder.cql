library TestDeviceOrder

using QICore version '4.1.1'

include FHIRHelpers version '4.0.1'
include QICoreCommon version '1.5.000'

valueset "Frailty Device": 'http://example.org/fhir/valueset/frailty-device'

parameter "Measurement Period" Interval<DateTime>

context Patient

define fluent function isDeviceOrder(DeviceRequest List<DeviceRequest>):
  DeviceRequest D
    where D.status in { 'active', 'completed' }
      and D.intent = 'order'

define "Has Criteria Indicating Frailty":
  exists ( (([DeviceRequest: "Frailty Device"]).isDeviceOrder()) FrailtyDeviceOrder
      where FrailtyDeviceOrder.doNotPerform() is not true
      and FrailtyDeviceOrder.authoredOn.toInterval() during day of "Measurement Period"
  )