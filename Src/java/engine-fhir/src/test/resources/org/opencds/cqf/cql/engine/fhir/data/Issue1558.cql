library Issue1558

using QICore version '6.0.0'

include FHIRHelpers version '4.0.1' called FHIRHelpers

context Patient
    
define "Care Teams Participant.Role Issue":
    [CareTeam] CT
        return CT.participant.role