library "Issue_246" version '1'

using FHIR version '3.0.0'

parameter "Medication Class 1" List<Medication>
parameter "Medication Class 2" List<Medication>
parameter medicationCodes List<Medication>

define Med1:
  [Medication] M
    where M.status.value = 'active'

define Med2:
  [Medication] M
    where M.status.value = 'inactive'

define formattedCase:
  case
    when medicationCodes included in "Medication Class 1" then true
    when medicationCodes included in "Medication Class 2" then true
    //other medication class checks in proceeding lines
    else false
  end

define unformattedCase:
case when medicationCodes included in "Medication Class 1" then true when medicationCodes included in "Medication Class 2" then true
  //other medication class checks in proceeding lines
  else false end

define complexCase:
  from
                  Med1 M1,
                  Med2 M2
                  let medicationCodes: { M1.code, M2.code }
                  return ( case
                        when medicationCodes included in "Medication Class 1" then true
                        when medicationCodes included in "Medication Class 2" then true
            //other medication class checks in proceeding lines
            else false
         end )