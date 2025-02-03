library TestHidingStringLiteral

define "IWantToBeHidden": 1
define "IWantToHide": 'IWantToBeHidden'

define function iWantToHide(value String) :
    'IWantToHide'

define function iWantToHide(value Integer) :
    'IWantToHide'
