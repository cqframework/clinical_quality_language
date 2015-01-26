importPackage(org.cqframework.cql.execution);

var CQL = require('./cql');
var FHIR = require('./fhir/models');

var source = new CQL.PatientSource(Engine.getPatientSource());
var patient = source.currentPatient();

while(patient != null) {
  if (patient instanceof FHIR.Patient) {
    Engine.add("FHIR Patient: " + patient.name()[0].family() );
  } else {
    Engine.add("Not a FHIR Patient.");
  }
  patient = source.nextPatient();
}
