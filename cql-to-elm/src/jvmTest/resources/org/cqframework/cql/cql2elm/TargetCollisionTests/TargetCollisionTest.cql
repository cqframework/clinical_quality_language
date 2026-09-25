library TargetCollisionTest

using TargetCollision

/*
  Repro for issue #1435: two fluent-function overloads declared across sibling types that share a
  model-info `target` (WidgetNotDone declares target="Widget"). TypeBuilder.dataTypeToQName
  substitutes `target` for the type's own name when building the ELM QName for a NamedType
  operand, so both overloads below should -- today -- serialize to the identical QName
  (ns, "Widget"), even though "recorded" is invoked against a WidgetNotDone.
*/
define fluent function "recorded"(procedureNotDone WidgetNotDone): null

define fluent function "recorded"(procedure Widget): null

define "CallOnNotDone": [WidgetNotDone] w return w.recorded()

define "IsNotDone": [WidgetNotDone] w return w is WidgetNotDone